package group.j.android.markdownald.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import group.j.android.markdownald.R;
import group.j.android.markdownald.adapter.ExpandableItemAdapter;
import group.j.android.markdownald.base.BaseActivity;
import group.j.android.markdownald.db.DatabaseHelper;
import group.j.android.markdownald.db.JsonCreator;
import group.j.android.markdownald.db.NoteSyncTask;
import group.j.android.markdownald.model.Note;
import group.j.android.markdownald.model.Notebook;

/**
 * Implements the homepage.
 * The notebooks are displayed and there is a default notebook on the top that cannot be moved/swiped and deleted.
 * <p>
 * Its functionality includes:
 * <ul>
 * <li>By clicking the button on the top right corner, create a new note or notebook;</li>
 * <li>By clicking one notebook, display/hide its notes;</li>
 * <li>By clicking one note, display and edit its content;</li>
 * <li>By swiping to left on a note/notebook, delete a note or notebook;</li>
 * <li>By swiping to left on a note/notebook, do more operations including moving and renaming;</li>
 * <li>By swiping to right, display the sidebar, including register, login, tutorial and some information</li>
 * </ul>
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private DatabaseHelper mDatabase;

    private Toolbar mToolbar;
    private TextView toolbar_title;
    private DrawerLayout layout_main;
    private NavigationView navigation_sidebar;
    private RecyclerView mRecyclerView;
    private List<MultiItemEntity> mNotes;
    private ExpandableItemAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView text_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure the Toolbar
        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        // Configure the navigation icon
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_menu_white);

        // Configure the title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar_title = mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.app_name));

        // Configure the overflow icon
        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_baseline_add_white));

        // Configure hte sidebar
        layout_main = findViewById(R.id.layout_main);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_main.openDrawer(Gravity.START);
            }
        });
        navigation_sidebar = findViewById(R.id.navigation_sidebar);
        navigation_sidebar.setNavigationItemSelectedListener(this);
        //change the header of navigation_sidebar in activity_main
        View headerView = navigation_sidebar.getHeaderView(0);
        text_username = (TextView) headerView.findViewById(R.id.text_username);

        // Configure the progress bar
        mProgressBar = findViewById(R.id.progress_circular);

        // Configure the RecyclerView
        mRecyclerView = findViewById(R.id.recycler_note_list);
        mDatabase = getDatabase();
        mNotes = mDatabase.loadDB();
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG, MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID, "");
        mAdapter = new ExpandableItemAdapter(mDatabase, mNotes, MainActivity.this, R.layout.activity_notebook_adapter, userId);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (isLogin()) {
            SharedPreferences sharedPreferences = getSharedPreferences(CONFIG, MODE_PRIVATE);
            String userId = sharedPreferences.getString(USER_ID, "");
            text_username.setText(userId);
        }

        mNotes.clear();
        mNotes.addAll(mDatabase.loadDB());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_note:
                Intent noteIntent = new Intent(this, NoteCreateActivity.class);
                startActivity(noteIntent);
                break;
            case R.id.menu_create_notebook:
                Intent notebookIntent = new Intent(this, NotebookCreateActivity.class);
                startActivity(notebookIntent);
                break;
            case R.id.menu_scan:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.menu_sync:
                SharedPreferences sharedPreferences = getSharedPreferences(CONFIG, MODE_PRIVATE);
                String userId = sharedPreferences.getString(USER_ID, "");
                String password = sharedPreferences.getString(PASSWORD, "");
                List<Notebook> notebooks = mDatabase.getAllNotebooks();
                for (Notebook notebook : notebooks) {
                    List<Note> notes = mDatabase.getAllNotesByNotebook(notebook.getName());
                    for (Note note : notes) {
                        NoteSyncTask syncTask = new NoteSyncTask(new NoteSyncTask.SyncListener() {
                            @Override
                            public void onStart() {
                                mRecyclerView.setVisibility(View.GONE);
                                mProgressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onSuccess() {
                                mProgressBar.setVisibility(View.GONE);
                                mNotes.clear();
                                mNotes.addAll(mDatabase.loadDB());
                                mAdapter.notifyDataSetChanged();
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailed() {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onRegistered() {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }, mDatabase);

                        JsonCreator js = new JsonCreator();
                        syncTask.execute(js.addNote(note.getId(), note.getName(), notebook.getName(), note.getContent(), userId).toString());
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.menu_logout:
                setLogin(false);
                text_username.setText(R.string.all_please_login);
                layout_main.closeDrawer(Gravity.START);
                SharedPreferences sharedPreferences = getSharedPreferences(CONFIG, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent logoutIntent = new Intent(this, MainActivity.class);
                startActivity(logoutIntent);
                break;
            case R.id.menu_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.menu_tutorial:
                Intent settingIntent = new Intent(this, TutorialActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            default:
                break;
        }

        return true;
    }
}

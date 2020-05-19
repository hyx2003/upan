package group.j.android.markdownald.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import group.j.android.markdownald.R;
import group.j.android.markdownald.base.BaseActivity;
import group.j.android.markdownald.db.DatabaseHelper;

/**
 * Implements the interface for renaming the note.
 */
public class NoteRenameActivity extends BaseActivity {
    private static final String TAG = "NoteRenameActivity";
    private static final String EXTRA_NOTE_NAME = "note_name";
    private static final String EXTRA_NOTEBOOK_NAME = "notebook_name";
    private static final String DUPLICATION_REMINDER = "Repeated title.";
    private static final String EMPTY_REMINDER = "The note name cannot be empty.";

    private String oldName;
    private String notebookName;

    private DatabaseHelper mDatabase;

    private Toolbar mToolbar;
    private TextView toolbar_title;
    private EditText edit_rename_note;
    private TextInputLayout layout_rename_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_rename);

        mDatabase = getDatabase();

        oldName = getIntent().getStringExtra(EXTRA_NOTE_NAME);
        notebookName = getIntent().getStringExtra(EXTRA_NOTEBOOK_NAME);

        // Configure the Toolbar
        mToolbar = findViewById(R.id.toolbar_note_rename);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Configure the title
        toolbar_title = mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.all_rename));
        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_baseline_done_white));

        edit_rename_note = findViewById(R.id.edit_rename_note);
        layout_rename_note = findViewById(R.id.layout_rename_note);
        edit_rename_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString().trim();

                if (mDatabase.isNoteByNotebook(name, notebookName)) {
                    layout_rename_note.setError(DUPLICATION_REMINDER);
                } else if (name.isEmpty()) {
                    layout_rename_note.setError(EMPTY_REMINDER);
                } else {
                    layout_rename_note.setError("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_rename, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_rename:
                String newName = edit_rename_note.getText().toString().trim();
                if (newName.isEmpty()) {
                    Toast toast = Toast.makeText(NoteRenameActivity.this, EMPTY_REMINDER, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (!mDatabase.isNoteByNotebook(newName, notebookName)) {
                    mDatabase.updateNoteName(oldName, newName);
                    Intent intent = new Intent(NoteRenameActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(NoteRenameActivity.this, DUPLICATION_REMINDER, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
            default:
                break;
        }

        return true;
    }
}

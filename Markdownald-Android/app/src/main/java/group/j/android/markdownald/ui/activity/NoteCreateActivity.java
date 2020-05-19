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
import group.j.android.markdownald.model.Note;

/**
 * Implements the interface for naming the new note.
 * After creating a new note, the user can edit it.
 * If the name is the same as the previous one, a hint should be offered.
 */
public class NoteCreateActivity extends BaseActivity {
    private static final String TAG = "NoteCreateActivity";
    private static final String DUPLICATION_REMINDER = "Note already exists.";
    private static final String EMPTY_REMINDER = "The note name cannot be empty.";

    private DatabaseHelper mDatabase;

    private Toolbar mToolbar;
    private TextView toolbar_title;
    private EditText edit_note_title;
    private TextInputLayout layout_note_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_create);

        mDatabase = getDatabase();

        // Configure the Toolbar
        mToolbar = findViewById(R.id.toolbar_note_create);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Configure the title
        toolbar_title = mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.all_create_note));

        // Configure the overflow icon
        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_baseline_create_white));

        // Configure the edit text
        edit_note_title = findViewById(R.id.edit_note_title);
        layout_note_title = findViewById(R.id.layout_note_title);
        edit_note_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString().trim();

                if (mDatabase.isNoteByNotebook(name, "Default")) {
                    layout_note_title.setError(DUPLICATION_REMINDER);
                } else if (name.isEmpty()) {
                    layout_note_title.setError(EMPTY_REMINDER);
                } else {
                    layout_note_title.setError("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_create:
                String name = edit_note_title.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast toast = Toast.makeText(NoteCreateActivity.this, EMPTY_REMINDER, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (!mDatabase.isNoteByNotebook(name, "Default")) {
                    long id = mDatabase.createNote(new Note(name));
                    mDatabase.createNoteToNotebook(id, mDatabase.getNotebookByName("Default").getId());
                    Intent intent = new Intent(NoteCreateActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(NoteCreateActivity.this, DUPLICATION_REMINDER, Toast.LENGTH_SHORT);
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

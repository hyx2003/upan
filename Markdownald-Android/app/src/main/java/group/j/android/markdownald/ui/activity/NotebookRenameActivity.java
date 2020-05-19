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
 * Implements the interface for renaming a notebook.
 */
public class NotebookRenameActivity extends BaseActivity {
    private static final String TAG = "NotebookRenameActivity";
    private static final String EXTRA_NOTEBOOK_NAME = "notebook_name";
    private static final String DUPLICATION_REMINDER = "Repeated notebook.";
    private static final String EMPTY_REMINDER = "The notebook name cannot be empty.";

    private String oldName;

    private DatabaseHelper mDatabase;

    private Toolbar mToolbar;
    private TextView toolbar_title;
    private EditText edit_rename_notebook;
    private TextInputLayout layout_rename_notebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook_rename);

        mDatabase = getDatabase();

        oldName = getIntent().getStringExtra(EXTRA_NOTEBOOK_NAME);

        // Configure the Toolbar
        mToolbar = findViewById(R.id.toolbar_notebook_rename);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar_title = mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.all_rename));

        edit_rename_notebook = findViewById(R.id.edit_rename_notebook);
        layout_rename_notebook = findViewById(R.id.layout_rename_notebook);
        edit_rename_notebook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString().trim();

                if (mDatabase.isNotebook(name)) {
                    layout_rename_notebook.setError(DUPLICATION_REMINDER);
                } else if (name.isEmpty()) {
                    layout_rename_notebook.setError(EMPTY_REMINDER);
                } else {
                    layout_rename_notebook.setError("");
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
                String newName = edit_rename_notebook.getText().toString().trim();
                if (newName.isEmpty()) {
                    Toast toast = Toast.makeText(NotebookRenameActivity.this, EMPTY_REMINDER, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (!mDatabase.isNotebook(newName)) {
                    mDatabase.updateNotebook(oldName, newName);
                    Intent intent = new Intent(NotebookRenameActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(NotebookRenameActivity.this, DUPLICATION_REMINDER, Toast.LENGTH_SHORT);
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

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
 * Implements the interface for naming the new directory.
 * If the name is the same as the previous one, a hint should be offered.
 */
public class NotebookCreateActivity extends BaseActivity {
    private static final String TAG = "NotebookCreateActivity";
    private static final String DUPLICATION_REMINDER = "Notebook already exists.";
    private static final String EMPTY_REMINDER = "The notebook name cannot be empty.";

    private DatabaseHelper mDatabase;

    private Toolbar mToolbar;
    private TextView toolbar_title;
    private EditText edit_notebook_title;
    private TextInputLayout layout_notebook_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook_create);

        mDatabase = getDatabase();

        // Configure the Toolbar
        mToolbar = findViewById(R.id.toolbar_notebook_create);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Configure the title
        toolbar_title = mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.all_create_notebook));

        // Configure the edit text
        edit_notebook_title = findViewById(R.id.edit_notebook_title);
        layout_notebook_title = findViewById(R.id.layout_notebook_title);
        edit_notebook_title.addTextChangedListener(new TextWatcher() {
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
                    layout_notebook_title.setError(DUPLICATION_REMINDER);
                } else if (name.isEmpty()) {
                    layout_notebook_title.setError(EMPTY_REMINDER);
                } else {
                    layout_notebook_title.setError("");
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
                String name = edit_notebook_title.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast toast = Toast.makeText(NotebookCreateActivity.this, EMPTY_REMINDER, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (!mDatabase.isNotebook(name)) {
                    mDatabase.createNotebook(name);
                    Intent intent = new Intent(NotebookCreateActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(NotebookCreateActivity.this, DUPLICATION_REMINDER, Toast.LENGTH_SHORT);
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

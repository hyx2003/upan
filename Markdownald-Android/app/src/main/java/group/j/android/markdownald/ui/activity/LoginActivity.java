package group.j.android.markdownald.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import group.j.android.markdownald.R;
import group.j.android.markdownald.base.BaseActivity;
import group.j.android.markdownald.db.DatabaseHelper;
import group.j.android.markdownald.db.JsonCreator;
import group.j.android.markdownald.db.NoteSyncTask;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private static final String ERROR_REMAINDER = "Wrong username or password";

    private DatabaseHelper mDatabase;

    private Toolbar mToolbar;
    private TextView toolbar_title;
    private EditText edit_login_userId;
    private EditText edit_login_password;
    private Button button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = getDatabase();

        // Configure the toolbar
        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar_title = mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.app_name));

        edit_login_userId = findViewById(R.id.edit_login_userId);
        edit_login_password = findViewById(R.id.edit_login_password);

        button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userId = edit_login_userId.getText().toString();
                final String password = edit_login_password.getText().toString();
                NoteSyncTask syncTask = new NoteSyncTask(new NoteSyncTask.SyncListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess() {
                        SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(CONFIG, Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(USER_ID, userId);
                        editor.putString(PASSWORD, password);
                        editor.commit();

                        setLogin(true);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailed() {
                    }

                    @Override
                    public void onRegistered() {
                        Toast toast = Toast.makeText(LoginActivity.this, ERROR_REMAINDER, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }, mDatabase);

                JsonCreator js = new JsonCreator();
                syncTask.execute(js.loginJson(userId, password).toString());
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return true;
    }


}

package group.j.android.markdownald.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jaeger.library.StatusBarUtil;

import group.j.android.markdownald.R;
import group.j.android.markdownald.db.DatabaseHelper;

/**
 * Implements base activity.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String CONFIG = "config";
    public static final String USER_ID = "userId";
    public static final String PASSWORD = "password";

    private DatabaseHelper mDatabase;

    private static boolean isLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = DatabaseHelper.getInstance(getApplicationContext());
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorAccentBlue));
        isLogin = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public DatabaseHelper getDatabase() {
        return mDatabase;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}

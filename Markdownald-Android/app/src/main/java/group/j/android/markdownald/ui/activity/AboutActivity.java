package group.j.android.markdownald.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import group.j.android.markdownald.R;
import group.j.android.markdownald.base.BaseActivity;
import group.j.android.markdownald.util.MarkdownRenderer;
import group.j.android.markdownald.util.MarkdownSyntaxHighlighter;

public class AboutActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView toolbar_title;
    private TextView text_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar_title = mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.app_name));

        // Load the tutorial
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("About.txt");
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();

            String tutorial = new String(bytes);
            text_about = findViewById(R.id.text_about);
            text_about.setMovementMethod(ScrollingMovementMethod.getInstance());
            MarkdownRenderer markdownRenderer = new MarkdownRenderer(this);
            markdownRenderer.setMarkdown(text_about, tutorial);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
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

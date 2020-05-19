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
import group.j.android.markdownald.util.MarkdownSyntaxHighlighter;

/**
 * Provides the Markdown tutorial.
 */
public class TutorialActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView toolbar_title;
    private TextView text_tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Configure the toolbar
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
            inputStream = getAssets().open("Tutorial.txt");
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();

            String tutorial = new String(bytes);
            text_tutorial = findViewById(R.id.text_tutorial);
            text_tutorial.setMovementMethod(ScrollingMovementMethod.getInstance());
            MarkdownSyntaxHighlighter highlighter = new MarkdownSyntaxHighlighter();
            text_tutorial.setText(highlighter.highlight(tutorial));
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

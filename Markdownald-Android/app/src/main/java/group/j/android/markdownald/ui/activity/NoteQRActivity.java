package group.j.android.markdownald.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import group.j.android.markdownald.R;
import group.j.android.markdownald.base.BaseActivity;
import group.j.android.markdownald.util.ShareNodeHandler;

public class NoteQRActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_qr);

        // Configure the Toolbar
        mToolbar = findViewById(R.id.toolbar_note_qr);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ShareNodeHandler shareNodeHandler = new ShareNodeHandler();
        Intent intent = getIntent();
        String content = intent.getStringExtra("note_QR");
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(shareNodeHandler.generateBitmap(content, 600, 600));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}

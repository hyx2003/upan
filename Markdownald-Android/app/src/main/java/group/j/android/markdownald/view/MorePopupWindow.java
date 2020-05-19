package group.j.android.markdownald.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import group.j.android.markdownald.R;
import group.j.android.markdownald.ui.activity.NoteMoveActivity;
import group.j.android.markdownald.ui.activity.NoteRenameActivity;
import group.j.android.markdownald.ui.activity.NotebookRenameActivity;

/**
 * Implements <code>PopupWindow</code> for more operations, such as renaming and move.
 */
public class MorePopupWindow extends PopupWindow {
    private static final String NOTE_NAME = "note_name";
    private static final String NOTEBOOK_NAME = "notebook_name";

    private Context mContext;
    private View mView;
    private boolean isNote;
    private Button btn_move;
    private Button btn_rename;
    private String mNote = "";
    private String mNotebook = "";

    public MorePopupWindow(Context mContext, Boolean isNote) {
        this.mContext = mContext;
        this.mView = LayoutInflater.from(mContext).inflate(R.layout.activity_main_window, null);
        this.setContentView(this.mView);
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setAnimationStyle(R.style.BottomWindowAnim);

        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.mView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(R.id.layout_window).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        this.isNote = isNote;
        btn_move = mView.findViewById(R.id.btn_move);
        if (isNote) {
            btn_move.setOnClickListener(onClickListener);
        } else {
            btn_move.setVisibility(View.GONE);
        }
        btn_rename = mView.findViewById(R.id.btn_rename);
        btn_rename.setOnClickListener(onClickListener);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setBackgroundAlpha(1f);
    }

    public void showAtLocation(View parent, int gravity, int x, int y, String notebook, String note) {
        super.showAtLocation(parent, gravity, x, y);
        setBackgroundAlpha(0.5f);
        this.mNotebook = notebook;
        this.mNote = note;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_move:
                    dismiss();
                    Intent moveIntent = new Intent(mContext, NoteMoveActivity.class);
                    moveIntent.putExtra(NOTE_NAME, mNote);
                    mContext.startActivity(moveIntent);
                    break;
                case R.id.btn_rename:
                    dismiss();
                    if (isNote) {
                        Intent renameIntent = new Intent(mContext, NoteRenameActivity.class);
                        renameIntent.putExtra(NOTE_NAME, mNote);
                        renameIntent.putExtra(NOTEBOOK_NAME, mNotebook);
                        mContext.startActivity(renameIntent);
                    } else {
                        Intent renameIntent = new Intent(mContext, NotebookRenameActivity.class);
                        renameIntent.putExtra(NOTEBOOK_NAME, mNotebook);
                        mContext.startActivity(renameIntent);
                    }
                    break;
            }
        }
    };

    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }
}

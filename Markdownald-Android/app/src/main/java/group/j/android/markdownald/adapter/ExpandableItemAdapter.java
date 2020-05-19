package group.j.android.markdownald.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout;

import java.util.List;

import group.j.android.markdownald.R;
import group.j.android.markdownald.db.DatabaseHelper;
import group.j.android.markdownald.db.JsonCreator;
import group.j.android.markdownald.db.NoteSyncTask;
import group.j.android.markdownald.model.Note;
import group.j.android.markdownald.model.Notebook;
import group.j.android.markdownald.view.MorePopupWindow;
import group.j.android.markdownald.ui.activity.NoteEditActivity;

/**
 * Implements <code>Adapter</code> with the expandable item.
 * Swiping to delete and more operations are offered.
 */
public class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private static final int TYPE_LEVEL_ZERO = 0;
    private static final int TYPE_LEVEL_ONE = 1;

    private static final String EXTRA_NOTE_NAME = "note_name";
    private static final String EXTRA_NOTE_CONTENT = "note_content";

    private DatabaseHelper mDatabase;
    private String pid;
    private Context context;
    private MorePopupWindow notePopupWindow;
    private MorePopupWindow notebookPopupWindow;
    private EasySwipeMenuLayout easySwipeMenuLayout;


    public ExpandableItemAdapter(DatabaseHelper mDatabase, List<MultiItemEntity> data, Context context, int layoutResId, String pid) {
        super(data);
        this.pid = pid;
        this.mDatabase = mDatabase;
        this.context = context;
        this.notePopupWindow = new MorePopupWindow(context, true);
        this.notebookPopupWindow = new MorePopupWindow(context, false);
        this.setDefaultViewTypeLayout(layoutResId);

        addItemType(TYPE_LEVEL_ZERO, R.layout.activity_notebook_adapter);
        addItemType(TYPE_LEVEL_ONE, R.layout.activity_note_adapter);
    }


    // Fixes the default notebook on the top
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        MultiItemEntity tmp = getData().get(position);
        if (tmp instanceof Notebook && ((Notebook) tmp).getName().equals("Default")) {
            this.easySwipeMenuLayout = holder.getView(R.id.layout_swipe_menu);
            easySwipeMenuLayout.setCanLeftSwipe(false);
        }
    }

    @Override
    protected void convert(final BaseViewHolder holder, final MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case TYPE_LEVEL_ZERO:
                holder.setText(R.id.text_title, ((Notebook) item).getName());
                holder.getView(R.id.view_content).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        Notebook notebook = (Notebook) getData().get(pos);
                        if (notebook.isExpanded()) {
                            collapse(pos, true);
                        } else {
                            expand(pos, true);
                        }
                    }
                });

                break;
            case TYPE_LEVEL_ONE:
                holder.setText(R.id.text_title, ((Note) item).getName());
                holder.getView(R.id.view_content).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        Note note = (Note) getData().get(pos);
                        Intent intent = new Intent(context, NoteEditActivity.class);
                        intent.putExtra(EXTRA_NOTE_NAME, note.getName());
                        intent.putExtra(EXTRA_NOTE_CONTENT, note.getContent());
                        context.startActivity(intent);
                    }
                });

                break;
        }

        holder.getView(R.id.text_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasRemoved = false;
                int pos = holder.getAdapterPosition();

                if (getData().get(pos) instanceof Note) {
                    mDatabase.deleteNote(((Note) (getData().get(pos))).getId());
                    deleteServerNote(((Note) (getData().get(pos))).getId());
                    hasRemoved = true;
                }
                if (getData().get(pos) instanceof Notebook) {
                    mDatabase.deleteNotebook((Notebook) (getData().get(pos)), true);
                    hasRemoved = true;
                }
                if (hasRemoved) {
                    remove(pos);
                    notifyItemRemoved(pos);
                }
            }
        });

        holder.getView(R.id.text_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasySwipeMenuLayout easySwipeMenuLayout = holder.getView(R.id.layout_swipe_menu);
                easySwipeMenuLayout.resetStatus();

                int pos = holder.getAdapterPosition();
                if (getData().get(pos) instanceof Note) {
                    String noteName = ((Note) getData().get(pos)).getName();
                    String notebookName = ((Notebook) (getData().get(getParentPosition(getData().get(pos))))).getName();
                    notePopupWindow.showAtLocation(
                            LayoutInflater.from(context).inflate(R.layout.activity_main_window, null),
                            Gravity.BOTTOM, 0, 0,
                            notebookName, noteName);
                    Log.d(TAG, "onClick: " + noteName);
                }
                if (getData().get(pos) instanceof Notebook) {
                    String notebookName = ((Notebook) (getData().get(pos))).getName();
                    notebookPopupWindow.showAtLocation(
                            LayoutInflater.from(context).inflate(R.layout.activity_main_window, null),
                            Gravity.BOTTOM, 0, 0,
                            notebookName, "");
                    Log.d(TAG, "onClick: " + notebookName);
                }
            }
        });
    }

    public void deleteServerNote(long note_id){
        NoteSyncTask syncTask = new NoteSyncTask(new NoteSyncTask.SyncListener() {
            @Override
            public void onStart() { }
            @Override
            public void onSuccess() { }
            @Override
            public void onFailed() { }
            @Override
            public void onRegistered() { }
        });
        JsonCreator js = new JsonCreator();
        syncTask.execute(js.deleteNote(note_id, pid).toString());
    }

}

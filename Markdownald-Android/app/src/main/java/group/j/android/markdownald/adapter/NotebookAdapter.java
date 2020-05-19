package group.j.android.markdownald.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import group.j.android.markdownald.R;
import group.j.android.markdownald.db.DatabaseHelper;
import group.j.android.markdownald.model.Notebook;
import group.j.android.markdownald.ui.activity.MainActivity;

/**
 * Implements <code>Adapter</code> for moving the note.
 */
public class NotebookAdapter extends RecyclerView.Adapter<NotebookAdapter.ViewHolder> {
    private static final String DUPLICATION_REMINDER = "Repeated note.";

    private DatabaseHelper mDatabase;
    private Context mContext;
    private List<MultiItemEntity> mNotebooks;
    private String mNote;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_notebook_title;

        public ViewHolder(View itemView) {
            super(itemView);
            this.text_notebook_title = itemView.findViewById(R.id.text_notebook_title);
        }
    }

    public NotebookAdapter(DatabaseHelper db, Context mContext, List<MultiItemEntity> mNotebooks) {
        this.mDatabase = db;
        this.mContext = mContext;
        this.mNotebooks = mNotebooks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.activity_note_move_item, parent, false);
        final ViewHolder holder = new ViewHolder(itemView);
        holder.text_notebook_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                String destination = ((Notebook) mNotebooks.get(pos)).getName();
                if (!mDatabase.isNoteByNotebook(mNote, destination)) {
                    mDatabase.updateNoteToNotebook(mDatabase.getNoteByName(mNote).getId(), mDatabase.getNotebookByName(destination).getId());
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(mContext, DUPLICATION_REMINDER, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notebook notebook = (Notebook) mNotebooks.get(position);
        holder.text_notebook_title.setText(notebook.getName());
    }

    @Override
    public int getItemCount() {
        return mNotebooks.size();
    }

    public void setNote(String mNote) {
        this.mNote = mNote;
    }
}

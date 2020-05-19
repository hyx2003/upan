package group.j.android.markdownald.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import group.j.android.markdownald.model.Note;
import group.j.android.markdownald.model.Notebook;

/**
 * Implements file operations.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    private static String getDataDir(Context context) {
        String dataDir = context.getFilesDir().getAbsolutePath() + File.separator + "Data";

        File file = new File(dataDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        return dataDir;
    }

    private static String getNotebookDir(Context context, String notebook) {
        String notebookDir = getDataDir(context) + File.separator + notebook;

        File file = new File(notebookDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        return notebookDir;
    }

    private static File getNoteFile(Context context, String notebook, String note) {
        String notePath = getNotebookDir(context, notebook) + File.separator + note;

        File noteFile = new File(notePath);
        if (!noteFile.exists()) {
            try {
                noteFile.createNewFile();

                Log.d(TAG, "Created note successfully: " + notePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return noteFile;
    }

    public static boolean exists(Context context, String name) {
        return new File(getNotebookDir(context, "Default"), name).exists();
    }

    public static boolean exists(Context context, String notebook, String note){
        return new File(getNotebookDir(context, notebook), note).exists();
    }

    public static boolean notebookExists(Context context, String notebook) {
        return new File(getDataDir(context), notebook).exists();
    }

    public static ArrayList<MultiItemEntity> load(Context context) {
        ArrayList<MultiItemEntity> data = new ArrayList<>();

        File dataDir = new File(getDataDir(context));
        for (File notebookFile : dataDir.listFiles()) {
            Notebook notebook = new Notebook(notebookFile.getName());
            for (File noteFile : notebookFile.listFiles()) {
                String title = noteFile.getName();
                Note note = new Note(title, load(context, notebook.getName(), title));
                notebook.addSubItem(note);
            }
            data.add(notebook);
        }

        if (data.isEmpty()) {
            data.add(new Notebook("Default"));
            getNotebookDir(context,"Default");
        }

        return data;
    }

    public static String load(Context context, String notebook, String note) {
        StringBuilder content = new StringBuilder();
        FileInputStream in = null;
        BufferedReader reader = null;

        try {
            File file = getNoteFile(context, notebook, note);
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }

            Log.d(TAG, "Loaded note successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content.toString();
    }

    public static void save(final Context context, final String notebook, final String note, final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            Handler handler = new Handler(Looper.myLooper());
            Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        saveToSpecific(context, notebook, note, editText.getText().toString());
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });
    }

    public static boolean saveToDir(Context context, String notebook) {
        boolean success = false;
        String notebookDir = getDataDir(context) + File.separator + notebook;

        File file = new File(notebookDir);
        if (!file.exists()) {
            file.mkdirs();
            success = true;
            Log.d(TAG, "Saved notebook successfully: " + notebookDir);
        }

        return success;
    }

    public static void saveToDefault(Context context, String note) {
        saveToSpecific(context, "Default", note, "");
    }

    public static void saveToSpecific(Context context, String notebook, String note, String content) {
        FileOutputStream out = null;
        BufferedWriter writer = null;

        try {
            File file = getNoteFile(context, notebook, note);
            out = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(content);

            Log.d(TAG, "Saved note successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteDefault(Context context, String note) {
        deleteNote(context, "Default", note);
    }

    public static void deleteNotebook(Context context, String notebook) {
        File file = new File(getNotebookDir(context, notebook));

        File[] subFiles = file.listFiles();
        for (int i = 0; i < subFiles.length; i++) {
            deleteNote(context, notebook, subFiles[i].getName());
        }
        if (file.delete()) {
            Log.d(TAG, "Deleted notebook successfully: " + file.getAbsolutePath());
        }
    }

    public static void deleteNote(Context context, String notebook, String note) {
        File file = getNoteFile(context, notebook, note);

        if (file.delete()) {
            Log.d(TAG, "Deleted note successfully: " + file.getAbsolutePath());
        }
    }

    public static void move(Context context, String source, String destination, String title) {
        String content = load(context, source, title);
        deleteNote(context, source, title);
        saveToSpecific(context, destination, title, content);
    }

    public static void rename(Context context, String notebook, String oldName, String newName) {
        File from = getNoteFile(context, notebook, oldName);
        File to = getNoteFile(context, notebook, newName);
        if (from.renameTo(to)) {
            Log.d(TAG, "rename: " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
        }
    }

    public static void rename(Context context, String oldName, String newName) {
        File from = new File(getNotebookDir(context, oldName));
        File to = new File(getNotebookDir(context, newName));
        if (from.renameTo(to)) {
            Log.d(TAG, "rename: " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
        }
    }

    public static String getFiles(Context context){
        return context.getFilesDir().getAbsolutePath();
    }

}

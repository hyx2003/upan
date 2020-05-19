package group.j.android.markdownald.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import group.j.android.markdownald.model.Note;
import group.j.android.markdownald.model.Notebook;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    // Database name
    private static final String DATABASE_NAME = "notes_db";

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_NOTE = "notes";
    private static final String TABLE_NOTEBOOK = "notebooks";
    private static final String TABLE_NOTE_NOTEBOOK = "note_notebooks";

    // Common column names for tables
    private static final String KEY_ID = "id";

    // Column names for NOTE table
    private static final String KEY_NOTE_NAME = "note_name";
    private static final String KEY_NOTE_CONTENT = "note_content";
    private static final String KEY_TIMESTAMP = "timestamp";

    // Column names for NOTEBOOK table
    private static final String KEY_NOTEBOOK_NAME = "notebook_name";

    // Column names for NOTE_NOTEBOOK table
    private static final String KEY_NOTE_ID = "note_id";
    private static final String KEY_NOTEBOOK_ID = "notebook_id";

    // NOTE table create statements
    private static final String CREATE_TABLE_NOTE =
            "CREATE TABLE " + TABLE_NOTE
                    + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_NOTE_NAME + " TEXT,"
                    + KEY_NOTE_CONTENT + " TEXT,"
                    + KEY_TIMESTAMP + " DATETIME"
                    + ")";

    // NOTEBOOK table create statements
    private static final String CREATE_TABLE_NOTEBOOK =
            "CREATE TABLE " + TABLE_NOTEBOOK
                    + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_NOTEBOOK_NAME + " TEXT UNIQUE"
                    + ")";

    // NOTE_NOTEBOOK table create statements
    private static final String CREATE_TABLE_NOTE_NOTEBOOK =
            "CREATE TABLE " + TABLE_NOTE_NOTEBOOK
                    + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_NOTE_ID + " INTEGER,"
                    + KEY_NOTEBOOK_ID + " INTEGER"
                    + ")";

    // Singleton instance
    private static DatabaseHelper sInstance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }

        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_NOTEBOOK);
        db.execSQL(CREATE_TABLE_NOTE_NOTEBOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTEBOOK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE_NOTEBOOK);

        onCreate(db);
    }

    // Creates a note
    public long createNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_NAME, note.getName());
        values.put(KEY_NOTE_CONTENT, note.getContent());
        values.put(KEY_TIMESTAMP, getDateTime());

        Log.d(TAG, "createNote: " + note.getName());

        return db.insert(TABLE_NOTE, null, values);
    }

    // Determines whether there exist a note that has the same name
    public boolean isNote(String note_name) {
        boolean isNote = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTE
                + " WHERE " + KEY_NOTE_NAME + " = '" + note_name + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            isNote = true;
        }
        c.close();

        return isNote;
    }

    // Gets a single note according to its name
    public Note getNoteByName(String note_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTE
                + " WHERE " + KEY_NOTE_NAME + " = '" + note_name + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
        }

        Note note = new Note();
        note.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        note.setName((c.getString(c.getColumnIndex(KEY_NOTE_NAME))));
        note.setContent((c.getString(c.getColumnIndex(KEY_NOTE_CONTENT))));
        note.setTimestamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));
        c.close();

        return note;
    }

    // Gets a single note according to its id
    public Note getNoteById(long note_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTE
                + " WHERE " + KEY_ID + " = " + note_id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
        }

        Note note = new Note();
        note.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        note.setName((c.getString(c.getColumnIndex(KEY_NOTE_NAME))));
        note.setContent((c.getString(c.getColumnIndex(KEY_NOTE_CONTENT))));
        note.setTimestamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));
        c.close();

        return note;
    }

    // Gets all notes
    public List<Note> getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTE;
        Cursor c = db.rawQuery(selectQuery, null);

        List<Note> notes = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                note.setName((c.getString(c.getColumnIndex(KEY_NOTE_NAME))));
                note.setContent((c.getString(c.getColumnIndex(KEY_NOTE_CONTENT))));
                note.setTimestamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));

                notes.add(note);
            } while (c.moveToNext());
        }
        c.close();

        return notes;
    }

    // Updates a note
    public int updateNoteName(String note_name, String updated_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Note note = getNoteByName(note_name);
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_NAME, updated_name);
        values.put(KEY_NOTE_CONTENT, note.getContent());
        values.put(KEY_TIMESTAMP, getDateTime());

        Log.d(TAG, "updateNoteName: " + "from " + note_name + " to " + updated_name);

        return db.update(TABLE_NOTE, values,
                KEY_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public int updateNoteContent(String note_name, String note_content) {
        SQLiteDatabase db = this.getWritableDatabase();

        Note note = getNoteByName(note_name);
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_NAME, note_name);
        values.put(KEY_NOTE_CONTENT, note_content);
        values.put(KEY_TIMESTAMP, getDateTime());

        Log.d(TAG, "updateNoteContent: ");

        return db.update(TABLE_NOTE, values,
                KEY_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    // Deletes a note according to its id
    public void deleteNote(long note_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NOTE,
                KEY_ID + " = ?",
                new String[]{String.valueOf(note_id)});
        db.delete(TABLE_NOTE_NOTEBOOK,
                KEY_NOTE_ID + " = ?"
                , new String[]{String.valueOf(note_id)});

        Log.d(TAG, "deleteNote: ");
    }

    // Determines whether there exists a note in the specified notebook
    public boolean isNoteByNotebook(String note_name, String notebook_name) {
        boolean isNote = false;

        List<Note> notes = getAllNotesByNotebook(notebook_name);
        for (Note note : notes) {
            if (note.getName().equals(note_name)) {
                isNote = true;
            }
        }

        return isNote;
    }

    // Gets all notes under single notebook
    public List<Note> getAllNotesByNotebook(String notebook_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM "
                + TABLE_NOTE + " nt, "
                + TABLE_NOTEBOOK + " nb, "
                + TABLE_NOTE_NOTEBOOK + " nn WHERE nb." + KEY_NOTEBOOK_NAME
                + " = '" + notebook_name + "'" + " AND nb." + KEY_ID
                + " = " + "nn." + KEY_NOTEBOOK_ID + " AND nt." + KEY_ID
                + " = " + "nn." + KEY_NOTE_ID;
        Cursor c = db.rawQuery(selectQuery, null);

        List<Note> notes = new ArrayList<>();
        if (c.moveToNext()) {
            do {
                Note note = new Note();
                note.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                note.setName(c.getString(c.getColumnIndex(KEY_NOTE_NAME)));
                note.setContent((c.getString(c.getColumnIndex(KEY_NOTE_CONTENT))));
                notes.add(note);
            } while (c.moveToNext());
        }
        c.close();

        return notes;
    }


    // Determines whether there exists a notebook that has the same name
    public boolean isNotebook(String notebook_name) {
        boolean isNotebook = false;

        List<Notebook> notebooks = getAllNotebooks();
        for (Notebook notebook : notebooks) {
            if (notebook.getName().equals(notebook_name)) {
                isNotebook = true;
            }
        }

        return isNotebook;
    }

    // Creates a notebook
    public long createNotebook(String notebook_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTEBOOK_NAME, notebook_name);

        Log.d(TAG, "createNotebook: " + notebook_name);

        return db.insert(TABLE_NOTEBOOK, null, values);
    }

    // Gets a existing notebook
    public Notebook getNotebookByName(String notebook_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTEBOOK
                + " WHERE " + KEY_NOTEBOOK_NAME + " = '" + notebook_name + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
        }

        Notebook notebook = new Notebook();
        notebook.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        notebook.setName((c.getString(c.getColumnIndex(KEY_NOTEBOOK_NAME))));
        c.close();

        return notebook;
    }

    // Gets all the existing notebooks
    public List<Notebook> getAllNotebooks() {
        List<Notebook> notebooks = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NOTEBOOK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Notebook notebook = new Notebook();
                notebook.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                notebook.setName(c.getString(c.getColumnIndex(KEY_NOTEBOOK_NAME)));

                notebooks.add(notebook);
            } while (c.moveToNext());
        }
        c.close();

        return notebooks;
    }

    // Updates the name of a notebook
    public int updateNotebook(String notebook_name, String updated_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTEBOOK_NAME, updated_name);

        Notebook notebook = getNotebookByName(notebook_name);

        Log.d(TAG, "updateNotebook: " + "from " + notebook_name + " to " + updated_name);

        return db.update(TABLE_NOTEBOOK, values, KEY_ID + " = ?",
                new String[]{String.valueOf(notebook.getId())});
    }

    // Deletes a notebook with/without its notes
    public void deleteNotebook(Notebook notebook, boolean shouldDeleteAllNotebookNotes) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (shouldDeleteAllNotebookNotes) {
            List<Note> allNotebookNotes = getAllNotesByNotebook(notebook.getName());
            for (Note note : allNotebookNotes) {
                deleteNote(note.getId());
            }
        }

        db.delete(TABLE_NOTEBOOK, KEY_ID + " = ?",
                new String[]{String.valueOf(notebook.getId())});

        Log.d(TAG, "deleteNotebook: " + notebook.getName());
    }

    public long createNoteToNotebook(long note_id, long notebook_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_ID, note_id);
        values.put(KEY_NOTEBOOK_ID, notebook_id);


        return db.insert(TABLE_NOTE_NOTEBOOK, null, values);
    }

    // Updates the notebook of a note
    public int updateNoteToNotebook(long note_id, long notebook_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTEBOOK_ID, notebook_id);

        return db.update(TABLE_NOTE_NOTEBOOK, values, KEY_NOTE_ID + " = ?",
                new String[]{String.valueOf(note_id)});
    }

    // Initialisation
    public List<MultiItemEntity> loadDB() {
        List<MultiItemEntity> notebooks = new ArrayList<>();

        if (getAllNotebooks().isEmpty()) {
            createNotebook("Default");
        }

        for (Notebook notebook : getAllNotebooks()) {
            List<Note> notes = getAllNotesByNotebook(notebook.getName());
            for (Note note : notes) {
                notebook.addSubItem(note);
            }
            notebooks.add(notebook);
        }

        return notebooks;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getWritableDatabase();

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}

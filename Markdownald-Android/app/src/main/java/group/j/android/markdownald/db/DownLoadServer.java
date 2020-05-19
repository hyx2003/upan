package group.j.android.markdownald.db;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

import group.j.android.markdownald.model.Note;

public class DownLoadServer {
    private String TAG = "Down";


    public void downloadData(String result, DatabaseHelper mDatabase) {
        JsonObject js = new JsonParser().parse(result).getAsJsonObject();
        int f = 0;
        for (Map.Entry<String, JsonElement> entry : js.entrySet()) {
            if (f >= 4) {
                JsonObject notejs = new JsonParser().parse(entry.getValue().getAsString()).getAsJsonObject();
                int nid = Integer.parseInt(entry.getKey());
                String title = notejs.get("title").getAsString();
                String cla = notejs.get("class").getAsString();
                String data = notejs.get("data").getAsString();
                if (!mDatabase.isNotebook(cla)) {
                    mDatabase.createNotebook(cla);
                }
                if (!mDatabase.isNoteByNotebook(title, cla)) {
                    long id = mDatabase.createNote(new Note(title, data));
                    mDatabase.createNoteToNotebook(id, mDatabase.getNotebookByName(cla).getId());
                }
            }
            f = f + 1;
        }
    }
}

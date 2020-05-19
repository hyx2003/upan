package group.j.android.markdownald.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Stores data for one note.
 */
public class Note implements MultiItemEntity {
    private int id;
    private String name;
    private String content;
    private String timestamp;

    public Note() {
    }

    public Note(String name) {
        this.name = name;
        this.content = "";
    }

    public Note(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Note(int id, String name, String content, String timestamp) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}

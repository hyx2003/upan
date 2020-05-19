package group.j.android.markdownald.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * Stores a list of <code>Note</code>.
 */
public class Notebook extends AbstractExpandableItem<Note> implements MultiItemEntity {
    private int id;
    private String name;

    public Notebook() {
    }

    public Notebook(String name) {
        this.name = name;
    }

    public Notebook(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}

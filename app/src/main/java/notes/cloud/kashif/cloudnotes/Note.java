package notes.cloud.kashif.cloudnotes;

import java.io.Serializable;

/**
 * Created by Kashif on 3/9/2017.
 * Note pojo
 */

public class Note implements Serializable {

    private int id;
    private String title;
    private String notes;
    private String timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}

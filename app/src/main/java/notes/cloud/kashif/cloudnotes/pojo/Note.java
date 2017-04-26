package notes.cloud.kashif.cloudnotes.pojo;

import java.io.Serializable;

/**
 * Created by Kashif on 3/9/2017.
 * Note pojo
 */

public class Note implements Serializable {

    private int id;
    private String title;
    private String note;
    private int type; //0=note, 1=checklist
    private int position; //the position in the list, used when manually arranged order
    private String createdOn;
    private String modifiedOn;
    private int encrypted; //0=false, 1=true. boolean not available in sqlite

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public boolean isEncrypted() {
        return encrypted == 1;
    }

    public void setEncrypted(int encrypted) {
        this.encrypted = encrypted;
    }

    public int getEncrypted(){
        return encrypted;
    }
}

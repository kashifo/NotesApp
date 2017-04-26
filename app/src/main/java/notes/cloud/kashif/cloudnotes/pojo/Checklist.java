package notes.cloud.kashif.cloudnotes.pojo;

/**
 * Created by Kashif on 4/25/2017.
 */

public class Checklist {

    private String text;
    private boolean checked;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
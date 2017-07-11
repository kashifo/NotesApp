package notes.app.notesapp;

import android.app.Activity;
import android.os.Bundle;

import notes.app.notesapp.pojo.Utils;

public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFrag()).commit();

    }
}

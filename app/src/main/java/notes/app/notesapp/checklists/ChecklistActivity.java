package notes.app.notesapp.checklists;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import notes.app.notesapp.Adapter2Home;
import notes.app.notesapp.R;
import notes.app.notesapp.helpers.DBHelper;
import notes.app.notesapp.pojo.Checklist;
import notes.app.notesapp.pojo.Note;


public class ChecklistActivity extends AppCompatActivity implements Adapter2Home {

    final String TAG = getClass().getSimpleName();
    RelativeLayout rl_rootLayout;
    RecyclerView rv_checklist;
    TextView tv_empty;
    EditText et_title;
    EditText et_textBox;
    ImageView iv_add_btn;

    List<Checklist> itemsList = new ArrayList<>();
    DBHelper dbHelper;
    boolean isNew = true;
    Note note;
    ChecklistAdapter adapter;
    boolean save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        rl_rootLayout = (RelativeLayout) findViewById(R.id.root_activity_checklist);
        rv_checklist = (RecyclerView) findViewById(R.id.rv_checklist);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        et_title = (EditText) findViewById(R.id.et_title);
        et_textBox = (EditText) findViewById(R.id.et_textBox);
        iv_add_btn = (ImageView) findViewById(R.id.iv_add);
        iv_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        rv_checklist.setHasFixedSize(true);
        rv_checklist.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChecklistAdapter(itemsList, this);
        rv_checklist.setAdapter(adapter);

        dbHelper = new DBHelper(this);

        if (getIntent().hasExtra("data")) {

            isNew = false;
            int noteId = getIntent().getExtras().getInt("noteId");
            note = dbHelper.getNote(noteId);
            et_title.setText(note.getTitle());
            parseItems(note.getNote());

        } else {
            note = new Note();
        }

    }//oncreate

    private void addItem() {

        if (!et_textBox.getText().toString().isEmpty()) {
            Checklist ckItem = new Checklist();
            ckItem.setText(et_textBox.getText().toString());
            ckItem.setChecked(false);

            itemsList.add(ckItem);
            adapter.notifyDataSetChanged();

            et_textBox.setText("");

            if(tv_empty.getVisibility()==View.VISIBLE){
                tv_empty.setVisibility(View.GONE);
            }

            save = true;

        } else {
            Toast.makeText(this, "Item can't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause(){
        if( save ) {
            saveItems();
        }
        super.onPause();
    }

    private void saveItems() {

        String title = et_title.getText().toString();

        if (!title.isEmpty() || !itemsList.isEmpty()) {

            if (!title.isEmpty()) {
                note.setTitle(title);
            } else {
                note.setTitle("");
            }

            Gson gson = new Gson();
            String tojson = gson.toJson( itemsList );
            Log.d( TAG, "tojson="+tojson );
            note.setNote( tojson );

            long id;

            if (isNew) {
                note.setType(1);
                id = dbHelper.insertNote(note);
            } else {
                id = dbHelper.updateNote(note);
            }

            if (id != 0L) {
                Snackbar.make(rl_rootLayout, "Checklist Saved", Snackbar.LENGTH_SHORT).show();
                finish();
            }

        }

    }

    private void parseItems(String data){

        Type type = new TypeToken<List<Checklist>>(){}.getType();
        Gson gson = new Gson();
        itemsList = gson.fromJson( data, type );

        if( itemsList!=null && !itemsList.isEmpty()){
            tv_empty.setVisibility(View.GONE);
            adapter = new ChecklistAdapter(itemsList, this);
            rv_checklist.setAdapter(adapter);
        }else {
            tv_empty.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void adapterActionPerformed(Bundle args) {

        int position = args.getInt("position");
        boolean checked = args.getBoolean("checked");

        itemsList.get(position).setChecked(checked);
        save = true;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_checklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.action_share:
                shareNote();
                break;

            case R.id.action_delete:
                deleteNote();
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    private void shareNote(){

        String data = et_title.getText().toString() + "\n";

        for( Checklist item : itemsList ){
            if( !item.isChecked() )
            data += item.getText() +", ";
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, data );
        startActivity(Intent.createChooser(sharingIntent,"Share using"));
    }

    private void deleteNote(){

        new AlertDialog.Builder(this)
                .setTitle("Delete Note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        save = false;

                        if(isNew){
                            finish();
                        }else {
                            dbHelper.deleteNote(note.getId());
                            finish();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

    }
}

package notes.cloud.kashif.cloudnotes.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import notes.cloud.kashif.cloudnotes.R;
import notes.cloud.kashif.cloudnotes.helpers.DBHelper;
import notes.cloud.kashif.cloudnotes.pojo.Note;

/*
Activity to add new note or edit
 */
public class NoteActivity extends AppCompatActivity {

    RelativeLayout rv_rootLayout;
    FloatingActionButton fab_save;
    EditText et_Title, et_Note;
    DBHelper dbHelper;
    boolean isNew=true; //checks whether it's new note
    Note note; //current note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_action_back );
        rv_rootLayout = (RelativeLayout) findViewById(R.id.activity_add_note);
        fab_save = (FloatingActionButton) findViewById(R.id.fabSave);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        et_Title = (EditText) findViewById(R.id.etTitle);
        et_Note = (EditText) findViewById(R.id.etNote);
        dbHelper = new DBHelper(this);

        if (getIntent().hasExtra("data")) {

            isNew = false;
            int noteId = getIntent().getExtras().getInt("noteId");
            note = dbHelper.getNote(noteId);
            et_Title.setText( note.getTitle() );
            et_Note.setText( note.getNote() );

        }else{
            note = new Note();
        }

    }//onCreate


    private void saveNote(){

        String inNote = et_Note.getText().toString();

        if( !inNote.isEmpty() ) {

            String inTitle = et_Title.getText().toString();

            //if no title get title from note
            if (inTitle.isEmpty()) {

                if (inNote.length() > 15) {
                    inTitle = inNote.substring(0, 15);
                    inTitle+="...";
                } else {
                    inTitle = inNote.substring(0, inNote.length());
                }

            }

            note.setTitle(inTitle);
            note.setNote(inNote);

            long id;

            if(isNew){
                note.setType(0);
                id = dbHelper.insertNote(note);
            }else{
                id = dbHelper.updateNote(note);
            }


            if (id != 0L) {
                Snackbar.make(rv_rootLayout, "Note Saved", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }else{
            Snackbar.make(rv_rootLayout, "Empty note can't be saved", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_note, menu);
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
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                et_Title.getText() +"\n"+
                        et_Note.getText()
        );
        startActivity(Intent.createChooser(sharingIntent,"Share using"));
    }

    private void deleteNote(){

        new AlertDialog.Builder(this)
                .setTitle("Delete Note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

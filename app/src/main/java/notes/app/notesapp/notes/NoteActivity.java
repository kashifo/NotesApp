package notes.app.notesapp.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;

import notes.app.notesapp.R;
import notes.app.notesapp.helpers.DBHelper;
import notes.app.notesapp.pojo.Note;
import notes.app.notesapp.pojo.Utils;

/*
Activity to add new note or edit
 */
public class NoteActivity extends AppCompatActivity {

    RelativeLayout rv_rootLayout;
    EditText et_Title, et_Note;
    DBHelper dbHelper;
    boolean isNew = true; //checks whether it's new note
    Note note; //current note
    String oldData = ""; //to check data changes for auto saving
    String prevData; //old data from existing note for revert purpose
    boolean save = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        rv_rootLayout = (RelativeLayout) findViewById(R.id.activity_add_note);

        et_Title = (EditText) findViewById(R.id.etTitle);
        et_Note = (EditText) findViewById(R.id.etNote);

        dbHelper = new DBHelper(this);
        receiveData();

    }//onCreate

    //checks and handle's note open mode
    private void receiveData(){

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //checks whther some other app is sending text data
        if( action != null && action.equals(Intent.ACTION_SEND) ){
            if( type != null && type.equals("text/plain") ){
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if ( sharedText != null  && !sharedText.isEmpty() ) {
                    // Update UI to reflect text being shared
                    note = new Note();
                    et_Note.setText( sharedText );
                }
            }
        }else {
            //check whether a note is opened from list
            if ( getIntent().hasExtra("data") ) {

                isNew = false;
                int noteId = getIntent().getExtras().getInt("noteId");
                note = dbHelper.getNote(noteId);
                et_Title.setText(note.getTitle());
                et_Note.setText(note.getNote());
                prevData = note.getNote();

            } else {
                //note is not opened, create new note
                note = new Note();
            }
        }

    }

    @Override
    public void onPause() {

        String currentText = et_Note.getText().toString();

        if( !currentText.isEmpty() && save ){
            if( !oldData.equals(currentText) ){
                saveNote();
            }
        }

        super.onPause();
    }

    private void saveNote() {

        String inNote = et_Note.getText().toString();

        if (!inNote.isEmpty()) {

            String inTitle = et_Title.getText().toString();

            //if no title get title from note
            if (inTitle.isEmpty()) {

                if (inNote.length() > 15) {
                    inTitle = inNote.substring(0, 15);
                    inTitle += "...";
                } else {
                    inTitle = inNote.substring(0, inNote.length());
                }

                et_Title.setText( inTitle );
            }

            note.setTitle(inTitle);
            note.setNote(inNote);

            long id;

            if (isNew) {
                note.setType(0);
                id = dbHelper.insertNote(note);
            } else {
                id = dbHelper.updateNote(note);
            }


            if (id != 0L) {
                oldData = inNote;
                Snackbar.make(rv_rootLayout, "Note Saved", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(rv_rootLayout, "Empty note can't be saved", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_revert:
                revertNote();
                break;

            case R.id.action_share:
                shareNote();
                break;

            case R.id.action_delete:
                deleteNote();
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    private void revertNote() {
        new AlertDialog.Builder(this)
                .setTitle("Revert Note?")
                .setMessage("Are you sure you want to reset note as it was while opening?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isNew)
                            et_Note.setText("");
                        else
                            et_Note.setText(prevData);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void shareNote() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                et_Title.getText() + "\n" +
                        et_Note.getText()
        );
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    private void deleteNote() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        save = false;

                        if (isNew) {
                            finish();
                        } else {
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

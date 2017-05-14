package notes.app.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import notes.app.notesapp.checklists.ChecklistActivity;
import notes.app.notesapp.helpers.DBHelper;
import notes.app.notesapp.helpers.ItemTouchHelperClass;
import notes.app.notesapp.helpers.SimpleDividerItemDecoration;
import notes.app.notesapp.notes.NoteActivity;
import notes.app.notesapp.notes.NotesListAdapter;
import notes.app.notesapp.pojo.Note;

/*
Created by Kashif on 3/9/2017.
Application starts here, notes loaded from db and displayed here
 */

public class MainActivity extends AppCompatActivity implements Adapter2Home {

    private final String TAG = getClass().getSimpleName();
    RelativeLayout rl_rootLayout;
    RecyclerView rv_notesList;
    TextView tv_empty;

    DBHelper dbHelper;
    ArrayList<Note> notesList = new ArrayList<>();
    Note justDeletedNote; //for undo purpose
    int justDeletedNotePosition;
    NotesListAdapter adapter;
    boolean other_fabs_visible;
    SearchView.OnQueryTextListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#555555'>" + getResources().getString(R.string.app_name) + "</font>"));

        rl_rootLayout = (RelativeLayout) findViewById(R.id.activity_main);
        rv_notesList = (RecyclerView) findViewById(R.id.recyclerView);
        rv_notesList.addItemDecoration(new SimpleDividerItemDecoration(this));
        rv_notesList.setHasFixedSize(true);
        rv_notesList.setLayoutManager(new LinearLayoutManager(this));
        tv_empty = (TextView) findViewById(R.id.tvEmpty);

        //search listener from action bar
        listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit query=" + query);
                searchNotes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange query=" + newText);
                searchNotes(newText);
                return false;
            }
        };

        dbHelper = new DBHelper(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        clearRecycler();
        //get notes saved in sqlite db
        notesList.addAll(dbHelper.getNotes());
        showNotes();
    }

    //clears notes list
    void clearRecycler() {

        if (notesList != null && !notesList.isEmpty() && adapter != null) {
            Log.d(TAG, "Clearing recyler");
            notesList.clear();
            adapter.notifyDataSetChanged();
        }

    }

    void showNotes() {

        if (notesList != null && !notesList.isEmpty()) {
            Log.d(TAG, "notesList!=null");
            tv_empty.setVisibility(View.GONE);

            adapter = new NotesListAdapter(this, notesList, this);
            rv_notesList.setAdapter(adapter);

            ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(rv_notesList);

        } else {
            Log.d(TAG, "empty");
            tv_empty.setVisibility(View.VISIBLE);
        }

    }

    void searchNotes(String query) {

        try {

            if (!query.equals("")) {
                clearRecycler();
                notesList = dbHelper.searchNotes(query);
                showNotes();
            } else {
                clearRecycler();
                //get notes saved in sqlite db
                notesList.addAll(dbHelper.getNotes());
                showNotes();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addClick(View v) {
        if (!other_fabs_visible) {
            findViewById(R.id.other_fabs).setVisibility(View.VISIBLE);
            other_fabs_visible = true;
        } else {
            findViewById(R.id.other_fabs).setVisibility(View.GONE);
            other_fabs_visible = false;
        }
    }

    public void addNewNote(View v) {
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    public void addNewChecklist(View v) {
        Intent intent = new Intent(this, ChecklistActivity.class);
        startActivity(intent);
    }


    //it's called when a notes from list is clicked
    @Override
    public void adapterActionPerformed(Bundle args) {

        Log.d(TAG, "adapterActionPerformed args=" + args);

        String action = args.getString("action");
        int noteId = args.getInt("noteId");

        if (action != null && action.equals(notes.app.notesapp.RowAction.CLICK.toString())) {

            int noteType = args.getInt("noteType");
            Intent intent = new Intent(this, NoteActivity.class);

            if (noteType == 0) {
                intent.putExtra("data", true);
                intent.putExtra("noteId", noteId);
            } else {
                intent = new Intent(this, ChecklistActivity.class);
                intent.putExtra("data", true);
                intent.putExtra("noteId", noteId);
            }

            startActivity(intent);

        } else if (action.equals(notes.app.notesapp.RowAction.DELETE.toString())) {

            justDeletedNote = dbHelper.getNote(noteId);
            justDeletedNotePosition = args.getInt("position");
            dbHelper.deleteNote(noteId);

            notesList.remove(justDeletedNotePosition);
            adapter.notifyItemRemoved(justDeletedNotePosition);

            Snackbar.make(rl_rootLayout, "Note deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbHelper.insertNote(justDeletedNote);
                    notesList.add(justDeletedNotePosition, justDeletedNote);
                    adapter.notifyItemInserted(justDeletedNotePosition);
                }
            }).show();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(listener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cloud) {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

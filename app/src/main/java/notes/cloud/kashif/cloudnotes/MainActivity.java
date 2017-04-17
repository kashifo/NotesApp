package notes.cloud.kashif.cloudnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    boolean adapterSet; //to check whether if list set already


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#555555'>" + getResources().getString(R.string.app_name) + "</font>"));

        rl_rootLayout = (RelativeLayout) findViewById(R.id.activity_main);
        rv_notesList = (RecyclerView) findViewById(R.id.recyclerView);
        rv_notesList.addItemDecoration(new SimpleDividerItemDecoration(this));
        rv_notesList.setHasFixedSize(true);
        rv_notesList.setLayoutManager(new LinearLayoutManager(this));
        tv_empty = (TextView) findViewById(R.id.tvEmpty);
        dbHelper = new DBHelper(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    //clears notes list
    void clearRecycler() {

        if (notesList != null && !notesList.isEmpty() && adapter != null) {
            Log.d(TAG, "Clearing recyler");
            notesList.clear();
            adapter.notifyDataSetChanged();
        }

    }

    void init() {

        clearRecycler();
        //get notes saved in sqlite db
        notesList.addAll(dbHelper.getNotes());

        if (notesList != null && !notesList.isEmpty()) {
            Log.d(TAG, "notesList!=null");
            tv_empty.setVisibility(View.GONE);

            //if first time setting list do all the initialization
            if (!adapterSet) {
                Log.d(TAG, "!adapterSet");
                adapter = new NotesListAdapter(this, notesList, this);
                rv_notesList.setAdapter(adapter);

                ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                itemTouchHelper.attachToRecyclerView(rv_notesList);
            } else {
                //all init done just notify list to update
                Log.d(TAG, "adapterSet");
                adapter.notifyDataSetChanged();
            }

            adapterSet = true;
        } else {
            Log.d(TAG, "empty");
            tv_empty.setVisibility(View.VISIBLE);
        }

    }


    public void addNote(View v) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    //it's called when a notes from list is clicked
    @Override
    public void adapterActionPerformed(Bundle args) {

        Log.d(TAG, "adapterActionPerformed args=" + args);

        String action = args.getString("action");
        Note note = (Note) args.getSerializable("note");

        if (action.equals(RowAction.CLICK.toString())) {

            Intent intent = new Intent(this, AddNoteActivity.class);
            intent.putExtra("data", true);
            intent.putExtra("note", note);
            startActivity(intent);

        } else if (action.equals(RowAction.DELETE.toString())) {

            justDeletedNote = note;
            justDeletedNotePosition = args.getInt("position");
            dbHelper.deleteNote(note.getId());

            notesList.remove(justDeletedNotePosition);
            adapter.notifyItemRemoved(justDeletedNotePosition);

            Snackbar.make(rl_rootLayout, "Note deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbHelper.insertNote(justDeletedNote);
                    notesList.add(justDeletedNotePosition, justDeletedNote);
                    adapter.notifyDataSetChanged();
                }
            }).show();


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

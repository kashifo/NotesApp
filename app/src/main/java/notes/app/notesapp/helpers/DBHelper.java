package notes.app.notesapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import notes.app.notesapp.pojo.Note;

/**
 * Created by Kashif on 3/9/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cloudnotes.db";
    private static final String TABLE1 = "table1";

    private static final String KEY_ID = "ID";
    private static final String KEY_TITLE = "title";
    private static final String KEY_NOTE = "note";
    private static final String KEY_TYPE = "type";

    private static final String KEY_POSITION = "position";
    private static final String KEY_CREATED = "created";
    private static final String KEY_MODIFIED = "modified";
    private static final String KEY_ENCRYPTED = "encrypted";

    private Context context;

    public DBHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE1 = "CREATE TABLE " + TABLE1 + "("

                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_NOTE + " TEXT,"
                + KEY_TYPE + " INTEGER,"
                + KEY_POSITION + " INTEGER,"
                + KEY_CREATED + " TEXT,"
                + KEY_MODIFIED + " TEXT,"
                + KEY_ENCRYPTED + " INTEGER"

                + ")";

        db.execSQL(CREATE_TABLE1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
        // Create tables again
        onCreate(db);
    }


    public long insertNote(Note note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, note.getTitle());
        cv.put(KEY_NOTE, note.getNote());
        cv.put(KEY_TYPE, note.getType());
        cv.put(KEY_POSITION, getRecordsCount()+1 );
        cv.put(KEY_CREATED, getDateTime() );
        cv.put(KEY_MODIFIED, getDateTime() );
        cv.put(KEY_ENCRYPTED, note.getEncrypted() );

        long id = db.insert(TABLE1, null, cv);
        db.close();
        Log.d("MyLog", note.getTitle() + " Note added");
        Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show();
        return id;
    }


    public long updateNote(Note note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, note.getTitle());
        cv.put(KEY_NOTE, note.getNote());
        cv.put(KEY_MODIFIED, getDateTime() );
        cv.put(KEY_ENCRYPTED, note.getEncrypted());

        long id = db.update(TABLE1, cv, KEY_ID + " = ? ", new String[]{String.valueOf(note.getId())});
        db.close();
        Log.d("MyLog", note.getTitle() + " Note updated");
        Toast.makeText(context, "Note Updated", Toast.LENGTH_SHORT).show();
        return id;
    }


    public ArrayList<Note> getNotes() {

        ArrayList<Note> notesList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {

            while (!cursor.isAfterLast()) {

                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setType(cursor.getInt(3));

                notesList.add(note);
                cursor.moveToNext();
            }

        }

        cursor.close();
        db.close();

        Log.d("MyLog", notesList.size() + "Notes retrieved");

        return notesList;
    }

    public Note getNote(int id){

        String query = "SELECT * FROM " + TABLE1 +" WHERE "+KEY_ID+" = "+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Note note = new Note();

        if (cursor.getCount() > 0) {

                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setNote(cursor.getString(2));
                note.setType(cursor.getInt(3));
                note.setCreatedOn(cursor.getString(5));
                note.setModifiedOn(cursor.getString(6));
                note.setEncrypted(cursor.getInt(7));

        }

        cursor.close();
        db.close();

        Log.d("getNote()", note.getTitle() + " Note retrieved");
        return note;
    }

    public int getRecordsCount() {

        String query = "SELECT COUNT(*) FROM " + TABLE1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int recordSize = cursor.getCount();
        cursor.close();
        Log.i( "getRecordsCount", ""+recordSize );
        return recordSize;

    }

    public void deleteNote(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE1, KEY_ID + "=?", new String[]{String.valueOf(id)});

        db.close();
        Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
        Log.d("MyLog", "Note deleted=" + id);

    }


    public void deleteDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }

    private String getDateTime(){

        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MMM-yyyy hh:mm a" );
        Date date = new Date();
        return dateFormat.format(date);

    }

}

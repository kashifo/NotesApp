package notes.cloud.kashif.cloudnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Kashif on 3/9/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cloudnotes.db";
    private static final String TABLE1 = "table1";

    private static final String KEY_DB_ID = "ID";
    private static final String KEY_TITLE = "title";
    private static final String KEY_NOTE = "note";
    private static final String KEY_CREATED = "created_at";

    Context context;

    public DBHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE1 = "CREATE TABLE " + TABLE1 + "("

                + KEY_DB_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_NOTE + " TEXT,"
                + KEY_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP"

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
        cv.put(KEY_TITLE, note.getTitle() );
        cv.put(KEY_NOTE, note.getNotes() );

        long id = db.insert(TABLE1, null, cv);
        db.close();
        Log.d("MyLog", note.getTitle() + " Note added");

        return id;
    }


    public long updateNote(Note note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, note.getTitle() );
        cv.put(KEY_NOTE, note.getNotes() );

        long id = db.update( TABLE1, cv, KEY_DB_ID +" = ? ", new String[]{ String.valueOf(note.getId()) } );
        db.close();
        Log.d("MyLog", note.getTitle() + " Note updated");

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
                note.setNotes(cursor.getString(2));
                note.setTimestamp(cursor.getString(3));

                notesList.add(note);
                cursor.moveToNext();
            }

        }

        cursor.close();
        db.close();

        Log.d("MyLog", notesList.size() + "Notes retrieved");

        return notesList;
    }


    public void deleteNote(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE1, KEY_DB_ID + "=?", new String[]{String.valueOf(id)});

        db.close();
        Log.d("MyLog", "Note deleted=" + id );

    }


    public void deleteDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }


}

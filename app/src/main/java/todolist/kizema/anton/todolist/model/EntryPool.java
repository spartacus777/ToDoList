package todolist.kizema.anton.todolist.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EntryPool {

    private Context c;
    private List <Entry> entries;
    DBHelper dbHelper;

    private boolean wasLoad = false;

    private static EntryPool entryPool = null;

    private EntryPool(Context c){
        this.c = c;

        dbHelper = new DBHelper(c);
        load();
    }

    public void load() {
        if (wasLoad){
            return;
        }

        wasLoad = true;

        entries = new ArrayList<Entry>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int titleColIndex = c.getColumnIndex(DBHelper.TITLE);
            int descrColIndex = c.getColumnIndex(DBHelper.DESCRIPTIONS);
            int aliveColIndex = c.getColumnIndex(DBHelper.ALIVE);
            do {
                String title = c.getString(titleColIndex);
                String descr = c.getString(descrColIndex);
                boolean alive = c.getInt(aliveColIndex) == 1;

                entries.add(new Entry(title, descr, alive));
            } while (c.moveToNext());
        } else{
            Log.d("ANT", "&&&&&&&&&&&&&&&&&&&&&&&&   0 rows");
        }
        c.close();
    }

    public void save(){
        wasLoad = false;
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DBHelper.TABLE_NAME, null, null);
        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);

        Log.i("ANT", "entries.size:"+entries.size());

        for (Entry entry : entries){
            cv.put(DBHelper.TITLE, entry.title);
            cv.put(DBHelper.DESCRIPTIONS, entry.description);
            cv.put(DBHelper.ALIVE, (entry.alive ? "1" : "0"));

            db.insert(DBHelper.TABLE_NAME, null, cv);
        }
    }

    public int getSize(){
        return entries.size();
    }

    public List<Entry> getEntries(){
        return entries;
    }

    public void remove(Entry entry){
        entries.remove(entry);
    }

    public void add(String t, String d){
        Entry entry = new Entry(t,d);
        entries.add(entry);
    }

    public static EntryPool getPool(Context c){
        if (entryPool == null){
            entryPool = new EntryPool(c);
        }

        entryPool.c = c;

        return entryPool;
    }
}

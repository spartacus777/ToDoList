package todolist.kizema.anton.todolist.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anton on 20.04.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "TILTLE_TABLE_NAME";
    public static final String TITLE = "TILTLE_TITLE";
    public static final String DESCRIPTIONS = "TILTLE_DESCRIPTIONS";
    public static final String ALIVE = "TILTLE_ALIVE";


    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" ("
                + "id integer primary key autoincrement,"
                + TITLE+" text,"
                + DESCRIPTIONS+" text,"
                + ALIVE+" integer"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
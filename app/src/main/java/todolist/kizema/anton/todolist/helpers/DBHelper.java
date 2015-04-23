package todolist.kizema.anton.todolist.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anton on 20.04.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "TITLE_TABLE_NAME";
    public static final String TITLE = "TITLE_TITLE";
    public static final String DESCRIPTIONS = "TITLE_DESCRIPTIONS";
    public static final String ALIVE = "TITLE_ALIVE";


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
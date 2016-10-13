package bg.dalexiev.bender.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TO_DOS = "create table " + Schema.ToDo.TABLE_NAME + " ("
                                                    + Schema.ToDo._ID + " integer primary key autoincrement, "
                                                    + Schema.ToDo.TITLE + " text not null, "
                                                    + Schema.ToDo.CREATION_DATE + " integer not null, "
                                                    + Schema.ToDo.IS_DONE + " integer not null default 0)";

    public ToDoHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TO_DOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing for now
    }
}

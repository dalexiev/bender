package bg.dalexiev.bender;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import bg.dalexiev.bender.content.DatabaseContentProvider;

public class ToDoProvider extends DatabaseContentProvider {

    public static final String AUTHORITY = "bg.dalexiev.bender.sample.todo.provider";

    @NonNull
    @Override
    protected String createAuthority() {
        return AUTHORITY;
    }

    @NonNull
    @Override
    protected SQLiteOpenHelper createHelper(@Nullable Context context) {
        return new ToDoHelper(context);
    }
}

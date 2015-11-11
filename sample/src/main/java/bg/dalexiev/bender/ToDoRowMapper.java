package bg.dalexiev.bender;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

import bg.dalexiev.bender.mapper.RowMapper;

public class ToDoRowMapper implements RowMapper<ToDoModel>{

    @Nullable
    @Override
    public ToDoModel toObject(@NonNull Cursor cursor, int rowNum) {
        final ToDoModel toDo = new ToDoModel();

        final int idIndex = cursor.getColumnIndex(Schema.ToDo._ID);
        toDo.setId(cursor.getLong(idIndex));

        final int titleIndex = cursor.getColumnIndex(Schema.ToDo.TITLE);
        toDo.setTitle(cursor.getString(titleIndex));

        final int creationDateIndex = cursor.getColumnIndex(Schema.ToDo.CREATION_DATE);
        final Calendar creationDate = Calendar.getInstance();
        creationDate.setTimeInMillis(cursor.getLong(creationDateIndex));
        toDo.setCreationDate(creationDate);

        final int isDoneIndex = cursor.getColumnIndex(Schema.ToDo.IS_DONE);
        toDo.setIsDone(cursor.getInt(isDoneIndex) == 1);

        return toDo;
    }
}

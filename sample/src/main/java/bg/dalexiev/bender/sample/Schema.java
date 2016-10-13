package bg.dalexiev.bender.sample;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Schema {

    private Schema() {
        // deny instantiation
    }

    public static final Uri BASE_URL = new Uri.Builder().scheme("content").authority(ToDoProvider.AUTHORITY).build();

    public interface ToDo extends BaseColumns {

        String TABLE_NAME = "to_dos";

        Uri URL = BASE_URL.buildUpon().appendPath(TABLE_NAME).build();

        String TITLE = "title";
        String CREATION_DATE = "creation_date";
        String IS_DONE = "is_done";
    }

}

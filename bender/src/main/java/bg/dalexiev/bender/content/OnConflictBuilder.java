package bg.dalexiev.bender.content;


import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

class OnConflictBuilder {

    private int mOnConflict;

    OnConflictBuilder() {
        mOnConflict = SQLiteDatabase.CONFLICT_NONE;
    }

    void setOnConflict(int onConflict) {
        mOnConflict = onConflict;
    }

    Uri appendOnConflictParameter(@NonNull Uri uri) {
        return uri
                .buildUpon()
                .appendQueryParameter(DatabaseContentProvider.PARAM_CONFLICT_ALGORITHM, String.valueOf(mOnConflict))
                .build();
    }

}

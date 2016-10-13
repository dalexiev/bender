package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import bg.dalexiev.bender.util.Preconditions;

/**
 * A wrapper around the default {@link ContentResolver} that adds the Bender syntax sugar.
 *
 * @author danail.alexiev
 * @since 1.1.3
 */
public class BenderContentResolver {

    private final ContentResolver mContentResolver;


    public BenderContentResolver(@NonNull ContentResolver contentResolver) {
        Preconditions.argumentNotNull(contentResolver, "ContentResolver can't be null.");

        mContentResolver = contentResolver;
    }

    @NonNull
    public InsertCommand insert() {
        return new InsertCommand(mContentResolver);
    }

    @NonNull
    public BulkInsertCommand bulkInsert() {
        return new BulkInsertCommand(mContentResolver);
    }

    @NonNull
    public UpdateCommand update() {
        return new UpdateCommand(mContentResolver);
    }

    @NonNull
    public DeleteCommand delete() {
        return new DeleteCommand(mContentResolver);
    }

    @NonNull
    public <R> QueryCommand<R> query(Class<R> clazz) {
        return new QueryCommand<>(mContentResolver);
    }
}

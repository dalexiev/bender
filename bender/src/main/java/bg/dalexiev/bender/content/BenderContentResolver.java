package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    /**
     * @since 1.1.4
     */
    @NonNull
    public BatchCommand applyBatch() {
        return new BatchCommand(mContentResolver);
    }

    /**
     * @since 1.1.4
     */
    public void registerContentObserver(@NonNull Uri uri, boolean notifyForDescendants, @NonNull ContentObserver observer) {
        Preconditions.argumentNotNull(observer, "Observer can't be null");
        Preconditions.argumentNotNull(uri, "Uri can't be null");

        mContentResolver.registerContentObserver(uri, notifyForDescendants, observer);
    }

    /**
     * @since 1.1.4
     */
    public void unregisterContentObserver(@Nullable ContentObserver observer) {
        if (observer != null) {
            mContentResolver.unregisterContentObserver(observer);
        }
    }

}

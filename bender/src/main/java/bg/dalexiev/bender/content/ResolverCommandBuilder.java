package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

/**
 * Deprecated since 1.1.2
 * <p>
 * Use {@link BenderContentResolver} instead.
 */
@Deprecated
public final class ResolverCommandBuilder {

    @NonNull
    public InsertCommand insert(ContentResolver contentResolver) {
        return new InsertCommand(contentResolver);
    }

    @NonNull
    public BulkInsertCommand bulkInsert(ContentResolver contentResolver) {
        return new BulkInsertCommand(contentResolver);
    }

    @NonNull
    public UpdateCommand update(ContentResolver contentResolver) {
        return new UpdateCommand(contentResolver);
    }

    @NonNull
    public DeleteCommand delete(ContentResolver contentResolver) {
        return new DeleteCommand(contentResolver);
    }

    @NonNull
    public <R> QueryCommand<R> query(ContentResolver contentResolver, Class<R> clazz) {
        return new QueryCommand<R>(contentResolver);
    }

}

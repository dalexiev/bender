package bg.dalexiev.bender.content;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import bg.dalexiev.bender.util.Preconditions;

/**
 * A batch of {@link android.content.ContentProvider} requests, executed in a single database transaction.
 *
 * @author danail.alexiev
 * @since 1.1.4
 */
public class BatchCommand extends BaseResolverCommand<ContentProviderResult[], BatchCommand.Callback, BatchCommand> {

    private String mAuthority;
    private final ArrayList<ContentProviderOperation> mOperations;

    protected BatchCommand(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
        mOperations = new ArrayList<>();
    }

    BatchCommand(WorkerHandler workerHandler, ContentResolver resolver, ArrayList<ContentProviderOperation> operations) {
        super(workerHandler, resolver);
        mOperations = operations;
    }

    @NonNull
    @Override
    public BatchCommand onUri(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Batch operations are executed against an authority, not an Uri. Check withAuthority().");
    }

    /**
     * Set the authority to send the batch to.
     *
     * @param authority the authority of the target content provider.
     * @return the current instance.
     */
    public BatchCommand withAuthority(@NonNull String authority) {
        Preconditions.argumentNotNull(authority, "Authority can't be null");

        mAuthority = authority;

        return this;
    }

    /**
     * Add an insert request to the batch.
     *
     * @param insert required. The insert to add.
     * @return the current instance.
     */
    public BatchCommand addInsert(@NonNull InsertCommand insert) {
        Preconditions.argumentNotNull(insert, "Insert can't be null");

        final ContentProviderOperation insertOperation = ContentProviderOperation.newInsert(insert.getUri())
                .withValueBackReferences(insert.getReference())
                .withValues(insert.getContentValues())
                .build();
        mOperations.add(insertOperation);

        return this;
    }

    /**
     * Add an update request to the batch.
     *
     * @param update required. The update to add.
     * @return the current instance.
     */
    public BatchCommand addUpdate(@NonNull UpdateCommand update) {
        Preconditions.argumentNotNull(update, "Update can't be null");

        final ContentProviderOperation updateOperation = ContentProviderOperation.newUpdate(update.getUri())
                .withValueBackReferences(update.getReference())
                .withValues(update.getContentValues())
                .withSelection(update.getSelection(), update.getSelectionArgs())
                .build();
        mOperations.add(updateOperation);

        return this;
    }

    /**
     * Add a delete request to the batch.
     *
     * @param delete required. The delete to add.
     * @return the current instance.
     */
    public BatchCommand addDelete(@NonNull DeleteCommand delete) {
        Preconditions.argumentNotNull(delete, "Delete can't be null");

        final ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(delete.getUri())
                .withSelection(delete.getSelection(), delete.getSelectionArgs())
                .build();
        mOperations.add(deleteOperation);

        return this;
    }

    /**
     * Add a bulk insert request to the batch.
     *
     * @param bulkInsert required. The bulk insert to add.
     * @return the current instance.
     */
    public BatchCommand addBulkInsert(@NonNull BulkInsertCommand bulkInsert) {
        Preconditions.argumentNotNull(bulkInsert, "Bulk Insert can't be null");

        final ContentValues[] values = bulkInsert.getContentValues();
        for (ContentValues contentValues : values) {
            final ContentProviderOperation insertOperation = ContentProviderOperation.newInsert(bulkInsert.getUri())
                    .withValues(contentValues)
                    .build();
            mOperations.add(insertOperation);
        }


        return this;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void validateStatePreExecute() {
        Preconditions.stateNotNull(mAuthority, "Authority not set. Did you call withAuthority()?");
    }

    @Nullable
    @Override
    protected ContentProviderResult[] executeResolverCommand(@NonNull ContentResolver contentResolver) {
        try {
            return contentResolver.applyBatch(mAuthority, mOperations);
        } catch (RemoteException | OperationApplicationException e) {
            throw new RuntimeException("Exception while applying operation batch.", e);
        }
    }

    @Override
    protected void notifyCallback(@NonNull Callback callback, int token, @Nullable ContentProviderResult[] result) {
        callback.onBatchComplete(token, result);
    }

    public interface Callback extends BaseResolverCommand.Callback {

        void onBatchComplete(int token, @Nullable ContentProviderResult[] results);

    }

}

package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import bg.dalexiev.bender.util.Preconditions;

/**
 * A {@code ContentResolver} insert request.
 *
 * @author danail.alexiev
 * @since 1.0
 */
public class InsertCommand extends BaseResolverCommand<Uri, InsertCommand.Callback, InsertCommand> {

    private final ContentValuesBuilder mContentValuesBuilder;

    InsertCommand(ContentResolver contentResolver) {
        super(contentResolver);
        mContentValuesBuilder = new ContentValuesBuilder();
    }

    @VisibleForTesting
    InsertCommand(WorkerHandler workerHandler, ContentResolver callbackHandler, ContentValuesBuilder contentValuesBuilder) {
        super(workerHandler, callbackHandler);
        mContentValuesBuilder = contentValuesBuilder;
    }

    @Override
    protected void validateStatePreExecute() {
        super.validateStatePreExecute();

        Preconditions
                .stateNotNull(mContentValuesBuilder,
                        "Entities and row mapper not set. Did you call forEntity() or forEntities()?");
    }

    /**
     * Set {@code null} as the value for {@code column}
     *
     * @param column required. The column name.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand setNull(@NonNull String column) {
        mContentValuesBuilder.setNull(column);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, byte value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, short value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, int value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, long value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, float value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, double value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, boolean value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value required. The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} or {@code value} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, @NonNull String value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value required. The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} or {@code value} is null.
     * @since 1.0
     */
    @NonNull
    public InsertCommand set(@NonNull String column, byte[] value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    @Override
    @Nullable
    protected Uri executeResolverCommand(@NonNull ContentResolver contentResolver) {
        final Uri insertUri = getUri();
        return contentResolver.insert(insertUri, mContentValuesBuilder.getSingleValue());
    }

    @Override
    protected void notifyCallback(@NonNull Callback callback, int token, Uri result) {
        callback.onInsertComplete(token, result);
    }

    /**
     * Defines the behaviour of the insert command callback.
     *
     * @author danail.alexiev
     * @since 1.0
     */
    public interface Callback extends BaseResolverCommand.Callback {

        /**
         * Called when a bulk insert has been completed.
         *
         * @param token the identifier of the completed command.
         * @param uri the URI of the inserted item
         * @since 1.0
         */
        void onInsertComplete(int token, Uri uri);

    }


}

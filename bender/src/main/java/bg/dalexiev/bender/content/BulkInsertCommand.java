package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

/**
 * A {@code ContentResolver} bulk insert request.
 *
 * @author danail.alexiev
 * @since 1.0
 */
public class BulkInsertCommand extends BaseResolverCommand<Integer, BulkInsertCommand.Callback, BulkInsertCommand> {

    private final ContentValuesBuilder mContentValuesBuilder;

    BulkInsertCommand(ContentResolver contentResolver) {
        super(contentResolver);
        mContentValuesBuilder = new ContentValuesBuilder();
    }

    @VisibleForTesting
    BulkInsertCommand(WorkerHandler workerHandler, ContentResolver contentResolver, ContentValuesBuilder contentValuesBuilder) {
        super(workerHandler, contentResolver);
        mContentValuesBuilder = contentValuesBuilder;
    }

    /**
     * Set {@code null} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand setNull(@NonNull String column) {
        mContentValuesBuilder.setNull(column);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, byte value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, short value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, int value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, long value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, float value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, double value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @param value The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, boolean value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }


    /**
     * Set {@code value} as the value for {@code column} in the current row
     *
     * @param column required. The column name.
     * @param value required. The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} or {@code value} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, @NonNull String value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column} in the current row
     * @param column required. The column name.
     * @param value required. The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} or {@code value} is null.
     * @since 1.0
     */
    @NonNull
    public BulkInsertCommand set(@NonNull String column, byte[] value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Create a new empty row for the bulk insert. The row can be populated using the various {@code set()} methods.
     *
     * @return the current instance.
     */
    @NonNull
    public BulkInsertCommand newRow() {
        mContentValuesBuilder.newValue();

        return this;
    }

    @Override
    protected Integer executeResolverCommand(@NonNull ContentResolver contentResolver) {
        return contentResolver.bulkInsert(getUri(), mContentValuesBuilder.getValuesAsArray());
    }

    @Override
    protected void notifyCallback(@NonNull Callback callback, int token, @Nullable Integer result) {
        //noinspection ConstantConditions
        callback.onBulkInsertComplete(token, result);
    }

    /**
     * Defines the behaviour of the bulk insert command callback.
     *
     * @author danail.alexiev
     * @since 1.0
     */
    public interface Callback extends BaseResolverCommand.Callback {

        /**
         * Called when a bulk insert has been completed.
         *
         * @param token the identifier of the completed command.
         * @param insertedRows the number of inserted rows.
         * @since 1.0
         */
        void onBulkInsertComplete(int token, int insertedRows);

    }
}

package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import bg.dalexiev.bender.db.Predicate;
import bg.dalexiev.bender.db.SqlSelectionBuilder;
import bg.dalexiev.bender.util.Preconditions;

/**
 * A {@code ContentResolver} update request.
 *
 * @author danail.alexiev
 * @since 1.0
 */
public class UpdateCommand
        extends BaseResolverCommand<Integer, UpdateCommand.Callback, UpdateCommand> {

    private final ContentValuesBuilder mContentValuesBuilder;
    private final SqlSelectionBuilder mSelectionBuilder;
    private final OnConflictBuilder mOnConflictBuilder;
    private final ContentValues mReference;

    UpdateCommand(ContentResolver contentResolver) {
        super(contentResolver);
        mContentValuesBuilder = new ContentValuesBuilder();
        mSelectionBuilder = new SqlSelectionBuilder();
        mOnConflictBuilder = new OnConflictBuilder();
        mReference = new ContentValues();
    }

    @VisibleForTesting
    UpdateCommand(WorkerHandler workerHandler, ContentResolver contentResolver,
                  ContentValuesBuilder contentValuesBuilder, SqlSelectionBuilder selectionBuilder, OnConflictBuilder onConflictBuilder, ContentValues reference) {
        super(workerHandler, contentResolver);
        mContentValuesBuilder = contentValuesBuilder;
        mSelectionBuilder = selectionBuilder;
        mOnConflictBuilder = onConflictBuilder;
        mReference = reference;
    }

    /**
     * Sets the conflict resolution algorithm.
     *
     * @param onConflict the algorithm to use when a unique constaint is violated. Supported values are:
     *                   {@link SQLiteDatabase#CONFLICT_NONE}, {@link SQLiteDatabase#CONFLICT_ROLLBACK}, {@link
     *                   SQLiteDatabase#CONFLICT_ABORT}, {@link SQLiteDatabase#CONFLICT_REPLACE}, {@link SQLiteDatabase#CONFLICT_IGNORE},
     *                   {@link SQLiteDatabase#CONFLICT_FAIL}.
     * @return the current instance.
     * @since 1.1.5
     */
    public UpdateCommand onConflict(int onConflict) {
        mOnConflictBuilder.setOnConflict(onConflict);

        return this;
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
    public UpdateCommand setNull(@NonNull String column) {
        mContentValuesBuilder.setNull(column);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, byte value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, short value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, int value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, long value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, float value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, double value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, boolean value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  required. The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} or {@code value} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, @NonNull String value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Set {@code value} as the value for {@code column}
     *
     * @param column required. The column name.
     * @param value  required. The column value.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code column} or {@code value} is null.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand set(@NonNull String column, byte[] value) {
        mContentValuesBuilder.set(column, value);

        return this;
    }

    /**
     * Only applied when adding the command to a batch.
     * <p>
     * Replace the value on the specified {@code column} with the result of the bath operation at
     * index {@code previousResult}
     * </p>
     *
     * @param column required. The column where the previous operation result is stored.
     * @param previousResult the index of the previous operation in the batch.
     * @return the current instance
     * @since 1.1.9
     */
    @NonNull
    public UpdateCommand withValueBackReference(@NonNull String column, int previousResult) {
        Preconditions.argumentNotNull(column, "Column can't be null");

        mReference.put(column, previousResult);

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will be added using the {@code AND} logical operator.
     *
     * @param selection     required. The expression to be added to the selection.
     * @param selectionArgs optional. The values to be bound to the selection.
     * @return the current instance
     * @throws IllegalArgumentException if {@code selection} is {@code null}.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand where(@NonNull String selection, String[] selectionArgs) {
        Preconditions.argumentNotNull(selection, "Selection can't be null");

        mSelectionBuilder.where(selection, selectionArgs);

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will be added using the {@code AND} logical operator.
     *
     * @param predicate required. The predicate to be added to the selection.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code predicate} is {@code null}
     * @since 1.0
     */
    @NonNull
    public UpdateCommand where(@NonNull Predicate predicate) {
        mSelectionBuilder.where(predicate);

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will be added using the {@code OR} logical operator.
     *
     * @param selection     required. The expression to be added to the selection.
     * @param selectionArgs optional. The values to be bound to the selection.
     * @return the current instance.
     * * @throws IllegalArgumentException if {@code selection} is {@code null}.
     * @since 1.0
     */
    @NonNull
    public UpdateCommand orWhere(@NonNull String selection, @Nullable String[] selectionArgs) {
        Preconditions.argumentNotNull(selection, "Selection can't be null");

        mSelectionBuilder.orWhere(selection, selectionArgs);

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will be added using the {@code OR} logical operator.
     *
     * @param predicate required. The predicate to be added to the selection.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code predicate} is {@code null}
     * @since 1.0
     */
    @NonNull
    public UpdateCommand orWhere(@NonNull Predicate predicate) {
        mSelectionBuilder.orWhere(predicate);

        return this;
    }

    @Override
    protected void validateStatePreExecute() {
        super.validateStatePreExecute();

        Preconditions.stateNotNull(mContentValuesBuilder,
                "No values to update. Did you call forEntity() or any of the set...() methods?");
    }

    @Override
    protected Integer executeResolverCommand(@NonNull ContentResolver contentResolver) {
        @SuppressWarnings("ConstantConditions") final Uri uri = mOnConflictBuilder.appendOnConflictParameter(getUri());

        return contentResolver
                .update(uri, getContentValues(), getSelection(), getSelectionArgs());

    }

    String[] getSelectionArgs() {
        return mSelectionBuilder.getSelectionArgs();
    }

    String getSelection() {
        return mSelectionBuilder.getSelection();
    }

    ContentValues getContentValues() {
        return mContentValuesBuilder.getSingleValue();
    }

    ContentValues getReference() {
        return mReference;
    }

    @Override
    protected void notifyCallback(@NonNull Callback callback, int token, Integer result) {
        callback.onUpdateComplete(token, result);
    }

    /**
     * Defines the behaviour of the update command callback.
     *
     * @author danail.alexiev
     * @since 1.0
     */
    public interface Callback extends BaseResolverCommand.Callback {

        /**
         * Called when an update has been completed.
         *
         * @param token       the identifier of the completed command.
         * @param updatedRows the number of updated rows.
         * @since 1.0
         */
        void onUpdateComplete(int token, int updatedRows);

    }

}

package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.content.ContentValues;
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

    UpdateCommand(ContentResolver contentResolver) {
        super(contentResolver);
        mContentValuesBuilder = new ContentValuesBuilder();
        mSelectionBuilder = new SqlSelectionBuilder();
    }

    @VisibleForTesting
    UpdateCommand(WorkerHandler workerHandler, ContentResolver contentResolver,
                  ContentValuesBuilder contentValuesBuilder, SqlSelectionBuilder selectionBuilder) {
        super(workerHandler, contentResolver);
        mContentValuesBuilder = contentValuesBuilder;
        mSelectionBuilder = selectionBuilder;
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
        Uri uri = getUri();

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

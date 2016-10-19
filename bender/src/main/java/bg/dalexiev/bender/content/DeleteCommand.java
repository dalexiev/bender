package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import bg.dalexiev.bender.db.Predicate;
import bg.dalexiev.bender.db.SqlSelectionBuilder;
import bg.dalexiev.bender.util.Preconditions;

/**
 * A {@code ContentResolver} delete request.
 *
 * @author danail.alexiev
 * @since 1.0
 */
public class DeleteCommand extends BaseResolverCommand<Integer, DeleteCommand.Callback, DeleteCommand> {

    private final SqlSelectionBuilder mSelectionBuilder;

    DeleteCommand(ContentResolver contentResolver) {
        super(contentResolver);
        mSelectionBuilder = new SqlSelectionBuilder();
    }

    @VisibleForTesting
    DeleteCommand(WorkerHandler workerHandler, ContentResolver contentResolver, SqlSelectionBuilder selectionBuilder) {
        super(workerHandler, contentResolver);
        mSelectionBuilder = selectionBuilder;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will be added using the {@code AND} logical operator.
     *
     * @param selection required. The expression to be added to the selection.
     * @param selectionArgs optional. The values to be bound to the selection.
     * @return the current instance
     * @throws IllegalArgumentException if {@code selection} is {@code null}.
     * @since 1.0
     */
    @NonNull
    public DeleteCommand where(@NonNull String selection, @Nullable String... selectionArgs) {
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
    public DeleteCommand where(@NonNull Predicate predicate) {
        mSelectionBuilder.where(predicate);

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will be added using the {@code OR} logical operator.
     *
     * @param selection required. The expression to be added to the selection.
     * @param selectionArgs optional. The values to be bound to the selection.
     * @return the current instance.
     * * @throws IllegalArgumentException if {@code selection} is {@code null}.
     * @since 1.0
     */
    public DeleteCommand orWhere(@NonNull String selection, @Nullable String... selectionArgs) {
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
    public DeleteCommand orWhere(@NonNull Predicate predicate) {
        mSelectionBuilder.orWhere(predicate);

        return this;
    }

    @Override
    protected Integer executeResolverCommand(@NonNull ContentResolver contentResolver) {
        final Uri uri = getUri();
        return contentResolver.delete(uri, getSelection(), getSelectionArgs());
    }

    String[] getSelectionArgs() {
        return mSelectionBuilder.getSelectionArgs();
    }

    String getSelection() {
        return mSelectionBuilder.getSelection();
    }

    @Override
    protected void notifyCallback(@NonNull Callback callback, int token, Integer result) {
        callback.onDeleteComplete(token, result);
    }

    /**
     * Defines the behaviour of the delete command callback.
     *
     * @author danail.alexiev
     * @since 1.0
     */
    public interface Callback extends BaseResolverCommand.Callback {

        /**
         * Called when a delete has been completed.
         *
         * @param token the identifier of the completed command.
         * @param deletedRows the number of deleted rows.
         * @since 1.0
         */
        void onDeleteComplete(int token, int deletedRows);

    }

}

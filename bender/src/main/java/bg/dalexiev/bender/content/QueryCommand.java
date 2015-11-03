package bg.dalexiev.bender.content;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import bg.dalexiev.bender.db.OrderBy;
import bg.dalexiev.bender.db.Predicate;
import bg.dalexiev.bender.db.SqlSelectionBuilder;
import bg.dalexiev.bender.mapper.CursorMapper;
import bg.dalexiev.bender.mapper.RowMapper;
import bg.dalexiev.bender.util.Preconditions;

/**
 * A {@code ContentResolver} query request.
 *
 * @author danail.alexiev
 * @since 1.0
 */
public class QueryCommand<R>
        extends BaseResolverCommand<EntityCursor<R>, QueryCommand.Callback<R>, QueryCommand<R>> {

    private String[] mProjection;
    private final SqlSelectionBuilder mSelectionBuilder;

    private CursorMapper<R> mCursorMapper;

    QueryCommand(ContentResolver contentResolver) {
        super(contentResolver);
        mSelectionBuilder = new SqlSelectionBuilder();
    }

    @VisibleForTesting
    QueryCommand(WorkerHandler workerHandler, ContentResolver contentResolver, SqlSelectionBuilder selectionBuilder) {
        super(workerHandler, contentResolver);
        mSelectionBuilder = selectionBuilder;
    }

    /**
     * Specify the columns that must be a part of the result
     *
     * <p>
     * Must be called before executing the query.
     * </p>
     *
     * @param columns required. The names of the result columns.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code columns} is {@code null} or {@code empty}.
     * @since 1.0
     */
    @NonNull
    public QueryCommand<R> select(@NonNull String... columns) {
        Preconditions.argumentNotNull(columns, "Projection can't be null");
        Preconditions.argumentCondition(columns.length == 0, "Projection can't be empty");

        mProjection = columns;

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will
     * be
     * added using the {@code AND} logical operator.
     *
     * @param selection     required. The expression to be added to the selection.
     * @param selectionArgs optional. The values to be bound to the selection.
     * @return the current instance
     * @throws IllegalArgumentException if {@code selection} is {@code null}.
     * @since 1.0
     */
    @NonNull
    public QueryCommand<R> where(@NonNull String selection, @Nullable String[] selectionArgs) {
        Preconditions.argumentNotNull(selection, "Selection can't be null");

        mSelectionBuilder.where(selection, selectionArgs);

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will
     * be
     * added using the {@code AND} logical operator.
     *
     * @param predicate required. The predicate to be added to the selection.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code predicate} is {@code null}
     * @since 1.0
     */
    @NonNull
    public QueryCommand<R> where(@NonNull Predicate predicate) {
        mSelectionBuilder.where(predicate);

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will
     * be
     * added using the {@code OR} logical operator.
     *
     * @param selection     required. The expression to be added to the selection.
     * @param selectionArgs optional. The values to be bound to the selection.
     * @return the current instance.
     * * @throws IllegalArgumentException if {@code selection} is {@code null}.
     * @since 1.0
     */
    @NonNull
    public QueryCommand orWhere(@NonNull String selection, @Nullable String[] selectionArgs) {
        Preconditions.argumentNotNull(selection, "Selection can't be null");

        mSelectionBuilder.orWhere(selection, selectionArgs);

        return this;
    }

    /**
     * Appends an expression to the selection of this command. If a selection already exists, the new expression will
     * be
     * added using the {@code OR} logical operator.
     *
     * @param predicate required. The predicate to be added to the selection.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code predicate} is {@code null}
     * @since 1.0
     */
    @NonNull
    public QueryCommand<R> orWhere(@NonNull Predicate predicate) {
        mSelectionBuilder.orWhere(predicate);

        return this;
    }

    /**
     * Specify the ordering of the result of this command.
     *
     * @param orderBy required. The order by expression used to sort the result.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code orderBy} is {@code null}
     * @since 1.0
     */
    @NonNull
    public QueryCommand<R> orderBy(@NonNull String orderBy) {
        Preconditions.argumentNotNull(orderBy, "Order by is required");

        mSelectionBuilder.orderBy(orderBy);

        return this;
    }

    /**
     * Specify the ordering of the result of this command.
     *
     * @param orderBy required. The {@code OrderBy} expressions used to sort the result.
     * @return the current instance
     * @throws IllegalArgumentException if {@code orderBy} is {@code null} or empty
     * @since 1.0
     */
    public QueryCommand<R> orderBy(@NonNull OrderBy... orderBy) {
        mSelectionBuilder.orderBy(orderBy);

        return this;
    }

    /**
     * Specify the row mapper used to handle the query result.
     *
     * <p>
     * Use this method if you need a simple one to one mapping between a resulting row and an entity object
     * </p>
     *
     * <p>
     * Either this method or {@link #useCursorMapper(CursorMapper)} must be called before executing a query.
     * </p>
     *
     * @param rowMapper required. The row mapper used to transform rows into objects
     * @return the current instance
     * @throws IllegalArgumentException if {@code rowMapper} is {@code null}
     * @since 1.0
     */
    @NonNull
    public QueryCommand<R> useRowMapper(@NonNull RowMapper<R> rowMapper) {
        Preconditions.stateCondition(mCursorMapper != null, "A mapper has already been set");
        Preconditions.argumentNotNull(rowMapper, "Row mapper is required");

        mCursorMapper = new DefaultCursorMapper<>(rowMapper);

        return this;
    }

    /**
     * Specify the cursor mapper used to handle the query result.
     *
     * <p>
     * Use this method if you need a more complex mapping of your result. For example mapping multiple rows into a
     * single object.
     * </p>
     *
     * <p>
     * Either this method or {@link #useRowMapper(RowMapper)} must be called before executing a query.
     * </p>
     *
     * @param cursorMapper required. The cursor mapper used to process the result
     * @return the current instance
     * @throws IllegalArgumentException if {@code rowMapper} is {@code null}
     * @since 1.0
     */
    public QueryCommand<R> useCursorMapper(@NonNull CursorMapper<R> cursorMapper) {
        Preconditions.stateCondition(mCursorMapper != null, "A mapper has already been set");
        Preconditions.argumentNotNull(cursorMapper, "Cursor mapper is required");

        mCursorMapper = cursorMapper;

        return this;
    }

    @Override
    protected void validateStatePreExecute() {
        super.validateStatePreExecute();

        Preconditions.stateNotNull(mProjection, "No projection has been set. Did you call setProjection()?");
        Preconditions.stateNotNull(mCursorMapper,
                "No mapper has been set. Did you call setRowMapper() or setCursorMapper()?");
    }

    @Override
    @Nullable
    protected EntityCursor<R> executeResolverCommand(@NonNull ContentResolver contentResolver) {
        @SuppressLint("Recycle") final Cursor cursor = contentResolver
                .query(getUri(), mProjection, mSelectionBuilder.getSelection(), mSelectionBuilder.getSelectionArgs(),
                        mSelectionBuilder.getOrderBy());
        if (cursor == null) {
            return null;
        }

        final EntityCursor<R> entityCursor = new EntityCursor<>(cursor, mCursorMapper);
        entityCursor.mapCursor();
        return entityCursor;
    }

    @Override
    protected void notifyCallback(@NonNull Callback<R> callback, int token, EntityCursor<R> result) {
        callback.onQueryComplete(token, result);
    }

    /**
     * Defines the behaviour of the query command callback.
     *
     * @author danail.alexiev
     * @since 1.0
     */
    public interface Callback<E> extends BaseResolverCommand.Callback {

        /**
         * Called when a query has been completed.
         *
         * @param token  the identifier of the completed command.
         * @param cursor the cursor, representing the result set. May be {@code null}.
         * @since 1.0
         */
        void onQueryComplete(int token, EntityCursor<E> cursor);

    }

}

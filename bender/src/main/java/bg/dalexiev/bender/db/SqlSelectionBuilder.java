package bg.dalexiev.bender.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bg.dalexiev.bender.util.Preconditions;

/**
 * Builds SQL selections and applies them to delete, update and query operations.
 *
 * @author danail.alexiev
 * @since 1.0
 */
public class SqlSelectionBuilder {

    private static final String AND = " and ";
    private static final String OR = " or ";

    private static final String SEPARATOR_COMMA = ", ";

    private String mTable;
    private boolean mIsDistinct;
    private final StringBuilder mSelection;
    private final List<String> mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private final StringBuilder mOrderBy;
    private String mLimit;

    public SqlSelectionBuilder() {
        mSelection = new StringBuilder();
        mSelectionArgs = new ArrayList<>(10);
        mOrderBy = new StringBuilder();
    }

    /**
     * Sets the table to perform operations on.
     * <p>This method must be called before trying to perform any operations using {@link #delete(SQLiteDatabase)}, {@link #update(SQLiteDatabase, ContentValues)} or {@link #query(SQLiteDatabase, String[])}</p>
     *
     * @param table required. The named of the database table ot operate on.
     * @throws IllegalArgumentException if {@code table} is {@code null}
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder setTable(@NonNull String table) {
        Preconditions.argumentNotNull(table, "Table name can't be null");

        mTable = table;

        return this;
    }

    /**
     * Specifies that this selection will target only distinct results. Has effect only when used together with {@link #query(SQLiteDatabase, String[])}.
     *
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder setDistinct() {
        mIsDistinct = true;

        return this;
    }

    /**
     * Appends an expression to the existing where clause of this selection. If a where clause already exists, the new expression will be added using the {@code AND} logical operator.
     *
     * <p>
     * The resulting expression will be the following: {@code [existing where AND] (selection) }
     * </p>
     *
     * @param selection optional. The expression to be added to the where clause.
     * @param args optional. The values to be bound to the expression parameters on execution.
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder where(@Nullable String selection, @Nullable String... args) {
        appendWhere(mSelection, mSelectionArgs, selection, args, AND);

        return this;
    }

    /**
     * Appends an expression to the existing where clause of this selection. If a where clause already exists, the new expression will be added using the {@code AND} logical operator.
     *
     * <p>
     * The resulting expression will be the following: {@code [existing where AND] (predicate expression) }
     * </p>
     *
     * @param predicate required. A predicate expression to add to the where clause.
     * @return the current instance
     * @throws IllegalArgumentException if {@code predicate} is {@code null}
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder where(@NonNull Predicate predicate) {
        Preconditions.argumentNotNull(predicate, "Predicate can't be null");

        appendWhere(mSelection, mSelectionArgs, predicate.getExpression(), predicate.getArguments(), AND);

        return this;
    }

    /**
     * Appends an expression to the existing where clause of this selection. If a where clause already exists, the new expression will be added using the {@code OR} logical operator.
     *
     * <p>
     * The resulting expression will be the following: {@code [existing where OR] (selection) }
     * </p>
     *
     * @param selection optional. The expression to be added to the where clause.
     * @param args optional. The values to be bound to the expression parameters on execution.
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder orWhere(@Nullable String selection, @Nullable String... args) {
        appendWhere(mSelection, mSelectionArgs, selection, args, OR);

        return this;
    }

    /**
     * Appends an expression to the existing where clause of this selection. If a where clause already exists, the new expression will be added using the {@code OR} logical operator.
     *
     * <p>
     * The resulting expression will be the following: {@code [existing where OR] (predicate expression) }
     * </p>
     *
     * @param predicate required. A predicate expression to add to the where clause.
     * @return the current instance
     * @throws IllegalArgumentException if {@code predicate} is {@code null}
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder orWhere(@NonNull Predicate predicate) {
        Preconditions.argumentNotNull(predicate, "Predicate can't be null");

        appendWhere(mSelection, mSelectionArgs, predicate.getExpression(), predicate.getArguments(), OR);

        return this;
    }

    private static void appendWhere(@NonNull StringBuilder selectionBuilder, List<String> selectionArgs,
            @Nullable String selection,
            @Nullable String[] args, @NonNull String logicalOperator) {
        if (selection == null) {
            return;
        }

        appendWhereClause(selectionBuilder, selection, logicalOperator);
        appendArgs(selectionArgs, args);
    }

    private static void appendWhereClause(@NonNull StringBuilder selectionBuilder, @NonNull String selection,
            @NonNull String logicalOperator) {
        if (selectionBuilder.length() > 0) {
            // if there is something, append the AND operator
            selectionBuilder.append(logicalOperator);
        }

        selectionBuilder.append('(').append(selection).append(')');
    }

    private static void appendArgs(@NonNull List<String> selectionArgs, @Nullable String[] args) {
        if (args != null) {
            Collections.addAll(selectionArgs, args);
        }
    }

    /**
     * Adds a {@code groupBy} clause to this selection. Has effect only when used together with {@link #query(SQLiteDatabase, String[])}.
     * @param columns optional. A list of columns to be used in the {@code groupBy} clause
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder groupBy(@Nullable String... columns) {
        if (columns == null) {
            return this;
        }

        int length = columns.length;
        if (length == 0) {
            return this;
        }

        final StringBuilder groupByBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            groupByBuilder.append(columns[i]);
            if (i != length - 1) {
                groupByBuilder.append(SEPARATOR_COMMA);
            }
        }
        mGroupBy = groupByBuilder.toString();

        return this;
    }

    /**
     * Adds a {@code having} clause to this selection. Has effect only when used together with {@link #query(SQLiteDatabase, String[])}.
     * @param having optional. A list of columns to be used in the {@code groupBy} clause
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder having(@Nullable String having) {
        mHaving = having;

        return this;
    }

    /**
     * Adds a {@code orderBy} clause to this selection. Has effect only when used together with {@link #query(SQLiteDatabase, String[])}.
     * @param orderBy optional. The expression to add ot the {@code groupBy} clause
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder orderBy(@Nullable String orderBy) {
        appendOrderBy(orderBy);

        return this;
    }

    /**
     * Adds a {@code orderBy} clause to this selection. Has effect only when used together with {@link #query(SQLiteDatabase, String[])}.
     * @param orderBy required. A list of orderBy expression to add to the {@code groupBy} clause
     * @return the current instance
     * @throws IllegalArgumentException if {@code orderBy} is {@code null} or empty
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder orderBy(@NonNull OrderBy... orderBy) {
        Preconditions.argumentNotNull(orderBy, "OrderBy can't be null");
        Preconditions.argumentCondition(orderBy.length == 0, "OrderBy can't be empty");

        for (OrderBy order : orderBy) {
            appendOrderBy(order.getExpression());
        }

        return this;
    }

    private void appendOrderBy(@Nullable String orderBy) {
        if (orderBy == null) {
            return;
        }

        if (mOrderBy.length() > 0) {
            mOrderBy.append(SEPARATOR_COMMA);
        }

        mOrderBy.append(orderBy);
    }

    /**
     * Adds a {@code limit} clause to this selection. Has effect only when used together with {@link #query(SQLiteDatabase, String[])}.
     * @param limit optional. The expression to use as a {@code limit} clause
     * @return the current instance
     * @throws IllegalArgumentException if {@code limit} is {@code null}
     * @since 1.0
     */
    @NonNull
    public SqlSelectionBuilder limit(@NonNull String limit) {
        Preconditions.argumentNotNull(limit, "Limit can't be null");

        mLimit = limit;

        return this;
    }

    /**
     * Performs a database update, applying the current {@code where} clause.
     *
     * @param db required. The database to perform the update against.
     * @param values required. The values to use for the update.
     * @throws IllegalArgumentException if {@code db} or {@code values} is {@code null}, or if {@code values} is empty.
     * @throws IllegalStateException @throws IllegalStateException if a table has not been set by calling {@link #setTable(String)} before trying to update.
     * @return the number of updated rows.
     * @since 1.0
     */
    public int update(@NonNull SQLiteDatabase db, @NonNull ContentValues values) {
        Preconditions.argumentNotNull(db, "Database can't be null");
        Preconditions.argumentNotNull(values, "Update values can't be null");
        Preconditions.argumentCondition(values.size() == 0, "Update values can't be empty");

        Preconditions.stateNotNull(mTable, "Can't execute an update with no table set. Did you call setTable()?");

        return db.update(mTable, values, getSelection(), getSelectionArgs());
    }

    /**
     * Performs a database delete, applying the current {@code where} clause.
     *
     * @param db required. The database to perform the delete against.
     * @throws IllegalArgumentException if {@code db} is {@code null}.
     * @throws IllegalStateException if a table has not been set by calling {@link #setTable(String)} before trying to delete.
     * @return the number of deleted rows.
     * @since 1.0
     */
    public int delete(@NonNull SQLiteDatabase db) {
        Preconditions.argumentNotNull(db, "Database can't be null");

        Preconditions.stateNotNull(mTable, "Can't execute a delete with no table set. Did you call setTable()?");

        return db.delete(mTable, getSelection(), getSelectionArgs());
    }

    /**
     * Perform a database query, applying the current state of this instance.
     *
     * @see Cursor
     *
     * @param db required. The database to query against.
     * @param projection optional. The columns to fetch in the result set. Passing {@code null} here is discouraged, since it <em>will</em> have performance implications.
     * @throws IllegalArgumentException if {@code db} is {@code null}.
     * @throws IllegalStateException if a table has not been set by calling {@link #setTable(String)} before trying to query.
     * @return a cursor holding the query result set.
     */
    @Nullable
    public Cursor query(@NonNull SQLiteDatabase db, @Nullable String[] projection) {
        Preconditions.argumentNotNull(db, "Database can't be null");

        Preconditions.stateNotNull(mTable, "Can't execute a query with no table set. Did you call setTable()?");

        return db.query(mIsDistinct, mTable, projection, getSelection(), getSelectionArgs(), mGroupBy, mHaving,
                getOrderBy(), mLimit);
    }

    @Nullable
    public String[] getSelectionArgs() {
        if (mSelectionArgs.isEmpty()) {
            return null;
        }

        return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
    }

    @Nullable
    public String getTable() {
        return mTable;
    }

    public boolean isDistinct() {
        return mIsDistinct;
    }

    @Nullable
    public String getSelection() {
        if (mSelection.length() == 0) {
            return null;
        }

        return mSelection.toString();
    }

    @Nullable
    public String getGroupBy() {
        return mGroupBy;
    }

    @Nullable
    public String getHaving() {
        return mHaving;
    }

    @Nullable
    public String getOrderBy() {
        if (mOrderBy.length() == 0) {
            return null;
        }

        return mOrderBy.toString();
    }

    @Nullable
    public String getLimit() {
        return mLimit;
    }
}

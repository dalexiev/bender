package bg.dalexiev.bender.content;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import bg.dalexiev.bender.db.Predicate;
import bg.dalexiev.bender.db.SqlInsertionBuilder;
import bg.dalexiev.bender.db.SqlSelectionBuilder;
import bg.dalexiev.bender.util.Preconditions;

/**
 * A skeleton implementation of a {@code ContentProvider} backed by a SQLite database.
 * Subclasses should provide two components for the provider to function properly:
 * <dl>
 * <dt>{@code authority}</dt>
 * <dd>A string used as a unique identifier of the provider. Usually in the format {@code <your package
 * name>.provider}</dd>
 *
 * <dt>{@code helper}</dt>
 * <dd>A {@link SQLiteOpenHelper} instance that is used to obtain database instances. This helper typically contains
 * all the database creation and migration logic.</dd>
 * </dl>
 * Optionally, a subclass may provide a custom {@link DatabaseUriMatcher} to deal with custom content URIs.
 *
 * @author danail.alexiev
 * @see ContentProvider
 * @see SQLiteOpenHelper
 * @see DatabaseUriMatcher
 * @since 1.0
 */
public abstract class DatabaseContentProvider extends ContentProvider {

    /**
     * Used to specify the conflict resolution algorithm to be used while performing the database operation.
     * Only taken into consideration when inserting or updating.
     *
     * <p>Supported values: {@link SQLiteDatabase#CONFLICT_NONE}, {@link SQLiteDatabase#CONFLICT_ROLLBACK}, {@link
     * SQLiteDatabase#CONFLICT_ABORT}, {@link SQLiteDatabase#CONFLICT_REPLACE}, {@link SQLiteDatabase#CONFLICT_IGNORE},
     * {@link SQLiteDatabase#CONFLICT_FAIL}.
     *
     * <p>Defaults to {@link SQLiteDatabase#CONFLICT_NONE}. All unknown values are replaced by the default value.
     *
     * @since 1.1.1
     */
    public static final String PARAM_CONFLICT_ALGORITHM = "onConflict";

    private String mAuthority;
    private SQLiteOpenHelper mHelper;
    private DatabaseUriMatcher mUriMatcher;

    @Override
    @CallSuper
    public boolean onCreate() {
        mAuthority = createAuthority();
        Preconditions.stateNotNull(mAuthority, "Authority can't be null. Check your createAuthority() implementation.");

        mHelper = createHelper(getContext());
        Preconditions.stateNotNull(mHelper, "Database helper can't be null, Check your createHelper() implementation");

        mUriMatcher = createUriMatcher(mAuthority);
        Preconditions
                .stateNotNull(mUriMatcher, "Uri matcher can't be null. Check your createUriMatcher() implementation");

        return true;
    }

    /**
     * Create an authority, used as an identifier of this content provider.
     *
     * @return the authority. Must not be {@code null}.
     */
    @NonNull
    protected abstract String createAuthority();

    /**
     * Create a {@code SQLiteOpenHelper} instance to be used in this content provider.
     *
     * @param context optional. The context of the current content provider.
     * @return the helper. Must not be {@code null}.
     */
    @NonNull
    protected abstract SQLiteOpenHelper createHelper(@Nullable Context context);


    /**
     * Create a {@code DatabaseUriMatcher} to be used in this content provider.
     *
     * @param authority required. The authority of the current content provider.
     * @return the URI matcher. Must not be {@code null}.
     */
    @SuppressWarnings("MethodMayBeStatic") // This is not static, because subclasses may need to override it
    @NonNull
    protected DatabaseUriMatcher createUriMatcher(@NonNull String authority) {
        return new DatabaseUriMatcher(authority);
    }

    /**
     * <p>
     * This implementation will call {@link #buildQuerySelection(Uri, DatabaseUriMatcher.Result, String, String[],
     * String)}
     * to create a {@code SqlSelectionBuilder} using the passed parameter values and later use the selection to perform
     * the query against the database.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        Preconditions.argumentNotNull(uri, "Uri can't be null");

        final DatabaseUriMatcher.Result match = matchUri(uri);

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final SqlSelectionBuilder builder = buildQuerySelection(uri, match, selection, selectionArgs,
                sortOrder);
        final Cursor cursor = builder.query(db, projection);
        final Context context = getContext();
        if ((cursor != null) && (context != null)) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    @NonNull
    private DatabaseUriMatcher.Result matchUri(@NonNull Uri uri) {
        final DatabaseUriMatcher.Result match = mUriMatcher.match(uri);
        if (match == null) {
            throw new UnsupportedOperationException("Unsupported content uri: " + uri);
        }
        return match;
    }


    /**
     * Create a {@code SqlSelectionBuilder} that will be used to execute a query against the database.
     *
     * <p>
     * Override this to customise the query building behaviour. The current implementation will add all parameter
     * values
     * to their appropriate clauses and, if a specific table row is requests, will add {@link BaseColumns#_ID}{@code =
     * ?} condition to the {@code where} clause.
     * </p>
     *
     * @param uri           required. The requested content URI.
     * @param match         required. The result of matching the requested URI with the {@code DatabaseUriMatcher} of
     *                      the provider.
     * @param selection     optional. The selection passed to the {@link #query(Uri, String[], String, String[],
     *                      String)} method.
     * @param selectionArgs optional. The selection arguments passed to the {@link #query(Uri, String[], String,
     *                      String[], String)} method.
     * @param sortOrder     optional. The sort order passed to the {@link #query(Uri, String[], String, String[],
     *                      String)} method/
     * @return a {@code non - null} selection builder, used to execute the query.
     * @since 1.0
     */
    @SuppressWarnings("MethodMayBeStatic") // This is not static, because subclasses may need to override it
    @NonNull
    protected SqlSelectionBuilder buildQuerySelection(@NonNull Uri uri, @NonNull DatabaseUriMatcher.Result match,
            @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SqlSelectionBuilder builder = new SqlSelectionBuilder();
        switch (match.code) {
            case DatabaseUriMatcher.TYPE_ROW:
                builder.where(Predicate.eq(BaseColumns._ID, match.id));
                // fallthrough
            case DatabaseUriMatcher.TYPE_TABLE:
                return builder.setTable(match.table).where(selection, selectionArgs).orderBy(sortOrder);
        }

        return builder;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Preconditions.argumentNotNull(uri, "Uri can't be null");

        final StringBuilder typeBuilder = new StringBuilder();
        final DatabaseUriMatcher.Result match = matchUri(uri);

        switch (match.code) {
            case DatabaseUriMatcher.TYPE_TABLE:
                typeBuilder.append(ContentResolver.CURSOR_DIR_BASE_TYPE);
                break;
            case DatabaseUriMatcher.TYPE_ROW:
                typeBuilder.append(ContentResolver.CURSOR_ITEM_BASE_TYPE);
                break;
        }

        typeBuilder.append("/vnd.").append(mAuthority).append('.').append(uri.getPathSegments().get(0));
        return typeBuilder.toString();
    }

    /**
     * <p>
     * This implementation calls {@link #buildInsertion(Uri, DatabaseUriMatcher.Result, ContentValues...)}
     * to create a {@code SqlInsertionBuilder} that will be used to perform the actual insert against the database.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Preconditions.argumentNotNull(uri, "Uri can't be null");

        final DatabaseUriMatcher.Result match = matchInsertUri(uri);

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final SqlInsertionBuilder builder = buildInsertion(uri, match, values);
        final int conflictAlgorithm = getConflictAlgorithm(uri);
        final List<Long> generatedIds = builder.insert(db, conflictAlgorithm);
        notifyChange(uri);
        return ContentUris.withAppendedId(uri, generatedIds.get(0));
    }

    /**
     * <p>
     * This implementation calls {@link #buildInsertion(Uri, DatabaseUriMatcher.Result, ContentValues...)}
     * to create a {@code SqlInsertionBuilder} that will be used to perform the actual insert against the database.
     * It will wrap all insertions in a single transaction for optimal performance.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @Nullable ContentValues[] values) {
        Preconditions.argumentNotNull(uri, "Uri can't be null");

        final DatabaseUriMatcher.Result match = matchInsertUri(uri);

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            final SqlInsertionBuilder builder = buildInsertion(uri, match, values);
            final int conflictAlgorithm = getConflictAlgorithm(uri);
            final List<Long> generatedIds = builder.insert(db, conflictAlgorithm);
            notifyChange(uri);
            db.setTransactionSuccessful();
            return generatedIds.size();
        } finally {
            db.endTransaction();
        }
    }

    @NonNull
    private DatabaseUriMatcher.Result matchInsertUri(@NonNull Uri uri) {
        final DatabaseUriMatcher.Result match = matchUri(uri);

        if (match.isRow) {
            throw new UnsupportedOperationException("Can't insert into a row uri: " + uri);
        }
        return match;
    }

    /**
     * Create a {@code SqlInsertionBuilder} that is used to perform the insert.
     *
     * <p>
     * Override this to customise the insertion building logic.
     * </p>
     *
     * @param uri    required. The request content URI.
     * @param match  required. The result of matching the requested URI with the {@code DatabaseUriMatcher} of the
     *               provider.
     * @param values optional. The values that must be inserted into the database.
     * @return a {@code non - null} insertion builder that is going to be used to perform the actual insert
     * @since 1.0
     */
    @SuppressWarnings("MethodMayBeStatic") // This is not static, because subclasses may need to override it
    @NonNull
    protected SqlInsertionBuilder buildInsertion(@NonNull Uri uri, @NonNull DatabaseUriMatcher.Result match,
            @Nullable ContentValues... values) {
        final SqlInsertionBuilder builder = new SqlInsertionBuilder().setTable(match.table);
        if (values != null) {
            builder.appendValues(values);
        }
        return builder;
    }

    /**
     * <p>
     * This implementation calls {@link #buildUpdateDeleteSelection(Uri, DatabaseUriMatcher.Result, String, String[])}
     * to create a {@code SqlSelectionBuilder} that will be used to perform the actual delete against the database.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Preconditions.argumentNotNull(uri, "Uri can't be null");

        final DatabaseUriMatcher.Result match = matchUri(uri);

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final SqlSelectionBuilder builder = buildUpdateDeleteSelection(uri, match, selection, selectionArgs);
        final int deletedRows = builder.delete(db);
        notifyChange(uri);
        return deletedRows;
    }

    /**
     * <p>
     * This implementation calls {@link #buildUpdateDeleteSelection(Uri, DatabaseUriMatcher.Result, String, String[])}
     * to create a {@code SqlSelectionBuilder} that will be used to perform the actual update against the database.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, String selection, String[] selectionArgs) {
        Preconditions.argumentNotNull(uri, "Uri can't be null");
        Preconditions.argumentNotNull(values, "Values can't be null");
        Preconditions.argumentCondition(values.size() == 0, "Values can't be empty");

        final DatabaseUriMatcher.Result match = matchUri(uri);

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final SqlSelectionBuilder builder = buildUpdateDeleteSelection(uri, match, selection, selectionArgs);
        final int conflictAlgorithm = getConflictAlgorithm(uri);
        final int updatedRows = builder.update(db, values, conflictAlgorithm);
        notifyChange(uri);
        return updatedRows;
    }

    /**
     * Create a {@code SqlSelectionBuilder} that will be used to execute a delete or an update against the database.
     *
     * <p>
     * Override this to customise the selection logic. The current implementation will apply the selection and
     * selection
     * arguments and, if a specific database row has been requested, will add a {@link BaseColumns#_ID}{@code = ?}
     * expression to the where clause.
     * </p>
     *
     * @param uri           required. The requested content URI.
     * @param match         required. The result of matching the requested URI with the {@code DatabaseUriMatcher} of
     *                      the provider.
     * @param selection     optional. The selection passed to the content provider method.
     * @param selectionArgs optional. The selection passed to the content provider method.
     * @return a {@code non - null} selection builder, used to execute the operation.
     * @since 1.0
     */
    @SuppressWarnings("MethodMayBeStatic") // This is not static, because subclasses may need to override it
    @NonNull
    public SqlSelectionBuilder buildUpdateDeleteSelection(@NonNull Uri uri, @NonNull DatabaseUriMatcher.Result match,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {
        final SqlSelectionBuilder builder = new SqlSelectionBuilder();
        switch (match.code) {
            case DatabaseUriMatcher.TYPE_ROW:
                builder.where(BaseColumns._ID + " = ?", match.id);
                // fallthrough
            case DatabaseUriMatcher.TYPE_TABLE:
                builder.setTable(match.table).where(selection, selectionArgs);
        }
        return builder;
    }

    /**
     * <p>
     * The current implementation wraps the execution of all batch operations in a transaction.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        Preconditions.argumentNotNull(operations, "Operations required");

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int operationCount = operations.size();
            final ContentProviderResult[] operationResults
                    = new ContentProviderResult[operationCount];
            for (int i = 0; i < operations.size(); i++) {
                operationResults[i] = operations.get(i).apply(this, operationResults, i);
            }
            db.setTransactionSuccessful();
            return operationResults;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Notifies the {@code ContentResolver} that a change has been made on a given URI.
     *
     * @param uri required. The URI that has been changed.
     */
    protected void notifyChange(@NonNull Uri uri) {
        final Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    private static int getConflictAlgorithm(Uri uri) {
        final String onConflict = uri.getQueryParameter(PARAM_CONFLICT_ALGORITHM);
        if (onConflict == null) {
            return SQLiteDatabase.CONFLICT_NONE;
        }

        return Integer.parseInt(onConflict);
    }

}

package bg.dalexiev.bender.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import bg.dalexiev.bender.util.Preconditions;

/**
 * Builds and executes SQLite insert statements.
 *
 * @author danail.alexiev
 * @since 1.0
 */
public final class SqlInsertionBuilder {

    private static final String SQL_TEMPLATE = "insert into %s(%s) values (%s)";

    private String mTable;
    private List<ContentValues> mContentValues;

    /**
     * Sets the table to insert into.
     * <p>It is mandatory to call this method before you try and call {@link #insert(SQLiteDatabase)}</p>
     *
     * @param table required. The name of the database table to insert into.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code table} is {@code null}
     * @since 1.0
     */
    @NonNull
    public SqlInsertionBuilder setTable(@NonNull String table) {
        Preconditions.argumentNotNull(table, "Table can't be null");

        mTable = table;

        return this;
    }

    /**
     * Appends an arbitrary number of values to be inserted into the database.
     *
     * @param values required. The values to be inserted to the database.
     * @return the current instance.
     * @throws IllegalArgumentException if {@code values} is {@code null} or empty.
     * @since 1.0
     */
    @NonNull
    public SqlInsertionBuilder appendValues(@NonNull ContentValues... values) {
        Preconditions.argumentNotNull(values, "Values can't be null");
        Preconditions.argumentCondition(values.length == 0, "Values can't be empty");

        if (mContentValues == null) {
            mContentValues = new ArrayList<>();
        }

        Collections.addAll(mContentValues, values);

        return this;
    }

    /**
     * Perform an insert against the provided database using the state of the current instance.
     * <p>If no values are added when trying to insert, an empty row will be inserted, using {@link BaseColumns#_ID} as
     * a null column hack.</p>
     *
     * @param db required. The database to insert into.
     * @throws IllegalArgumentException if {@code db} is null.
     * @throws IllegalStateException if a table has not been set by calling {@link #setTable(String)} before trying to insert.
     * @return the current instance
     */
    public List<Long> insert(@NonNull SQLiteDatabase db) {
        Preconditions.argumentNotNull(db, "Database can't be null");

        Preconditions.stateNotNull(mTable, "Can't execute an insert with no table set. Did you call setTable()?");

        final String sql = generateSql();
        final SQLiteStatement statement = db.compileStatement(sql);
        final List<Long> generatedIds = new LinkedList<>();
        try {
            executeInsertStatement(statement, generatedIds);
        } finally {
            statement.close();
        }

        return generatedIds;
    }

    private void executeInsertStatement(SQLiteStatement statement, List<Long> generatedIds) {
        for (ContentValues values : mContentValues) {
            statement.clearBindings();
            bindValues(statement, values);
            generatedIds.add(statement.executeInsert());
        }
    }

    @VisibleForTesting
    String generateSql() {
        Set<String> columnNames = null;
        final ContentValues contentValues = mContentValues.get(0);
        if (contentValues == null) {
            columnNames = Collections.singleton(BaseColumns._ID);
        } else {
            columnNames = contentValues.keySet();
        }
        final String columns = generateColumns(columnNames);
        final String values = generateValues(columnNames.size());
        return String.format((Locale) null, SQL_TEMPLATE, mTable, columns, values);
    }

    private static String generateColumns(Set<String> columnNames) {
        final StringBuilder columnBuilder = new StringBuilder();
        for (String columnName : columnNames) {
            columnBuilder.append(columnName);
            columnBuilder.append(", ");
        }
        return columnBuilder.substring(0, columnBuilder.length() - 2);
    }

    private static String generateValues(int size) {
        final StringBuilder valueBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            valueBuilder.append("?, ");
        }
        return valueBuilder.substring(0, valueBuilder.length() - 2);
    }

    private static void bindValues(SQLiteStatement statement, ContentValues contentValues) {
        if (contentValues == null) {
            return;
        }

        int parameterIndex = 1;
        for (String column : contentValues.keySet()) {
            final String value = contentValues.getAsString(column);
            if (value == null) {
                statement.bindNull(parameterIndex);
            } else {
                statement.bindString(parameterIndex, value);
            }

            parameterIndex++;
        }
    }
}

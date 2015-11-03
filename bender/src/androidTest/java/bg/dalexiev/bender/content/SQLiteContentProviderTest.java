package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.test.ProviderTestCase2;

public class SQLiteContentProviderTest extends ProviderTestCase2<SQLiteContentProviderTest.MockSQLiteContentProvider> {

    private ContentResolver mContentResolver;

    public static final Uri INVALID_URI = MockSQLiteContentProvider.USER_TABLE_URI.buildUpon().appendPath("blah")
            .build();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContentResolver = getMockContentResolver();
    }

    public void testQueryAll() {
        final Cursor cursor = mContentResolver
                .query(MockSQLiteContentProvider.USER_TABLE_URI, new String[]{BaseColumns._ID, "name"}, null, null,
                        null);
        assertEquals(5, cursor.getCount());
    }

    public void testQueryById() {
        final long expectedId = 1;
        final Cursor cursor = mContentResolver
                .query(ContentUris.withAppendedId(MockSQLiteContentProvider.USER_TABLE_URI, expectedId),
                        new String[]{BaseColumns._ID}, null, null, null);

        cursor.moveToFirst();

        final long actualId = cursor.getLong(0);

        assertEquals(expectedId, actualId);
    }

    public void testQueryBySelection() {
        final String expectedName = "Pesho";

        final Cursor cursor = mContentResolver
                .query(MockSQLiteContentProvider.USER_TABLE_URI, new String[]{"name"}, "name = ?",
                        new String[]{expectedName}, null);

        cursor.moveToFirst();

        final String actualName = cursor.getString(0);

        assertEquals(expectedName, actualName);
    }

    public void testQueryWithInvalidUri() {
        try {
            mContentResolver.query(INVALID_URI, new String[]{BaseColumns._ID}, null, null, null);
            throw new AssertionError("Should fail with " + UnsupportedOperationException.class.getSimpleName());
        } catch (UnsupportedOperationException e) {
            // test passed
        }
    }

    public void testGetTableMimeType() {
        final String actualType = mContentResolver.getType(MockSQLiteContentProvider.USER_TABLE_URI);

        assertEquals(ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + MockSQLiteContentProvider.AUTHORITY + ".users",
                actualType);
    }

    public void testGetRowMimeType() {
        final String actualType = mContentResolver
                .getType(ContentUris.withAppendedId(MockSQLiteContentProvider.USER_TABLE_URI, 1));

        assertEquals(ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + MockSQLiteContentProvider.AUTHORITY + ".users",
                actualType);
    }

    public void testGetTypeWithInvalidUrl() {
        final String url = mContentResolver.getType(INVALID_URI);

        // the exception is swallowed and null is returned
        assertNull(url);
    }

    public void testInsert() {
        final ContentValues values = new ContentValues();
        final String expectedName = "Inserted from test";
        values.put("name", expectedName);

        final Uri resultUri = mContentResolver.insert(MockSQLiteContentProvider.USER_TABLE_URI, values);

        final Cursor cursor = mContentResolver.query(resultUri, new String[]{"name"}, null, null, null);
        if (!cursor.moveToFirst()) {
            throw new AssertionError("Inserted row not found");
        }
        final String actualName = cursor.getString(0);

        assertEquals(expectedName, actualName);
    }

    public void testInsertWithInvalidId() {
        try {
            final ContentValues values = new ContentValues();
            final String expectedName = "Inserted from test";
            values.put("name", expectedName);

            mContentResolver.insert(INVALID_URI, values);
            throw new AssertionError("Should fail with an " + UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            // test passed
        }
    }

    public void testInsertBulk() {
        final ContentValues[] contentValues = new ContentValues[2];
        contentValues[0] = new ContentValues();
        contentValues[0].put("name", "Bulk insert 1");
        contentValues[1] = new ContentValues();
        contentValues[1].put("name", "Bulk insert 2");

        int insertedRows = mContentResolver.bulkInsert(MockSQLiteContentProvider.USER_TABLE_URI, contentValues);

        assertEquals(contentValues.length, insertedRows);
    }

    public void testDeleteById() {
        final int deletedId = 5;
        final Uri uri = ContentUris.withAppendedId(MockSQLiteContentProvider.USER_TABLE_URI, deletedId);
        final int deletedRows = mContentResolver
                .delete(uri, null, null);

        final Cursor cursor = mContentResolver.query(uri, new String[]{BaseColumns._ID}, null, null, null);

        assertEquals(1, deletedRows);
        assertEquals(0, cursor.getCount());
    }

    public void testDeleteBySelection() {
        final int deletedRows = mContentResolver
                .delete(MockSQLiteContentProvider.USER_TABLE_URI, "name = ?", new String[]{"Nikodim"});

        final Cursor cursor = mContentResolver
                .query(MockSQLiteContentProvider.USER_TABLE_URI, new String[]{BaseColumns._ID}, "name = ?",
                        new String[]{"Nikodim"}, null);

        assertEquals(1, deletedRows);
        assertEquals(0, cursor.getCount());
    }

    public void testDeleteWithInvalidUrl() {
        try {
            mContentResolver.delete(INVALID_URI, null, null);
            throw new AssertionError("Should have failed with an " + UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            // test passed
        }
    }

    public void testUpdateById() {
        final ContentValues contentValues = new ContentValues(1);
        final String expectedName = "Updated";
        contentValues.put("name", expectedName);
        final Uri uri = ContentUris.withAppendedId(MockSQLiteContentProvider.USER_TABLE_URI, 1);

        final int updatedRows = mContentResolver
                .update(uri, contentValues,
                        null, null);

        final Cursor cursor = mContentResolver.query(uri, new String[]{"name"}, null, null, null);
        if (!cursor.moveToFirst()) {
            throw new AssertionError("Couldn't find updated row");
        }
        final String actualName = cursor.getString(0);

        assertEquals(1, updatedRows);
        assertEquals(expectedName, actualName);
    }

    public void testUpdateWithSelection() {
        final ContentValues contentValues = new ContentValues(1);
        final String expectedName = "Updated with selection";
        contentValues.put("name", expectedName);

        final String selection = "name like ?";
        final String[] selectionArgs = {"%sho"};

        final int updatedRows = mContentResolver
                .update(MockSQLiteContentProvider.USER_TABLE_URI, contentValues, selection, selectionArgs);

        final Cursor cursor = mContentResolver
                .query(MockSQLiteContentProvider.USER_TABLE_URI, new String[]{"name"}, "name = ?",
                        new String[]{expectedName}, null);
        assertRowsWithName(expectedName, cursor);
        assertEquals(2, updatedRows);
    }

    private static void assertRowsWithName(String expectedName, Cursor cursor) {
        while (cursor.moveToNext()) {
            assertEquals(expectedName, cursor.getString(0));
        }
    }

    public void testUpdateAll() {
        final ContentValues contentValues = new ContentValues(1);
        final String expectedName = "Bulk Updated";
        contentValues.put("name", expectedName);

        final int updatedRows = mContentResolver
                .update(MockSQLiteContentProvider.USER_TABLE_URI, contentValues, null, null);

        final Cursor cursor = mContentResolver
                .query(MockSQLiteContentProvider.USER_TABLE_URI, new String[]{"name"}, null, null, null);
        assertRowsWithName(expectedName, cursor);
        assertEquals(updatedRows, cursor.getCount());
    }

    public void testUpdateWithInvalidUrl() {
        try {
            final ContentValues contentValues = new ContentValues(1);
            final String expectedName = "Bulk Updated";
            contentValues.put("name", expectedName);

            mContentResolver.update(INVALID_URI, contentValues, null, null);
            throw new AssertionError("Should fail with an " + UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            // test passed
        }
    }

    public void testUpdateWithNullValues() {
        try {
            mContentResolver.update(MockSQLiteContentProvider.USER_TABLE_URI, null, null, null);
            throw new AssertionError("Should fail with an " + IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // test passed
        }
    }

    public void testUpdateWithEmptyValues() {
        try {
            mContentResolver.update(MockSQLiteContentProvider.USER_TABLE_URI, new ContentValues(), null, null);
            throw new AssertionError("Should fail with an " + IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // test passed
        }
    }

    public SQLiteContentProviderTest() {
        super(MockSQLiteContentProvider.class, MockSQLiteContentProvider.AUTHORITY);
    }

    public static class MockSQLiteContentProvider extends DatabaseContentProvider {

        private static final String AUTHORITY = "bg.dalexiev.bender.test.mock.provider";

        private static final Uri USER_TABLE_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .appendPath("users").build();

        @NonNull
        @Override
        protected String createAuthority() {
            return AUTHORITY;
        }

        @NonNull
        @Override
        protected SQLiteOpenHelper createHelper(@NonNull Context context) {
            return new SQLiteOpenHelper(context, "test.db", null, 1) {
                @Override
                public void onCreate(SQLiteDatabase db) {
                    db.execSQL("create table users(_id integer primary key, name text);");

                    db.beginTransaction();
                    try {
                        final SQLiteStatement insertStatement = db
                                .compileStatement("insert into users(name) values (?)");
                        final String[] names = {"Pesho", "Gosho", "Stamat", "Nikodim", "Pantaley"};
                        for (String name : names) {
                            insertStatement.clearBindings();
                            insertStatement.bindString(1, name);
                            insertStatement.executeInsert();
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    // do nothing
                }
            };
        }
    }
}

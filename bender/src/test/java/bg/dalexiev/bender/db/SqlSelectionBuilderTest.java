package bg.dalexiev.bender.db;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SqlSelectionBuilderTest {

    private SqlSelectionBuilder mTested;

    @Mock
    private SQLiteDatabase mDatabase;

    @Before
    public void setUp() {
        mTested = new SqlSelectionBuilder();
    }

    @Test
    public void shouldSetTable() {
        final String expectedTable = "table";

        mTested.setTable(expectedTable);

        assertEquals(expectedTable, mTested.getTable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullTable() {
        mTested.setTable(null);
    }

    @Test
    public void shouldNotBeDistinctByDefault() {
        assertFalse(mTested.isDistinct());
    }

    @Test
    public void shouldSetDistinct() {
        mTested.setDistinct();

        assertTrue(mTested.isDistinct());
    }

    @Test
    public void shouldAddWhereClause() {
        final String selection = "test = ?";
        final String[] selectionArgs = {"1"};

        final String expectedSelection = '(' + selection + ')';

        mTested.where(selection, selectionArgs);

        assertEquals(expectedSelection, mTested.getSelection());
        assertTrue(Arrays.equals(selectionArgs, mTested.getSelectionArgs()));
    }

    @Test
    public void shouldAddWhereClauseWithNoArgs() {
        final String selection = "test is null";
        final String expectedSelection = '(' + selection + ')';

        mTested.where(selection, null);

        assertEquals(expectedSelection, mTested.getSelection());
        assertNull(mTested.getSelectionArgs());
    }

    @Test
    public void shouldNotAddWhereIfNoSelectionAndArgs() {
        mTested.where(null, null);

        assertNull(mTested.getSelection());
        assertNull(mTested.getSelectionArgs());
    }

    @Test
    public void shouldAddWhereWithAnd() {
        final String firstExpression = "test is null";
        final String secondExpression = "foo = ?";

        final String expectedSelection = '(' + firstExpression + ") and (" + secondExpression + ')';
        final String[] expectedArgs = {"1"};

        mTested.where(firstExpression, null).where(secondExpression, expectedArgs);

        assertEquals(expectedSelection, mTested.getSelection());
        assertTrue(Arrays.equals(expectedArgs, mTested.getSelectionArgs()));
    }

    @Test
    public void shouldAddWhereWithOr() {
        final String firstExpression = "test is null";
        final String secondExpression = "foo = ?";

        final String expectedSelection = '(' + firstExpression + ") or (" + secondExpression + ')';
        final String[] expectedArgs = {"1"};

        mTested.where(firstExpression, null).orWhere(secondExpression, expectedArgs);

        assertEquals(expectedSelection, mTested.getSelection());
        assertTrue(Arrays.equals(expectedArgs, mTested.getSelectionArgs()));
    }

    @Test
    public void shouldAddWherePredicate() {
        final String column = "test";
        final String value = "foo";

        final String expectedSelection = '(' + column + " = ?)";
        final String[] expectedArgs = {value};

        mTested.where(Predicate.eq(column, value));

        assertEquals(expectedSelection, mTested.getSelection());
        assertTrue(Arrays.equals(expectedArgs, mTested.getSelectionArgs()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullWherePredicate() {
        mTested.where(null);
    }

    @Test
    public void shouldAddWherePredicateWithAnd() {
        final String column1 = "foo";
        final String column2 = "bar";

        final String expectedSelection = '(' + column1 + " = ?) and (" + column2 + " = ?)";
        final String[] expectedArgs = {"1", "2"};

        mTested.where(Predicate.eq(column1, "1")).where(Predicate.eq(column2, "2"));

        assertEquals(expectedSelection, mTested.getSelection());
        assertTrue(Arrays.equals(expectedArgs, mTested.getSelectionArgs()));
    }

    @Test
    public void shouldAddOrWherePredicate() {
        final String column = "test";

        final String expectedSelection = '(' + column + " is not null)";

        mTested.orWhere(Predicate.isNotNull(column));

        assertEquals(expectedSelection, mTested.getSelection());
        assertNull(mTested.getSelectionArgs());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullOrPredicate() {
        mTested.orWhere(null);
    }

    @Test
    public void shouldAddWherePredicateWithOr() {
        final String column1 = "foo";
        final String column2 = "bar";

        final String expectedSelection = '(' + column1 + " = ?) or (" + column2 + " = ?)";
        final String[] expectedArgs = {"1", "2"};

        mTested.where(Predicate.eq(column1, "1")).orWhere(Predicate.eq(column2, "2"));

        assertEquals(expectedSelection, mTested.getSelection());
        assertTrue(Arrays.equals(expectedArgs, mTested.getSelectionArgs()));
    }

    @Test
    public void shouldSetGroupBy() {
        final String column1 = "foo";
        final String column2 = "bar";

        final String expectedGroupBy = column1 + ", " + column2;

        mTested.groupBy(column1, column2);

        assertEquals(expectedGroupBy, mTested.getGroupBy());
    }

    @Test
    public void shouldNotSetGroupByWhenNullColumns() {
        mTested.groupBy(null);

        assertNull(mTested.getGroupBy());
    }

    @Test
    public void shouldNotSetGroupByWhenEmptyColumns() {
        mTested.groupBy(new String[]{});

        assertNull(mTested.getGroupBy());
    }

    @Test
    public void shouldSetHaving() {
        final String expectedHaving = "max(foo) < 1";

        mTested.having(expectedHaving);

        assertEquals(expectedHaving, mTested.getHaving());
    }

    @Test
    public void shouldNotSetHavingWhenNullExpression() {
        mTested.having(null);

        assertNull(mTested.getHaving());
    }

    @Test
    public void shouldSetOrderBy() {
        final String expectedOrderBy = "foo asc, bar desc";

        mTested.orderBy(expectedOrderBy);

        assertEquals(expectedOrderBy, mTested.getOrderBy());
    }

    @Test
    public void shouldNotSetOrderByWithNullExpression() {
        mTested.orderBy((String) null);

        assertNull(mTested.getOrderBy());
    }

    @Test
    public void shouldSetOrderByWithOrderByPredicate() {
        final String column1 = "foo";
        final String column2 = "bar";

        final String expectedOrderBy = column1 + " asc, " + column2 + " desc";

        mTested.orderBy(OrderBy.asc(column1), OrderBy.desc(column2));

        assertEquals(expectedOrderBy, mTested.getOrderBy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullPredicatesInOrderBy() {
        mTested.orderBy((OrderBy[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenEmptyPredicatesInOrderBy() {
        mTested.orderBy(new OrderBy[]{});
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenNullPredicateInOrderBy() {
        mTested.orderBy((OrderBy) null);
    }

    @Test
    public void shouldSetLimit() {
        final String expectedLimit = "10";

        mTested.limit(expectedLimit);

        assertEquals(expectedLimit, mTested.getLimit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullLimit() {
        mTested.limit(null);
    }

    @Test
    public void shouldPerformUpdate() {
        final String table = "test";

        final ContentValues values = mock(ContentValues.class);
        doReturn(1).when(values).size();

        mTested.setTable(table).update(mDatabase, values);

        verify(mDatabase).update(eq(table), eq(values), isNull(String.class), isNull(String[].class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullDbInUpdate() {
        final String table = "test";

        final ContentValues values = mock(ContentValues.class);
        doReturn(1).when(values).size();

        mTested.setTable(table).update(null, values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullValuesInUpdate() {
        final String table = "test";

        mTested.setTable(table).update(mDatabase, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenEmptyValuesInUpdate() {
        final String table = "test";

        mTested.setTable(table).update(mDatabase, new ContentValues());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoTableSetInUpdate() {
        final ContentValues values = mock(ContentValues.class);
        doReturn(1).when(values).size();

        mTested.update(mDatabase, values);
    }

    @Test
    public void shouldPerformDelete() {
        final String table = "table";

        mTested.setTable(table).delete(mDatabase);

        verify(mDatabase).delete(eq(table), isNull(String.class), isNull(String[].class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullDatabaseInDelete() {
        final String table = "table";

        mTested.setTable(table).delete(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoTableSetInDelete() {
        mTested.delete(mDatabase);
    }

    @Test
    public void shouldPerformQuery() {
        final String table = "table";
        final String[] projection = {"foo", "bar"};

        mTested.setTable(table).query(mDatabase, projection);

        verify(mDatabase).query(eq(false), eq(table), eq(projection), isNull(String.class), isNull(String[].class),
                isNull(String.class), isNull(String.class), isNull(String.class), isNull(String.class));
    }

    @Test
    public void shouldPerformQueryWithNoProjection() {
        final String table = "table";

        mTested.setTable(table).query(mDatabase, null);

        verify(mDatabase).query(eq(false), eq(table), isNull(String[].class), isNull(String.class), isNull(String[].class),
                isNull(String.class), isNull(String.class), isNull(String.class), isNull(String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullDatabaseInQuery() {
        final String table = "table";
        final String[] projection = {"foo", "bar"};

        mTested.setTable(table).query(null, projection);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoTableSetInQuery() {
        final String[] projection = {"foo", "bar"};

        mTested.query(mDatabase, projection);
    }
}
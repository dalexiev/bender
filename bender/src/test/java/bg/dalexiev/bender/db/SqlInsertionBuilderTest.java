package bg.dalexiev.bender.db;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SqlInsertionBuilderTest {

    private SqlInsertionBuilder mTested;

    @Mock
    private SQLiteDatabase mDatabase;

    @Before
    public void setUp() {
        mTested = new SqlInsertionBuilder();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfNullTable() {
        mTested.setTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIfNoTableSet() {
        mTested.insert(mDatabase);
    }

    @Test
    public void shouldGenerateCorrectSqlWhenSingleColumn() {
        final ContentValues contentValues = mock(ContentValues.class);

        doReturn(Collections.singleton("foo")).when(contentValues).keySet();

        doReturn(1).when(contentValues).size();

        mTested.setTable("test").appendValues(contentValues);

        final String sql = mTested.generateSql();

        assertEquals("insert into test(foo) values (?)", sql);
    }

    @Test
    public void shouldGenerateCorrectSqlWhenMultipleColumns() {
        final ContentValues contentValues = providedHasContentValues();

        mTested.setTable("test").appendValues(contentValues);

        final String sql = mTested.generateSql();

        final StringBuilder columnBuilder = new StringBuilder();
        for (String column : contentValues.keySet()) {
            columnBuilder.append(column).append(", ");
        }
        assertEquals("insert into test(" + columnBuilder.substring(0, columnBuilder.length() - 2)
                + ") values (?, ?)", sql);
    }

    @Test
    public void shouldInsertEmptyRow() {
        mTested.setTable("test").appendValues((ContentValues) null);

        final String sql = mTested.generateSql();

        assertEquals("insert into test(_id) values (?)", sql);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnExecuteWhenDatabaseNull() {
        mTested.insert(null);
    }

    @Test
    public void shouldExecute() {
        final ContentValues contentValues = providedHasContentValues();

        final SQLiteStatement statement = mock(SQLiteStatement.class);
        doReturn(statement).when(mDatabase).compileStatement(anyString());

        doReturn(1L).when(statement).executeInsert();

        mTested.setTable("test").appendValues(contentValues);

        final List<Long> generatedIds = mTested.insert(mDatabase);

        InOrder executionOrder = inOrder(mDatabase, statement);
        executionOrder.verify(mDatabase).compileStatement(anyString());
        executionOrder.verify(statement).clearBindings();
        final Iterator<String> columnIterator = contentValues.keySet().iterator();
        final String firstColumn = columnIterator.next();
        if ("foo".equals(firstColumn)) {
            executionOrder.verify(statement).bindString(eq(1), eq("value"));
            executionOrder.verify(statement).bindNull(eq(2));
        } else {
            executionOrder.verify(statement).bindNull(eq(1));
            executionOrder.verify(statement).bindString(eq(2), eq("value"));
        }
        executionOrder.verify(statement).executeInsert();
        executionOrder.verify(statement).close();

        assertTrue(generatedIds.equals(Collections.singletonList(1L)));
    }

    private static ContentValues providedHasContentValues() {
        final ContentValues contentValues = mock(ContentValues.class);

        final Set<String> columns = new HashSet<>(Arrays.asList("foo", "bar"));
        doReturn(columns).when(contentValues).keySet();

        doReturn(columns.size()).when(contentValues).size();

        doAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                String columnName = (String) invocationOnMock.getArguments()[0];
                return "foo".equals(columnName) ? "value" : null;
            }
        }).when(contentValues).getAsString(anyString());

        return contentValues;
    }
}
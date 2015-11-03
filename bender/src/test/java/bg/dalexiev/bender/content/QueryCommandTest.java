package bg.dalexiev.bender.content;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import bg.dalexiev.bender.db.OrderBy;
import bg.dalexiev.bender.db.Predicate;
import bg.dalexiev.bender.db.SqlSelectionBuilder;
import bg.dalexiev.bender.mapper.CursorMapper;
import bg.dalexiev.bender.mapper.RowMapper;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QueryCommandTest extends
        ResolverCommandTestBase<QueryCommand.Callback<ResolverCommandTestBase.MockEntity>, QueryCommand<ResolverCommandTestBase.MockEntity>> {

    private RowMapper<MockEntity> mRowMapper;

    private CursorMapper<MockEntity> mCursorMapper;

    @Spy
    private SqlSelectionBuilder mSelectionBuilder;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        mRowMapper = new RowMapper<MockEntity>() {
            @Nullable
            @Override
            public MockEntity toObject(@NonNull Cursor cursor, int rowNum) {
                return new MockEntity(1L, "test");
            }
        };

        mCursorMapper = new CursorMapper<MockEntity>() {

            @Nullable
            @Override
            public SparseArray<MockEntity> mapCursor(@NonNull Cursor cursor) {
                return new SparseArray<>(0);
            }
        };
    }

    @Test
    public void shouldDelegateWhereWithSelection() {
        final String selection = "foo = ?";
        final String[] selectionArgs = {"selectionArgs"};
        mTested.where(selection, selectionArgs);

        verify(mSelectionBuilder).where(eq(selection), any(String[].class));
    }

    @Test
    public void shouldDelegateWhereWithPredicate() {
        final Predicate predicate = Predicate.eq("foo", "bar");

        mTested.where(predicate);

        verify(mSelectionBuilder).where(eq(predicate));
    }

    @Test
    public void shouldDelegateOrWhereWithSelection() {
        final String selection = "foo = ?";
        final String[] selectionArgs = {"selectionArgs"};
        mTested.orWhere(selection, selectionArgs);

        verify(mSelectionBuilder).orWhere(eq(selection), any(String[].class));
    }

    @Test
    public void shouldDelegateOrWhereWithPredicate() {
        final Predicate predicate = Predicate.eq("foo", "bar");

        mTested.orWhere(predicate);

        verify(mSelectionBuilder).orWhere(eq(predicate));
    }

    @Test
    public void shouldDelegateOrderBy() {
        final String orderBy = "foo";

        mTested.orderBy(orderBy);

        verify(mSelectionBuilder).orderBy(eq(orderBy));
    }

    @Test
    public void shouldDelegateOrderByWithPredicate() {
        final OrderBy orderBy = OrderBy.asc("foo");

        mTested.orderBy(orderBy);

        verify(mSelectionBuilder).orderBy(any(OrderBy[].class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullPredicatesInOrderBy() {
        mTested.orderBy((OrderBy[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullPredicateInOrderBy() {
        mTested.orderBy((OrderBy) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnEmptyPredicatesInOrderBy() {
        mTested.orderBy(new OrderBy[]{});
    }

    @Override
    protected QueryCommand.Callback<MockEntity> createCallback() {
        return mock(QueryCommand.Callback.class);
    }

    @Test
    public void shouldExecuteQueryWithRowMapper() {
        final String[] projection = {"test"};
        final String selection = "foo = ?";
        final String[] selectionArgs = {"bar"};
        final String orderBy = "baz asc";

        mTested.onUri(mUri).select(projection).where(selection, selectionArgs).orderBy(orderBy)
                .useRowMapper(mRowMapper).execute();

        verify(mContentResolver).query(eq(mUri), eq(projection), eq('(' + selection + ')'), eq(selectionArgs), eq(orderBy));
    }

    @Test
    public void shouldExecuteQueryWithCursorMapper() {
        final String[] projection = {"test"};
        final String selection = "foo = ?";
        final String[] selectionArgs = {"bar"};
        final String orderBy = "baz asc";

        mTested.onUri(mUri).select(projection).where(selection, selectionArgs).orderBy(orderBy)
                .useCursorMapper(mCursorMapper).execute();

        verify(mContentResolver).query(eq(mUri), eq(projection), eq('(' + selection + ')'), eq(selectionArgs), eq(orderBy));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullProjection() {
        mTested.select(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullSelection() {
        mTested.where(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullOrderBy() {
        mTested.orderBy((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullRowMapper() {
        mTested.useRowMapper(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowForRowMapperWhenAlreadySet() {
        mTested.useCursorMapper(mCursorMapper).useRowMapper(mRowMapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullCursorMapper() {
        mTested.useCursorMapper(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowForCursorMapperWhenAlreadySet() {
        mTested.useRowMapper(mRowMapper).useCursorMapper(mCursorMapper);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoUriSet() {
        mTested.select(new String[]{"test"}).useRowMapper(mRowMapper).execute();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoProjectionSet() {
        mTested.onUri(mUri).useRowMapper(mRowMapper).execute();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoMapperSet() {
        mTested.onUri(mUri).select(new String[]{"test"}).execute();
    }

    @Override
    protected void verifyContentResolverMethodCalled(@NonNull InOrder executionOrder,
            @Nullable Map<String, Object> executionParams) {
        final String[] projection = (String[]) executionParams.get("projection");
        final String selection = (String) executionParams.get("selection");
        final String[] selectionArgs = (String[]) executionParams.get("selectionArgs");
        final String orderBy = (String) executionParams.get("orderBy");

        executionOrder.verify(mContentResolver)
                .query(eq(mUri), eq(projection), eq('(' + selection + ')'), eq(selectionArgs), eq(orderBy));
    }

    @Override
    protected void verifyCallbackCalled(Message msg,
            QueryCommand.Callback<MockEntity> callback) {
        final int token = msg.what;
        verify(callback).onQueryComplete(eq(token), any(EntityCursor.class));
    }

    @Override
    protected Message createCallbackMessage() {
        final EntityCursor<MockEntity> cursor = mock(EntityCursor.class);

        final Message msg = new Message();
        msg.what = 1;
        msg.obj = cursor;

        return msg;
    }

    @Nullable
    @Override
    protected Map<String, Object> executeCommand() {
        final String[] projection = {"test"};
        final String selection = "foo = ?";
        final String[] selectionArgs = {"bar"};
        final String orderBy = "baz asc";

        mTested.onUri(mUri).select(projection).where(selection, selectionArgs).orderBy(orderBy)
                .useRowMapper(mRowMapper).execute();

        final Map<String, Object> params = new HashMap<>(4);
        params.put("projection", projection);
        params.put("selection", selection);
        params.put("selectionArgs", selectionArgs);
        params.put("orderBy", orderBy);

        return params;
    }

    @Nullable
    @Override
    protected Map<String, Object> executeAsyncCommand(int token,
            QueryCommand.Callback<MockEntity> callback) {
        final String[] projection = {"test"};
        final String selection = "foo = ?";
        final String[] selectionArgs = {"bar"};
        final String orderBy = "baz asc";

        mTested.onUri(mUri).select(projection).where(selection, selectionArgs).orderBy(orderBy)
                .useRowMapper(mRowMapper).executeAsync(token, callback);

        final Map<String, Object> params = new HashMap<>(4);
        params.put("projection", projection);
        params.put("selection", selection);
        params.put("selectionArgs", selectionArgs);
        params.put("orderBy", orderBy);

        return params;
    }

    @NonNull
    @Override
    protected QueryCommand<ResolverCommandTestBase.MockEntity> createTested(
            @NonNull BaseResolverCommand.WorkerHandler workerHandler, @NonNull ContentResolver contentResolver) {
        return new QueryCommand<>(workerHandler, contentResolver, mSelectionBuilder);
    }

}
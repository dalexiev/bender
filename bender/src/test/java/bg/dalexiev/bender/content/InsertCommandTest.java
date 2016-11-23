package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class InsertCommandTest extends ResolverCommandTestBase<InsertCommand.Callback, InsertCommand> {

    @Spy
    private ContentValuesBuilder mContentValuesBuilder;

    @Spy
    private OnConflictBuilder mOnConflictBuilder;

    @Mock
    private ContentValues mReference;

    @Test
    public void shouldSetUpdateValues() {
        final String nullColumn = "test0";
        mTested.setNull(nullColumn);

        final String booleanColumn = "test1";
        final boolean booleanValue = true;
        mTested.set(booleanColumn, booleanValue);

        final String longColumn = "test2";
        final long longValue = 1L;
        mTested.set(longColumn, longValue);

        final String intColumn = "test3";
        final int intValue = 1;
        mTested.set(intColumn, intValue);

        final String shortColumn = "test4";
        final short shortValue = 5;
        mTested.set(shortColumn, shortValue);

        final String byteColumn = "test5";
        final byte byteValue = 7;
        mTested.set(byteColumn, byteValue);

        final String doubleColumn = "test6";
        final double doubleValue = 17.45;
        mTested.set(doubleColumn, doubleValue);

        final String floatColumn = "test7";
        final float floatValue = 89.43f;
        mTested.set(floatColumn, floatValue);

        final String stringColumn = "test8";
        final String stringValue = "testy testy";
        mTested.set(stringColumn, stringValue);

        final String byteArrayColumn = "test9";
        final byte[] byteArrayValue = {1, 2, 3, 4, 5, 6};
        mTested.set(byteArrayColumn, byteArrayValue);

        final InOrder executionOrder = inOrder(mContentValuesBuilder);
        executionOrder.verify(mContentValuesBuilder).setNull(eq(nullColumn));
        executionOrder.verify(mContentValuesBuilder).set(eq(booleanColumn), eq(booleanValue));
        executionOrder.verify(mContentValuesBuilder).set(eq(longColumn), eq(longValue));
        executionOrder.verify(mContentValuesBuilder).set(eq(intColumn), eq(intValue));
        executionOrder.verify(mContentValuesBuilder).set(eq(shortColumn), eq(shortValue));
        executionOrder.verify(mContentValuesBuilder).set(eq(byteColumn), eq(byteValue));
        executionOrder.verify(mContentValuesBuilder).set(eq(doubleColumn), eq(doubleValue));
        executionOrder.verify(mContentValuesBuilder).set(eq(floatColumn), eq(floatValue));
        executionOrder.verify(mContentValuesBuilder).set(eq(stringColumn), eq(stringValue));
        executionOrder.verify(mContentValuesBuilder).set(eq(byteArrayColumn), eq(byteArrayValue));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForNull() {
        mTested.setNull(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForByte() {
        mTested.set(null, (byte) 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForShort() {
        mTested.set(null, (short) 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForInt() {
        mTested.set(null, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForLong() {
        mTested.set(null, 5L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForFloat() {
        mTested.set(null, 14.45f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForDouble() {
        mTested.set(null, 45.56);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForBoolean() {
        mTested.set(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForString() {
        mTested.set(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullValueForString() {
        mTested.set("test", (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnNameForByteArray() {
        mTested.set(null, new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThenWhenNullValueForByteArray() {
        mTested.set("test", (byte[]) null);
    }

    @Test
    public void shouldSetOnConflict() {
        final int expectedOnConflict = SQLiteDatabase.CONFLICT_ABORT;

        mTested
                .onUri(mUri)
                .onConflict(expectedOnConflict)
                .set("foo", "bar")
                .execute();

        final InOrder executionOrder = inOrder(mOnConflictBuilder, mUri, mUri.buildUpon());
        executionOrder.verify(mOnConflictBuilder).setOnConflict(eq(expectedOnConflict));
        executionOrder.verify(mOnConflictBuilder).appendOnConflictParameter(eq(mUri));
        executionOrder.verify(mUri).buildUpon();
        executionOrder.verify(mUri.buildUpon()).appendQueryParameter(eq(DatabaseContentProvider.PARAM_CONFLICT_ALGORITHM), eq(String.valueOf(expectedOnConflict)));
    }

    @Test
    public void shouldSetValueBackReference() {
        final String column = "test";
        final int index = 4;

        mTested.withValueBackReference(column, index);

        verify(mReference).put(eq(column), eq(index));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNoColumnForValueBackReference() {
        mTested.withValueBackReference(null, 0);
    }

    @NonNull
    @Override
    protected InsertCommand createTested(@NonNull BaseResolverCommand.WorkerHandler workerHandler, @NonNull ContentResolver contentResolver) {
        return new InsertCommand(workerHandler, contentResolver, mContentValuesBuilder, mOnConflictBuilder, mReference);
    }

    @Override
    protected InsertCommand.Callback createCallback() {
        return mock(InsertCommand.Callback.class);
    }

    @Nullable
    @Override
    protected Map<String, Object> executeCommand() {
        mTested.onUri(mUri).set("foo", "bar").execute();

        return null;
    }

    @Override
    protected Map<String, Object> executeAsyncCommand(int token, InsertCommand.Callback callback) {
        mTested.onUri(mUri).set("foo", "bar").executeAsync(token, callback);

        return null;
    }

    @Override
    protected void verifyContentResolverMethodCalled(@NonNull InOrder executionOrder,
                                                     Map<String, Object> executionParams) {
        executionOrder.verify(mContentResolver).insert(eq(mUri), any(ContentValues.class));
    }

    @Override
    protected void verifyCallbackCalled(Message msg, InsertCommand.Callback callback) {
        final int token = msg.what;
        final Uri result = (Uri) msg.obj;

        verify(callback).onInsertComplete(eq(token), eq(result));
    }

    @Override
    protected Message createCallbackMessage() {
        final Message message = new Message();
        message.what = 1;
        message.obj = mock(Uri.class);

        return message;
    }
}
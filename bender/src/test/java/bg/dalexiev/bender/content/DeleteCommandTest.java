package bg.dalexiev.bender.content;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.ContentResolver;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import bg.dalexiev.bender.db.Predicate;
import bg.dalexiev.bender.db.SqlSelectionBuilder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCommandTest extends ResolverCommandTestBase<DeleteCommand.Callback, DeleteCommand> {

    @Spy
    private SqlSelectionBuilder mSelectionBuilder;

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
    public void shouldDeleteWithoutSelection() {
        mTested.onUri(mUri).execute();

        verify(mContentResolver).delete(eq(mUri), isNull(String.class), isNull(String[].class));
    }

    @Test
    public void shouldDeleteWithSelectionAndNoArgs() {
        final String selection = "foo is not null";

        mTested.onUri(mUri).where(selection, null).execute();

        verify(mContentResolver).delete(eq(mUri), eq('(' + selection + ')'), isNull(String[].class));
    }

    @Test
    public void shouldDeleteWithSelectionAndArgs() {
        final String selection = "foo = ?";
        final String[] selectionArgs = {"bar"};

        mTested.onUri(mUri).where(selection, selectionArgs).execute();

        verify(mContentResolver).delete(eq(mUri), eq('(' + selection + ')'), eq(selectionArgs));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullSelection() {
        mTested.where(null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoResolverSet() {
        mTested.where("foo is null", null).execute();
    }

    @Override
    protected void verifyContentResolverMethodCalled(@NonNull InOrder executionOrder,
            @Nullable Map<String, Object> executionParams) {
        executionOrder.verify(mContentResolver).delete(eq(mUri), isNull(String.class), isNull(String[].class));
    }

    @Override
    protected void verifyCallbackCalled(Message msg, DeleteCommand.Callback callback) {
        final int token = msg.what;
        final int result = (int) msg.obj;
        verify(callback).onDeleteComplete(eq(token), eq(result));
    }

    @Override
    protected Message createCallbackMessage() {
        final Message msg = new Message();
        msg.what = 1;
        msg.obj = 1;
        return msg;
    }

    @NonNull
    @Override
    protected DeleteCommand createTested(@NonNull BaseResolverCommand.WorkerHandler workerHandler, @NonNull ContentResolver contentResolver) {
        return new DeleteCommand(workerHandler, contentResolver, mSelectionBuilder);
    }

    @Override
    protected DeleteCommand.Callback createCallback() {
        return mock(DeleteCommand.Callback.class);
    }

    @Nullable
    @Override
    protected Map<String, Object> executeCommand() {
        mTested.onUri(mUri).execute();

        return null;
    }

    @Override
    protected Map<String, Object> executeAsyncCommand(int token, DeleteCommand.Callback callback) {
        mTested.onUri(mUri).executeAsync(token, callback);

        return null;
    }
}
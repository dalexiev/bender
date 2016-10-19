package bg.dalexiev.bender.content;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BatchCommandTest extends ResolverCommandTestBase<BatchCommand.Callback, BatchCommand> {

    private static final String AUTHORITY = "testAuthority";

    @Spy
    private ArrayList<ContentProviderOperation> mOperations;

    @Override
    protected BatchCommand.Callback createCallback() {
        return mock(BatchCommand.Callback.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullAuthority() {
        mTested.withAuthority(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullInsert() {
        mTested.addInsert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullDelete() {
        mTested.addDelete(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullUpdate() {
        mTested.addUpdate(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenTryingToSetUri() {
        mTested.onUri(mUri);
    }

    @Override
    @Test
    @Ignore
    public void shouldThrowWhenNullUri() {
        // ignored
    }

    @Override
    @Test
    @Ignore
    public void shouldNotNotifyObservers() {
        // ignored - no Uri here.
    }

    @Nullable
    @Override
    protected Map<String, Object> executeCommand() {
        mTested
                .withAuthority(AUTHORITY)
                .execute();

        final Map<String, Object> map = new HashMap<>();
        map.put("authority", AUTHORITY);
        return map;
    }

    @Nullable
    @Override
    protected Map<String, Object> executeAsyncCommand(int token, BatchCommand.Callback callback) {
        mTested
                .withAuthority(AUTHORITY)
                .executeAsync(token, callback);

        final Map<String, Object> map = new HashMap<>();
        map.put("authority", AUTHORITY);
        return map;
    }

    @Override
    protected void verifyContentResolverMethodCalled(@NonNull InOrder executionOrder, @Nullable Map<String, Object> executionParams) throws RemoteException, OperationApplicationException {
        final String expectedAuthority = (String) executionParams.get("authority");
        executionOrder.verify(mContentResolver).applyBatch(eq(expectedAuthority), any(ArrayList.class));
    }

    @Override
    protected void verifyCallbackCalled(Message msg, BatchCommand.Callback callback) {
        verify(callback).onBatchComplete(eq(msg.what), eq((ContentProviderResult[]) msg.obj));
    }

    @Override
    protected Message createCallbackMessage() {
        final Message message = new Message();
        message.what = 1;
        message.obj = new ContentProviderResult[0];
        return message;
    }

    @NonNull
    @Override
    protected BatchCommand createTested(@NonNull BaseResolverCommand.WorkerHandler workerHandler, @NonNull ContentResolver contentResolver) {
        return new BatchCommand(workerHandler, contentResolver, mOperations);
    }
}

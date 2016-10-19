package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public abstract class ResolverCommandTestBase<C extends BaseResolverCommand.Callback, T extends BaseResolverCommand<?, C, T>> {

    protected T mTested;

    private C mCallback;

    @Mock
    protected BaseResolverCommand.WorkerHandler mWorkerHandler;

    @Mock
    protected ContentResolver mContentResolver;

    @Mock
    protected Uri mUri;

    @Before
    public void setUp() {
        final Uri.Builder uriBuilder = mock(Uri.Builder.class);
        doReturn(uriBuilder).when(uriBuilder).appendQueryParameter(anyString(), anyString());
        doReturn(mUri).when(uriBuilder).build();

        doReturn(uriBuilder).when(mUri).buildUpon();

        mTested = createTested(mWorkerHandler, mContentResolver);
        mCallback = createCallback();
    }

    protected abstract C createCallback();

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullUri() {
        mTested.onUri(null);
    }

    @Test
    public void shouldExecute() throws Exception {
        final Map<String, Object> params = executeCommand();

        verifyZeroInteractions(mWorkerHandler);
        InOrder executionOrder = inOrder(mContentResolver);
        verifyContentResolverMethodCalled(executionOrder, params);
    }

    @Nullable
    protected abstract Map<String, Object> executeCommand();

    @Test
    public void shouldExecuteAsync() throws Exception {
        providedAsyncMessageSent();

        final Map<String, Object> params = executeAsyncCommand(1, mCallback);

        final InOrder executionOrder = inOrder(mWorkerHandler, mContentResolver);
        executionOrder.verify(mWorkerHandler).obtainMessage(eq(1), any());
        executionOrder.verify(mWorkerHandler).sendMessage(any(Message.class));
        verifyContentResolverMethodCalled(executionOrder, params);
    }

    @Nullable
    protected abstract Map<String, Object> executeAsyncCommand(int token, C callback);

    protected abstract void verifyContentResolverMethodCalled(@NonNull InOrder executionOrder,
                                                              @Nullable Map<String, Object> executionParams) throws Exception;

    @Test
    public void shouldNotifyCallback() {
        final Message msg = createCallbackMessage();
        doReturn(msg).when(mWorkerHandler).obtainMessage(anyInt(), any());

        executeAsyncCommand(1, mCallback);
        mTested.handleMessage(msg);

        verifyCallbackCalled(msg, mCallback);
    }

    protected abstract void verifyCallbackCalled(Message msg, C callback);

    protected abstract Message createCallbackMessage();

    @Test
    public void shouldCancel() {
        final int token = 1;

        providedAsyncMessageSent();

        executeAsyncCommand(token, mCallback);
        mTested.cancel();

        verify(mWorkerHandler).removeMessages(eq(token));
    }

    @Test
    public void shouldNotNotifyObservers() {
        mTested.cancelObserverNotification();
        executeCommand();

        verify(mUri.buildUpon()).appendQueryParameter(eq(DatabaseContentProvider.PARAM_SHOULD_NOTIFY), eq("false"));
    }

    @Test
    public void shouldNotifyObservers() {
        executeCommand();

        verify(mUri.buildUpon(), never()).appendQueryParameter(eq(DatabaseContentProvider.PARAM_SHOULD_NOTIFY), eq("false"));
    }

    @NonNull
    protected abstract T createTested(@NonNull BaseResolverCommand.WorkerHandler workerHandler,
                                      @NonNull ContentResolver contentResolver);

    protected void providedAsyncMessageSent() {
        final Message message = mock(Message.class);
        doReturn(message).when(mWorkerHandler).obtainMessage(anyInt(), any());

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                mWorkerHandler.sendMessage(message);
                return null;
            }
        }).when(message).sendToTarget();

        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                mWorkerHandler.handleMessage((Message) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(mWorkerHandler).sendMessage(any(Message.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                mTested.executeResolverCommand(mContentResolver);
                return null;
            }
        }).when(mWorkerHandler).handleMessage(any(Message.class));

        doReturn("Mocked Handler").when(mWorkerHandler).toString();
    }

    public static class MockEntity {

        private final long mId;
        private final String mName;

        public MockEntity(long id, String name) {
            mId = id;
            mName = name;
        }


        @Override
        public boolean equals(Object o) {
            if (o instanceof MockEntity) {
                MockEntity other = (MockEntity) o;

                boolean areEqual = mId == other.mId;
                areEqual = areEqual && mName.equals(other.mName);
                return areEqual;
            }

            return false;
        }
    }
}
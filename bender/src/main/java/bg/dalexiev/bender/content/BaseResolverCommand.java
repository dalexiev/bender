package bg.dalexiev.bender.content;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.lang.ref.WeakReference;

import bg.dalexiev.bender.util.Preconditions;

/**
 * A skeleton implementation of a resolver command that can be executed both synchronously and asynchronously.
 *
 * @param <T> the type of the result, returned by the command
 * @param <C> the type of the callback that can be registered on the command
 * @param <RC> the concrete type of the command
 *
 * @author danail.alexiev
 * @since 1.0
 */
abstract class BaseResolverCommand<T, C extends BaseResolverCommand.Callback, RC extends BaseResolverCommand<T, C, RC>>
        implements Handler.Callback {

    private static Looper sLooper;

    private final WeakReference<ContentResolver> mResolverRef;
    private WeakReference<C> mCallbackRef;

    private final WorkerHandler mWorkerHandler;

    private Uri mUri;
    private int mToken;

    protected BaseResolverCommand(@NonNull ContentResolver contentResolver) {
        Preconditions.argumentNotNull(contentResolver, "ContentResolver can't be null");

        synchronized (BaseResolverCommand.class) {
            if (sLooper == null) {
                HandlerThread workerThread = new HandlerThread("ResolverCommandWorker");
                workerThread.start();

                sLooper = workerThread.getLooper();
            }
        }

        //noinspection unchecked
        mWorkerHandler = new WorkerHandler(sLooper, this);
        mResolverRef = new WeakReference<>(contentResolver);
    }

    @VisibleForTesting
    BaseResolverCommand(WorkerHandler workerHandler, ContentResolver resolver) {
        mWorkerHandler = workerHandler;
        mResolverRef = new WeakReference<>(resolver);
    }

    /**
     * Specify the content URI to execute the command against.
     *
     * <p>
     * Must be called before trying to use {@link #execute()} or {@link #executeAsync(int, Callback)}
     * </p>
     *
     * @param uri required. The content URI to target.
     * @return the current instance.
     * @since 1.0
     */
    @NonNull
    public RC onUri(@NonNull Uri uri) {
        Preconditions.argumentNotNull(uri, "Uri can't be null");

        mUri = uri;

        //noinspection unchecked
        return (RC) this;
    }

    /**
     * Execute the {@code ContentResolver} command on the current thread.
     *
     * @return the result of the command. May by {@code null}.
     * @throws IllegalStateException if no URI has been set by calling {@link #onUri(Uri)}
     * @since 1.0
     */
    @Nullable
    public T execute() {
        validateStatePreExecute();

        final ContentResolver contentResolver = mResolverRef.get();
        if (contentResolver == null) {
            return null;
        }

        return executeResolverCommand(contentResolver);
    }

    /**
     * Execute the {@code ContentResolver} command on a worker thread.
     *
     * @param token a uniquer identifier for the command.
     * @param callback optional. A callback that will be notified on the current thread when the operation completes.
     * @throws IllegalStateException if no URI has been set by calling {@link #onUri(Uri)}
     * @since 1.0
     */
    public void executeAsync(int token, @Nullable C callback) {
        validateStatePreExecute();

        mToken = token;

        if (callback != null) {
            mCallbackRef = new WeakReference<>(callback);
        }

        final Message message = mWorkerHandler.obtainMessage(token, new Handler(this));
        message.sendToTarget();
    }

    /**
     * Try ot cancel the current operation before it has been completed. Cancellation is not guaranteed.
     *
     * @since 1.0
     */
    public void cancel() {
        mWorkerHandler.removeMessages(mToken);
    }

    /**
     * Get the currently set URI
     *
     * @return the target URI
     * @since 1.0
     */
    @Nullable
    protected Uri getUri() {
        return mUri;
    }

    /**
     * Validate the state of the command before execution.
     *
     * @throws IllegalStateException if the command can't be executed with the current state.
     * @since 1.0
     */
    @CallSuper
    protected void validateStatePreExecute() {
        Preconditions.stateNotNull(mUri, "Uri not set. Did you call onUri()?");
    }

    /**
     * Execute the required command on the provided {@code ContentResolver}.
     *
     * @param contentResolver required. The current {@code ContentResolver}.
     * @return the result of the operation. May be {@code null}
     * @since 1.0
     */
    @Nullable
    protected abstract T executeResolverCommand(@NonNull ContentResolver contentResolver);

    @Override
    public boolean handleMessage(Message msg) {
        if (mCallbackRef == null) {
            return true;
        }
        final C callback = mCallbackRef.get();
        if (callback == null) {
            return true;
        }

        //noinspection unchecked
        notifyCallback(callback, msg.what, (T) msg.obj);
        return true;
    }

    /**
     * Notify the registered callback that the operation has been successfully executed on a background thread.
     *
     * @param callback required. The registered callback.
     * @param token the unique identified of the operation.
     * @param result optional. The result of the operation.
     * @since 1.0
     */
    protected abstract void notifyCallback(@NonNull C callback, int token, @Nullable T result);

    /**
     * A handler used to communicate with the worker thread that executes commands.
     *
     * @param <T> the type of the command result
     *
     * @author danail.alexiev
     * @since 1.0
     */
    @VisibleForTesting
    static class WorkerHandler<T> extends Handler {

        private final WeakReference<BaseResolverCommand<T, ?, ?>> mCommandRef;

        WorkerHandler(Looper looper, BaseResolverCommand<T, ?, ?> command) {
            super(looper);
            mCommandRef = new WeakReference<BaseResolverCommand<T, ?, ?>>(command);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseResolverCommand<T, ?, ?> command = mCommandRef.get();
            if (command == null) {
                return;
            }

            final ContentResolver contentResolver = command.mResolverRef.get();
            if (contentResolver == null) {
                return;
            }


            final T result = command.executeResolverCommand(contentResolver);

            final int what = msg.what;
            final Handler callback = (Handler) msg.obj;
            final Message callbackMessage = callback.obtainMessage(what, result);
            callbackMessage.sendToTarget();
        }
    }

    /**
     * A base interface for all command callbacks
     */
    protected interface Callback {

    }
}

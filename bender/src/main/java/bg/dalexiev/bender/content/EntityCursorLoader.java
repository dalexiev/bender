package bg.dalexiev.bender.content;

import android.content.Context;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;

import bg.dalexiev.bender.util.Preconditions;

/**
 */
public class EntityCursorLoader<E> extends Loader<EntityCursor<E>> implements QueryCommand.Callback<E> {

    private final QueryCommand<E> mQueryCommand;
    private final ContentObserver mObserver;
    private final int mToken;

    private EntityCursor<E> mCursor;

    private boolean mIsLoadComplete;
    private boolean mIsCancelled;

    EntityCursorLoader(Context context, @NonNull QueryCommand<E> queryCommand, int token) {
        super(context);
        Preconditions.argumentNotNull(queryCommand, "Query command is required");

        mQueryCommand = queryCommand;
        mObserver = new ForceLoadContentObserver();
        mToken = token;
    }

    @Override
    protected void onStartLoading() {
        mIsCancelled = false;
        mIsLoadComplete = false;
        if (mCursor != null) {
            deliverResult(mCursor);
            return;
        }

        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        mIsCancelled = false;
        mQueryCommand.executeAsync(mToken, this);
    }

    @Override
    protected void onStopLoading() {
        onCancelLoad();
    }

    @Override
    protected void onReset() {
        mIsCancelled = false;
        mIsLoadComplete = false;

        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    protected boolean onCancelLoad() {
        mIsCancelled = true;
        mQueryCommand.cancel();

        return !mIsLoadComplete;
    }

    @Override
    public void deliverResult(EntityCursor<E> data) {
        if (isReset()) {
            closeCursor(data);
        }

        final Cursor oldCursor = mCursor;
        mCursor = data;

        if (isStarted()) {
            super.deliverResult(mCursor);
        }

        if (oldCursor != null && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    public void onQueryComplete(int token, EntityCursor<E> cursor) {
        if (token == mToken) {
            mIsLoadComplete = true;

            if (mIsCancelled) {
                dispatchCancelled(cursor);
            } else {
                dispatchResult(cursor);
            }
        }
    }

    private void dispatchResult(EntityCursor<E> cursor) {
        if (isAbandoned()) {
            closeCursor(cursor);
            return;
        }

        cursor.registerContentObserver(mObserver);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            commitContentChanged();
        }
        deliverResult(cursor);
    }

    private void dispatchCancelled(EntityCursor<E> cursor) {
        closeCursor(cursor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            rollbackContentChanged();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            deliverCancellation();
        }
    }

    private void closeCursor(EntityCursor<E> cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

}

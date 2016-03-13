package bg.dalexiev.bender.content;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWrapper;
import android.util.SparseArray;

import bg.dalexiev.bender.mapper.CursorMapper;
import bg.dalexiev.bender.mapper.RowMapper;

/**
 */
public class EntityCursor<E> extends CursorWrapper {

    private final CursorMapper<E> mCursorMapper;

    private SparseArray<E> mCache;

    private int mPosition;

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    EntityCursor(Cursor cursor, CursorMapper<E> cursorMapper) {
        super(cursor);
        mCursorMapper = cursorMapper;

        mPosition = -1;
    }

    EntityCursor(Cursor cursor, RowMapper<E> rowMapper) {
        this(cursor, new DefaultCursorMapper<E>(rowMapper));
    }

    final void mapCursor() {
        final Cursor cursor = getWrappedCursor();
        if ((cursor == null) || (cursor.getCount() == 0)) {
            return;
        }

        mCache = mCursorMapper.mapCursor(cursor);
        cursor.moveToFirst();
    }

    public E getEntity() {
        if ((0 > mPosition) || (mPosition >= getCount())) {
            throw new CursorIndexOutOfBoundsException("Invalid cursor position: " + mPosition);
        }

        return mCache.get(mPosition);
    }

    @Override
    public int getCount() {
        if (mCache == null) {
            return 0;
        }

        return mCache.size();
    }

    @Override
    public int getPosition() {
        return mPosition;
    }

    @Override
    public boolean moveToPosition(int position) {
        if (position >= getCount()) {
            mPosition = getCount();
            return false;
        }

        if (position < 0) {
            mPosition = -1;
            return false;
        }

        if (position == mPosition) {
            return true;
        }

        mPosition = position;
        return true;
    }

    @Override
    public boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public boolean moveToNext() {
        return moveToPosition(mPosition + 1);
    }

    @Override
    public boolean moveToPrevious() {
        return moveToPosition(mPosition - 1);
    }

    @Override
    public boolean move(int offset) {
        final int newPosition = mPosition + offset;
        if (offset < 0) {
            mPosition = Math.max(-1, newPosition);
        } else {
            mPosition = Math.min(getCount(), newPosition);
        }

        return mPosition == newPosition;
    }

    @Override
    public boolean isBeforeFirst() {
        return -1 == mPosition;
    }

    @Override
    public boolean isAfterLast() {
        return getCount() == mPosition;
    }

    @Override
    public boolean isFirst() {
        return 0 == mPosition;
    }

    @Override
    public boolean isLast() {
        return (getCount() - 1) == mPosition;
    }

    @Override
    public void close() {
        super.close();
        if (mCache != null) {
            mCache.clear();
        }
    }
}

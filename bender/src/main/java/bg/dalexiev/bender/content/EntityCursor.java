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
        cursor.moveToPosition(0);
    }

    public E getEntity() {
        if ((-1 >= mPosition) || (getCount() <= mPosition)) {
            throw new CursorIndexOutOfBoundsException("Invalid cursor position " + mPosition);
        }

        final E entity = mCache.get(mPosition);
        return entity;
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
    public boolean move(int offset) {
        final int newPosition = mPosition + offset;
        if (newPosition > getCount()) {
            mPosition = getCount();
            return false;
        } else if (-1 > newPosition) {
            mPosition = -1;
            return false;
        } else {
            mPosition = newPosition;
            return true;
        }
    }

    @Override
    public boolean moveToPosition(int position) {
        if ((-1 > position) || (getCount() < position)) {
            return false;
        }

        mPosition = position;
        return true;
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
    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public boolean isLast() {
        return mPosition == getCount() - 1;
    }

    @Override
    public boolean isFirst() {
        return mPosition == 0;
    }

    @Override
    public boolean isAfterLast() {
        return mPosition >= getCount();
    }

    @Override
    public boolean isBeforeFirst() {
        return mPosition <= -1;
    }
}

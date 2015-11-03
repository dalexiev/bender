package bg.dalexiev.bender.content;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import bg.dalexiev.bender.mapper.CursorMapper;
import bg.dalexiev.bender.mapper.RowMapper;

/**
 */
class DefaultCursorMapper<E> implements CursorMapper<E> {

    private final RowMapper<E> mRowMapper;

    DefaultCursorMapper(RowMapper<E> rowMapper) {
        mRowMapper = rowMapper;
    }

    @Nullable
    @Override
    public SparseArray<E> mapCursor(@NonNull Cursor cursor) {
        final SparseArray<E> cache = new SparseArray<>(cursor.getCount());
        while (cursor.moveToNext()) {
            final int position = cursor.getPosition();
            final E entity = mRowMapper.toObject(cursor, position);
            cache.put(position, entity);
        }

        return cache;
    }
}

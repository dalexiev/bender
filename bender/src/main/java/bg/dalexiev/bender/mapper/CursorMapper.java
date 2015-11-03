package bg.dalexiev.bender.mapper;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

/**
 * Maps a query result set to a collection of objects.
 *
 * <p>
 * It is suitable to use this class when you need a more fine - grained mapping logic.
 * For example, if you have a one to many relationship and you need to map more that one cursor row to a single result
 * object.
 * </p>
 *
 * <p>
 * If you just need to map each row to an object, use {@link RowMapper}.
 * </p>
 *
 * @param <E> the type of the mapped result objects
 *
 * @author danail.alexiev
 * @since 1.0
 */
public interface CursorMapper<E> {

    /**
     * Maps the provided {@code cursor} to a {@code SparseArray} of result objects
     *
     * <p>
     * Each object should be mapped to a cursor position. The positions should have subsequent increasing values.
     * </p>
     *
     * <p>
     * Implementations should move the {@code cursor} to the requested positions in this method.
     * </p>
     *
     * @param cursor required. The raw cursor, holding the SQL result set.
     * @return a {@link SparseArray}, mapping a cursor position to a result object. Can be {@code null}.
     * @since 1.0
     */
    @Nullable
    SparseArray<E> mapCursor(@NonNull Cursor cursor);

}

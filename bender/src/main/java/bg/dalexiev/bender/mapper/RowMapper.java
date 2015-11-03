package bg.dalexiev.bender.mapper;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Maps a row of a query result set to an object.
 *
 * <p>
 * Using this class is suitable when you have a simple one to one mapping between a result set row and your entities.
 * </p>
 *
 * <p>
 * If you need a more fine - grained mapping, use {@link CursorMapper}
 * </p>
 *
 * @param <E> the type of the mapped result objects
 *
 * @author danail.alexiev
 * @since 1.0
 */
public interface RowMapper<E> {

    /**
     * Maps the provided {@code cursor} to an object.
     *
     * <p>
     * Implementations shouldn't perform any moves on the {@code cursor} in this method.
     * </p>
     *
     * @param cursor required. The raw cursor, holding the SQL result set.
     * @param rowNum the number of the row being mapped in the SQL result set.
     * @return an object, representing the data in the row being mapped. Can be {@code null}.
     * @since 1.0
     */
    @Nullable
    E toObject(@NonNull Cursor cursor, int rowNum);

}

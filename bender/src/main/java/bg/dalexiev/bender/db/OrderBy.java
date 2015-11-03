package bg.dalexiev.bender.db;

import android.support.annotation.NonNull;

import bg.dalexiev.bender.util.Preconditions;

/**
 * A helper class that builds SQL {@code orderBy} clause expressions.
 *
 * @author danail.alexiev
 * @since 1.0
 */
public final class OrderBy {

    private static final String ORDER_ASC = " asc";
    private static final String ORDER_DESC = " desc";

    private final String mExpression;

    private OrderBy(String expression) {
        mExpression = expression;
    }

    /**
     * Create a ascending ordering on {@code column}
     *
     * @param column required. The named of the column to order by.
     * @return an ascending order expression.
     * @throws IllegalArgumentException if {@code column} is null
     * @since 1.0
     */
    @NonNull
    public static OrderBy asc(@NonNull String column) {
        Preconditions.argumentNotNull(column, "Column can't be null");

        return new OrderBy(column + ORDER_ASC);
    }

    /**
     * Create a descending ordering on {@code column}
     *
     * @param column required. The named of the column to order by.
     * @return a descending order expression.
     * @throws IllegalArgumentException if {@code column} is null
     * @since 1.0
     */
    @NonNull
    public static OrderBy desc(@NonNull String column) {
        Preconditions.argumentNotNull(column, "Column can't be null");

        return new OrderBy(column + ORDER_DESC);
    }

    String getExpression() {
        return  mExpression;
    }
}

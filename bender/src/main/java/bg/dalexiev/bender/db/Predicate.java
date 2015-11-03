package bg.dalexiev.bender.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bg.dalexiev.bender.util.Preconditions;

/**
 * A helper class that build SQL {@code where} clause expressions
 *
 * @author danail.alexiev
 * @since 1.0
 */
public final class Predicate {

    private static final String OPERATOR_EQ = "=";
    private static final String OPERATOR_NOT_EQ = "<>";
    private static final String OPERATOR_GT = ">";
    private static final String OPERATOR_GE = ">=";
    private static final String OPERATOR_LT = "<";
    private static final String OPERATOR_LE = "<=";
    private static final String OPERATOR_LIKE = "like";
    private static final String OPERATOR_IS_NULL = "is null";
    private static final String OPERATOR_IS_NOT_NULL = "is not null";
    private static final String OPERATOR_BETWEEN = "between";
    private static final String OPERATOR_IN = "in";

    private static final char WILDCARD_PARAMETER = '?';

    private final StringBuilder mExpression;
    private final List<String> mArguments;


    private Predicate(@NonNull StringBuilder expression, @Nullable String... arguments) {
        mExpression = expression;

        mArguments = new ArrayList<>(5);
        if (arguments != null) {
            Collections.addAll(mArguments, arguments);
        }
    }

    /**
     * Create a complex predicate by adding another condition and applying the {@code AND} logical operator
     *
     * <p>
     * The resulting predicate will be the following: {@code expression and expression}
     * </p>
     *
     * @param predicate required. The predicate to add.
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public Predicate and(@NonNull Predicate predicate) {
        appendPredicate(predicate, " and ");

        return this;
    }

    /**
     * Create a complex predicate by adding another condition and applying the {@code OR} logical operator
     *
     * <p>
     * The resulting predicate will be the following: {@code expression or expression}
     * </p>
     *
     * @param predicate required. The predicate to add.
     * @return the current instance
     * @since 1.0
     */
    @NonNull
    public Predicate or(@NonNull Predicate predicate) {
        appendPredicate(predicate, " or ");

        return this;
    }

    private void appendPredicate(@NonNull Predicate predicate, @NonNull String logicalOperator) {
        Preconditions.argumentNotNull(predicate, "Predicate can't be null");

        mExpression.append(logicalOperator).append(predicate.mExpression);
        mArguments.addAll(predicate.mArguments);
    }

    @NonNull
    String getExpression() {
        return mExpression.toString();
    }

    @Nullable
    String[] getArguments() {
        if (mArguments == null) {
            return null;
        }

        return mArguments.toArray(new String[mArguments.size()]);
    }

    /**
     * Creates a SQL expression of the type {@code column = ?}
     *
     * @param column required. The name of the column for the expression.
     * @param value  required. The value of the parameter. Will be bound when executing a database operation.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} or {@code value} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate eq(@NonNull String column, @NonNull String value) {
        validateColumnAndValue(column, value);

        return new Predicate(buildUnaryExpression(column, OPERATOR_EQ), value);
    }

    /**
     * Creates a SQL expression of the type {@code column <> ?}
     *
     * @param column required. The name of the column for the expression.
     * @param value  required. The value of the parameter. Will be bound when executing a database operation.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} or {@code value} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate notEq(@NonNull String column, @NonNull String value) {
        validateColumnAndValue(column, value);

        return new Predicate(buildUnaryExpression(column, OPERATOR_NOT_EQ), value);
    }

    /**
     * Creates a SQL expression of the type {@code column > ?}
     *
     * @param column required. The name of the column for the expression.
     * @param value  required. The value of the parameter. Will be bound when executing a database operation.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} or {@code value} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate gt(@NonNull String column, @NonNull String value) {
        validateColumnAndValue(column, value);

        return new Predicate(buildUnaryExpression(column, OPERATOR_GT), value);
    }

    /**
     * Creates a SQL expression of the type {@code column >= ?}
     *
     * @param column required. The name of the column for the expression.
     * @param value  required. The value of the parameter. Will be bound when executing a database operation.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} or {@code value} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate ge(@NonNull String column, @NonNull String value) {
        validateColumnAndValue(column, value);

        return new Predicate(buildUnaryExpression(column, OPERATOR_GE), value);
    }

    /**
     * Creates a SQL expression of the type {@code column < ?}
     *
     * @param column required. The name of the column for the expression.
     * @param value  required. The value of the parameter. Will be bound when executing a database operation.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} or {@code value} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate lt(@NonNull String column, @NonNull String value) {
        validateColumnAndValue(column, value);

        return new Predicate(buildUnaryExpression(column, OPERATOR_LT), value);
    }

    /**
     * Creates a SQL expression of the type {@code column <= ?}
     *
     * @param column required. The name of the column for the expression.
     * @param value  required. The value of the parameter. Will be bound when executing a database operation.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} or {@code value} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate le(@NonNull String column, @NonNull String value) {
        validateColumnAndValue(column, value);

        return new Predicate(buildUnaryExpression(column, OPERATOR_LE), value);
    }

    /**
     * Creates a SQL expression of the type {@code column like ?}
     *
     * @param column required. The name of the column for the expression.
     * @param value  required. The value of the parameter. Will be bound when executing a database operation. Should
     *               contain any required wildcards.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} or {@code value} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate like(@NonNull String column, @NonNull String value) {
        validateColumnAndValue(column, value);

        return new Predicate(buildUnaryExpression(column, OPERATOR_LIKE), value);
    }

    /**
     * Creates a SQL expression of the type {@code column is null}
     *
     * @param column required. The name of the column for the expression.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate isNull(@NonNull String column) {
        Preconditions.argumentNotNull(column, "Column can't be null");

        return new Predicate(buildBaseExpression(column, OPERATOR_IS_NULL), (String[]) null);
    }

    /**
     * Creates a SQL expression of the type {@code column is not null}
     *
     * @param column required. The name of the column for the expression.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate isNotNull(@NonNull String column) {
        Preconditions.argumentNotNull(column, "Column can't be null");

        return new Predicate(buildBaseExpression(column, OPERATOR_IS_NOT_NULL), (String[]) null);
    }

    /**
     * Creates a SQL expression of the type {@code column between ? and ?}
     *
     * @param column required. The name of the column for the expression.
     * @param start  required. The value bound to the first expression parameters on execution.
     * @param end    required. The value bound to the second expression parameters on execution.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column}, {@code start} or {@code end} is {@code null}
     * @since 1.0
     */
    @NonNull
    public static Predicate between(@NonNull String column, @NonNull String start, @NonNull String end) {
        Preconditions.argumentNotNull(column, "Column can't be null");
        Preconditions.argumentNotNull(start, "Start can't be null");
        Preconditions.argumentNotNull(end, "End can't be null");

        return new Predicate(buildBetweenExpression(column), start, end);
    }

    /**
     * Create a SQL expression of the type {@code column in (?, .. ?)}.
     *
     * <p>To increase performance an expression of the type {@code column = ?} will be created if there is only one
     * value passed to this method</p>
     *
     * @param column required. The name of the column for the expression.
     * @param values required. Must not be empty. The values bound to the expression parameters on execution.
     * @return the resulting expression.
     * @throws IllegalArgumentException if {@code column} or {@code values} is {@code null} or if {@code values} is
     *                                  empty
     * @since 1.0
     */
    @NonNull
    public static Predicate in(@NonNull String column, @NonNull String... values) {
        Preconditions.argumentNotNull(column, "Column can't be null");
        Preconditions.argumentNotNull(values, "Arguments can't be null");
        Preconditions.argumentCondition(values.length == 0, "Arguments can't be empty");

        return new Predicate(buildInExpression(column, values.length), values);
    }

    private static void validateColumnAndValue(String column, String value) {
        Preconditions.argumentNotNull(column, "Column can't be null");
        Preconditions.argumentNotNull(value, "Value can't be null");
    }

    @NonNull
    private static StringBuilder buildUnaryExpression(@NonNull String column, @NonNull String operator) {
        return buildBaseExpression(column, operator).append(' ').append(WILDCARD_PARAMETER);
    }

    @NonNull
    private static StringBuilder buildBetweenExpression(@NonNull String column) {
        return buildBaseExpression(column, OPERATOR_BETWEEN).append(" ? and ?");
    }

    @NonNull
    private static StringBuilder buildInExpression(@NonNull String column, int parameterCount) {
        if (parameterCount == 1) {
            return buildUnaryExpression(column, OPERATOR_EQ);
        }

        final StringBuilder expressionBuilder = buildBaseExpression(column, OPERATOR_IN).append(" (");
        for (int i = 0; i < parameterCount; i++) {
            expressionBuilder.append(WILDCARD_PARAMETER);
            if (i != parameterCount - 1) {
                expressionBuilder.append(", ");
            }
        }
        expressionBuilder.append(')');
        return expressionBuilder;
    }

    @NonNull
    private static StringBuilder buildBaseExpression(@NonNull String column, @NonNull String operator) {
        final StringBuilder expressionBuilder = new StringBuilder();
        expressionBuilder.append(column).append(' ').append(operator);
        return expressionBuilder;
    }

}

package bg.dalexiev.bender.util;

/**
 * A helper class for evaluating preconditions
 *
 * @author danail.alexiev
 * @since 1.0
 */
public final class Preconditions {

    private Preconditions() {
        // deny instantiation
    }

    /**
     * Throws a {@code NullPointerException} with {@code message} if {@code reference} is {@code null}
     *
     * @param reference the reference to perform the null check on
     * @param message the message of the exception that will be thrown if {@code reference} is {@code null}
     * @param <R> the concrete type of the reference
     * @throws NullPointerException if {@code reference} is {@code null}
     * @since 1.0
     */
    public static <R> void notNull(R reference, String message) {
        notNull(reference, new NullPointerException(message));
    }

    /**
     * Throws a {@code IllegalStateException} with {@code message} if {@code reference} is {@code null}
     *
     * @param reference the reference to perform the null check on
     * @param message the message of the exception that will be thrown if {@code reference} is {@code null}
     * @param <R> the concrete type of the reference
     * @throws IllegalStateException if {@code reference} is {@code null}
     * @since 1.0
     */
    public static <R> void stateNotNull(R reference, String message) {
        notNull(reference, new IllegalStateException(message));
    }

    /**
     * Throws a {@code IllegalArgumentException} with {@code message} if {@code reference} is {@code null}
     *
     * @param reference the reference to perform the null check on
     * @param message the message of the exception that will be thrown if {@code reference} is {@code null}
     * @param <R> the concrete type of the reference
     * @throws IllegalArgumentException if {@code reference} is {@code null}
     * @since 1.0
     */
    public static <R> void argumentNotNull(R reference, String message) {
        notNull(reference, new IllegalArgumentException(message));
    }

    /**
     * Throws a {@code IllegalStateException} with {@code message} if {@code condition} is {@code true}
     *
     * @param condition the condition to be evaluated
     * @param message the message of the exception that will be thrown if {@code condition} is {@code true}
     * @throws IllegalStateException if {@code condition} is {@code true}
     * @since 1.0
     */
    public static void stateCondition(boolean condition, String message) {
        condition(condition, new IllegalStateException(message));
    }

    /**
     * Throws a {@code IllegalArgumentException} with {@code message} if {@code condition} is {@code true}
     *
     * @param condition the condition to be evaluated
     * @param message the message of the exception that will be thrown if {@code condition} is {@code true}
     * @throws IllegalArgumentException if {@code condition} is {@code true}
     * @since 1.0
     */
    public static void argumentCondition(boolean condition, String message) {
        condition(condition, new IllegalArgumentException(message));
    }

    private static <R> void notNull(R reference, RuntimeException exception) {
        if (reference == null) {
            throw exception;
        }
    }

    private static void condition(boolean condition, RuntimeException exception) {
        if (condition) {
            throw exception;
        }
    }
}

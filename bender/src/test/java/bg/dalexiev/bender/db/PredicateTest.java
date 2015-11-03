package bg.dalexiev.bender.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PredicateTest {

    @Test
    public void shouldCreateEqPredicate() {
        final String column = "test";
        final String value = "foo";

        final String expectedExpression = column + " = ?";
        final String[] expectedArgs = {value};

        final Predicate predicate = Predicate.eq(column, value);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInEqPredicate() {
        Predicate.eq(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInEqPredicate() {
        Predicate.eq("test", null);
    }

    @Test
    public void shouldCreateNotEqPredicate() {
        final String column = "test";
        final String value = "foo";

        final String expectedExpression = column + " <> ?";
        final String[] expectedArgs = {value};

        final Predicate predicate = Predicate.notEq(column, value);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInNotEqPredicate() {
        Predicate.notEq(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInNotEqPredicate() {
        Predicate.notEq("test", null);
    }

    @Test
    public void shouldCreateGtPredicate() {
        final String column = "test";
        final String value = "foo";

        final String expectedExpression = column + " > ?";
        final String[] expectedArgs = {value};

        final Predicate predicate = Predicate.gt(column, value);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInGtPredicate() {
        Predicate.gt(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInGtPredicate() {
        Predicate.gt("test", null);
    }

    @Test
    public void shouldCreateNotGePredicate() {
        final String column = "test";
        final String value = "foo";

        final String expectedExpression = column + " >= ?";
        final String[] expectedArgs = {value};

        final Predicate predicate = Predicate.ge(column, value);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInGePredicate() {
        Predicate.ge(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInGePredicate() {
        Predicate.ge("test", null);
    }

    @Test
    public void shouldCreateLtPredicate() {
        final String column = "test";
        final String value = "foo";

        final String expectedExpression = column + " < ?";
        final String[] expectedArgs = {value};

        final Predicate predicate = Predicate.lt(column, value);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInLtPredicate() {
        Predicate.lt(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInLtPredicate() {
        Predicate.lt("test", null);
    }

    @Test
    public void shouldCreateLePredicate() {
        final String column = "test";
        final String value = "foo";

        final String expectedExpression = column + " <= ?";
        final String[] expectedArgs = {value};

        final Predicate predicate = Predicate.le(column, value);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInLePredicate() {
        Predicate.le(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInLePredicate() {
        Predicate.le("test", null);
    }

    @Test
    public void shouldCreateLikePredicate() {
        final String column = "test";
        final String value = "foo%";

        final String expectedExpression = column + " like ?";
        final String[] expectedArgs = {value};

        final Predicate predicate = Predicate.like(column, value);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInLikePredicate() {
        Predicate.like(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInLikePredicate() {
        Predicate.like("test", null);
    }

    @Test
    public void shouldCreateIsNullPredicate() {
        final String column = "test";
        final String expectedExpression = column + " is null";

        final Predicate predicate = Predicate.isNull(column);

        assertEquals(expectedExpression, predicate.getExpression());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInIsNullPredicate() {
        Predicate.isNull(null);
    }

    @Test
    public void shouldCreateIsNotNullPredicate() {
        final String column = "test";
        final String expectedExpression = column + " is not null";

        final Predicate predicate = Predicate.isNotNull(column);

        assertEquals(expectedExpression, predicate.getExpression());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullValueInIsNotNullPredicate() {
        Predicate.isNotNull(null);
    }

    @Test
    public void shouldCreateBetweenPredicate() {
        final String column = "test";
        final String start = "bar";
        final String end = "foo";

        final String expectedExpression = column + " between ? and ?";
        final String[] expectedArgs = {start, end};

        final Predicate predicate = Predicate.between(column, start, end);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInBetweenPredicate() {
        Predicate.between(null, "foo", "bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullStartValueInBetweenPredicate() {
        Predicate.between("test", null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullEndValueInBetweenPredicate() {
        Predicate.between("test", "foo", null);
    }

    @Test
    public void shouldCreateInPredicate() {
        final String column = "test";

        final String expectedExpression = column + " in (?, ?, ?)";
        final String[] expectedArgs = {"1", "2", "3"};

        final Predicate predicate = Predicate.in(column, expectedArgs);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test
    public void shouldCreateEqWhenOnlyOneInValue() {
        final String column = "test";

        final String expectedExpression = column + " = ?";
        final String[] expectedArgs = {"foo"};

        final Predicate predicate = Predicate.in(column, expectedArgs);

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullColumnInInPredicate() {
        Predicate.in(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullValuesInInPredicate() {
        Predicate.in("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenEmptyValuesInInPredicate() {
        Predicate.in("test", new String[]{});
    }

    @Test
    public void shouldAddPredicateWithAnd() {
        final String column1 = "foo";
        final String arg1 = "1";

        final String column2 = "bar";
        final String arg2 = "baz";

        final String expectedExpression = column1 + " = ? and " + column2 + " = ?";
        final String[] expectedArgs = {arg1, arg2};

        final Predicate predicate = Predicate.eq(column1, arg1).and(Predicate.eq(column2, arg2));

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullAndPredicate() {
        final String column1 = "foo";
        final String arg1 = "1";

        Predicate.eq(column1, arg1).and(null);
    }

    @Test
    public void shouldAddPredicateWithOr() {
        final String column1 = "foo";
        final String arg1 = "1";

        final String column2 = "bar";
        final String arg2 = "baz";

        final String expectedExpression = column1 + " = ? or " + column2 + " = ?";
        final String[] expectedArgs = {arg1, arg2};

        final Predicate predicate = Predicate.eq(column1, arg1).or(Predicate.eq(column2, arg2));

        assertEquals(expectedExpression, predicate.getExpression());
        assertTrue(Arrays.equals(expectedArgs, predicate.getArguments()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNullOrPredicate() {
        final String column1 = "foo";
        final String arg1 = "1";

        Predicate.eq(column1, arg1).or(null);
    }
}
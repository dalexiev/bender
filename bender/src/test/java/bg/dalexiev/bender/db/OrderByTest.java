package bg.dalexiev.bender.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class OrderByTest {

    @Test
    public void shouldCreateAscOrder() {
        final String expected = "test asc";

        final String actual = OrderBy.asc("test").getExpression();

        assertEquals(expected, actual);
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInAsc() {
        OrderBy.asc(null);
    }

    @Test
    public void shouldCreateDescOrder() {
        final String expected = "test desc";

        final String actual = OrderBy.desc("test").getExpression();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullColumnInDesc() {
        OrderBy.desc(null);
    }
}
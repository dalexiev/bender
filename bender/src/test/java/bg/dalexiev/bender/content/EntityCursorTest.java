package bg.dalexiev.bender.content;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.SparseArray;

import bg.dalexiev.bender.mapper.CursorMapper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class EntityCursorTest {

    private EntityCursor<ResolverCommandTestBase.MockEntity> mTested;

    @Mock
    private CursorMapper<ResolverCommandTestBase.MockEntity> mCursorMapper;

    @Mock
    private Cursor mCursor;

    @Before
    public void setUp() {
        mTested = spy(new EntityCursor<>(mCursor, mCursorMapper));

        doReturn(mCursor).when(mTested).getWrappedCursor();
    }

    @Test
    public void shouldDelegateMapCursor() {
        doReturn(5).when(mCursor).getCount();

        mTested.mapCursor();

        verify(mCursorMapper).mapCursor(any(Cursor.class));
    }

    public void shouldNotMapEmptyCursor() {
        doReturn(0).when(mCursor).getCount();

        mTested.mapCursor();

        verifyZeroInteractions(mCursorMapper);
    }

    @Test
    public void shouldHaveZeroEntities() {
        int actualCount = mTested.getCount();
        assertEquals(0, actualCount);
    }

    @Test
    public void shouldHaveCorrectEntityCount() {
        final int expectedCount = 3;
        providedHasCount(3);

        final int actualCount = mTested.getCount();

        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void shouldGetEntity() {
        doReturn(1).when(mCursor).getCount();

        final ResolverCommandTestBase.MockEntity mockEntity = new ResolverCommandTestBase.MockEntity(1L, "test");

        final SparseArray<ResolverCommandTestBase.MockEntity> mockCache = mock(SparseArray.class);
        doReturn(mockEntity).when(mockCache).get(eq(0));
        doReturn(1).when(mockCache).size();

        doReturn(mockCache).when(mCursorMapper).mapCursor(any(Cursor.class));

        mTested.mapCursor();

        if (!mTested.moveToFirst()) {
            throw new AssertionError("Cursor empty");
        }

        ResolverCommandTestBase.MockEntity actualEntity = mTested.getEntity();

        assertEquals(mockEntity, actualEntity);
    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void shouldThrowOnGetEntityWhenBeforeFirst() {
        mTested.moveToPosition(-1);
        mTested.getEntity();
    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void shouldThrowOnGetEntityWhenAfterLast() {
        providedHasCount(1);

        mTested.moveToPosition(1);
        mTested.getEntity();
    }

    @Test
    public void shouldMoveToBeforeFirst() {
        providedHasCount(5);

        final boolean fullSuccess = mTested.move(-3);

        assertFalse(fullSuccess);
        assertEquals(-1, mTested.getPosition());
    }

    @Test
    public void shouldMoveToAfterLast() {
        final int expectedCount = 3;
        providedHasCount(expectedCount);

        final boolean fullSuccess = mTested.move(100);

        assertFalse(fullSuccess);
        assertEquals(expectedCount, mTested.getPosition());
    }

    @Test
    public void shouldMakeValidMove() {
        providedHasCount(4);

        final boolean fullSuccess = mTested.move(1);

        assertTrue(fullSuccess);
        assertEquals(0, mTested.getPosition());
    }

    @Test
    public void shouldNotMoveToPositionIfBeforeFirst() {
        providedHasCount(2);

        final boolean success = mTested.moveToPosition(-5);

        assertFalse(success);
        assertEquals(-1, mTested.getPosition());
    }

    @Test
    public void shouldNotMoveToPositionIfAfterLast() {
        providedHasCount(3);

        final boolean success = mTested.moveToPosition(5);

        assertFalse(success);
        assertEquals(mTested.getCount(), mTested.getPosition());
    }

    @Test
    public void shouldMoveToPosition() {
        providedHasCount(3);

        final int expectedPosition = 1;
        final boolean success = mTested.moveToPosition(expectedPosition);

        assertTrue(success);
        assertEquals(expectedPosition, mTested.getPosition());
    }

    @Test
    public void shouldMoveToNext() {
        providedHasCount(3);

        final boolean success = mTested.moveToNext();

        assertTrue(success);
        assertEquals(0, mTested.getPosition());
    }

    @Test
    public void shouldMoveToPrevious() {
        providedHasCount(3);
        mTested.moveToPosition(2);

        final boolean success = mTested.moveToPrevious();

        assertTrue(success);
        assertEquals(1, mTested.getPosition());
    }

    @Test
    public void shouldMoveToFirst() {
        providedHasCount(3);
        mTested.moveToPosition(1);

        final boolean success = mTested.moveToFirst();

        assertTrue(success);
        assertEquals(0, mTested.getPosition());
    }

    @Test
    public void shouldMoveToLast() {
        final int expectedCount = 4;
        providedHasCount(4);

        final boolean success = mTested.moveToLast();

        assertTrue(success);
        assertEquals(expectedCount - 1, mTested.getPosition());
    }

    @Test
    public void shouldBeFirst() {
        providedHasCount(1);
        mTested.moveToFirst();

        assertTrue(mTested.isFirst());
    }

    @Test
    public void shouldNotBeFirst() {
        providedHasCount(3);
        mTested.moveToLast();

        assertFalse(mTested.isFirst());
    }

    @Test
    public void shouldBeLast() {
        providedHasCount(3);
        mTested.moveToLast();

        assertTrue(mTested.isLast());
    }

    @Test
    public void shouldNotBeLast() {
        providedHasCount(3);
        mTested.moveToFirst();

        assertFalse(mTested.isLast());
    }

    @Test
    public void shouldBeBeforeFirst() {
        providedHasCount(3);

        assertTrue(mTested.isBeforeFirst());
    }

    @Test
    public void shouldNotBeBeforeFirst() {
        providedHasCount(2);
        mTested.moveToFirst();

        assertFalse(mTested.isBeforeFirst());
    }

    @Test
    public void shouldBeAfterLast() {
        providedHasCount(4);
        mTested.moveToPosition(mTested.getCount());

        assertTrue(mTested.isAfterLast());
    }

    @Test
    public void shouldNotBeAfterLast() {
        providedHasCount(1);

        assertFalse(mTested.isAfterLast());
    }

    private void providedHasCount(int expectedCount) {
        doReturn(expectedCount).when(mCursor).getCount();

        final SparseArray<ResolverCommandTestBase.MockEntity> mockCache = mock(SparseArray.class);
        doReturn(expectedCount).when(mockCache).size();

        doReturn(mockCache).when(mCursorMapper).mapCursor(any(Cursor.class));

        mTested.mapCursor();
    }


}
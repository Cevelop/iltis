package ch.hsr.ifs.iltis.core.tests.arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.arrays.ArrayUtil;
import ch.hsr.ifs.iltis.core.collections.CollectionUtil;


public class ArrayUtilTest {

    @Test
    public void testRemoveAndTrimReferenceTypes() {
        Integer[] expected = { 1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] input = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] output = ArrayUtil.removeAndTrim(input, 5);
        assertArrayEquals(expected, output);
    }

    @Test
    public void testCopyWithoutMatchingDefaultCase() {
        Integer[] expected = { 1, 2, 3, 4, 6, 7, 8, 9, 11, 12 };
        Integer[] input = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] actual = ArrayUtil.copyWithoutMatching(input, i -> i % 5 == 0);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testCopyWithoutMatchingNoneMatches() {
        Integer[] expected = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] input = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] actual = ArrayUtil.copyWithoutMatching(input, i -> i > 20);
        assertArrayEquals(expected, actual);
        assertNotSame(input, actual);
    }

    @Test
    public void testCopyWithoutValuesAtIndicesBaseCase() {
        Integer[] expected = { 1, 2, 4, 5, 6, 7, 8, 9, 10, 11 };
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] actual = ArrayUtil.copyWithoutValuesAtIndices(input, CollectionUtil.set(0, 3, 12));
        assertArrayEquals(expected, actual);
        assertNotSame(input, actual);
    }

    @Test
    public void testCopyWithoutValuesAtIndicesSubsequent() {
        Integer[] expected = { 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] actual = ArrayUtil.copyWithoutValuesAtIndices(input, CollectionUtil.set(0, 1, 2, 3));
        assertArrayEquals(expected, actual);
        assertNotSame(input, actual);
    }

    @Test
    public void testCopyWithoutValuesAtIndicesEmpty() {
        Integer[] expected = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        Integer[] actual = ArrayUtil.copyWithoutValuesAtIndices(input, CollectionUtil.set());
        assertArrayEquals(expected, actual);
        assertNotSame(input, actual);
    }

    @Test
    public void testReplaceMatchesWithNullAndMoveToBackBaseCase() {
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] expected = { 0, 1, 3, 4, 5, 7, 8, 10, null, null, null };
        ArrayUtil.replaceMatchesWithNullAndMoveToBack(input, CollectionUtil.array(2, 6, 9));
        assertArrayEquals(expected, input);
    }

    @Test
    public void testReplaceMatchesWithNullAndMoveToBackMultiple() {
        Integer[] input = { 2, 2, 2, 3, 3, 3, 4, 4, 4 };
        Integer[] expected = { 3, 3, 3, 4, 4, 4, null, null, null };
        ArrayUtil.replaceMatchesWithNullAndMoveToBack(input, CollectionUtil.array(2, 6, 9));
        assertArrayEquals(expected, input);
    }

    @Test
    public void testReplaceIndicesWithNullAndMoveToBackBaseCase() {
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] expected = { 0, 2, 5, 6, 7, 8, 9, 10, null, null, null };
        ArrayUtil.replaceIndicesWithNullAndMoveToBack(input, CollectionUtil.set(1, 3, 4));
        assertArrayEquals(expected, input);
    }

    @Test
    public void testReplaceIndicesWithNullAndMoveToBackAll() {
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] expected = { null, null, null, null, null, null, null, null, null, null, null };
        ArrayUtil.replaceIndicesWithNullAndMoveToBack(input, CollectionUtil.set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        assertArrayEquals(expected, input);
    }

    @Test
    public void testReplaceIndicesWithNullAndMoveToBackFirst() {
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] expected = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null };
        ArrayUtil.replaceIndicesWithNullAndMoveToBack(input, CollectionUtil.set(0));
        assertArrayEquals(expected, input);
    }

    @Test
    public void testReplaceIndicesWithNullAndMoveToBackLast() {
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] expected = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, null };
        ArrayUtil.replaceIndicesWithNullAndMoveToBack(input, CollectionUtil.set(11));
        assertArrayEquals(expected, input);
    }

    @Test
    public void testReplaceIndicesWithNullAndMoveToBackLeaveOtherNullValues() {
        Integer[] input = { 0, 1, 2, 3, 4, 5, 6, 7, null, 9, 10 };
        Integer[] expected = { 0, 2, 5, 6, 7, null, 9, 10, null, null, null };
        ArrayUtil.replaceIndicesWithNullAndMoveToBack(input, CollectionUtil.set(1, 3, 4));
        assertArrayEquals(expected, input);
    }

    @Test
    public void testReplaceIndicesWithNullAndMoveToBackEmpty() {
        Integer[] input = {};
        Integer[] expected = {};
        ArrayUtil.replaceIndicesWithNullAndMoveToBack(input, CollectionUtil.set(1, 3, 4));
        assertArrayEquals(expected, input);
    }

    @Test
    public void testContainsBaseCase() {
        Integer[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int position = ArrayUtil.contains(array, 6);
        assertEquals(6, position);
    }

    @Test
    public void testContainsEmpty() {
        Integer[] array = {};
        int position = ArrayUtil.contains(array, 6);
        assertEquals(-1, position);
    }

    @Test
    public void testContainsNotContained() {
        Integer[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int position = ArrayUtil.contains(array, 11);
        assertEquals(-1, position);
    }

    @Test
    public void testPrependBaseCase() {
        Integer[] left = { 1, 2, 3, 4, 5 };
        Integer[] right = { 6, 7, 8, 9, 10 };

        Integer[] expected = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] actual = ArrayUtil.prepend(right, left);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testPrependLeftEmpty() {
        Integer[] left = {};
        Integer[] right = { 6, 7, 8, 9, 10 };

        Integer[] expected = { 6, 7, 8, 9, 10 };
        Integer[] actual = ArrayUtil.prepend(right, left);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testPrependRightEmpty() {
        Integer[] left = { 1, 2, 3, 4, 5 };
        Integer[] right = {};

        Integer[] expected = { 1, 2, 3, 4, 5 };
        Integer[] actual = ArrayUtil.prepend(right, left);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testAppendBaseCase() {
        Integer[] left = { 1, 2, 3, 4, 5 };
        Integer[] right = { 6, 7, 8, 9, 10 };

        Integer[] expected = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Integer[] actual = ArrayUtil.append(left, right);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testAppendLeftEmpty() {
        Integer[] left = {};
        Integer[] right = { 6, 7, 8, 9, 10 };

        Integer[] expected = { 6, 7, 8, 9, 10 };
        Integer[] actual = ArrayUtil.append(left, right);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testAppendRightEmpty() {
        Integer[] left = { 1, 2, 3, 4, 5 };
        Integer[] right = {};

        Integer[] expected = { 1, 2, 3, 4, 5 };
        Integer[] actual = ArrayUtil.append(left, right);
        assertArrayEquals(expected, actual);
    }

}

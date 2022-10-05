package ch.hsr.ifs.iltis.core.arrays;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.core.runtime.Assert;

import ch.hsr.ifs.iltis.core.functional.functions.Equals;


public class ArrayUtil {

    /**
     * Removes the first matching element from an array. Whether an element matches is determined by {@link T#equals}.
     * 
     * @param <T>
     * The element-type. 
     * @param array
     * The array to search.
     * @param element
     * The element to remove.
     * @return A new array without the first matching element.
     */
    public static <T> T[] removeAndTrim(final T[] array, final T element) {
        final int index = contains(array, element);
        if (index != -1) {
            final T[] result = newArray(array, array.length - 1);
            System.arraycopy(array, 0, result, 0, index);
            System.arraycopy(array, index + 1, result, index, array.length - index - 1);
            return result;
        } else {
            return array;
        }
    }

    /**
     * Filters the passed array. Any matching elements will not be part of the resulting array.
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to search.
     * @param predicate
     * The predicate used to match elements.
     * @return A new array without the matching elements.
     * 
     * @since 2.1
     */
    public static <T> T[] copyWithoutMatching(final T[] array, final Predicate<T> predicate) {
        LinkedHashSet<Integer> indices = new LinkedHashSet<>(array.length);
        for (int idx = 0; idx < array.length; idx++) {
            if (predicate.test(array[idx])) indices.add(idx);
        }
        return copyWithoutValuesAtIndices(array, indices);
    }

    /**
     * Creates a copy of array without the elements behind any of the passed indices. This method does not check
     * whether the indices are inside the array's bounds.
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to copy.
     * @param indices
     * The indices to remove (<b>must be sorted in ascending order</b>).
     * 
     * @throws ArrayIndexOutOfBoundsException
     * If an index is outside of the array's bounds.
     * 
     * @since 2.1
     */
    public static <T> T[] copyWithoutValuesAtIndices(final T[] array, final Set<Integer> indices) {
        final T[] filtered = newArray(array, array.length - indices.size());
        Iterator<Integer> indexIterator = indices.iterator();
        for (int ii = 0, lastIndex = -1; ii <= indices.size(); ii++) {
            int thisIndex = ii == indices.size() ? array.length : indexIterator.next();
            int noOfElementsToCopy = (thisIndex - 1) - (lastIndex);
            if (noOfElementsToCopy > 0) System.arraycopy(array, lastIndex + 1, filtered, lastIndex + 1 - ii, noOfElementsToCopy);
            lastIndex = thisIndex;
        }
        return filtered;
    }

    /**
     * Replaces matches with {@code null} and moves them to the end of the array. Otherwise the order is kept stable. An element is considered
     * matching when the {@link T#equals} method returns {@code true}.
     * 
     * <dl>
     * <dt><span class="strong">important</span></dt>
     * <dd>This method alters the passed array.</dd>
     * </dl>
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to alter.
     * @param elements
     * The elements to set to {@code null}.
     * 
     * @since 2.1
     */
    public static <T> void replaceMatchesWithNullAndMoveToBack(final T[] array, final T[] elements) {
        replaceMatchesWithNullAndMoveToBack(array, (e) -> contains(elements, e) != -1);
    }

    /**
     * Replaces matches with {@code null} and moves them to the end of the array. Otherwise the order is kept stable.
     * 
     * <dl>
     * <dt><span class="strong">important</span></dt>
     * <dd>This method alters the passed array.</dd>
     * </dl>
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to alter.
     * @param predicate
     * The predicate used to check for matches.
     * 
     * @since 2.1
     */
    public static <T> void replaceMatchesWithNullAndMoveToBack(final T[] array, final Predicate<T> predicate) {
        LinkedHashSet<Integer> indices = new LinkedHashSet<>(array.length);
        for (int idx = 0; idx < array.length; idx++) {
            if (predicate.test(array[idx])) indices.add(idx);
        }
        replaceIndicesWithNullAndMoveToBack(array, indices);
    }

    /**
     * Replaces the elements at the indices with {@code null} and moves those {@code null} values to the back of the array. This method does not check
     * whether the
     * indices are inside the array's bounds.
     * 
     * <dl>
     * <dt><span class="strong">important</span></dt>
     * <dd>This method alters the passed array.</dd>
     * </dl>
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to alter.
     * @param indices
     * The indices to replace with null (<b>must be sorted in ascending order</b>).
     * 
     * @throws ArrayIndexOutOfBoundsException
     * If an index is outside of the array's bounds.
     * 
     * @since 2.1
     */
    public static <T> void replaceIndicesWithNullAndMoveToBack(final T[] array, final Set<Integer> indices) throws ArrayIndexOutOfBoundsException {
        if (indices.size() == 0 || array.length == 0) return;
        Iterator<Integer> indexIterator = indices.iterator();
        for (int ii = 0, lastIndex = -1; ii <= indices.size(); ii++) {
            int thisIndex = ii == indices.size() ? array.length : indexIterator.next();
            int noOfElementsToCopy = (thisIndex - 1) - (lastIndex);
            if (noOfElementsToCopy > 0) System.arraycopy(array, lastIndex + 1, array, lastIndex + 1 - ii, noOfElementsToCopy);
            lastIndex = thisIndex;
        }
        for (int ii = array.length - indices.size(); ii >= 0 && ii < array.length; ii++) {
            array[ii] = null;
        }
    }

    /**
     * Searches for an element in an array. Whether an element matches is determined by {@link T#equals}.
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to search.
     * @param element
     * The element to search for.
     * @return The index where the first matching element can be found, otherwise {@code -1}.
     */
    public static <T> int contains(final T[] array, final T element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) return i;
        }
        return -1;
    }

    /**
     * Searches for an element in an array. Whether an element matches is determined by the passed comparator. Thereby, the comparator may ignore the
     * second argument if {@code null} was passed as element.
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to search.
     * @param element
     * The element to search for.
     * @param comparator
     * The comparator. It may ignore the second argument if {@code null} was passed as element.
     * @return The index where the first matching element can be found, otherwise {@code -1}.
     */
    public static <T> int contains(final T[] array, final T element, final Equals<T, T> comparator) {
        for (int i = 0; i < array.length; i++) {
            if (comparator.equal(array[i], element)) return i;
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] newArray(final T[] array, final int size) {
        return (T[]) Array.newInstance(array.getClass().getComponentType(), size);
    }

    @SafeVarargs
    /**
     * Prepends the objects to the array. This creates a new array.
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to prepend to.
     * @param obj
     * The object(s) to prepend.
     * @return A new array with the object(s) in {@code obj} prepended to array.
     */
    public static <T> T[] prepend(final T[] array, final T... obj) {
        Assert.isNotNull(array);

        if (obj.length == 0) return array;
        if (array.length == 0) return obj;

        return concat(obj, array);
    }

    @SafeVarargs
    /**
     * Appends the objects to the array. This creates a new array.
     * 
     * @param <T>
     * The element-type.
     * @param array
     * The array to append to.
     * @param obj
     * The object(s) to append.
     * @return A new array with the object(s) in {@code obj} appended to array.
     */
    public static <T> T[] append(final T[] array, final T... obj) {
        Assert.isNotNull(array);

        if (obj.length == 0) return array;
        if (array.length == 0) return obj;

        return concat(array, obj);
    }

    private static <T> T[] concat(final T[] first, final T[] second) {
        final T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}

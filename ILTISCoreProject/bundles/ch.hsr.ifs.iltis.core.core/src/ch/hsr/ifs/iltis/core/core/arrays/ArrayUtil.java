package ch.hsr.ifs.iltis.core.core.arrays;

import java.lang.reflect.Array;

import org.eclipse.core.runtime.Assert;

import ch.hsr.ifs.iltis.core.core.functional.functions.Equals;


public class ArrayUtil {

    public static <T> T[] removeAndTrim(final T[] array, final T element) {
        final int index = contains(array, element);
        if (index > -1) {
            final T[] result = newArray(array, array.length - 1);
            System.arraycopy(array, 0, result, 0, index);
            System.arraycopy(array, index + 1, result, index, array.length - index - 1);
            return result;
        } else {
            return array;
        }

    }

    public static <T> int contains(final T[] array, final T element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) return i;
        }
        return -1;
    }

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
    public static <T> T[] prepend(final T[] array, final T... obj) {
        Assert.isNotNull(array);

        if (obj.length == 0) return array;
        if (array.length == 0) return obj;

        return concat(obj, array);
    }

    @SafeVarargs
    public static <T> T[] append(final T[] array, final T... obj) {
        Assert.isNotNull(array);

        if (obj.length == 0) return array;
        if (array.length == 0) return obj;

        return concat(array, obj);
    }

    private static <T> T[] concat(final T[] first, final T[] second) {
        final T[] result = newArray(first, first.length + second.length);
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}

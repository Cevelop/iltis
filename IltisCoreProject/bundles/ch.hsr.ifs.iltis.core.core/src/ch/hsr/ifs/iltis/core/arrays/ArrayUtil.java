package ch.hsr.ifs.iltis.core.arrays;

import java.lang.reflect.Array;

import org.eclipse.core.runtime.Assert;

import ch.hsr.ifs.iltis.core.functional.functions.Equals;


public class ArrayUtil {

   public static <T> T[] removeAndTrim(T[] array, T element) {
      int index = contains(array, element);
      if (index > -1) {
         T[] result = newArray(array, array.length - 1);
         System.arraycopy(array, 0, result, 0, index);
         System.arraycopy(array, index + 1, result, index, array.length - index - 1);
         return result;
      } else {
         return array;
      }

   }

   public static <T> int contains(T[] array, T element) {
      for (int i = 0; i < array.length; i++) {
         if (array[i].equals(element)) return i;
      }
      return -1;
   }

   public static <T> int contains(T[] array, T element, Equals<T, T> comparator) {
      for (int i = 0; i < array.length; i++) {
         if (comparator.equal(array[i], element)) return i;
      }
      return -1;
   }

   @SuppressWarnings("unchecked")
   private static <T> T[] newArray(T[] array, int size) {
      return (T[]) Array.newInstance(array.getClass().getComponentType(), size);
   }

   @SafeVarargs
   public static <T> T[] prepend(T[] array, T... obj) {
      Assert.isNotNull(array);

      if (obj.length == 0) return array;
      if (array.length == 0) return obj;

      return concat(obj, array);
   }

   @SafeVarargs
   public static <T> T[] append(T[] array, T... obj) {
      Assert.isNotNull(array);

      if (obj.length == 0) return array;
      if (array.length == 0) return obj;

      return concat(array, obj);
   }

   private static <T> T[] concat(T[] first, T[] second) {
      T[] result = newArray(first, first.length + second.length);
      System.arraycopy(first, 0, result, 0, first.length);
      System.arraycopy(second, 0, result, first.length, second.length);
      return result;
   }

}

package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.impl.list.mutable.AbstractMutableList;
import org.eclipse.collections.impl.list.mutable.FastList;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IStringifyable;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;


/**
 * Stringifyable-List that is stringifyable. Can be used as an {@link InfoArgument}.
 * 
 * @author tstauber
 *
 */
public class StringifyableList<StringifyableType extends IStringifyable<StringifyableType>> extends AbstractMutableList<StringifyableType> implements
      IStringifyable<StringifyableList<StringifyableType>> {

   private static final Object[] DEFAULT_SIZED_EMPTY_ARRAY = {};
   private static final Object[] ZERO_SIZED_ARRAY          = {};
   private static final int      MAXIMUM_ARRAY_SIZE        = Integer.MAX_VALUE - 8;

   /**
    * Must be altered if stringifyable list must be nested.
    */
   protected final String LIST_SEPARATOR = IStringifyable.LIST_SEPARATOR;

   /**
    * Used to create the elements from the strings.
    */
   protected final Supplier<StringifyableType> elementDefaultConstructor;

   protected int                           size;
   @SuppressWarnings("unchecked")
   protected transient StringifyableType[] items = (StringifyableType[]) DEFAULT_SIZED_EMPTY_ARRAY;

   public StringifyableList(Supplier<StringifyableType> elementDefaultConstructor) {
      this.elementDefaultConstructor = elementDefaultConstructor;
   }

   @SuppressWarnings("unchecked")
   public StringifyableList(Supplier<StringifyableType> elementDefaultConstructor, int initialCapacity) {
      this(elementDefaultConstructor);
      this.items = initialCapacity == 0 ? (StringifyableType[]) ZERO_SIZED_ARRAY : (StringifyableType[]) new Object[initialCapacity];
   }

   protected StringifyableList(Supplier<StringifyableType> elementDefaultConstructor, StringifyableType[] array) {
      this(elementDefaultConstructor, array.length, array);
   }

   protected StringifyableList(Supplier<StringifyableType> elementDefaultConstructor, int size, StringifyableType[] array) {
      this(elementDefaultConstructor);
      this.size = size;
      this.items = array;
   }

   @SuppressWarnings("unchecked")
   public StringifyableList(Supplier<StringifyableType> elementDefaultConstructor, Collection<StringifyableType> source) {
      this(elementDefaultConstructor);
      this.items = (StringifyableType[]) source.toArray();
      this.size = this.items.length;
   }

   public static <StringifyableType extends IStringifyable<StringifyableType>> StringifyableList<StringifyableType> newList(
         Supplier<StringifyableType> elementDefaultConstructor) {
      return new StringifyableList<>(elementDefaultConstructor);
   }

   @SuppressWarnings("unchecked")
   public static <StringifyableType extends IStringifyable<StringifyableType>> StringifyableList<StringifyableType> wrapCopy(
         Supplier<StringifyableType> elementDefaultConstructor, String... array) {
      StringifyableType[] newArray = (StringifyableType[]) new Object[array.length];
      System.arraycopy(array, 0, newArray, 0, array.length);
      return new StringifyableList<>(elementDefaultConstructor, newArray);
   }

   public static <StringifyableType extends IStringifyable<StringifyableType>> StringifyableList<StringifyableType> newList(
         Supplier<StringifyableType> elementDefaultConstructor, int initialCapacity) {
      return new StringifyableList<>(elementDefaultConstructor, initialCapacity);
   }

   /**
    * Creates a new list using the passed {@code elements} argument as the backing store.
    * <p>
    * !!! WARNING: This method uses the passed in array, so can be very unsafe if the original
    * array is held onto anywhere else. !!!
    */
   @SuppressWarnings("unchecked")
   public static <StringifyableType extends IStringifyable<StringifyableType>> StringifyableList<StringifyableType> newListWith(
         Supplier<StringifyableType> elementDefaultConstructor, StringifyableType... elements) {
      return new StringifyableList<>(elementDefaultConstructor, elements);
   }

   /**
    * Creates a new FastList pre-sized to the specified size filled with default values generated by the specified function.
    *
    * @since 3.0
    */
   public static <E> FastList<E> newWithNValues(int size, Function0<E> factory) {
      FastList<E> newFastList = FastList.newList(size);
      for (int i = 0; i < size; i++) {
         newFastList.add(factory.value());
      }
      return newFastList;
   }

   protected static String SEPARATOR = "{LS}";

   @Override
   public StringifyableList<StringifyableType> unstringify(String string) {
      StringTokenizer tokenizer = new StringTokenizer(string, SEPARATOR);
      while (tokenizer.hasMoreTokens()) {
         StringifyableType element = elementDefaultConstructor.get();
         add(element.unstringify(tokenizer.nextToken()));
      }
      return this;
   }

   @Override
   public String stringify() {
      return Stream.of(items).map(IStringifyable::stringify).collect(Collectors.joining(SEPARATOR));
   }

   @Override
   public int size() {
      return size;
   }

   @Override
   public void clear() {
      Arrays.fill(this.items, 0, this.size, null);
      this.size = 0;
   }

   @Override
   public boolean addAll(int index, Collection<? extends StringifyableType> source) {
      if (index > this.size || index < 0) {
         this.throwOutOfBounds(index);
      }
      if (source.isEmpty()) { return false; }

      Object[] newItems = source.toArray();
      int sourceSize = newItems.length;
      int newSize = this.size + sourceSize;
      this.ensureCapacity(newSize);
      this.shiftElementsAtIndex(index, sourceSize);
      this.size = newSize;
      System.arraycopy(newItems, 0, this.items, index, sourceSize);

      return true;
   }

   private void shiftElementsAtIndex(int index, int sourceSize) {
      int numberToMove = this.size - index;
      if (numberToMove > 0) {
         System.arraycopy(this.items, index, this.items, index + sourceSize, numberToMove);
      }
   }

   @Override
   public StringifyableType get(int index) {
      if (index < this.size) return this.items[index];
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size);
   }

   @Override
   public StringifyableType set(int index, StringifyableType element) {
      StringifyableType previous = this.get(index);
      this.items[index] = element;
      return previous;
   }

   @Override
   public boolean add(StringifyableType newItem) {
      if (this.items.length == this.size) {
         this.ensureCapacityForAdd();
      }
      this.items[this.size++] = newItem;
      return true;
   }

   @Override
   public void add(int index, StringifyableType element) {
      if (index > -1 && index < this.size) {
         this.addAtIndex(index, element);
      } else if (index == this.size) {
         this.add(element);
      } else {
         this.throwOutOfBounds(index);
      }
   }

   @SuppressWarnings("unchecked")
   private void addAtIndex(int index, StringifyableType element) {
      int oldSize = this.size++;
      if (this.items.length == oldSize) {
         StringifyableType[] newItems = (StringifyableType[]) new Object[this.sizePlusFiftyPercent(oldSize)];
         if (index > 0) {
            System.arraycopy(this.items, 0, newItems, 0, index);
         }
         System.arraycopy(this.items, index, newItems, index + 1, oldSize - index);
         this.items = newItems;
      } else {
         System.arraycopy(this.items, index, this.items, index + 1, oldSize - index);
      }
      this.items[index] = element;
   }

   @SuppressWarnings("unchecked")
   private void ensureCapacityForAdd() {
      if (this.items == DEFAULT_SIZED_EMPTY_ARRAY) {
         this.items = (StringifyableType[]) new Object[10];
      } else {
         this.transferItemsToNewArrayWithCapacity(this.sizePlusFiftyPercent(this.size));
      }
   }

   private void throwOutOfBounds(int index) {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size);
   }

   public void ensureCapacity(int minCapacity) {
      int oldCapacity = this.items.length;
      if (minCapacity > oldCapacity) {
         int newCapacity = Math.max(this.sizePlusFiftyPercent(oldCapacity), minCapacity);
         this.transferItemsToNewArrayWithCapacity(newCapacity);
      }
   }

   private void transferItemsToNewArrayWithCapacity(int newCapacity) {
      this.items = this.copyItemsWithNewCapacity(newCapacity);
   }

   @SuppressWarnings("unchecked")
   private StringifyableType[] copyItemsWithNewCapacity(int newCapacity) {
      StringifyableType[] newItems = (StringifyableType[]) new Object[newCapacity];
      System.arraycopy(this.items, 0, newItems, 0, Math.min(this.size, newCapacity));
      return newItems;
   }

   private int sizePlusFiftyPercent(int oldSize) {
      int result = oldSize + (oldSize >> 1) + 1;
      return result < oldSize ? MAXIMUM_ARRAY_SIZE : result;
   }

   @Override
   public StringifyableType remove(int index) {
      StringifyableType previous = this.get(index);
      int totalOffset = this.size - index - 1;
      if (totalOffset > 0) {
         System.arraycopy(this.items, index + 1, this.items, index, totalOffset);
      }
      this.items[--this.size] = null;
      return previous;
   }

}

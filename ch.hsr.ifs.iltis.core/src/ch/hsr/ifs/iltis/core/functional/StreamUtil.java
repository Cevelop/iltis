package ch.hsr.ifs.iltis.core.functional;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A utility class which provides static convenience methods about {@linkplain Stream}s
 * 
 * @author tstauber
 *
 */
public abstract class StreamUtil {

   /**
    * A collector which collects a Stream of StreamPairs into a Map.
    * 
    * @param <K>
    *        Key type
    * @param <V>
    *        Value type
    * 
    * @return A Collector
    * @throws NullPointerException
    *         If one of the StreamPairs contains {@code null}
    */
   public static <K, V> Collector<StreamPair<K, V>, ?, Map<K, V>> toMap() {
      return Collectors.toMap((StreamPair<K, V> pair) -> pair.first(), (StreamPair<K, V> pair) -> pair.second());
   }

   /**
    * A collector which collects a Stream of StreamPairs into a Map.
    * 
    * @param <K>
    *        Key type
    * @param <V>
    *        Value type
    * 
    * @return A Collector which creates a HashMap
    */
   public static <K, V> Collector<StreamPair<K, V>, ?, HashMap<K, V>> toNullableMap() {
      return new Collector<StreamPair<K, V>, HashMap<K, V>, HashMap<K, V>>() {

         @Override
         public Supplier<HashMap<K, V>> supplier() {
            return HashMap<K, V>::new;
         }

         @Override
         public BiConsumer<HashMap<K, V>, StreamPair<K, V>> accumulator() {
            return (map, element) -> {
               K key = element.first();
               if (map.containsKey(key)) {
                  throw new IllegalStateException("Duplicate key " + key);
               } else {
                  map.put(key, element.second());
               }
            };
         }

         @Override
         public BinaryOperator<HashMap<K, V>> combiner() {
            return (current, additional) -> {
               current.putAll(additional);
               return current;
            };
         }

         @Override
         public Function<HashMap<K, V>, HashMap<K, V>> finisher() {
            return Function.identity();
         }

         @Override
         public Set<Collector.Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH, Collector.Characteristics.UNORDERED));
         }

      };
   }

}

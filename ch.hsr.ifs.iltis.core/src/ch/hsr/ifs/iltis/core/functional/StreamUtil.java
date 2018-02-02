package ch.hsr.ifs.iltis.core.functional;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public abstract class StreamUtil {

   public static <T1, T2> Collector<StreamPair<T1, T2>, ?, Map<T1, T2>> toMap() {
      return Collectors.toMap((StreamPair<T1, T2> pair) -> pair.first(), (StreamPair<T1, T2> pair) -> pair.second());
   }

}

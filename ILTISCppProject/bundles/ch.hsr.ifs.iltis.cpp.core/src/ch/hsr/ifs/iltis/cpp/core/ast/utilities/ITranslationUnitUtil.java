package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.tuple.Tuples;


public class ITranslationUnitUtil {

   /**
    * Crates a map indexed by line number, containing pairs of line-start-offset and line-content.
    * 
    * @param tu
    *        The translation unit to extract the content from
    * @return The created map
    */
   public static MutableMap<Integer, Pair<Integer, char[]>> createLinenoOffsetContentMap(final ITranslationUnit tu) {
      char[] contents = tu.getContents();
      MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap = Maps.mutable.empty();
      int lastOffset = 0;
      int lineCounter = 0;
      for (int currentOffset = 0; currentOffset < contents.length; currentOffset++) {
         if (contents[currentOffset] == '\n') {
            char[] line = new char[currentOffset + 1 - lastOffset];
            System.arraycopy(contents, lastOffset, line, 0, currentOffset + 1 - lastOffset);
            linenoOffsetContentMap.put(lineCounter++, Tuples.pair(lastOffset, line));
            lastOffset = currentOffset + 1;
         }
      }
      return linenoOffsetContentMap;
   }

}

package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Map;

import org.eclipse.cdt.codan.internal.core.model.CodanProblemMarker;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;


/**
 * TODO Javadoc
 */
public class UnifiedMarkerArgumentMap extends UnifiedMap<String, Object> {

   public UnifiedMarkerArgumentMap() {}

   public UnifiedMarkerArgumentMap(Map<String, String> map) {
      super(map);
   }

   public UnifiedMarkerArgumentMap(final Object[] args) {
      putAll(args);
   }

   public UnifiedMarkerArgumentMap(final IMarker marker) {
      try {
         putAll(marker.getAttributes());
      } catch (CoreException e) {
         ILTISException.wrap(e).rethrowUnchecked();
      }
   }

   public static UnifiedMarkerArgumentMap fromCodanProblemMarker(final IMarker marker) {
      UnifiedMarkerArgumentMap map = new UnifiedMarkerArgumentMap();
      // Assume that we're dealing with a well-formed {@Link #CodanProblemMarker}
      // which was created from a UnifiedMapArray.
      map.putAll(CodanProblemMarker.getProblemArguments(marker));
      try {
         map.putAll(marker.getAttributes());
      } catch (CoreException e) {
         ILTISException.wrap(e).rethrowUnchecked();
      }
      return map;
   }

   private void putAll(Object[] arguments) {
      for (int i = 0; i < arguments.length; i += 2) {
         put((String) arguments[i], arguments[i + 1]);
      }
   }

   @Override
   public Object[] toArray() {
      return keyValuesView().flatCollect(p -> Lists.mutable.of(p.getOne(), p.getTwo())).toArray();
   }
}

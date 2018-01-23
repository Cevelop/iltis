package ch.hsr.ifs.iltis.core.tests.functional;

import static ch.hsr.ifs.iltis.core.functional.Functional.as;
import static ch.hsr.ifs.mockator.plugin.base.collections.CollectionHelper.unorderedMap;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class CastHelperTest {

   //TODO remove if cast helper not moved to ILTIS
   //   @Test
   //   public void instanceIsOfSameClass() {
   //      assertTrue(CastHelper.isInstanceOf(42, Integer.class));
   //   }
   //
   //   @Test
   //   public void instanceIsOfDifferentClass() {
   //      assertFalse(CastHelper.isInstanceOf(42, String.class));
   //   }

   @Test
   public void unsecureCastYieldsCastedValue() {
      final Map<String, String> m = unorderedMap();
      final HashMap<String, String> hm = as(m);
      assertTrue(hm != null);
   }
}

package ch.hsr.ifs.iltis.core.tests.functional;

import static ch.hsr.ifs.iltis.core.functional.Functional.as;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class CastHelperTest {

   
   // FIXME Change to ILTISException test
   
   //   @Test
   //   public void instanceIsOfSameClass() {
   //      assertTrue(CastHelper.isInstanceOf(42, Integer.class));
   //   }
   //
   //   @Test
   //   public void instanceIsOfDifferentClass() {
   //      assertFalse(CastHelper.isInstanceOf(42, String.class));
   //   }

   //FIXME move to FunctionalTest
   
   @Test
   public void unsecureCastYieldsCastedValue() {
      final Map<String, String> m = new HashMap<>();
      final HashMap<String, String> hm = as(m);
      assertTrue(hm != null);
   }
}

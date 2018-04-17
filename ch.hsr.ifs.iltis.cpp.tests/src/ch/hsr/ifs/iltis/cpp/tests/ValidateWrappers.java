package ch.hsr.ifs.iltis.cpp.tests;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.resources.StringUtil;
import ch.hsr.ifs.iltis.cpp.wrappers.CPPVisitor;
import ch.hsr.ifs.iltis.cpp.wrappers.ILTISWrapper;
import ch.hsr.ifs.iltis.cpp.wrappers.IndexToASTNameHelper;
import ch.hsr.ifs.iltis.cpp.wrappers.SelectionHelper;

//TODO(tstauber - Apr 13, 2018) Extract once ILTIS Testing stands. Then also test ILTIS Core like
public class ValidateWrappers {

   @Test
   public void validateCPPVisitor() {
      validate(CPPVisitor.class);
   }

   @Test
   public void validateSelectionHelper() {
      validate(SelectionHelper.class);
   }

   @Test
   public void validateIndexToASTNameHelper() {
      validate(IndexToASTNameHelper.class);
   }

   /* V Infrastructure V */

   public void validate(Class<?> wrapingClass) {
      List<String> missingMethods = new ArrayList<>();
      ILTISWrapper wraper = wrapingClass.getAnnotation(ILTISWrapper.class);
      ILTISException.Unless.notNull("Class " + wrapingClass.getName() + " is no ILTISWrapper!", wraper);
      Class<?> wrapedClass = wraper.value();

      List<Method> wrapeMethods = Arrays.asList(wrapedClass.getMethods());
      List<Method> wraperMethods;
      if (wrapedClass.isAssignableFrom(wrapingClass)) {
         wraperMethods = Stream.of(wrapingClass.getDeclaredMethods()).filter(m -> Modifier.isPublic(m.getModifiers())).collect(Collectors.toList());
      } else {
         wraperMethods = Arrays.asList(wrapingClass.getMethods());
      }

      for (Method wrapeeMethod : wrapeMethods) {
         boolean foundMethod = !wrapeeMethod.getDeclaringClass().getPackage().getName().contains(".internal.");
         if (!foundMethod) {
            for (Method wraperMethod : wraperMethods) {
               foundMethod |= compareMethods(wrapeeMethod, wraperMethod);
            }
         }

         if (!foundMethod) missingMethods.add(getMethodSignature(wrapeeMethod));
      }
      String link = " (" + wrapingClass.getSimpleName() + ".java:" + 1 + ")";
      assertTrue("Missing methods in " + link + ". Proposal: \n" + StringUtil.toString(missingMethods, "", "\n\n", "", Object::toString),
            missingMethods.isEmpty());
   }

   private boolean compareMethods(Method wrapeeMethod, Method wraperMethod) {
      Class<?>[] wrapeeParameterTypes = wrapeeMethod.getParameterTypes();
      if (wrapeeMethod.getName().equals(wraperMethod.getName())) {
         wrapeeMethod.getReturnType().getName().equals(wraperMethod.getReturnType().getName());
         Class<?>[] wraperParameterTypes = wraperMethod.getParameterTypes();
         if (wrapeeParameterTypes.length == wraperParameterTypes.length) {
            if (wrapeeParameterTypes.length > 0) {
               for (Class<?> p1 : wrapeeParameterTypes) {
                  for (Class<?> p2 : wraperParameterTypes) {
                     if (p1.getName().equals(p2.getName())) { return true; }
                  }
               }
            } else {
               return true;
            }
         }
      }
      return wrapeeMethod.isAnnotationPresent(Deprecated.class);
   }

   protected String getMethodSignature(Method m) {
      return "public " + m.getReturnType().getSimpleName() + " " + generateSignature(m) + " {\n   return " + m.getDeclaringClass().getName() + "." +
             generateCall(m) + ";\n}";
   }

   private String generateSignature(Method m) {
      return m.getName() + StringUtil.toString(m.getParameters(), "(", ", ", ")", param -> param.getType().getSimpleName() + " " + param.getName());
   }

   private String generateCall(Method m) {
      return m.getName() + StringUtil.toString(m.getParameters(), "(", ", ", ")", Parameter::getName);
   }
}

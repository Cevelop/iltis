package ch.hsr.ifs.iltis.testing.core.validators;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


// FIXME https://gitlab.dev.ifs.hsr.ch/iltis/iltis-cpp/issues/2

/**
 * A validator for wrapped classes.
 * 
 * @author tstauber
 *
 */
public class WrapperValidationTest {

   /**
    * Validates that every public method from the wrapped class was wrapped in the wrapper.
    * 
    * @param wrapingClass
    *        The wrapper-class annotated with {@link ILTISWrapper}
    */
   public <T> void validate(Class<T> wrapingClass) {
      List<String> missingMethods = new ArrayList<>();
      ILTISWrapper wraper = wrapingClass.getAnnotation(ILTISWrapper.class);
      if (wraper == null) throw new IllegalArgumentException("Class " + wrapingClass.getName() + " is not annotated with an ILTISWrapper!");
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
      assertTrue("Missing methods in " + link + ". Proposal: \n" + toString(missingMethods, "", "\n\n", "", Object::toString), missingMethods
            .isEmpty());
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
      return m.getName() + toString(m.getParameters(), "(", ", ", ")", param -> param.getType().getSimpleName() + " " + param.getName());
   }

   private String generateCall(Method m) {
      return m.getName() + toString(m.getParameters(), "(", ", ", ")", Parameter::getName);
   }

   private static <O> String toString(Iterable<O> it, String prefix, String separator, String postfix, Function<O, String> fun) {
      StringBuffer buff = new StringBuffer(prefix);
      boolean isFirst = true;
      for (O o : it) {
         if (!isFirst) buff.append(separator);
         buff.append(fun.apply(o));
         isFirst = false;
      }
      buff.append(postfix);
      return buff.toString();
   }

   public static <O> String toString(O[] it, String prefix, String separator, String postfix, Function<O, String> fun) {
      return toString(Arrays.asList(it), prefix, separator, postfix, fun);
   }

}

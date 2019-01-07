package ch.hsr.ifs.iltis.testing.core.core.validators;

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
     * @param wrappingClass
     * The wrapper-class annotated with {@link ILTISWrapper}
     */
    public <T> void validate(Class<T> wrappingClass) {
        List<String> missingMethods = new ArrayList<>();
        ILTISWrapper wrapper = wrappingClass.getAnnotation(ILTISWrapper.class);
        if (wrapper == null) throw new IllegalArgumentException("Class " + wrappingClass.getName() + " is not annotated with an ILTISWrapper!");
        Class<?> wrappedClass = wrapper.value();

        List<Method> wrappedClassMethods = Arrays.asList(wrappedClass.getMethods());
        List<Method> wrapperMethods;
        if (wrappedClass.isAssignableFrom(wrappingClass)) {
            wrapperMethods = Stream.of(wrappingClass.getDeclaredMethods()).filter(m -> Modifier.isPublic(m.getModifiers())).collect(Collectors
                    .toList());
        } else {
            wrapperMethods = Arrays.asList(wrappingClass.getMethods());
        }

        for (Method wrappingCandidateMethod : wrappedClassMethods) {
            if (needsWrapping(wrappingCandidateMethod) && noneWraps(wrapperMethods, wrappingCandidateMethod)) {
                missingMethods.add(getMethodSignature(wrappingCandidateMethod));
            }
        }
        String link = " (" + wrappingClass.getSimpleName() + ".java:" + 1 + ")";
        assertTrue("Missing methods in " + link + ". Proposal: \n" + toString(missingMethods, "", "\n\n", "", Object::toString), missingMethods
                .isEmpty());
    }

    private static boolean noneWraps(List<Method> wrapperMethods, Method wrappingCandidateMethod) {
        return !(wrapperMethods.stream().anyMatch(wrapperMethod -> doesWrap(wrappingCandidateMethod, wrapperMethod)));
    }

    private static boolean needsWrapping(Method wrappingCandidateMethod) {
        /* Methods on non internal (super)classes don't have to be wrapped */
        //TODO implement using ExportPackageObject.isInternal()
        boolean isInternal = wrappingCandidateMethod.getDeclaringClass().getPackage().getName().contains(".internal.");
        /* Static methods do not need to be wrapped */
        boolean isStatic = !Modifier.isStatic(wrappingCandidateMethod.getModifiers());

        return !isInternal && !isStatic;
    }

    private static boolean doesWrap(Method wrappeeMethod, Method wrapperMethod) {
        Class<?>[] wrappeeParameterTypes = wrappeeMethod.getParameterTypes();
        if (wrappeeMethod.getName().equals(wrapperMethod.getName())) {
            wrappeeMethod.getReturnType().getName().equals(wrapperMethod.getReturnType().getName());
            Class<?>[] wrapperParameterTypes = wrapperMethod.getParameterTypes();
            if (wrappeeParameterTypes.length == wrapperParameterTypes.length) {
                if (wrappeeParameterTypes.length > 0) {
                    for (Class<?> p1 : wrappeeParameterTypes) {
                        for (Class<?> p2 : wrapperParameterTypes) {
                            if (p1.getName().equals(p2.getName())) {
                                return true;
                            }
                        }
                    }
                } else {
                    return true;
                }
            }
        }
        return wrappeeMethod.isAnnotationPresent(Deprecated.class);
    }

    protected static String getMethodSignature(Method m) {
        return "@Override\npublic " + m.getReturnType().getSimpleName() + " " + generateSignature(m) + " {\n   return " + m.getDeclaringClass()
                .getName() + "." + generateCall(m) + ";\n}";
    }

    private static String generateSignature(Method m) {
        return m.getName() + toString(m.getParameters(), "(", ", ", ")", param -> param.getType().getSimpleName() + " " + param.getName());
    }

    private static String generateCall(Method m) {
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

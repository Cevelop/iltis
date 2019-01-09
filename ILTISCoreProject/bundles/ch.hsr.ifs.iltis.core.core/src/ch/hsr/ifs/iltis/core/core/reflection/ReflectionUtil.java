package ch.hsr.ifs.iltis.core.core.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.collections.impl.utility.ArrayIterate;


public class ReflectionUtil {

    /**
     * Searches for any field (even protected, private, or final) and makes it accessible
     *
     * @param <TargetType>
     * The target type
     * @param target
     * The instance on which to search for this field.
     * @param fieldName
     * The name of the field to find
     * @return The field made accessible
     * @throws SecurityException
     * if {@link Class#getDeclaredFields()} throws
     * @throws NoSuchFieldException
     * if no fields exist
     */
    public static <TargetType> Field getFieldAccessible(final TargetType target, final String fieldName) throws NoSuchFieldException {
        Class<?> clazz = target.getClass();
        for (Field[] fields = clazz.getDeclaredFields(); clazz.getSuperclass() != null; fields = (clazz = clazz.getSuperclass())
                .getDeclaredFields()) {
            for (final Field field : fields) {
                if (field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        }
        throw new NoSuchFieldException();
    }

    /**
     * Calls a method
     * 
     * @since 1.1
     * 
     * @param target
     * @param methodName
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <TargetType, ReturnType> ReturnType call(final TargetType target, final String methodName, Object... parameters) {
        ArrayIterate.collect(parameters, p -> p.getClass()).toArray(new Class<?>[parameters.length]);
        try {
            Method method = target.getClass().getMethod(methodName, ArrayIterate.collect(parameters, p -> p.getClass()).toArray(
                    new Class<?>[parameters.length]));
            method.setAccessible(true);
            return (ReturnType) method.invoke(target, parameters);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @since 1.1
     */
    public static <TargetType> void callVoid(final TargetType target, final String methodName, Object... parameters) {
        ArrayIterate.collect(parameters, p -> p.getClass()).toArray(new Class<?>[parameters.length]);
        try {
            Method method = target.getClass().getMethod(methodName, ArrayIterate.collect(parameters, p -> p.getClass()).toArray(
                    new Class<?>[parameters.length]));
            method.setAccessible(true);
            method.invoke(target, parameters);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to get the value of the field with fieldName on the object target.
     * <b>This method does not work with value-types</b>
     * 
     * @since 1.1
     * 
     * @param target
     * The object on which to get the field
     * @param fieldName
     * The name of the field to get
     * @return The value of the field
     */
    public static <TargetType, FieldType> FieldType get(final TargetType target, final String fieldName) {
        return getOrDefault(target, fieldName, null);
    }

    /**
     * Tries to get the value of the field with fieldName on the object target.
     * <b>This method does not work with value-types</b>
     * 
     * @since 1.1
     * 
     * @param target
     * The object on which to get the field
     * @param fieldName
     * The name of the field to get
     * @param def
     * The default value to use if the field could not be read
     * @return The value of the field
     */
    @SuppressWarnings("unchecked")
    public static <TargetType, FieldType> FieldType getOrDefault(final TargetType target, final String fieldName, final FieldType def) {
        try {
            Field f = getFieldAccessible(target, fieldName);
            return (FieldType) f.get(target);
        } catch (ClassCastException e) {
            return def;
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return def;
        }
    }

    /**
     * Sets the value of any field (even protected, private, or final) to the value passed
     *
     * @param <TargetType>
     * The target type
     * @param <FieldType>
     * The field type
     * @param target
     * The instance on which to manipulate the field
     * @param fieldName
     * The name of the field to manipulate
     * @param value
     * The value to set the field to
     * @return true iff the value has been changed
     */
    public static <TargetType, FieldType> boolean setValue(final TargetType target, final String fieldName, final FieldType value) {
        try {
            getFieldAccessible(target, fieldName).set(target, value);
            return true;
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A convenience method to set a field's value to null.
     *
     * @param <TargetType>
     * The target type
     * @param target
     * The instance on which to manipulate the field
     * @param fieldName
     * The name of the field to manipulate
     * @return true iff the value has been changed
     */
    public static <TargetType> boolean setNull(final TargetType target, final String fieldName) {
        return setValue(target, fieldName, null);
    }

    /**
     * Replaces an array referenced by a field with an empty array
     *
     * @param <TargetType>
     * The target type
     * @param target
     * The instance on which to manipulate
     * @param fieldName
     * The field to manipulate
     * @return true iff the array has been replaced
     */
    public static <TargetType> boolean emptyArray(final TargetType target, final String fieldName) {
        try {
            final Field field = getFieldAccessible(target, fieldName);
            final Class<?> clazz = field.getType();
            field.set(target, Array.newInstance(clazz.getComponentType(), Array.getLength(field.get(target))));
            return true;
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}

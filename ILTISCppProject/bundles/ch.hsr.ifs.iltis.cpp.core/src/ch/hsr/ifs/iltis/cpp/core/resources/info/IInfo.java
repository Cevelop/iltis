package ch.hsr.ifs.iltis.cpp.core.resources.info;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.core.functional.functions.Consumer2;
import ch.hsr.ifs.iltis.core.core.object.ICopyable;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.NonPersistentCopyArgument;


public interface IInfo<T extends IInfo<T>> extends ICopyable<T> {

    /**
     * Hook to replace the info converter in implementing classes.
     *
     * @return An InfoConverter
     *
     * @noreference This method is not intended to be referenced by clients.
     */
    default InfoConverter hook_getInfoConverter() {
        return InfoConverter.INSTANCE;
    }

    /**
     * Gathers this info's {@link InfoArgument} fields and creates a map from them.
     *
     * <pre></pre>
     *
     * <b>This operation will be long running. If used multiple times, this map should be cached, as long as the values in the info did not
     * change.</b>
     *
     * <pre></pre>
     *
     * @return The map representing the state of this info.
     */
    default Map<String, String> toMap() {
        final Field[] fields = getClass().getFields();
        try {
            final MutableMap<String, String> map = Maps.mutable.empty();

            for (final Field f : hook_getInfoConverter().getMessageInfoArgFieldsOrdered(fields)) {
                hook_getInfoConverter().validateMessageInfoArgField(f);
                map.put(f.getName(), (String) f.get(this));
            }
            for (final Field f : hook_getInfoConverter().getInfoArgumentFields(fields)) {
                hook_getInfoConverter().validateInfoArgField(f);
                map.put(f.getName(), hook_getInfoConverter().stringifyField(f, this));
            }
            return map;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw ILTISException.wrap(e).rethrowUnchecked();
        }
    }

    /**
     * Default implementation for copying a IInfo. If the field is of a cloneable type, cloning is attempted. Else, the value is copied by reference.
     */
    @Override
    @SuppressWarnings("unchecked")
    default T copy() {
        try {
            final Object copy = getClass().newInstance();
            for (final Field f : getClass().getFields()) {
                if (f.isAnnotationPresent(InfoArgument.class) || f.isAnnotationPresent(NonPersistentCopyArgument.class)) {
                    final Object value = f.get(this);
                    if (value instanceof Cloneable) {
                        Method cloneMethod;
                        try {
                            cloneMethod = Object.class.getMethod("clone");
                            cloneMethod.setAccessible(true);
                            f.set(copy, cloneMethod.invoke(value));
                        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                            f.set(copy, value);
                        }
                    } else {
                        f.set(copy, value);
                    }
                }
            }
            return (T) copy;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a new info and loads the fields from the map into it.
     *
     * @param constructor
     * The constructor for the specific info.
     * @param map
     * The map to load the fields from
     * @return A new info with the data from the map loaded.
     */
    static <R extends IInfo<R>> R fromMap(final Supplier<R> constructor, final Map<String, ?> map) {
        final R info = constructor.get();
        info.hook_getInfoConverter().fillAllFieldsInInfo(info, map);
        return info;
    }

    /**
     * Loads the fields from the map into the info
     *
     * @param info
     * The info to load the map into
     * @param map
     * The info map
     */
    static <R extends IInfo<R>> void loadMap(final R info, final Map<String, ?> map) {
        info.hook_getInfoConverter().fillAllFieldsInInfo(info, map);
    }

}



class InfoConverter {

    public static InfoConverter INSTANCE = new InfoConverter();

    protected static final String INFO_DATA_PREFIX    = "{%%%}";
    /**
     * Separates field-name from value in the stringified format
     */
    protected static final String INFO_DATA_SEPARATOR = "{@@@}";
    /**
     * Separates the info's bundle's symbolic-name form the info-class-name
     */
    protected static final String INFO_TYPE_SEPARATOR = "{###}";

    protected InfoConverter() {};

    protected String stringifyField(final Field f, final IInfo<?> info) throws IllegalAccessException {
        final Class<?> clazz = f.getType();
        if (IStringifyable.class.isAssignableFrom(clazz)) {
            return ((IStringifyable<?>) f.get(info)).stringify();
        } else {
            return String.valueOf(f.get(info));
        }
    }

    /**
     * Throws an exception if the field is not of type String.
     *
     * @param f
     * A field annotated with MessageInfoArgument
     */
    protected void validateMessageInfoArgField(final Field f) {
        if (!f.getType().isAssignableFrom(String.class)) throw new InfoException("Used MessageInfoArgument annotation on field " + f.getName() +
                                                                                 " which is not of type String");
    }

    /**
     * Throws an exception if the field is not of type String.
     *
     * @param f
     * A field annotated with InfoArgument
     */
    protected void validateInfoArgField(final Field f) {
        final Class<?> c = f.getType();
        if (!(c == Boolean.TYPE || c == Character.TYPE || c == Byte.TYPE || c == Short.TYPE || c == Integer.TYPE || c == Long.TYPE ||
              c == Float.TYPE || c == Double.TYPE || c == String.class || IStringifyable.class.isAssignableFrom(c))) throw new InfoException(
                      "Used InfoArgument annotation on field " + f.getName() + " which is not of a valid type!");
    }

    /**
     * Returns the InfoArgument fields
     *
     * @param fields
     * The fields to filter
     * @return A mutable list of fields annotated with InfoArgument.
     */
    protected MutableList<Field> getInfoArgumentFields(final Field[] fields) {
        return ArrayIterate.select(fields, f -> f.isAnnotationPresent(InfoArgument.class));
    }

    /**
     * Returns the MessageInfoArgument fields sorted in order of their value
     *
     * @param fields
     * The fields to filter
     * @return A mutable list of fields annotated with MessageInfoArgument sorted by their value.
     */
    protected MutableList<Field> getMessageInfoArgFieldsOrdered(final Field[] fields) {
        return ArrayIterate.select(fields, f -> f.isAnnotationPresent(MessageInfoArgument.class)).sortThis((f1, f2) -> f1.getAnnotation(
                MessageInfoArgument.class).value() - f2.getAnnotation(MessageInfoArgument.class).value());
    }

    protected <R extends IInfo<R>> MutableList<Field> getPersistableFields(final R info) {
        return ArrayIterate.select(info.getClass().getFields(), f -> f.isAnnotationPresent(InfoArgument.class) || f.isAnnotationPresent(
                MessageInfoArgument.class));
    }

    /**
     * Executes task for every @InfoArgument or @MessageInfoArgument field in the info
     *
     * @param info
     * The info to get the fields for
     * @param task
     * The task to execute
     */
    protected <R extends IInfo<R>> void doForEachPersistableField(final R info, final Consumer2<Field, String> task) {
        for (final Field f : getPersistableFields(info)) {
            task.accept(f, f.getName());
        }
    }

    protected <R extends IInfo<R>, T> void fillAllFieldsInInfo(final R info, final Map<String, T> map) {
        doForEachPersistableField(info, (f, fieldName) -> {
            if (map.containsKey(fieldName)) {
                final T value = map.get(fieldName);
                if (value != null) fillField(info, f, value);
            } else {
                throw new InfoException("Could not find map entry for @InfoArgument field [" + fieldName + "] in " + info.getClass().getSimpleName());
            }
        });
    }

    protected <R extends IInfo<R>, T> void fillFieldsPresentInMap(final R info, final Map<String, T> map) {
        doForEachPersistableField(info, (f, fieldName) -> {
            if (map.containsKey(fieldName)) {
                final T value = map.get(fieldName);
                if (value != null) fillField(info, f, value);
            }
        });
    }

    /**
     * Sets the value of field f in info to value.
     *
     * @param info
     * The info object
     * @param f
     * The field to set
     * @param value
     * The value to set the field to
     */
    protected <R extends IInfo<R>, T> void fillField(final R info, final Field f, final T value) {
        try {
            f.set(info, convertTo(f.getType(), value));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            ILTISException.wrap(e).rethrowUnchecked();
        }
    }

    static Map<String, String> convert(final MutableList<? extends IInfo<?>> infos) {
        return infos.toMap(InfoConverter::generateInfoKey, InfoConverter::generateInfoDataString);
    }

    private static String generateInfoKey(final IInfo<?> info) {
        final String name = info.getClass().getName();
        final String symbolicName = FrameworkUtil.getBundle(info.getClass()).getSymbolicName();
        return INFO_DATA_PREFIX + symbolicName + INFO_TYPE_SEPARATOR + name;
    }

    private static String generateInfoDataString(final IInfo<?> info) {
        return Maps.adapt(info.toMap()).keyValuesView().collect(p -> String.join(INFO_DATA_SEPARATOR, p.getOne(), p.getTwo())).makeString(
                INFO_DATA_SEPARATOR);
    }

    @SuppressWarnings("unchecked")
    private static <T, T2> T convertTo(final Class<T> type, final T2 value) {
        if (type.isInstance(value)) {
            return "null".equals(value) ? null : type.cast(value);
        }
        if (type == Boolean.TYPE && value instanceof Boolean) {
            return (T) value;
        }
        if (type == Character.TYPE && value instanceof Character) {
            return (T) value;
        }
        if (type == Byte.TYPE && value instanceof Byte) {
            return (T) value;
        }
        if (type == Short.TYPE && value instanceof Short) {
            return (T) value;
        }
        if (type == Integer.TYPE && value instanceof Integer) {
            return (T) value;
        }
        if (type == Long.TYPE && value instanceof Long) {
            return (T) value;
        }
        if (type == Float.TYPE && value instanceof Float) {
            return (T) value;
        }
        if (type == Double.TYPE && value instanceof Double) {
            return (T) value;
        }
        if (value instanceof String) {
            final String stringValue = (String) value;
            if (type == boolean.class || type == Boolean.class) {
                return (T) Boolean.valueOf(stringValue);
            } else if (type == Character.class || type == Character.class) {
                return (T) Character.valueOf(stringValue.isEmpty() ? '\0' : stringValue.charAt(0));
            } else if (type == byte.class || type == Byte.class) {
                return (T) Byte.valueOf(stringValue);
            } else if (type == short.class || type == Short.class) {
                return (T) Short.valueOf(stringValue);
            } else if (type == int.class || type == Integer.class) {
                return (T) Integer.valueOf(stringValue);
            } else if (type == long.class || type == Long.class) {
                return (T) Long.valueOf(stringValue);
            } else if (type == float.class || type == Float.class) {
                return (T) Float.valueOf(stringValue);
            } else if (type == double.class || type == Double.class) {
                return (T) Double.valueOf(stringValue);
            } else if (IStringifyable.class.isAssignableFrom(type)) { //
                return (T) fromString(type.asSubclass(IStringifyable.class), stringValue);
            }
        }
        throw new InfoException("Cannot parse object of type: " + value.getClass().getSimpleName() + " into " + type.getSimpleName());
    }

    /**
     * Load IStringifyable from the string
     */
    private static <I extends IStringifyable<I>> I fromString(final Class<I> clazz, final String string) {
        try {
            if (Enum.class.isAssignableFrom(clazz)) {
                /* If clazz is an enum, load the matching constant instead of creating a new one */
                final I[] enumConstants = clazz.getEnumConstants();
                for (final I stringifyable : enumConstants) {
                    if (stringifyable.stringify().equals(string)) return stringifyable;
                }
                throw new IllegalArgumentException("No enum constant for " + string + " in " + clazz.getSimpleName());
            } else {
                /* Create a new instance */
                return clazz.newInstance().unstringify(string);
            }
        } catch (IllegalAccessException | SecurityException | IllegalArgumentException | InstantiationException e) {
            ILTISException.wrap(e).rethrowUnchecked();
        }
        return null;
    }
}

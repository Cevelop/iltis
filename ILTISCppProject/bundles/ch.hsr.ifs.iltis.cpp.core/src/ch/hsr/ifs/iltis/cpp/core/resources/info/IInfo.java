package ch.hsr.ifs.iltis.cpp.core.resources.info;

import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.core.object.ICopyable;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;
import ch.hsr.ifs.iltis.cpp.core.collections.UnifiedMarkerArgumentMap;


public interface IInfo<T extends IInfo<T>> extends ICopyable<T> {

   default Map<String, String> toMap() {
      return ArrayIterate.select(getClass().getFields(), f -> f.getAnnotation(InfoArgument.class) != null).toMap(f -> f.getName(), f -> {
         try {
            return IStringifyable.class.isAssignableFrom(f.getType()) ? ((IStringifyable<?>) f.get(this)).stringify() : String.valueOf(f.get(this));
         } catch (IllegalArgumentException | IllegalAccessException e) {
            throw ILTISException.wrap(e).rethrowUnchecked();
         }
      });
   }

   static <R extends IInfo<R>> R fromMap(Supplier<R> constructor, Map<String, String> map) {
      R info = constructor.get();
      InfoConverter.fillFields(info, map);
      return info;
   }

   default Object[] toUnifiedMapArray() {
      return new UnifiedMarkerArgumentMap(toMap()).toArray();
   }

   static <R extends IInfo<R>> R fromUnifiedMapArray(Supplier<R> constructor, Object[] objects) {
      R info = constructor.get();
      InfoConverter.fillFields(info, new UnifiedMarkerArgumentMap(objects));
      return info;
   }
}



class InfoConverter {

   private static final String INFO_DATA_PREFIX    = "%%%";
   private static final String INFO_DATA_SEPARATOR = "@@@";
   private static final String INFO_TYPE_SEPARATOR = "###";

   static <R extends IInfo<R>, T> void fillFields(R info, Map<String, T> map) {
      ArrayIterate.select(info.getClass().getFields(), f -> f.getAnnotation(InfoArgument.class) != null).forEach(f -> {
         try {
            String fieldName = f.getName();
            if (!map.containsKey(fieldName)) {
               throw new InstantiationError("Could not find map entry for @InfoArgument field: " + fieldName + " in " + info.getClass()
                     .getSimpleName());
            } else {
               T value = map.get(fieldName);
               if (value != null) f.set(info, convertTo(f.getType(), value));
            }
         } catch (IllegalArgumentException | IllegalAccessException e) {
            ILTISException.wrap(e).rethrowUnchecked();
         }
      });
      if (info instanceof CompositeMarkerInfo) {
         ((CompositeMarkerInfo) info).loadInfos(createInfos(map).collectIf(i -> i instanceof MarkerInfo, i -> (MarkerInfo<?>) i));
      }
   }

   static Map<String, String> convert(MutableList<? extends IInfo<?>> infos) {
      return infos.toMap(InfoConverter::generateInfoKey, InfoConverter::generateInfoDataString);
   }

   private static String generateInfoKey(IInfo<?> info) {
      // TODO: Cleanup
      String name = info.getClass().getName();
      String symbolicName = FrameworkUtil.getBundle(info.getClass()).getSymbolicName();
      return INFO_DATA_PREFIX + symbolicName + INFO_TYPE_SEPARATOR + name;
   }

   private static String generateInfoDataString(IInfo<?> info) {
      return Maps.adapt(info.toMap()).keyValuesView().collect(p -> String.join(INFO_DATA_SEPARATOR, p.getOne(), p.getTwo())).makeString(
            INFO_DATA_SEPARATOR);
   }

   @SuppressWarnings("unchecked")
   private static <R extends IInfo<R>, T> MutableList<R> createInfos(Map<String, T> data) {
      MutableMap<String, String> infosData = Maps.adapt(data).keyValuesView().partition(p -> p.getOne().startsWith(INFO_DATA_PREFIX)).getSelected()
            .toMap(p -> p.getOne().substring(INFO_DATA_PREFIX.length()), p -> (String) p.getTwo());
      return infosData.keyValuesView().collect(p -> IInfo.fromUnifiedMapArray(() -> {
         try {
            String[] infoElement = p.getOne().split(INFO_TYPE_SEPARATOR);
            return (R) Platform.getBundle(infoElement[0]).loadClass(infoElement[1]).newInstance();
         } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw ILTISException.wrap(e).rethrowUnchecked();
         }
      }, p.getTwo().split(INFO_DATA_SEPARATOR))).toList();
   }

   @SuppressWarnings("unchecked")
   private static <T, T2> T convertTo(Class<T> type, T2 value) {
      if (type.isInstance(value)) { return "null".equals(value) ? null : type.cast(value); }
      if (type == Boolean.TYPE && value instanceof Boolean) { return (T) value; }
      if (type == Character.TYPE && value instanceof Character) { return (T) value; }
      if (type == Byte.TYPE && value instanceof Byte) { return (T) value; }
      if (type == Short.TYPE && value instanceof Short) { return (T) value; }
      if (type == Integer.TYPE && value instanceof Integer) { return (T) value; }
      if (type == Long.TYPE && value instanceof Long) { return (T) value; }
      if (type == Float.TYPE && value instanceof Float) { return (T) value; }
      if (type == Double.TYPE && value instanceof Double) { return (T) value; }
      if (value instanceof String) {
         String stringValue = (String) value;
         if (type == boolean.class || type == Boolean.class) {
            return (T) Boolean.valueOf(stringValue);
         } else if (type == Character.class || type == Character.class) {
            return (T) Character.valueOf((stringValue.isEmpty() ? '\0' : stringValue.charAt(0)));
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
      throw new InstantiationError("Cannot parse object of type: " + value.getClass().getSimpleName() + " into " + type.getSimpleName());
   }

   static <I extends IStringifyable<I>> I fromString(Class<I> clazz, String string) {
      try {
         if (Enum.class.isAssignableFrom(clazz)) {
            I[] enumConstants = clazz.getEnumConstants();
            for (final I stringifyable : enumConstants) {
               if (stringifyable.stringify().equals(string)) { return stringifyable; }
            }
            throw new IllegalArgumentException("No enum constant for " + string + " in " + clazz.getSimpleName());
         } else {
            return clazz.newInstance().unstringify(string);
         }
      } catch (IllegalAccessException | SecurityException | IllegalArgumentException | InstantiationException e) {
         ILTISException.wrap(e).rethrowUnchecked();
      }
      return null;
   }
}

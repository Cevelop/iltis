package ch.hsr.ifs.iltis.core.core.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;


public class ReflectionUtil {

   /**
    * Searches for any field (even protected, private, or final) and makes it accessible
    * 
    * @param target
    *        The instance on which to search for this field.
    * @param fieldName
    *        The name of the field to find
    * @return The field made accessible
    * @throws SecurityException
    * @throws NoSuchFieldException
    */
   public static <TargetType> Field getFieldAccessible(TargetType target, String fieldName) throws NoSuchFieldException {
      Class<?> clazz = target.getClass();
      for (Field[] fields = clazz.getDeclaredFields(); clazz.getSuperclass() != null; fields = (clazz = clazz.getSuperclass()).getDeclaredFields()) {
         for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
               field.setAccessible(true);
               return field;
            }
         }
      }
      throw new NoSuchFieldException();
   }

   /**
    * Sets the value of any field (even protected, private, or final) to the value passed
    * 
    * @param target
    *        The instance on which to manipulate the field
    * @param fieldName
    *        The name of the field to manipulate
    * @param value
    *        The value to set the field to
    * @return true iff the value has been changed
    */
   public static <TargetType, FieldType> boolean setValue(TargetType target, String fieldName, FieldType value) {
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
    * @param target
    *        The instance on which to manipulate the field
    * @param fieldName
    *        The name of the field to manipulate
    * @return true iff the value has been changed
    */
   public static <TargetType> boolean setNull(TargetType target, String fieldName) {
      return setValue(target, fieldName, null);
   }

   /**
    * Replaces an array referenced by a field with an empty array
    * 
    * @param target
    *        The instance on which to manipulate
    * @param fieldName
    *        The field to manipulate
    * @return true iff the array has been replaced
    */
   public static <TargetType> boolean emptyArray(TargetType target, String fieldName) {
      try {
         Field field = getFieldAccessible(target, fieldName);
         Class<?> clazz = field.getType();
         field.set(target, Array.newInstance(clazz.getComponentType(), Array.getLength(field.get(target))));
         return true;
      } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
         e.printStackTrace();
         return false;
      }
   }
}

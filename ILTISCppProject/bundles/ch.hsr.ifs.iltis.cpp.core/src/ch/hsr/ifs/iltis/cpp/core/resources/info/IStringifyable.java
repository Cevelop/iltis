package ch.hsr.ifs.iltis.cpp.core.resources.info;

/**
 * TODO mention needs static method unstingify(String)
 * 
 * @author void
 *
 * @param <T>
 */
public interface IStringifyable<T extends IStringifyable<T>> {

   /**
    * Default list separator for the top level.
    */
   public static String LIST_SEPARATOR = "{|LS|}";
   /**
    * Default pair separator for the top level.
    */
   public static String PAIR_SEPARATOR = "{|PS|}";

   T unstringify(String string);

   String stringify();
}

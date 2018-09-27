package ch.hsr.ifs.iltis.cpp.core.resources.info;

/**
 * TODO mention needs static method unstingify(String)
 * 
 * @author void
 *
 * @param <T>
 */
public interface IStringifyable<T extends IStringifyable<T>> {

   T unstringify(String string);

   String stringify();
}

package ch.hsr.ifs.iltis.core.functional.functions;

@FunctionalInterface
public interface Equals<T1, T2> {

   boolean equal(T1 l, T2 r);
}

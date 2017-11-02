package ch.hsr.ifs.iltis.core.functional;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

   public R apply(T t) throws E;
}

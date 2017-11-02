package ch.hsr.ifs.iltis.core.functional;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {

   public void accept(T t) throws E;
}

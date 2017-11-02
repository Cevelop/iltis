package ch.hsr.ifs.iltis.core.functional;

@FunctionalInterface
public interface ThrowingSupplier<R, E extends Exception> {

   public R get() throws E;
}

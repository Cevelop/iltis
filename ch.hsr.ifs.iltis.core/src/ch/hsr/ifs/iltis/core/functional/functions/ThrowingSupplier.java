package ch.hsr.ifs.iltis.core.functional.functions;

@FunctionalInterface
public interface ThrowingSupplier<R, E extends Exception> {

   public R get() throws E;
}

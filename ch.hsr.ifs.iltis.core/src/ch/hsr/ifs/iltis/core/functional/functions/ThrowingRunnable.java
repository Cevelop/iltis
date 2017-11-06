package ch.hsr.ifs.iltis.core.functional.functions;

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {

   public void run() throws E;
}

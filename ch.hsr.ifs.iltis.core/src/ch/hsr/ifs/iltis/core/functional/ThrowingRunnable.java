package ch.hsr.ifs.iltis.core.functional;

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {

   public void run() throws E;
}

package ch.hsr.ifs.iltis.core.functional.functions;

@FunctionalInterface
public interface Function2<P1, P2, R> {

   public R apply(P1 t1, P2 t2);
}

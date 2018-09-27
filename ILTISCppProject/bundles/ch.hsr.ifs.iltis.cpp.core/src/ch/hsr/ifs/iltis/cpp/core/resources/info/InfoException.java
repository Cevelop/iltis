package ch.hsr.ifs.iltis.cpp.core.resources.info;

public class InfoException extends RuntimeException {

   private static final long serialVersionUID = -1967047532469794683L;

   public InfoException(String message) {
      super(message, null, true, false);
   }

}

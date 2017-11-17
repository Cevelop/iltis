package ch.hsr.ifs.iltis.cpp.codan.marker;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

   private static final String BUNDLE_NAME = "com.cevelop.intwidthfixator.quickfixes.messages"; //$NON-NLS-1$
   public static String        PMRG_CannotCompile;
   public static String        PMRG_Invalid;
   public static String        PMRG_NotDefined;
   static {
      // initialize resource bundle
      NLS.initializeMessages(BUNDLE_NAME, Messages.class);
   }

   private Messages() {}
}

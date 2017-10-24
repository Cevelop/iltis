package ch.hsr.ifs.iltis.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;


public class ILTIS extends Plugin {

   private static BundleContext context;
   private static Plugin        plugin;

   public static String PLUGIN_ID;
   public static String PLUGIN_NAME;

   static BundleContext getContext() {
      return context;
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   @Override
   public void start(final BundleContext bundleContext) throws Exception {
      plugin = this;
      ILTIS.context = bundleContext;
      PLUGIN_ID = getBundle().getSymbolicName();
      PLUGIN_NAME = getBundle().getHeaders().get(Constants.BUNDLE_NAME);
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   @Override
   public void stop(final BundleContext bundleContext) throws Exception {
      ILTIS.context = null;
   }

   public static void log(final IStatus status) {
      plugin.getLog().log(status);
   }

   /**
    * Logs an internal error with the specified throwable
    *
    * @param e
    *        The exception to be logged
    */
   public static void log(final Throwable e) {
      log(new Status(IStatus.ERROR, ILTIS.PLUGIN_ID, 1, "Internal Error", e));
   }

   /**
    * Logs an error with the specified throwable and message
    *
    * @param e
    *        The exception to be logged
    * @param message
    *        An additional message
    */
   public static void log(final Throwable e, final String message) {
      log(new Status(IStatus.ERROR, ILTIS.PLUGIN_ID, 1, message, e));
   }

   /**
    * Logs an internal error with the specified message.
    *
    * @param message
    *        The error message to be logged
    */
   public static void log(final String message) {
      log(new Status(IStatus.ERROR, ILTIS.PLUGIN_ID, 1, message, null));
   }
}

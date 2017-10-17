package ch.hsr.ifs.iltis.core;

import org.eclipse.core.runtime.IStatus;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

  private static BundleContext context;
  private static ServiceTracker logServiceTracker;
  private static LogService logService;

  static BundleContext getContext() {
    return context;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(BundleContext bundleContext) throws Exception {
    Activator.context = bundleContext;
    logServiceTracker = new ServiceTracker(context, LogService.class.getName(), null);
    logServiceTracker.open();
    logService = (LogService) logServiceTracker.getService();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(BundleContext bundleContext) throws Exception {
    logServiceTracker.close();
    logServiceTracker = null;
    Activator.context = null;
  }

  public static void log(final IStatus status) {
    if (logService != null) {
      logService.log(status.getSeverity(), status.getMessage());
    }
  }
}

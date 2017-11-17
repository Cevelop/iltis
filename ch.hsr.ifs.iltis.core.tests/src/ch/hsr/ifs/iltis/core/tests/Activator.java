package ch.hsr.ifs.iltis.core.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin {

   public static final String PLUGIN_ID = "ch.hsr.ifs.iltis.core.tests";
   private static Activator   plugin;

   @Override
   public void start(final BundleContext context) throws Exception {
      super.start(context);
      plugin = this;
   }

   @Override
   public void stop(final BundleContext context) throws Exception {
      plugin = null;
      super.stop(context);
   }

   public static Activator getDefault() {
      return plugin;
   }
}

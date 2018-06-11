package ch.hsr.ifs.iltis.testing.tools.pasta.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


public class ColorUtil {

   public static ColorUtil instance = new ColorUtil();

   private ColorUtil() {}

   public final Color GOLDEN_YELLOW = new Color(Display.getCurrent(), 255, 168, 0);
   public final Color CUDA_GREEN    = new Color(Display.getCurrent(), 118, 185, 0);
   public final Color WHITE         = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);

   @Override
   protected void finalize() throws Throwable {
      if (!GOLDEN_YELLOW.isDisposed()) GOLDEN_YELLOW.dispose();
      if (!CUDA_GREEN.isDisposed()) CUDA_GREEN.dispose();
      super.finalize();
   }
}

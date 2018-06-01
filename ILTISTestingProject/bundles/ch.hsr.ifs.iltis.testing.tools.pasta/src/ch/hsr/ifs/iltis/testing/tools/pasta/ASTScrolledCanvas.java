package ch.hsr.ifs.iltis.testing.tools.pasta;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public class ASTScrolledCanvas extends Canvas {

   private static final int CURSOR_SIZE = 38;

   private Point   dragSource = new Point(0, 0);
   private boolean dragFlag   = false;

   private final ImageData grabImage  = AbstractUIPlugin.imageDescriptorFromPlugin(PastaPlugin.PLUGIN_ID, "/icons/closedhand.gif").getImageData(100);
   private final Cursor    grabCursor = new Cursor(getDisplay(), grabImage.scaledTo(CURSOR_SIZE, CURSOR_SIZE), CURSOR_SIZE / 2, CURSOR_SIZE / 4);

   private final ImageData openImage  = AbstractUIPlugin.imageDescriptorFromPlugin(PastaPlugin.PLUGIN_ID, "/icons/openhand.gif").getImageData(100);
   private final Cursor    openCursor = new Cursor(getDisplay(), openImage.scaledTo(CURSOR_SIZE, CURSOR_SIZE), CURSOR_SIZE / 2, CURSOR_SIZE / 4);

   ASTScrolledCanvas(final ScrolledComposite parent, final int style) {
      super(parent, style | SWT.DOUBLE_BUFFERED);
      setCursor(openCursor);
      addDragFunctionality();
   }

   @Override
   public ScrolledComposite getParent() {
      return (ScrolledComposite) super.getParent();
   }

   private void addDragFunctionality() {
      addMouseListener(new MouseAdapter() {

         @Override
         public void mouseDown(final MouseEvent e) {
            dragSource.x = e.x;
            dragSource.y = e.y;
            setCursor(grabCursor);
         }

         @Override
         public void mouseUp(final MouseEvent e) {
            dragFlag = false;
            setCursor(openCursor);
         }

      });

      addMouseMoveListener(new MouseMoveListener() {

         @Override
         public void mouseMove(final MouseEvent e) {
            if (dragFlag) {
               ASTScrolledCanvas.this.getParent().setOrigin(ASTScrolledCanvas.this.getParent().getOrigin().x + (dragSource.x - e.x),
                     ASTScrolledCanvas.this.getParent().getOrigin().y + (dragSource.y - e.y));
            }
         }

      });

      addDragDetectListener(new DragDetectListener() {

         @Override
         public void dragDetected(final DragDetectEvent e) {
            dragFlag = true;
         }

      });
   }

   @Override
   protected void finalize() throws Throwable {
      super.finalize();
      if (!grabCursor.isDisposed()) grabCursor.dispose();
      if (!openCursor.isDisposed()) openCursor.dispose();
   }

}

package ch.hsr.ifs.iltis.testing.tools.pasta;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.hsr.ifs.iltis.testing.tools.pasta.util.ColorUtil;
import ch.hsr.ifs.iltis.testing.tools.pasta.visualtree.ASTCanvas;


public class ASTWidget extends ScrolledComposite {

    {
        addDisposeListener(e -> {
            grabCursor.dispose();
            openCursor.dispose();
        });
    }

    /* CURSORS */
    private static final int CURSOR_SIZE = 38;

    private final ImageData grabImage  = AbstractUIPlugin.imageDescriptorFromPlugin(PastaPlugin.PLUGIN_ID, "/icons/closedhand.gif").getImageData(100);
    private final Cursor    grabCursor = new Cursor(getDisplay(), grabImage.scaledTo(CURSOR_SIZE, CURSOR_SIZE), CURSOR_SIZE / 2, CURSOR_SIZE / 4);

    private final ImageData openImage  = AbstractUIPlugin.imageDescriptorFromPlugin(PastaPlugin.PLUGIN_ID, "/icons/openhand.gif").getImageData(100);
    private final Cursor    openCursor = new Cursor(getDisplay(), openImage.scaledTo(CURSOR_SIZE, CURSOR_SIZE), CURSOR_SIZE / 2, CURSOR_SIZE / 4);

    /* CANVAS */
    private Point   dragSource = new Point(0, 0);
    private boolean dragFlag   = false;

    private final ASTCanvas canvas = new ASTCanvas(this, this::ensureBoxIsVisible, SWT.BACKGROUND);
    {
        canvas.setCursor(openCursor);
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(final MouseEvent e) {
                dragSource.x = e.x;
                dragSource.y = e.y;
                canvas.setCursor(grabCursor);
            }

            @Override
            public void mouseUp(final MouseEvent e) {
                dragFlag = false;
                canvas.setCursor(openCursor);
            }

        });

        canvas.addMouseMoveListener(e -> {
            if (dragFlag) {
                setOrigin(getOrigin().x + (dragSource.x - e.x), getOrigin().y + (dragSource.y - e.y));
            }
        });

        canvas.addDragDetectListener(e -> dragFlag = true);
    }

    /* SHOW NODE LISTENER */

    public void showSelectedNode(final IASTNode sParent) {
        canvas.showSelectedNode(sParent);
    }

    /* NODE VIEW LISTENER */

    public void setListener(final NodeSelectionListener listener) {
        canvas.setListener(listener);
    }

    /* WIDGET */

    public ASTWidget(final Composite parent) {
        super(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        setAlwaysShowScrollBars(true);
        setBackground(ColorUtil.instance.WHITE);
        setContent(canvas);
    }

    public void resetAndDrawAST(final IASTTranslationUnit ast) {
        if (ast != null) canvas.cleanAndRedrawAST(ast);
    }

    public void ensureBoxIsVisible(Rectangle itemRect) {
        checkWidget();
        if (itemRect == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);

        itemRect = getDisplay().map(canvas, this, itemRect);

        Rectangle area = getClientArea();
        Point origin = getOrigin();
        if (itemRect.x < 0) {
            origin.x = Math.max(0, origin.x + itemRect.x);
        } else if (area.width < itemRect.x + itemRect.width) {
            origin.x = Math.max(0, origin.x + itemRect.x + Math.min(itemRect.width, area.width) - area.width);
        }
        if (itemRect.y < 0) {
            origin.y = Math.max(0, origin.y + itemRect.y);
        } else if (area.height < itemRect.y + itemRect.height) {
            origin.y = Math.max(0, origin.y + itemRect.y + Math.min(itemRect.height, area.height) - area.height);
        }
        setOrigin(origin);
    }

}

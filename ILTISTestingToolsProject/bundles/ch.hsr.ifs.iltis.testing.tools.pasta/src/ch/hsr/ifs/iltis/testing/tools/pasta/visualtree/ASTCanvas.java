package ch.hsr.ifs.iltis.testing.tools.pasta.visualtree;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import ch.hsr.ifs.iltis.core.core.functional.functions.Consumer;

import ch.hsr.ifs.iltis.testing.tools.pasta.ASTWidget;
import ch.hsr.ifs.iltis.testing.tools.pasta.NodeSelectionListener;
import ch.hsr.ifs.iltis.testing.tools.pasta.PastaPlugin;
import ch.hsr.ifs.iltis.testing.tools.pasta.plugin.preferences.PreferenceConstants;
import ch.hsr.ifs.iltis.testing.tools.pasta.tree.NodeVisitor;
import ch.hsr.ifs.iltis.testing.tools.pasta.util.ColorUtil;
import ch.hsr.ifs.iltis.testing.tools.pasta.util.VASTNodeMouseTrackAndClickAdapter;


public class ASTCanvas extends Canvas {

    private VisualASTNode root;

    private static final float SIBLING_DISTANCE  = 4;
    private static final float BRANCH_DISTANCE   = 15;
    static final int           VERTICAL_DISTANCE = 20;
    /** The full height of the oval if 360 degrees are drawn */
    private static final int   arcHeight         = VERTICAL_DISTANCE;
    /** The full width of the oval if 360 degrees are drawn */
    private static final int   arcWidth          = (arcHeight * 3) / 2;

    private static final int DEFAULT_NODE_HEIGHT = 20;
    private static int       nodeHeight          = DEFAULT_NODE_HEIGHT;

    protected int getNodeHeight() {
        return nodeHeight;
    }

    protected void setNodeHeight(int newHeight) {
        nodeHeight = Math.max(newHeight, DEFAULT_NODE_HEIGHT);
    }

    /* PREFERENCES */
    private final IPreferenceStore prefStore;

    boolean selectByRightClick;
    boolean selectByLeftClick;
    boolean selectByHovering;

    private void updateSelectors(String preferenceValue) {
        selectByRightClick = preferenceValue.equals(PreferenceConstants.P_SELECT_BY_RIGHT_CLICK);
        selectByLeftClick = preferenceValue.equals(PreferenceConstants.P_SELECT_BY_LEFT_CLICK);
        selectByHovering = preferenceValue.equals(PreferenceConstants.P_SELECT_BY_MOUSE_OVER);
    }

    {
        prefStore = PastaPlugin.getDefault().getPreferenceStore();
        updateSelectors(prefStore.getString(PreferenceConstants.P_HOW_TO_SELECT));
        prefStore.addPropertyChangeListener(event -> {
            if (event.getProperty().equals(PreferenceConstants.P_HOW_TO_SELECT)) {
                updateSelectors((String) event.getNewValue());
            }
        });
    }

    private NodeSelectionListener listener;

    private VisualASTNode lastOperatedNode;

    private Consumer<Rectangle> focusParentOn;

    private ASTWidget uiParent;

    public ASTCanvas(final ASTWidget uiParent, Consumer<Rectangle> focusParentOn, final int style) {
        super(uiParent, style | SWT.DOUBLE_BUFFERED);
        this.focusParentOn = focusParentOn;
        this.uiParent = uiParent;
        applyTheme();

        addPaintListener(e -> {
            if (lastOperatedNode != null) {
                focusParentOn.accept(getAreaOfChildren(lastOperatedNode));
                lastOperatedNode = null;
            }
            if (root != null) {
                root.visit(node -> {
                    if (node.button.isVisible()) {
                        drawArrowsToChildren(e.gc, node);
                        return NodeVisitor.AfterVisitBehaviour.Continue;
                    }
                    return NodeVisitor.AfterVisitBehaviour.Abort;
                });
            }
        });
    }

    private void applyTheme() {
        setBackground(ColorUtil.instance.WHITE);
    }

    public void cleanAndRedrawAST(final IASTTranslationUnit ast) {
        clear();
        root = createTreeRoot(this, ast);
        root.button.setVisible(true);
        refresh(root);
    }

    private void refresh(VisualASTNode node) {
        root.adjust(SIBLING_DISTANCE, BRANCH_DISTANCE);
        root.updateNodePositions();
        pack();
        focusParentOn.accept(getAreaOfChildren(node));
    }

    /* SHOW NODE LISTENER */

    public void showSelectedNode(final IASTNode sParent) {
        VisualASTNode node = root.find(sParent);
        node.showNode();
        refresh(node);
    }

    private <T1> void doIfNotNull(T1 value, Consumer<T1> task) {
        if (value != null) task.accept(value);
    }

    private Rectangle getAreaOfChildren(VisualASTNode node) {
        final Rectangle buttonBounds = node.button.getBounds();
        doIfNotNull(node.leftMostChild(), child -> buttonBounds.add(child.button.getBounds()));
        doIfNotNull(node.rightMostChild(), child -> buttonBounds.add(child.button.getBounds()));
        return buttonBounds;
    }

    /* NODE VIEW LISTENER */

    public void setListener(final NodeSelectionListener listener) {
        this.listener = listener;
    }

    private void handleNodeSelection(final VisualASTNode astNode) {
        setNodeInNodeView(astNode);
        selectNodeInEditor(astNode);
    }

    private void setNodeInNodeView(final VisualASTNode astNode) {
        if (listener != null) {
            listener.nodeSelected(astNode.data);
        }
    }

    private void selectNodeInEditor(final VisualASTNode astNode) {
        IASTFileLocation fileLocation = astNode.data.getFileLocation();

        for (IASTPreprocessorIncludeStatement stmt = fileLocation.getContextInclusionStatement(); stmt != null; stmt = fileLocation
                .getContextInclusionStatement()) {
            fileLocation = stmt.getFileLocation();
        }
        final TextSelection textSelection = new TextSelection(fileLocation.getNodeOffset(), fileLocation.getNodeLength());
        CUIPlugin.getActivePage().getActiveEditor().getEditorSite().getSelectionProvider().setSelection(textSelection);
    }

    /* NODES */

    protected void setLastOperatedNode(VisualASTNode node) {
        this.lastOperatedNode = node;
    }

    private VisualASTNode createTreeRoot(final ASTCanvas canvas, final IASTTranslationUnit ast) {
        VASTNodeMouseTrackAndClickAdapter mouseAdapter = new VASTNodeMouseTrackAndClickAdapter() {

            @Override
            public void mouseDown(final MouseEvent e, VisualASTNode node) {
                if (e.button == 3 && selectByRightClick || e.button == 1 && selectByLeftClick) {
                    handleNodeSelection(node);
                }
                if (e.button == 1) {
                    toggleNode(node);
                    setLastOperatedNode(node);
                }
            }

            @Override
            public void mouseHover(final MouseEvent e, VisualASTNode node) {
                if (selectByHovering) {
                    handleNodeSelection(node);
                }
            }

        };
        return VisualASTNode.createTree(canvas, ast, mouseAdapter);
    }

    private void toggleNode(final VisualASTNode node) {
        if (node.isTreatedAsLeaf()) {
            node.expand();
        } else {
            node.collaps();
        }
        refresh(node);
    }

    @Override
    public void pack() {
        Point computeSize = computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Point parentSize = uiParent.getSize();
        setSize(Math.max(computeSize.x, parentSize.x), Math.max(computeSize.y, parentSize.y));
    }

    private static void drawArrowsToChildren(final GC gc, VisualASTNode node) {
        if (node.hasChildren()) {
            Point from = node.getArrowSource();
            for (VisualASTNode child : node.getChildren()) {
                Point to = child.getArrowTarget();
                if (Math.abs(from.x - to.x) < arcWidth) {
                    drawStraightArrow(gc, from, to);
                } else {
                    if (child == node.leftMostChild() || child == node.rightMostChild()) {
                        drawCurvedArrow(gc, from, to);
                    } else {
                        drawCurvedArrowEnd(gc, from, to);
                        drawCurvedArrowHead(gc, from, to);
                    }
                }
            }
        }
    }

    private static void drawStraightArrow(final GC gc, Point from, Point to) {
        gc.drawLine(from.x, from.y, to.x, to.y);
        drawArrowHead(gc, to.x, to.y, Math.atan2(to.y - from.y, to.x - from.x));
    }

    private static void drawCurvedArrow(final GC gc, Point from, Point to) {
        drawCurvedArrowStart(gc, from, to);
        drawCurvedArrowShaft(gc, from, to);
        drawCurvedArrowEnd(gc, from, to);
        drawCurvedArrowHead(gc, from, to);
    }

    private static void drawCurvedArrowStart(final GC gc, Point from, Point to) {
        if (from.x > to.x) {
            gc.drawArc(from.x - arcWidth, from.y - arcHeight / 2, arcWidth, arcHeight, 270, 90);
        } else {
            gc.drawArc(from.x, from.y - arcHeight / 2, arcWidth, arcHeight, 270, -90);
        }
    }

    private static void drawCurvedArrowShaft(final GC gc, Point from, Point to) {
        final int centerY = (from.y + to.y) / 2;
        if (from.x > to.x) {
            gc.drawLine(from.x - arcWidth / 2, centerY, to.x + arcWidth / 2, centerY);
        } else {
            gc.drawLine(from.x + arcWidth / 2, centerY, to.x - arcWidth / 2, centerY);
        }
    }

    private static void drawCurvedArrowEnd(final GC gc, Point from, Point to) {
        final int centerY = (from.y + to.y) / 2;
        if (from.x > to.x) {
            gc.drawArc(to.x, centerY, arcWidth, arcHeight, 180, -90);
        } else {
            gc.drawArc(to.x - arcWidth, centerY, arcWidth, arcHeight, 90, -90);
        }
    }

    private static void drawCurvedArrowHead(final GC gc, Point from, Point to) {
        if (from.x > to.x) {
            drawArrowHead(gc, --to.x, to.y, Math.toRadians(90 + 30));
        } else {
            drawArrowHead(gc, ++to.x, to.y, Math.toRadians(90 - 30));
        }
    }

    private static void drawArrowHead(final GC gc, int x2, final int y2, double theta) {
        final double arrowAngle = Math.toRadians(30.0);
        final double arrowLength = 8.0;

        gc.drawLine((int) (x2 - arrowLength * Math.cos(theta - arrowAngle)), (int) (y2 - arrowLength * Math.sin(theta - arrowAngle)), x2, y2);
        gc.drawLine((int) (x2 - arrowLength * Math.cos(theta + arrowAngle)), (int) (y2 - arrowLength * Math.sin(theta + arrowAngle)), x2, y2);
    }

    private void clear() {
        if (root != null && !root.isDisposed()) root.dispose();
    }

}

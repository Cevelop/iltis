package ch.hsr.ifs.iltis.testing.tools.pasta.visualtree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.utils.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.osgi.framework.Bundle;

import ch.hsr.ifs.iltis.testing.tools.pasta.tree.TreeNode;
import ch.hsr.ifs.iltis.testing.tools.pasta.util.ColorUtil;
import ch.hsr.ifs.iltis.testing.tools.pasta.util.VASTNodeMouseTrackAndClickAdapter;


public class VisualASTNode extends TreeNode<IASTNode, VisualASTNode> {

   /**
    * Starts a new VisualASTTree
    * 
    * @param uiParent
    *        The ASTCanvas on which this node will be drawn
    * @param node
    *        The IASTNode held by this VisualASTNode
    * @param trackAndClickAdapter
    *        This will be CLONED and adapted for the newly constructed node.
    */
   public static VisualASTNode createTree(ASTCanvas parent, IASTTranslationUnit ast, VASTNodeMouseTrackAndClickAdapter trackAndClickAdapter) {
      return new VisualASTNode(parent, ast, trackAndClickAdapter);
   }

   public Button                                   button;
   private final ASTCanvas                         uiParent;
   private final VASTNodeMouseTrackAndClickAdapter trackAndClickAdapter;
   protected boolean                               childrenLoaded = false;
   private boolean                                 isDisposed;

   protected VisualASTNode(ASTCanvas uiParent, IASTNode node, VASTNodeMouseTrackAndClickAdapter trackAndClickAdapter) {
      super(node);
      this.trackAndClickAdapter = trackAndClickAdapter.cloneForNode(this);
      this.uiParent = uiParent;
      this.button = createButton(node);
   }

   @Override
   public void addChild(VisualASTNode child) {
      children.add(child);
      child.setNumber(children.size());
      child.setY(y() + uiParent.getNodeHeight() + ASTCanvas.VERTICAL_DISTANCE);
      child.setParent(this);
   }

   @Override
   public List<VisualASTNode> getChildren() {
      if (!childrenLoaded) {
         lazyLoadChildren();
      }
      return children;
   }

   public void showNode() {
      propagateUp((node) -> {
         if (node.parent != null) node.parent.expand();
      });
   }

   private void lazyLoadChildren() {
      for (IASTNode node : data.getChildren()) {
         VisualASTNode visualChild = new VisualASTNode(uiParent, node, trackAndClickAdapter);
         addChild(visualChild);
      }
      childrenLoaded = true;
   }

   public void expand() {
      for (VisualASTNode child : getChildren()) {
         child.setVisible(true);
      }
      if (!children.isEmpty()) {
         treatAsLeaf(false);
      }
   }

   public void collaps() {
      for (VisualASTNode child : children) {
         child.collaps();
         child.setVisible(false);
      }
      treatAsLeaf(true);
   }

   public void setVisible(boolean visible) {
      button.setVisible(visible);
   }

   protected boolean hasChildren() {
      return !((childrenLoaded && children.isEmpty()) || (!childrenLoaded && data.getChildren().length == 0) || treatAsLeaf);
   }

   public VisualASTNode find(IASTNode node) {
      ArrayList<IASTNode> reversedNodeThread = new ArrayList<>();
      for (IASTNode n = node; n.getParent() != null; n = n.getParent()) {
         reversedNodeThread.add(n);
      }
      return find(reversedNodeThread);
   }

   private VisualASTNode find(ArrayList<IASTNode> reversedNodeThread) {
      if (reversedNodeThread.isEmpty()) return this;

      for (VisualASTNode child : getChildren()) {
         if (child.data.getRawSignature().equals(reversedNodeThread.get(reversedNodeThread.size() - 1).getRawSignature())) {
            reversedNodeThread.remove(reversedNodeThread.size() - 1);
            return child.find(reversedNodeThread);
         }
      }

      throw new IllegalStateException("Trying to find a node which does not seem to exist...");
   }

   private Button createButton(IASTNode node) {
      final Button button = new Button(uiParent, SWT.FLAT);
      button.setText(getNormalizedCaption(node));

      /* Set listener */
      button.addMouseTrackListener(trackAndClickAdapter);
      button.addMouseListener(trackAndClickAdapter);
      button.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseDown(MouseEvent e) {
            if (e.button == 1 && !childrenLoaded) lazyLoadChildren();
         }
      });

      /* Make real leaf-nodes unclickable */
      button.setEnabled(node.getChildren().length > 0);

      styleButton(button, node);
      final Point minButtonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      uiParent.setNodeHeight(Math.max(uiParent.getNodeHeight(), minButtonSize.y));
      setWidth(minButtonSize.x);
      return button;
   }

   private void styleButton(Button button, IASTNode astNode) {
      button.setVisible(false);
      button.getFont().getFontData()[0].setHeight(10);
      button.setCursor(new Cursor(button.getDisplay(), SWT.CURSOR_ARROW));

      if (astNode instanceof ICPPASTTranslationUnit) {
         button.setBackground(ColorUtil.instance.GOLDEN_YELLOW);
         button.getFont().getFontData()[0].setStyle(SWT.BOLD);
      } else {
         /* TODO replace once own cuda parser was implemented */
         Bundle bundle = Platform.getBundle("com.nvidia.cuda.ui");
         if (bundle != null) {
            if (isInstanceOf(bundle, "com.nvidia.cuda.ui.editor.language.ast.KernelCallExpression", astNode) || isInstanceOf(bundle,
                  "com.nvidia.cuda.ui.editor.language.ast.CudaASTFieldReference", astNode) || isInstanceOf(bundle,
                        "com.nvidia.cuda.ui.editor.language.ast.CudaASTIdExpression", astNode) || isInstanceOf(bundle,
                              "com.nvidia.cuda.ui.editor.language.ast.CudaASTSimpleDeclSpecifier", astNode)) {
               button.setBackground(ColorUtil.instance.CUDA_GREEN);
            }
         }
      }
   }

   private boolean isInstanceOf(Bundle bundle, String typeName, Object obj) {
      try {
         return bundle.loadClass(typeName).isInstance(obj);
      } catch (ClassNotFoundException e) {
         return false;
      }
   }

   private String getNormalizedCaption(IASTNode node) {
      if (node.getChildren().length != 0) {
         return node.getClass().getSimpleName();
      } else {
         return node.getRawSignature().replaceAll("\\{[\\S\\s]*\\}", "{ ... }");
      }
   }

   public void propagateDownDepthFirst(final Consumer<VisualASTNode> task, boolean loadChildren) {
      if (loadChildren || childrenLoaded) {
         for (VisualASTNode child : children) {
            child.propagateDownDepthFirst(task, loadChildren);
         }
      }
      task.accept((VisualASTNode) this);
   }

   public void clearChildren() {
      childrenLoaded = false;
      super.clearChildren();
   }

   public boolean isDisposed() {
      return isDisposed;
   }

   public void dispose() {
      if (isDisposed()) return;

      propagateDownDepthFirst(node -> {
         if (node.button != null) {
            node.setVisible(false);
            node.button.dispose();
            node.button = null;
         }
         node.clearChildren();
      }, false);

      isDisposed = true;
   }

   public Point getArrowSource() {
      return new Point((int) (x() + (width() / 2)), (int) y() + uiParent.getNodeHeight());
   }

   public Point getArrowTarget() {
      return new Point((int) (x() + (width() / 2)), (int) y());
   }

   public void updateNodePositions() {
      button.setBounds((int) x(), (int) y(), (int) width(), uiParent.getNodeHeight());
      if (hasChildren()) {
         for (final VisualASTNode child : children) {
            child.updateNodePositions();
         }
      }
   }

}

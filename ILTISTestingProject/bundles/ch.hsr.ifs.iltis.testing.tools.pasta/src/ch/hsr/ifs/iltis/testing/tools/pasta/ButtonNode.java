package ch.hsr.ifs.iltis.testing.tools.pasta;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.swt.widgets.Button;


public class ButtonNode {

   public final Button   button;
   public final IASTNode astNode;

   public ButtonNode(Button button, IASTNode astNode) {
      this.button = button;
      this.astNode = astNode;
   }
}

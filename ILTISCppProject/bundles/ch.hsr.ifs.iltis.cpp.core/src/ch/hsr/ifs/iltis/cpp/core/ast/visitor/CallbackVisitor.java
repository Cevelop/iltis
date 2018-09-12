package ch.hsr.ifs.iltis.cpp.core.ast.visitor;

import java.util.function.Function;

import org.eclipse.cdt.core.dom.ast.ASTGenericVisitor;
import org.eclipse.cdt.core.dom.ast.IASTNode;


public class CallbackVisitor extends ASTGenericVisitor {

   private Function<IASTNode, Integer> callback;

   public CallbackVisitor(Function<IASTNode, Integer> callback) {
      super(true);
      this.callback = callback;
   }

   @Override
   protected int genericVisit(IASTNode node) {
      return callback.apply(node);
   }
}

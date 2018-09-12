package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.collections.impl.utility.ArrayIterate;


public enum CPPUnaryOperator implements ICPPOperator {

   BRACKETED_PRIMARY(15, IASTUnaryExpression.op_bracketedPrimary), //
   NOEXCEPT(17, IASTUnaryExpression.op_noexcept), //
   POSTFIX_INCR(20, IASTUnaryExpression.op_postFixIncr), //
   POSTFIX_DECR(20, IASTUnaryExpression.op_postFixDecr), //
   PREFIX_INCR(30, IASTUnaryExpression.op_prefixIncr), //
   PREFIX_DECR(30, IASTUnaryExpression.op_prefixDecr), //
   PLUS(30, IASTUnaryExpression.op_plus), //
   MINUS(30, IASTUnaryExpression.op_minus), //
   NOT(30, IASTUnaryExpression.op_not), //
   TILDE(30, IASTUnaryExpression.op_tilde), //
   STAR(30, IASTUnaryExpression.op_star), //
   AMPER(30, IASTUnaryExpression.op_amper), //
   SIZEOF(30, IASTUnaryExpression.op_sizeof), //
   SIZEOF_PARAMETER_PACK(30, IASTUnaryExpression.op_sizeofParameterPack), //
   TYPEID(30, IASTUnaryExpression.op_typeid), //
   THROW(160, IASTUnaryExpression.op_throw);

   private int relativePrecedence;
   private int equivalentCDTOp;

   private CPPUnaryOperator(int relativePrecedence, int equivalentCDTOp) {
      this.relativePrecedence = relativePrecedence;
      this.equivalentCDTOp = equivalentCDTOp;
   }

   public int getCDTOperator() {
      return equivalentCDTOp;
   }

   @Override
   public Associativity getAssociativity() {
      return Associativity.NOTATIONAL_LEFT;
   }

   @Override
   public int getRelativePrecedence() {
      return relativePrecedence;
   }

   public boolean operandNeedsWraping(IASTInitializerClause operand) {
      if (operand instanceof ICPPASTSimpleTypeConstructorExpression) {
         return false;
      } else if (operand instanceof IASTFunctionCallExpression) {
         return getRelativePrecedence() <= CPPOtherOperators.FUNCTION_CALL.getRelativePrecedence();
      } else if (operand instanceof IASTBinaryExpression) {
         return getRelativePrecedence() <= CPPBinaryOperator.getForBinaryExpr(((IASTBinaryExpression) operand)).getRelativePrecedence();
      } else if (operand instanceof IASTUnaryExpression) {
         return getRelativePrecedence() <= CPPUnaryOperator.getForUnaryExpr(((IASTUnaryExpression) operand)).getRelativePrecedence();
      } else if (operand instanceof IASTExpressionList) {
         return getRelativePrecedence() <= CPPOtherOperators.COMMA_OPERATOR.getRelativePrecedence();
      } else if (operand instanceof IASTConditionalExpression) {
         return getRelativePrecedence() <= CPPOtherOperators.CONDITIONAL.getRelativePrecedence();
      } else if (operand instanceof IASTArraySubscriptExpression) {
         return getRelativePrecedence() <= CPPOtherOperators.ARRAY_SUBSCRIPT.getRelativePrecedence();
      } else if (operand instanceof IASTTypeIdExpression) {
         return getRelativePrecedence() <= CPPOtherOperators.TYPEID.getRelativePrecedence();
      } else {
         return true;
      }
   }

   public static CPPUnaryOperator getForCDTOperator(int unaryOperator) {
      return ArrayIterate.detect(values(), v -> v.equivalentCDTOp == unaryOperator);
   }

   public static CPPUnaryOperator getForUnaryExpr(IASTUnaryExpression expr) {
      return getForCDTOperator(expr.getOperator());
   }

}

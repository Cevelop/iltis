package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.collections.impl.utility.ArrayIterate;


/**
 * Mapping for the stupid {@code int op_XYZ} constants representing operators in {@link IASTBinaryExpression}.
 * 
 * @author tstauber
 *
 */
public enum CPPBinaryOperator implements ICPPOperator {
   MULTIPLY(50, Associativity.MATHEMATICAL, IASTBinaryExpression.op_multiply), //
   PLUS(60, Associativity.MATHEMATICAL, IASTBinaryExpression.op_plus), //
   DIVIDE(50, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_divide), //
   MODULO(50, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_modulo), //
   MINUS(60, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_minus), //
   SHIFT_LEFT(70, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_shiftLeft), //
   SHIFT_RIGHT(70, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_shiftRight), //
   //SPACESHIP(80,Associativity.NOTATIONAL_LEFT , IASTBinaryExpression.op_spaceship),//
   LESS_THAN(90, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_lessThan), //
   LESS_EQUAL(90, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_lessEqual), //
   GREATER_THAN(90, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_greaterThan), //
   GREATER_EQUAL(90, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_greaterEqual), //
   EQUALS(100, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_equals), //
   NOT_EQUALS(100, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_notequals), //
   BINARY_AND(110, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_binaryAnd), //
   BINARY_XOR(120, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_binaryXor), //
   BINARY_OR(130, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_binaryOr), //
   LOGICAL_AND(140, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_logicalAnd), //
   LOGICAL_OR(150, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_logicalOr), //
   ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_assign), //
   PLUS_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_plusAssign), //
   MINUS_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_minusAssign), //
   MULTIPLY_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_multiplyAssign), //
   DIVIDE_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_divideAssign), //
   MODULO_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_moduloAssign), //
   SHIFTLEFT_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_shiftLeftAssign), //
   SHIFTRIGHT_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_shiftRightAssign), //
   BINARYAND_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_binaryAndAssign), //
   BINARYXOR_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_binaryXorAssign), //
   BINARYOR_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_binaryOrAssign);//

   private int           relativePrecedence;
   private Associativity associativity;
   private int           equivalentCDTOp;

   CPPBinaryOperator(int relativePrecedence, Associativity associativity, int equivalentCDTOp) {
      this.relativePrecedence = relativePrecedence;
      this.associativity = associativity;
      this.equivalentCDTOp = equivalentCDTOp;
   }

   public int getCDTOperator() {
      return equivalentCDTOp;
   }

   @Override
   public Associativity getAssociativity() {
      return associativity;
   }

   @Override
   public int getRelativePrecedence() {
      return relativePrecedence;
   }

   public boolean leftOperandNeedsWraping(IASTExpression leftOperand) {
      return operandNeedsWraping(leftOperand, ExprOperandPos.LHS);
   }

   public boolean rightOperandNeedsWraping(IASTInitializerClause rightOperand) {
      return operandNeedsWraping(rightOperand, ExprOperandPos.RHS);
   }

   protected boolean operandNeedsWraping(IASTInitializerClause operand, ExprOperandPos operandPos) {
      if (operand instanceof IASTInitializerList) {
         return false;
      } else if (operand instanceof IASTTypeIdExpression) {
         return false;
      } else if (operand instanceof IASTIdExpression) {
         return false;
      } else if (operand instanceof ICPPASTSimpleTypeConstructorExpression) {
         return false;
      } else if (operand instanceof IASTFunctionCallExpression) {
         return getRelativePrecedence() <= CPPOtherOperators.FUNCTION_CALL.getRelativePrecedence();
      } else if (operand instanceof IASTExpressionList) {
         return getRelativePrecedence() <= CPPOtherOperators.COMMA_OPERATOR.getRelativePrecedence();
      } else if (operand instanceof IASTUnaryExpression) {
         return getRelativePrecedence() <= CPPUnaryOperator.getForUnaryExpr((IASTUnaryExpression) operand).getRelativePrecedence();
      } else if (operand instanceof IASTArraySubscriptExpression) {
         return getRelativePrecedence() <= CPPOtherOperators.ARRAY_SUBSCRIPT.getRelativePrecedence();
      } else if (operand instanceof IASTBinaryExpression) {
         int p = getRelativePrecedence() - CPPBinaryOperator.getForBinaryExpr(((IASTBinaryExpression) operand)).getRelativePrecedence();
         if (p > 0) {
            return true; /* Example [x + y] * [z] or [x] * [y + z] the operand needs grouping */
         } else if (p < 0) {
            return false; /* Example [x * y] + [z] or [x] = [y + z] the operand does not need grouping */
         } else {
            switch (operandPos) {
            case LHS:
               /* Not simplified for better readability */
               switch (getAssociativity()) {
               case MATHEMATICAL:
                  return false; /* Example [x * y] * [z] or [x + y] + [z] the operand does not need grouping */
               case NOTATIONAL_LEFT:
                  return false; /* Example [x / y] / [z] or [x - y] - [z] the operand does not need grouping */
               case NOTATIONAL_RIGHT:
                  return true; /* Example [x = y] = [z] or [x = y] += [z] the operand needs grouping */
               case NOTATIONAL_NONE:
                  throw new IllegalStateException("");
               }
            case RHS:
               switch (getAssociativity()) {
               case MATHEMATICAL:
                  return false; /* Example [x] * [y * z] or [x] + [y + z] the operand does not need grouping */
               case NOTATIONAL_LEFT:
                  return true; /* Example [x] / [y * z] or [x] - [y - z] the operand needs grouping */
               case NOTATIONAL_RIGHT:
                  return false; /* Example [x] = [y = z] the operand does not need grouping */
               case NOTATIONAL_NONE:
                  throw new IllegalStateException("");
               }
            }
         }
      } else if (operand instanceof IASTConditionalExpression) {
         int p = getRelativePrecedence() - CPPOtherOperators.CONDITIONAL.getRelativePrecedence();
         if (p > 0) {
            return true;
         } else if (p < 0) {
            return false;
         } else {
            switch (operandPos) {
            case LHS:
               /* All operators of same precedence are NOTATIONAL_RIGHT */
               return true; /* Example [w ? x : y] = [z] the operand needs grouping */
            case RHS:
               /* All operators of same precedence are NOTATIONAL_RIGHT */
               return false; /* Example [w] = [x ? y : z] the operand does not need grouping */
            }
         }
      }
      return true;
   }

   public static CPPBinaryOperator getForCDTOperator(int binaryOperator) {
      return ArrayIterate.detect(values(), v -> v.equivalentCDTOp == binaryOperator);
   }

   public static CPPBinaryOperator getForBinaryExpr(IASTBinaryExpression expr) {
      return getForCDTOperator(expr.getOperator());
   }

   private enum ExprOperandPos {
      LHS, RHS;
   }

}

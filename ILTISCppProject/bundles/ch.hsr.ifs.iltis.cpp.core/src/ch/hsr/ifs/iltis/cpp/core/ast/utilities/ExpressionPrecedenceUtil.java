package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;


public class ExpressionPrecedenceUtil {

   /**
    * Calculates the precedence for a binary-operator. The values should only considered stable in relation to each other. The lower the value the
    * higher the precedence.
    */
   public static int getBinaryOpPrecedence(int binaryOperator) {
      switch (binaryOperator) {
      case IASTBinaryExpression.op_multiply:
      case IASTBinaryExpression.op_divide:
      case IASTBinaryExpression.op_modulo:
         return 50;
      case IASTBinaryExpression.op_plus:
      case IASTBinaryExpression.op_minus:
         return 60;
      case IASTBinaryExpression.op_shiftLeft:
      case IASTBinaryExpression.op_shiftRight:
         return 70;
      //      case IASTBinaryExpression.op_spaceship:
      //         return 80;
      case IASTBinaryExpression.op_lessThan:
      case IASTBinaryExpression.op_lessEqual:
      case IASTBinaryExpression.op_greaterThan:
      case IASTBinaryExpression.op_greaterEqual:
         return 90;
      case IASTBinaryExpression.op_equals:
      case IASTBinaryExpression.op_notequals:
         return 100;
      case IASTBinaryExpression.op_binaryAnd:
         return 110;
      case IASTBinaryExpression.op_binaryXor:
         return 120;
      case IASTBinaryExpression.op_binaryOr:
         return 130;
      case IASTBinaryExpression.op_logicalAnd:
         return 140;
      case IASTBinaryExpression.op_logicalOr:
         return 150;
      case IASTBinaryExpression.op_assign:
      case IASTBinaryExpression.op_plusAssign:
      case IASTBinaryExpression.op_minusAssign:
      case IASTBinaryExpression.op_multiplyAssign:
      case IASTBinaryExpression.op_divideAssign:
      case IASTBinaryExpression.op_moduloAssign:
      case IASTBinaryExpression.op_shiftLeftAssign:
      case IASTBinaryExpression.op_shiftRightAssign:
      case IASTBinaryExpression.op_binaryAndAssign:
      case IASTBinaryExpression.op_binaryXorAssign:
      case IASTBinaryExpression.op_binaryOrAssign:
         return 160;
      default:
         return Integer.MAX_VALUE;
      }
   }

   public static int getConditionalOpPrecedence() {
      return 160;
   }

   public static int getArraySubscriptOpPrecedence() {
      return 30;
   }

   public static int getCommaOpPrecedence() {
      return 170;
   }

   public static int getTypeIdOpPrecedence() {
      /* Treat this as lower than bracketed but higher than everything else */
      return 17;
   }

   public static int getUnaryOpPrecedence(int unaryOperator) {
      switch (unaryOperator) {
      case IASTUnaryExpression.op_bracketedPrimary:
         /* Treat bracketed expressions as lower than scope resolution */
         return 15;
      case IASTUnaryExpression.op_noexcept: //TODO test if this is correct
         return 17;
      case IASTUnaryExpression.op_postFixIncr:
      case IASTUnaryExpression.op_postFixDecr:
         return 20;
      case IASTUnaryExpression.op_prefixIncr:
      case IASTUnaryExpression.op_prefixDecr:
      case IASTUnaryExpression.op_plus:
      case IASTUnaryExpression.op_minus:
      case IASTUnaryExpression.op_not:
      case IASTUnaryExpression.op_tilde:
      case IASTUnaryExpression.op_star:
      case IASTUnaryExpression.op_amper:
      case IASTUnaryExpression.op_sizeof:
      case IASTUnaryExpression.op_sizeofParameterPack: //TODO test if this is correct
      case IASTUnaryExpression.op_typeid: //TODO test if this is correct
         return 30;
      case IASTUnaryExpression.op_throw:
         return 160;
      default:
         return Integer.MAX_VALUE;
      }
   }

}

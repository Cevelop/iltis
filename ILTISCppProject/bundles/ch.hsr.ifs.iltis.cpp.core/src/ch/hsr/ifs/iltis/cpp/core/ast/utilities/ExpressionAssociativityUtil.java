package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;


public class ExpressionAssociativityUtil {

   public static Associativity getBinaryOperatorAssociativity(int binaryOperator) {
      switch (binaryOperator) {
      case IASTBinaryExpression.op_multiply:
      case IASTBinaryExpression.op_plus:
         return Associativity.MATHEMATICAL;
      case IASTBinaryExpression.op_divide:
      case IASTBinaryExpression.op_modulo:
      case IASTBinaryExpression.op_minus:
      case IASTBinaryExpression.op_shiftLeft:
      case IASTBinaryExpression.op_shiftRight:
         //      case IASTBinaryExpression.op_spaceship:
      case IASTBinaryExpression.op_lessThan:
      case IASTBinaryExpression.op_lessEqual:
      case IASTBinaryExpression.op_greaterThan:
      case IASTBinaryExpression.op_greaterEqual:
      case IASTBinaryExpression.op_equals:
      case IASTBinaryExpression.op_notequals:
      case IASTBinaryExpression.op_binaryAnd:
      case IASTBinaryExpression.op_binaryXor:
      case IASTBinaryExpression.op_binaryOr:
      case IASTBinaryExpression.op_logicalAnd:
      case IASTBinaryExpression.op_logicalOr:
         return Associativity.NOTATIONAL_LEFT;
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
         return Associativity.NOTATIONAL_RIGHT;
      default:
         return null;
      }
   }

   public static Associativity getCommaOperatorAssociativity() {
      return Associativity.NOTATIONAL_LEFT;
   }

   public static Associativity getConditionalOperatorAssociativity() {
      return Associativity.NOTATIONAL_RIGHT;
   }

   public static enum Associativity {
      NOTATIONAL_NONE, NOTATIONAL_LEFT, NOTATIONAL_RIGHT, MATHEMATICAL;
   }
}

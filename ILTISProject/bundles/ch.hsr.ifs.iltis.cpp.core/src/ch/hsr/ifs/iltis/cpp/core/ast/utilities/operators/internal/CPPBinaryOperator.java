package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.internal;

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

import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPBinaryOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPUnaryOperator;


/**
 * Mapping for the unwieldy {@code int op_XYZ} constants representing operators in {@link IASTBinaryExpression}.
 *
 * @author tstauber
 *
 */
public enum CPPBinaryOperator implements ICPPBinaryOperator {
    MULTIPLY(50, Associativity.MATHEMATICAL, IASTBinaryExpression.op_multiply, false), //
    PLUS(60, Associativity.MATHEMATICAL, IASTBinaryExpression.op_plus, false), //
    DIVIDE(50, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_divide, false), //
    MODULO(50, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_modulo, false), //
    MINUS(60, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_minus, false), //
    SHIFT_LEFT(70, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_shiftLeft, false), //
    SHIFT_RIGHT(70, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_shiftRight, false), //
    // SPACESHIP(80,Associativity.NOTATIONAL_LEFT , IASTBinaryExpression.op_spaceship,false),// TODO uncomment once C++ 20 is supported
    LESS_THAN(90, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_lessThan, false), //
    LESS_EQUAL(90, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_lessEqual, false), //
    GREATER_THAN(90, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_greaterThan, false), //
    GREATER_EQUAL(90, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_greaterEqual, false), //
    EQUALS(100, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_equals, false), //
    NOT_EQUALS(100, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_notequals, false), //
    BINARY_AND(110, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_binaryAnd, false), //
    BINARY_XOR(120, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_binaryXor, false), //
    BINARY_OR(130, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_binaryOr, false), //
    LOGICAL_AND(140, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_logicalAnd, false), //
    LOGICAL_OR(150, Associativity.NOTATIONAL_LEFT, IASTBinaryExpression.op_logicalOr, false), //
    ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_assign, true), //
    PLUS_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_plusAssign, true), //
    MINUS_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_minusAssign, true), //
    MULTIPLY_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_multiplyAssign, true), //
    DIVIDE_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_divideAssign, true), //
    MODULO_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_moduloAssign, true), //
    SHIFTLEFT_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_shiftLeftAssign, true), //
    SHIFTRIGHT_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_shiftRightAssign, true), //
    BINARYAND_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_binaryAndAssign, true), //
    BINARYXOR_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_binaryXorAssign, true), //
    BINARYOR_ASSIGN(160, Associativity.NOTATIONAL_RIGHT, IASTBinaryExpression.op_binaryOrAssign, true);//

    private int           relativePrecedence;
    private Associativity associativity;
    private int           equivalentCDTOp;
    private boolean       isAssignment;

    CPPBinaryOperator(final int relativePrecedence, final Associativity associativity, final int equivalentCDTOp, final boolean isAssignment) {
        this.relativePrecedence = relativePrecedence;
        this.associativity = associativity;
        this.equivalentCDTOp = equivalentCDTOp;
        this.isAssignment = isAssignment;
    }

    @Override
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

    @Override
    public boolean leftOperandNeedsWraping(final IASTExpression leftOperand) {
        return operandNeedsWraping(leftOperand, ExprOperandPos.LHS);
    }

    @Override
    public boolean rightOperandNeedsWraping(final IASTInitializerClause rightOperand) {
        return operandNeedsWraping(rightOperand, ExprOperandPos.RHS);
    }

    protected boolean operandNeedsWraping(final IASTInitializerClause operand, final ExprOperandPos operandPos) {
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
            return getRelativePrecedence() <= ICPPUnaryOperator.getForUnaryExpr((IASTUnaryExpression) operand).getRelativePrecedence();
        } else if (operand instanceof IASTArraySubscriptExpression) {
            return getRelativePrecedence() <= CPPOtherOperators.ARRAY_SUBSCRIPT.getRelativePrecedence();
        } else if (operand instanceof IASTBinaryExpression) {
            final int p = getRelativePrecedence() - ICPPBinaryOperator.getForBinaryExpr((IASTBinaryExpression) operand).getRelativePrecedence();
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
            final int p = getRelativePrecedence() - CPPOtherOperators.CONDITIONAL.getRelativePrecedence();
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

    private enum ExprOperandPos {
        LHS, RHS;
    }

    @Override
    public boolean isAssigment() {
        return this.isAssignment;
    }

}

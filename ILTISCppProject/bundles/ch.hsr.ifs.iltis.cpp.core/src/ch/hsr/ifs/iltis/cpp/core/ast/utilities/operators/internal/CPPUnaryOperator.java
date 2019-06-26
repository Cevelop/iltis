package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.internal;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;

import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPBinaryOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPUnaryOperator;


public enum CPPUnaryOperator implements ICPPUnaryOperator {

    BRACKETED_PRIMARY(15, IASTUnaryExpression.op_bracketedPrimary, false), //
    NOEXCEPT(17, IASTUnaryExpression.op_noexcept, false), //
    POSTFIX_INCR(20, IASTUnaryExpression.op_postFixIncr, true), //
    POSTFIX_DECR(20, IASTUnaryExpression.op_postFixDecr, true), //
    PREFIX_INCR(30, IASTUnaryExpression.op_prefixIncr, true), //
    PREFIX_DECR(30, IASTUnaryExpression.op_prefixDecr, true), //
    PLUS(30, IASTUnaryExpression.op_plus, false), //
    MINUS(30, IASTUnaryExpression.op_minus, false), //
    NOT(30, IASTUnaryExpression.op_not, false), //
    TILDE(30, IASTUnaryExpression.op_tilde, false), //
    STAR(30, IASTUnaryExpression.op_star, false), //
    AMPER(30, IASTUnaryExpression.op_amper, false), //
    SIZEOF(30, IASTUnaryExpression.op_sizeof, false), //
    SIZEOF_PARAMETER_PACK(30, IASTUnaryExpression.op_sizeofParameterPack, false), //
    TYPEID(30, IASTUnaryExpression.op_typeid, false), //
    THROW(160, IASTUnaryExpression.op_throw, false);

    private int     relativePrecedence;
    private int     equivalentCDTOp;
    private boolean isAssignment;

    private CPPUnaryOperator(final int relativePrecedence, final int equivalentCDTOp, final boolean isAssignment) {
        this.relativePrecedence = relativePrecedence;
        this.equivalentCDTOp = equivalentCDTOp;
        this.isAssignment = isAssignment;
    }

    @Override
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

    @Override
    public boolean operandNeedsWraping(final IASTInitializerClause operand) {
        if (operand instanceof ICPPASTSimpleTypeConstructorExpression) {
            return false;
        } else if (operand instanceof IASTFunctionCallExpression) {
            return getRelativePrecedence() <= CPPOtherOperators.FUNCTION_CALL.getRelativePrecedence();
        } else if (operand instanceof IASTBinaryExpression) {
            return getRelativePrecedence() <= ICPPBinaryOperator.getForBinaryExpr((IASTBinaryExpression) operand).getRelativePrecedence();
        } else if (operand instanceof IASTUnaryExpression) {
            return getRelativePrecedence() <= ICPPUnaryOperator.getForUnaryExpr((IASTUnaryExpression) operand).getRelativePrecedence();
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

    @Override
    public boolean isAssigment() {
        return isAssignment;
    }

}

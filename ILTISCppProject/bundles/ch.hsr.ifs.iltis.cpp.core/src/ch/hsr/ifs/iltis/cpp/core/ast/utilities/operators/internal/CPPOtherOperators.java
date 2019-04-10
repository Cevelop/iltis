package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.internal;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPBinaryOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPOperator.Associativity;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPOtherOperators.ICPPArraySubscriptOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPOtherOperators.ICPPCommaOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPOtherOperators.ICPPConditionalOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPOtherOperators.ICPPFunctionCall;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPOtherOperators.ICPPTypeIdOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPUnaryOperator;


/**
 * Mapping for operators different from {@link IASTBinaryExpression} and {@link IASTUnaryExpression}.
 *
 * @author tstauber
 *
 */
public class CPPOtherOperators {

    public static ICPPConditionalOperator    CONDITIONAL     = new CPPConditionalOperator(160, Associativity.NOTATIONAL_RIGHT);
    public static ICPPCommaOperator          COMMA_OPERATOR  = new CPPCommaOperator(170, Associativity.NOTATIONAL_LEFT);
    public static ICPPTypeIdOperator         TYPEID          = new CPPTypeIdOperator(17, Associativity.NOTATIONAL_LEFT);
    public static ICPPArraySubscriptOperator ARRAY_SUBSCRIPT = new CPPArraySubscriptOperator(20, Associativity.NOTATIONAL_LEFT);
    public static ICPPFunctionCall           FUNCTION_CALL   = new CPPFunctionCall(20, Associativity.NOTATIONAL_LEFT);

    public static class CPPConditionalOperator implements ICPPConditionalOperator {

        private final int           relativePrecedence;
        private final Associativity associativity;

        private CPPConditionalOperator(final int relativePrecedence, final Associativity associativity) {
            this.relativePrecedence = relativePrecedence;
            this.associativity = associativity;
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
        public boolean conditionalOperandNeedsWraping(final IASTExpression conditionalOperand) {
            return operandNeedsWraping(conditionalOperand, ExprOperandPos.LHS);
        }

        @Override
        public boolean elseOperandNeedsWraping(final IASTExpression elseOperand) {
            return operandNeedsWraping(elseOperand, ExprOperandPos.RHS);
        }

        protected boolean operandNeedsWraping(final IASTExpression operand, final ExprOperandPos operandPos) {
            if (operand instanceof IASTExpressionList) {
                return getRelativePrecedence() <= COMMA_OPERATOR.getRelativePrecedence();
            } else if (operand instanceof IASTArraySubscriptExpression) {
                return getRelativePrecedence() <= ARRAY_SUBSCRIPT.getRelativePrecedence();
            } else if (operand instanceof IASTTypeIdExpression) {
                return getRelativePrecedence() <= TYPEID.getRelativePrecedence();
            } else if (operand instanceof IASTUnaryExpression) {
                return getRelativePrecedence() <= ICPPUnaryOperator.getForUnaryExpr((IASTUnaryExpression) operand).getRelativePrecedence();
            } else if (operand instanceof IASTBinaryExpression) {
                final int p = getRelativePrecedence() - ICPPBinaryOperator.getForBinaryExpr((IASTBinaryExpression) operand).getRelativePrecedence();
                if (p > 0) {
                    return true;
                } else if (p < 0) {
                    return false;
                } else {
                    switch (operandPos) {
                    case LHS:
                        return true; /* Example [w = x] ? [y : z] the operand needs grouping */
                    case RHS:
                        return false; /* Example [w ? x] : [y += z] the operand does not need grouping */
                    }
                }
            } else if (operand instanceof IASTConditionalExpression) {
                switch (operandPos) {
                case LHS:
                    return true; /* Example [u ? v : w] ? [y : z] the operand needs grouping */
                case RHS:
                    return false; /* Example [u ? v] : [w ? y : z] the operand does not need grouping */
                }
            }
            return true;
        }

        private enum ExprOperandPos {
            LHS, RHS;
        }

        @Override
        public boolean isAssigment() {
            return false;
        }
    }

    public static class CPPCommaOperator implements ICPPCommaOperator {

        private final int           relativePrecedence;
        private final Associativity associativity;

        private CPPCommaOperator(final int relativePrecedence, final Associativity associativity) {
            this.relativePrecedence = relativePrecedence;
            this.associativity = associativity;
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
        public boolean isAssigment() {
            return false;
        }

    }

    public static class CPPFunctionCall implements ICPPFunctionCall {

        private final int           relativePrecedence;
        private final Associativity associativity;

        private CPPFunctionCall(final int relativePrecedence, final Associativity associativity) {
            this.relativePrecedence = relativePrecedence;
            this.associativity = associativity;
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
        public boolean isAssigment() {
            return false;
        }

    }

    public static class CPPTypeIdOperator implements ICPPTypeIdOperator {

        private final int           relativePrecedence;
        private final Associativity associativity;

        private CPPTypeIdOperator(final int relativePrecedence, final Associativity associativity) {
            this.relativePrecedence = relativePrecedence;
            this.associativity = associativity;
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
        public boolean isAssigment() {
            return false;
        }

    }

    public static class CPPArraySubscriptOperator implements ICPPArraySubscriptOperator {

        private final int           relativePrecedence;
        private final Associativity associativity;

        private CPPArraySubscriptOperator(final int relativePrecedence, final Associativity associativity) {
            this.relativePrecedence = relativePrecedence;
            this.associativity = associativity;
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
        public boolean isAssigment() {
            return false;
        }

    }
}

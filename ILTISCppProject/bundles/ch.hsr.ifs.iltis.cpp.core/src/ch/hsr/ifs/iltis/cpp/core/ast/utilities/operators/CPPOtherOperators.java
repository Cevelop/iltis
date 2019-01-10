package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPOperator.Associativity;


/**
 * Mapping for operators different from {@link IASTBinaryExpression}.
 *
 * @author tstauber
 *
 */
public class CPPOtherOperators {

    public static CPPConditionalOperator    CONDITIONAL     = new CPPConditionalOperator(160, Associativity.NOTATIONAL_RIGHT);
    public static CPPCommaOperator          COMMA_OPERATOR  = new CPPCommaOperator(170, Associativity.NOTATIONAL_LEFT);
    public static CPPTypeIdOperator         TYPEID          = new CPPTypeIdOperator(17, Associativity.NOTATIONAL_LEFT);
    public static CPPArraySubscriptOperator ARRAY_SUBSCRIPT = new CPPArraySubscriptOperator(20, Associativity.NOTATIONAL_LEFT);
    public static CPPFunctionCall           FUNCTION_CALL   = new CPPFunctionCall(20, Associativity.NOTATIONAL_LEFT);

    public static class CPPConditionalOperator implements ICPPOperator {

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

        public boolean conditionalOperandNeedsWraping(final IASTExpression conditionalOperand) {
            return operandNeedsWraping(conditionalOperand, ExprOperandPos.LHS);
        }

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
                return getRelativePrecedence() <= CPPUnaryOperator.getForUnaryExpr((IASTUnaryExpression) operand).getRelativePrecedence();
            } else if (operand instanceof IASTBinaryExpression) {
                final int p = getRelativePrecedence() - CPPBinaryOperator.getForBinaryExpr((IASTBinaryExpression) operand).getRelativePrecedence();
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
    }

    public static class CPPCommaOperator implements ICPPOperator {

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

    }

    public static class CPPFunctionCall implements ICPPOperator {

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

    }

    public static class CPPTypeIdOperator implements ICPPOperator {

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

    }

    public static class CPPArraySubscriptOperator implements ICPPOperator {

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

    }

}

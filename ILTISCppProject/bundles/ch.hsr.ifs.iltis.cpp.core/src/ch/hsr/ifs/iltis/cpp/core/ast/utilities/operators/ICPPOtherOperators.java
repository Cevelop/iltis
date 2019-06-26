package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators;

import org.eclipse.cdt.core.dom.ast.IASTExpression;

import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.internal.CPPOtherOperators;


/**
 * 
 * @author tstauber
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 3.0
 *
 */
public interface ICPPOtherOperators extends ICPPOperator {

    /**
     * Representation for the ternary {@code [boolean <- expr] ? [expr] : [expr]} expression
     * 
     * @author tstauber
     * @noimplement This interface is not intended to be implemented by clients.
     *
     */
    public interface ICPPConditionalOperator extends ICPPOtherOperators {

        /**
         * @return The ICPPConditionalOperator object.
         */
        public static ICPPConditionalOperator getICPPConditionalOperator() {
            return CPPOtherOperators.CONDITIONAL;
        }

        /**
         * If the node-subtree below this IASTExpression would be written, would it require to be wrapped in braces to avoid being parsed into a
         * different subtree? (This may happen due to associativity and precedence)
         * 
         * @param conditionalOperand
         * The {@link IASTExpression} used as conditional operand.
         * @return {@code true} iff this expression must be wrapped by braces to preserve the intended meaning represented by the tree structure.
         */
        boolean conditionalOperandNeedsWraping(IASTExpression conditionalOperand);

        /**
         * If the node-subtree below this IASTExpression would be written, would it require to be wrapped in braces to avoid being parsed into a
         * different subtree? (This may happen due to associativity and precedence)
         * 
         * @param elseOperand
         * The {@link IASTExpression} used as else operand.
         * @return {@code true} iff this expression must be wrapped by braces to preserve the intended meaning represented by the tree structure.
         */
        boolean elseOperandNeedsWraping(IASTExpression elseOperand);

    }

    /**
     * 
     * @author tstauber
     * @noimplement This interface is not intended to be implemented by clients.
     *
     */
    public interface ICPPCommaOperator extends ICPPOtherOperators {

        /**
         * @return The ICPPCommaOperator object.
         */
        public static ICPPCommaOperator getICPPConditionalOperator() {
            return CPPOtherOperators.COMMA_OPERATOR;
        }

    }

    /**
     * 
     * @author tstauber
     * @noimplement This interface is not intended to be implemented by clients.
     *
     */
    public interface ICPPFunctionCall extends ICPPOtherOperators {

        /**
         * @return The ICPPFunctionCall object.
         */
        public static ICPPFunctionCall getICPPConditionalOperator() {
            return CPPOtherOperators.FUNCTION_CALL;
        }
    }

    /**
     * 
     * @author tstauber
     * @noimplement This interface is not intended to be implemented by clients.
     *
     */
    public interface ICPPTypeIdOperator extends ICPPOtherOperators {

        /**
         * @return The ICPPTypeIdOperator object.
         */
        public static ICPPTypeIdOperator getICPPConditionalOperator() {
            return CPPOtherOperators.TYPEID;
        }
    }

    /**
     * 
     * @author tstauber
     * @noimplement This interface is not intended to be implemented by clients.
     *
     */
    public interface ICPPArraySubscriptOperator extends ICPPOtherOperators {

        /**
         * @return The ICPPArraySubscriptOperator object.
         */
        public static ICPPArraySubscriptOperator getICPPConditionalOperator() {
            return CPPOtherOperators.ARRAY_SUBSCRIPT;
        }
    }

}

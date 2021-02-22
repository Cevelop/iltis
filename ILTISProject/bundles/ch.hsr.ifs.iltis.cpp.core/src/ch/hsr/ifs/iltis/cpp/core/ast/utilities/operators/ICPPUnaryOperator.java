package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators;

import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.collections.impl.utility.ArrayIterate;

import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.internal.CPPUnaryOperator;


/**
 * Mapping for the unwieldy {@code int op_XYZ} constants representing operators in {@link IASTUnaryExpression}.
 * 
 * @author tstauber
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 3.0
 *
 */
public interface ICPPUnaryOperator extends ICPPOperator {

    /**
     * @return The CDT int constant representing this unary operator
     */
    int getCDTOperator();

    /**
     * @return The relative precedence (use only to compare to other ICPPUnaryOperators.)
     */
    int getRelativePrecedence();

    /**
     * If the node-subtree below this IASTInitializerClause would be written, would it require to be wrapped in braces to avoid being parsed into a
     * different subtree? (This may happen due to associativity and precedence)
     * 
     * @param operand
     * The {@link IASTInitializerClause} used as operand.
     * @return {@code true} iff this initializer clause must be wrapped by braces to preserve the intended meaning represented by the tree structure.
     */
    boolean operandNeedsWraping(IASTInitializerClause operand);

    /**
     * Return the ICPPUnaryOperator corresponding to the CDT unary operator constant.
     * 
     * @param unaryOperator
     * The unary operator constant.
     * @return The corresponding ICPPUnaryOperator object.
     */
    public static ICPPUnaryOperator getForCDTOperator(final int unaryOperator) {
        return ArrayIterate.detect(CPPUnaryOperator.values(), v -> v.getCDTOperator() == unaryOperator);
    }

    /**
     * Comfort method for {@link #getForCDTOperator}{@code (expr.getOperator())}
     * 
     * @param expr
     * The expression to get the operator for.
     * @return The corresponding ICPPUnaryOperator object.
     */
    public static ICPPUnaryOperator getForUnaryExpr(final IASTUnaryExpression expr) {
        return getForCDTOperator(expr.getOperator());
    }
}

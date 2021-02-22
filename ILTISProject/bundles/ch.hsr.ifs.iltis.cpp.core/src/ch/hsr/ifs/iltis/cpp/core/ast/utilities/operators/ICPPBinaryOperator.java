package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.collections.impl.utility.ArrayIterate;

import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.internal.CPPBinaryOperator;


/**
 * Mapping for the unwieldy {@code int op_XYZ} constants representing operators in {@link IASTBinaryExpression}.
 * 
 * @author tstauber
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @since 3.0
 */
public interface ICPPBinaryOperator extends ICPPOperator {

    public static final ICPPBinaryOperator MULTIPLY    = CPPBinaryOperator.MULTIPLY;
    public static final ICPPBinaryOperator PLUS        = CPPBinaryOperator.PLUS;
    public static final ICPPBinaryOperator DIVIDE      = CPPBinaryOperator.DIVIDE;
    public static final ICPPBinaryOperator MODULO      = CPPBinaryOperator.MODULO;
    public static final ICPPBinaryOperator MINUS       = CPPBinaryOperator.MINUS;
    public static final ICPPBinaryOperator SHIFT_LEFT  = CPPBinaryOperator.SHIFT_LEFT;
    public static final ICPPBinaryOperator SHIFT_RIGHT = CPPBinaryOperator.SHIFT_RIGHT;
    //public static final ICPPBinaryOperator     SPACESHIP = CPPBinaryOperator.SPACESHIP;
    public static final ICPPBinaryOperator LESS_THAN         = CPPBinaryOperator.LESS_THAN;
    public static final ICPPBinaryOperator LESS_EQUAL        = CPPBinaryOperator.LESS_EQUAL;
    public static final ICPPBinaryOperator GREATER_THAN      = CPPBinaryOperator.GREATER_THAN;
    public static final ICPPBinaryOperator GREATER_EQUAL     = CPPBinaryOperator.GREATER_EQUAL;
    public static final ICPPBinaryOperator EQUALS            = CPPBinaryOperator.EQUALS;
    public static final ICPPBinaryOperator NOT_EQUALS        = CPPBinaryOperator.NOT_EQUALS;
    public static final ICPPBinaryOperator BINARY_AND        = CPPBinaryOperator.BINARY_AND;
    public static final ICPPBinaryOperator BINARY_XOR        = CPPBinaryOperator.BINARY_XOR;
    public static final ICPPBinaryOperator BINARY_OR         = CPPBinaryOperator.BINARY_OR;
    public static final ICPPBinaryOperator LOGICAL_AND       = CPPBinaryOperator.LOGICAL_AND;
    public static final ICPPBinaryOperator LOGICAL_OR        = CPPBinaryOperator.LOGICAL_OR;
    public static final ICPPBinaryOperator ASSIGN            = CPPBinaryOperator.ASSIGN;
    public static final ICPPBinaryOperator PLUS_ASSIGN       = CPPBinaryOperator.PLUS_ASSIGN;
    public static final ICPPBinaryOperator MINUS_ASSIGN      = CPPBinaryOperator.MINUS_ASSIGN;
    public static final ICPPBinaryOperator MULTIPLY_ASSIGN   = CPPBinaryOperator.MULTIPLY_ASSIGN;
    public static final ICPPBinaryOperator DIVIDE_ASSIGN     = CPPBinaryOperator.DIVIDE_ASSIGN;
    public static final ICPPBinaryOperator MODULO_ASSIGN     = CPPBinaryOperator.MODULO_ASSIGN;
    public static final ICPPBinaryOperator SHIFTLEFT_ASSIGN  = CPPBinaryOperator.SHIFTLEFT_ASSIGN;
    public static final ICPPBinaryOperator SHIFTRIGHT_ASSIGN = CPPBinaryOperator.SHIFTRIGHT_ASSIGN;
    public static final ICPPBinaryOperator BINARYAND_ASSIGN  = CPPBinaryOperator.BINARYAND_ASSIGN;
    public static final ICPPBinaryOperator BINARYXOR_ASSIGN  = CPPBinaryOperator.BINARYXOR_ASSIGN;
    public static final ICPPBinaryOperator BINARYOR_ASSIGN   = CPPBinaryOperator.BINARYOR_ASSIGN;

    /**
     * @return The CDT int constant representing this binary operator
     */
    public int getCDTOperator();

    /**
     * @return The relative precedence (use only to compare to other ICPPBinaryOperators.)
     */
    public int getRelativePrecedence();

    /**
     * If the node-subtree below this IASTExpression would be written, would it require to be wrapped in braces to avoid being parsed into a different
     * subtree? (This may happen due to associativity and precedence)
     * 
     * @param leftOperand
     * The {@link IASTExpression} used as left operand.
     * @return {@code true} iff this expression must be wrapped by braces to preserve the intended meaning represented by the tree structure.
     */
    public boolean leftOperandNeedsWraping(IASTExpression leftOperand);

    /**
     * If the node-subtree below this IASTInitializerClause would be written, would it require to be wrapped in braces to avoid being parsed into a
     * different subtree? (This may happen due to associativity and precedence)
     * 
     * @param rightOperand
     * The {@link IASTInitializerClause} used as right operand.
     * @return {@code true} iff this initializer clause must be wrapped by braces to preserve the intended meaning represented by the tree structure.
     */
    public boolean rightOperandNeedsWraping(IASTInitializerClause rightOperand);

    /**
     * Return the ICPPBinaryOperator corresponding to the CDT binary operator constant.
     * 
     * @param binaryOperator
     * The binary operator constant.
     * @return The corresponding ICPPBinaryOperator object.
     */
    public static ICPPBinaryOperator getForCDTOperator(final int binaryOperator) {
        return ArrayIterate.detect(CPPBinaryOperator.values(), v -> v.getCDTOperator() == binaryOperator);
    }

    /**
     * Comfort method for {@link #getForCDTOperator}{@code (expr.getOperator())}
     * 
     * @param expr
     * The expression to get the operator for.
     * @return The corresponding ICPPBinaryOperator object.
     */
    public static ICPPBinaryOperator getForBinaryExpr(final IASTBinaryExpression expr) {
        return getForCDTOperator(expr.getOperator());
    }
}

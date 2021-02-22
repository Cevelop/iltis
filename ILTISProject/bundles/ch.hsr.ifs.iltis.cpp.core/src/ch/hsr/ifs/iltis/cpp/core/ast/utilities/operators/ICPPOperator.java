package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators;

/**
 * An interface providing a mapping for the unwieldy {@code int op_XYZ} constants representing operators in cdt.
 *
 * @author tstauber
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *
 */
public interface ICPPOperator {

    /**
     * @return The associativity of this operator
     */
    Associativity getAssociativity();

    /**
     * The values returned by this method are not guaranteed to be stable, but the relative precedence will be correct.
     * The lower the returned value, the stronger the operator binds.
     *
     * @return The precedence of this operator, whereby lower is stronger.
     */
    int getRelativePrecedence();

    public static enum Associativity {
        NOTATIONAL_NONE, NOTATIONAL_LEFT, NOTATIONAL_RIGHT, MATHEMATICAL;
    }

    /**
     * Used to test if an operator is an assignment operator. Assignment includes the impure assignment operators like +=, ++, and --.
     * 
     * @return {@code true} iff the operator is an assignment operator
     * 
     * @since 3.0
     */
    public boolean isAssigment();

}

package ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators;

/**
 * An interface implemented by the iltis mapping for C++ operators.
 * 
 * @author tstauber
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

}

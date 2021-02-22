package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;

/**
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 * @since 3.0
 *
 */
public class ASTInitializerUtil extends ASTNodeUtil {

   /**
    * This method will extract the initializer clauses and flattens the first level of possible initializer-lists.
    * 
    * <pre>
    * X foo = {1,2,3}; -> [1,2,3]
    * X bar(1,2,{1,3}); -> [1,2,{1,3}]
    * </pre>
    * 
    * @param initializer
    * @return
    */
   public static IASTInitializerClause[] getFlattenedInitializerClauses(IASTInitializer initializer) {
      if (initializer instanceof IASTInitializerList) {
         return ((IASTInitializerList) initializer).getClauses();
      } else if (initializer instanceof ICPPASTConstructorInitializer) {
         return ((ICPPASTConstructorInitializer) initializer).getArguments();
      } else if (initializer instanceof IASTEqualsInitializer) {
         IASTInitializerClause icl = ((IASTEqualsInitializer) initializer).getInitializerClause();
         if (icl instanceof IASTInitializerList) {
            return ((IASTInitializerList) icl).getClauses();
         } else if (icl instanceof IASTExpression) { return new IASTInitializerClause[] { icl }; }
      }
      throw new IllegalStateException("This exception means, that this method is outdated.");
   }
}

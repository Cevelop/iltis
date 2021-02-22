package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPBasicType;

/**
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 * @since 3.0
 *
 */
public class ASTInitializerClauseUtil extends ASTNodeUtil {

    public static IType getType(final IASTInitializerClause clause) {
        if (clause instanceof IASTInitializerList) {
            final IASTInitializerClause[] clauses = ((IASTInitializerList) clause).getClauses();
            if (clauses.length > 0) {
                return getType(clauses[0]);
            }
    
            final int noQualifiers = 0;
            return new CPPBasicType(Kind.eInt, noQualifiers);
        }
    
        if (clause instanceof IASTIdExpression) {
            final IASTName name = ((IASTIdExpression) clause).getName();
            final IBinding binding = name.resolveBinding();
    
            if (binding instanceof IVariable) {
                return ((IVariable) binding).getType();
            }
            if (binding instanceof ICPPConstructor) {
                return ((ICPPConstructor) binding).getClassOwner();
            }
            if (binding instanceof IFunction) {
                return ((IFunction) binding).getType().getReturnType();
            }
            if (binding instanceof ITypedef) {
                return ((ITypedef) binding).getType();
            }
            if (binding instanceof ICPPClassType) {
                return (ICPPClassType) binding;
            }
        }
    
        if (clause instanceof IASTExpression) {
            return ((IASTExpression) clause).getExpressionType();
        }
    
        return new CPPBasicType(Kind.eInt, 0);
    }

}

package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;


/**
 * Static utility methods to work with IASTDeclarators
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 *
 * @since 3.0
 */
public class ASTDeclaratorUtil {

    public static boolean hasPointerType(final IASTDeclarator declarator) {
        if (declarator == null) return false;

        if (declarator instanceof IASTArrayDeclarator) {
            final IASTArrayDeclarator arrayDecl = (IASTArrayDeclarator) declarator;
            return arrayDecl.getPointerOperators().length > 0;
        }

        if (declarator.getPointerOperators().length > 0) return true;

        final IBinding declBinding = declarator.getName().resolveBinding();

        if (declBinding instanceof IVariable) {
            return ((IVariable) declBinding).getType() instanceof IPointerType;
        } else if (declBinding instanceof ICPPMethod) {
            return ((ICPPMethod) declBinding).getType().getReturnType() instanceof IPointerType;
        }

        return false;
    }

    public static boolean isPointerOrRefType(final IASTDeclarator declarator) {
        if (declarator == null) return false;

        if (declarator instanceof IASTArrayDeclarator) {
            final IASTArrayDeclarator arrayDecl = (IASTArrayDeclarator) declarator;
            return arrayDecl.getPointerOperators().length > 0;
        }

        if (declarator.getPointerOperators().length > 0) return true;

        final IBinding declBinding = declarator.getName().resolveBinding();

        if (declBinding instanceof IVariable) {
            final IType type = ((IVariable) declBinding).getType();
            return ASTTypeUtil.hasPointerOrRefType(type);
        } else if (declBinding instanceof ICPPMethod) {
            final IType type = ((ICPPMethod) declBinding).getType().getReturnType();
            return ASTTypeUtil.hasPointerOrRefType(type);
        }

        return false;
    }

}

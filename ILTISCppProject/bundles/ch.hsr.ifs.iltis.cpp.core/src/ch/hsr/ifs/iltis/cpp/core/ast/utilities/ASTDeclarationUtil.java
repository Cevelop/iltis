package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;


/**
 * Static utility methods to work with IASTDeclarations
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 *
 * @since 3.0
 */
public class ASTDeclarationUtil extends ASTNodeUtil {

    /**
     * If the declaration is either a IASTSimpleDeclaration, IASTParameterDeclaration, or a ICPPASTFunctionDefinition, the decl specifier will be
     * returned, else null.
     * 
     * @param declaration
     * The declaration to get the decl specifier from
     * @return The decl specifier iff the declaration is an IASTSimpleDeclaration or an ICPPASTFunctionDefinition.
     */
    public static IASTDeclSpecifier getDeclSpecifier(IASTDeclaration declaration) {
        if (declaration instanceof IASTParameterDeclaration) {
            return ((IASTParameterDeclaration) declaration).getDeclSpecifier();
        } else if (declaration instanceof IASTSimpleDeclaration) {
            return ((IASTSimpleDeclaration) declaration).getDeclSpecifier();
        } else if (declaration instanceof ICPPASTFunctionDefinition) {
            return ((ICPPASTFunctionDefinition) declaration).getDeclSpecifier();
        } else {
            return null;
        }
    }

    /**
     * If the declaration is either a IASTSimpleDeclaration, IASTParameterDeclaration, or a ICPPASTFunctionDefinition, the decl specifier will be set,
     * and true returned. Else false will be returned.
     * 
     * @param declaration
     * The declaration to set the decl specifier on.
     * @param declSpec
     * The decl specifier to set.
     * @return true iff decl specifier could be set
     */
    public static boolean setDeclSpecifier(IASTDeclaration declaration, IASTDeclSpecifier declSpec) {
        if (declaration instanceof IASTParameterDeclaration) {
            ((IASTParameterDeclaration) declaration).setDeclSpecifier(declSpec);
            return true;
        } else if (declaration instanceof IASTSimpleDeclaration) {
            ((IASTSimpleDeclaration) declaration).setDeclSpecifier(declSpec);
            return true;
        } else if (declaration instanceof ICPPASTFunctionDefinition) {
            ((ICPPASTFunctionDefinition) declaration).setDeclSpecifier(declSpec);
            return true;
        } else {
            return false;
        }
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

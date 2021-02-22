package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;


/**
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 * @since 3.0
 *
 */
public class ASTDeclSpecifierUtil extends ASTNodeUtil{

    /**
     * Compares if the bare type of the two decl-specifiers matches.
     * 
     * @param d1
     * Decl-Specifier 1
     * @param d2
     * Decl-Specifier 2
     * @return {@code true} iff the bare type of the two decl-specifiers is the same.
     */
    public static boolean equalsBareType(IASTDeclSpecifier d1, IASTDeclSpecifier d2) {
        if (d1.getClass() != d2.getClass()) return false;
        if (d1 instanceof ICPPASTSimpleDeclSpecifier) {
            ICPPASTSimpleDeclSpecifier sd1 = (ICPPASTSimpleDeclSpecifier) d1;
            ICPPASTSimpleDeclSpecifier sd2 = (ICPPASTSimpleDeclSpecifier) d2;
            return sd1.getType() == sd2.getType();
        } else if (d1 instanceof IASTNamedTypeSpecifier) {
            IASTNamedTypeSpecifier nd1 = (IASTNamedTypeSpecifier) d1;
            IASTNamedTypeSpecifier nd2 = (IASTNamedTypeSpecifier) d2;
            return nd1.getName().resolveBinding() == nd2.getName().resolveBinding();
        } else {
            throw new IllegalStateException("This method needs to be extended to handle this case");
        }
    }

    public static boolean isStatic(final IASTDeclSpecifier specifier) {
        return specifier != null && specifier.getStorageClass() == IASTDeclSpecifier.sc_static;
    }

    public static boolean isVoid(final IASTDeclSpecifier specifier) {
        return specifier instanceof IASTSimpleDeclSpecifier && ((IASTSimpleDeclSpecifier) specifier).getType() == IASTSimpleDeclSpecifier.t_void;
    }
}

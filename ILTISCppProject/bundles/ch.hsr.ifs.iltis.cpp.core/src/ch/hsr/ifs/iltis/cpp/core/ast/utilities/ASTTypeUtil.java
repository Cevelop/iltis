package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;


/**
 * 
 * @author tstauber
 * @since 3.0
 *
 */
public class ASTTypeUtil {

    public static boolean hasPointerOrRefType(final IType type) {
        return type instanceof ICPPReferenceType || type instanceof IPointerType;
    }

    public static boolean hasVolatilePart(final IType type) {
        if (!(type instanceof ITypeContainer)) {
            return false;
        }
    
        if (type instanceof IQualifierType && ((IQualifierType) type).isVolatile()) {
            return true;
        }
    
        return hasVolatilePart(((ITypeContainer) type).getType());
    }

    public static boolean hasConstPart(final IType type) {
        if (!(type instanceof ITypeContainer)) {
            return false;
        }
    
        if (type instanceof IQualifierType) {
            if (((IQualifierType) type).isConst()) {
                return true;
            }
        }
    
        return hasConstPart(((ITypeContainer) type).getType());
    }

    public static IType unwindPointerOrRefType(IType type) {
        if (type instanceof IPointerType) {
            type = ((IPointerType) type).getType();
        }
    
        if (type instanceof ICPPReferenceType) {
            type = ((ICPPReferenceType) type).getType();
        }
    
        return type;
    }

    public static IType asNonConst(IType type) {
        while (type instanceof IQualifierType && ((IQualifierType) type).isConst()) {
            type = ((IQualifierType) type).getType();
        }
        return type;
    }

}

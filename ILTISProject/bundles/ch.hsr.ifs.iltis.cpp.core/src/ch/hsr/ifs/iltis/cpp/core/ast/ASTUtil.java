package ch.hsr.ifs.iltis.cpp.core.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.ASTTypeMatcher;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPDeferredClassInstance;

import ch.hsr.ifs.iltis.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.ASTDeclSpecifierUtil;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.ASTDeclarationUtil;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CPPVisitor;


/**
 * DOC
 *
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 */
@SuppressWarnings("restriction")
public abstract class ASTUtil {

    public static boolean isSameType(final IType lhs, final IType rhs) {
        return new ASTTypeMatcher().isEquivalent(lhs, rhs);
    }

    public static boolean isStructType(final ICPPASTCompositeTypeSpecifier clazz) {
        return clazz.getKey() == IASTCompositeTypeSpecifier.k_struct;
    }

    public static boolean isClassType(final ICPPASTCompositeTypeSpecifier clazz) {
        return clazz.getKey() == ICPPASTCompositeTypeSpecifier.k_class;
    }

    @Deprecated
    public static boolean isClass(final IASTNode node) {
        return node instanceof ICPPASTCompositeTypeSpecifier;
    }

    @Deprecated
    public static boolean isQualifiedName(final IASTName name) {
        return name instanceof ICPPASTQualifiedName;
    }

    public static boolean isConstructor(final ICPPASTFunctionDefinition function) {
        return function.getDeclarator().getName().resolveBinding() instanceof ICPPConstructor;
    }

    public static boolean isDeclConstructor(final IASTDeclaration declaration) {
        final IASTDeclarator declaratorForNode = ASTUtil.getDeclaratorForNode(declaration);
        if (!(declaratorForNode instanceof ICPPASTFunctionDeclarator)) return false;

        final IASTDeclSpecifier declSpec = ASTDeclarationUtil.getDeclSpecifier(declaration);
        if (declSpec == null) return false;
        return declSpec instanceof IASTSimpleDeclSpecifier && isUnspecified((IASTSimpleDeclSpecifier) declSpec);
    }

    public static boolean isUnspecified(final IASTSimpleDeclSpecifier declSpec) {
        return declSpec.getType() == IASTSimpleDeclSpecifier.t_unspecified;
    }

    public static boolean isCopyCtor(final ICPPConstructor ctor, final ICPPClassType classType) {
        final IType[] paramTypes = ctor.getType().getParameterTypes();

        if (paramTypes.length == 0) {
            return false;
        }

        if (!isFstCopyCtorParam(paramTypes[0], classType)) {
            return false;
        }

        final ICPPParameter[] params = ctor.getParameters();

        for (int i = 1; i < params.length; i++) {
            if (!params[i].hasDefaultValue()) {
                return false;
            }
        }

        return true;
    }

    private static boolean isFstCopyCtorParam(final IType paramType, final ICPPClassType classType) {
        if (!(paramType instanceof ICPPReferenceType)) {
            return false;
        }

        IType candidate = ((ICPPReferenceType) paramType).getType();

        if (candidate instanceof IQualifierType) {
            candidate = ((IQualifierType) candidate).getType();
        }

        if (candidate instanceof ICPPDeferredClassInstance) {
            candidate = ((ICPPDeferredClassInstance) candidate).getClassTemplate();
        }

        if (candidate == null) {
            return false;
        }

        return candidate.isSameType(classType);
    }

    public static boolean isDefaultCtor(final ICPPConstructor ctor) {
        final ICPPParameter[] params = ctor.getParameters();
        return params.length == 0 || params.length == 1 && isVoid(params[0]) || haveAllDefaultValue(params);
    }

    private static boolean isVoid(final ICPPParameter param) {
        if (param.getType() instanceof IBasicType) {
            return ((IBasicType) param.getType()).getKind().equals(IBasicType.Kind.eVoid);
        }
        return false;
    }

    public static boolean isVoid(final ICPPASTParameterDeclaration param) {
        return param.getDeclarator().getPointerOperators().length == 0 && ASTDeclSpecifierUtil.isVoid(param.getDeclSpecifier());
    }

    public static IASTName getName(final IASTFunctionCallExpression callExpr) {
        if (callExpr instanceof IASTIdExpression) {
            final IASTIdExpression idExpr = (IASTIdExpression) callExpr.getFunctionNameExpression();
            return idExpr.getName();
        }

        final IASTExpression expression = callExpr.getFunctionNameExpression();

        if (expression instanceof ICPPASTFieldReference) {
            return ((ICPPASTFieldReference) expression).getFieldName();
        } else if (expression instanceof IASTIdExpression) {
            return ((IASTIdExpression) expression).getName().getLastName();
        }

        throw new ILTISException("Was not able to determine name of function call").rethrowUnchecked();
    }

    public static IASTDeclaration[] getAllDeclarations(final IASTNode parent) {
        if (parent instanceof IASTTranslationUnit) {
            return ((IASTTranslationUnit) parent).getDeclarations();
        }
        if (parent instanceof ICPPASTCompositeTypeSpecifier) {
            return ((ICPPASTCompositeTypeSpecifier) parent).getMembers();
        }
        if (parent instanceof ICPPASTNamespaceDefinition) {
            return ((ICPPASTNamespaceDefinition) parent).getDeclarations();
        }

        return new IASTDeclaration[0];
    }

    public static ICPPASTDeclSpecifier getDeclSpec(final ICPPASTFunctionDeclarator funDecl) {
        final ICPPASTFunctionDefinition funDef = CPPVisitor.findAncestorWithType(funDecl, ICPPASTFunctionDefinition.class).orElse(null);

        if (funDef != null) {
            return (ICPPASTDeclSpecifier) funDef.getDeclSpecifier();
        }

        final IASTSimpleDeclaration simpleDecl = CPPVisitor.findAncestorWithType(funDecl, IASTSimpleDeclaration.class).orElse(null);
        return (ICPPASTDeclSpecifier) simpleDecl.getDeclSpecifier();
    }

    public static List<ICPPASTFunctionDefinition> getFunctionDefinitions(final Collection<IASTDeclaration> publicMemFuns) {
        final List<ICPPASTFunctionDefinition> result = new ArrayList<>();

        for (final IASTDeclaration fun : publicMemFuns) {
            final ICPPASTFunctionDefinition candidate = CPPVisitor.findChildWithType(fun, ICPPASTFunctionDefinition.class).orElse(null);

            if (candidate != null) {
                result.add(candidate);
            }
        }
        return result;
    }

    public static IASTDeclarator getDeclaratorForNode(final IASTNode node) {
        IASTDeclarator declarator = null;

        if (node instanceof IASTSimpleDeclaration) {
            final IASTSimpleDeclaration decl = (IASTSimpleDeclaration) node;

            if (decl.getDeclarators().length > 0) {
                declarator = decl.getDeclarators()[0];
            }
        } else if (node instanceof IASTParameterDeclaration) {
            final IASTParameterDeclaration decl = (IASTParameterDeclaration) node;
            declarator = decl.getDeclarator();
        } else if (node instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition fun = (ICPPASTFunctionDefinition) node;
            declarator = fun.getDeclarator();
        }

        return declarator;
    }

    private static boolean haveAllDefaultValue(final ICPPParameter[] parameters) {
        for (final ICPPParameter parameter : parameters) {
            if (!parameter.hasDefaultValue()) {
                return false;
            }
        }

        return true;
    }

    public static String getQfNameF(final ICPPASTCompositeTypeSpecifier clazz) {
        final IBinding clazzBinding = clazz.getName().resolveBinding();
        ILTISException.Unless.assignableFrom("Class expected", ICPPClassType.class, clazzBinding);
        return getQfName((ICPPClassType) clazzBinding);
    }

    public static String getQfName(final ICPPBinding binding) {
        try {
            return getQfName(binding.getQualifiedName());
        } catch (final DOMException e) {
            throw new ILTISException(e).rethrowUnchecked();
        }
    }

    public static String getQfName(final String[] names) {
        return Arrays.asList(names).stream().collect(Collectors.joining("::"));
    }

    public static void removeExternalStorageIfSet(final IASTDeclSpecifier newDeclSpec) {
        if (newDeclSpec.getStorageClass() == IASTDeclSpecifier.sc_extern) {
            newDeclSpec.setStorageClass(IASTDeclSpecifier.sc_unspecified);
        }
    }

    public static boolean isPushBack(final IASTName name) {
        final IASTFunctionCallExpression funcCall = CPPVisitor.findAncestorWithType(name, IASTFunctionCallExpression.class).orElse(null);

        if (funcCall != null && funcCall.getFunctionNameExpression() instanceof IASTFieldReference) {
            final IASTFieldReference idExp = (IASTFieldReference) funcCall.getFunctionNameExpression();
            return idExp.getFieldName().toString().equals("push_back");
        }

        return false;
    }

    public static IType windDownToRealType(IType type, final boolean stopAtTypeDef) {
        if (type instanceof ITypeContainer) {
            if (stopAtTypeDef && type instanceof ITypedef) return type;

            type = ((ITypeContainer) type).getType();
            return windDownToRealType(type, stopAtTypeDef);
        }

        return type;
    }
}

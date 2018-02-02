package ch.hsr.ifs.iltis.cpp.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.ASTTypeMatcher;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
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
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompoundStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPBasicType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPDeferredClassInstance;

import ch.hsr.ifs.iltis.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.functional.Functional;


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

   public static boolean isClass(final IASTNode node) {
      return node instanceof ICPPASTCompositeTypeSpecifier;
   }

   public static boolean isQualifiedName(final IASTName name) {
      return name instanceof ICPPASTQualifiedName;
   }

   public static boolean isStatic(final IASTDeclSpecifier specifier) {
      return specifier != null && specifier.getStorageClass() == IASTDeclSpecifier.sc_static;
   }

   public static boolean isConstructor(final ICPPASTFunctionDefinition function) {
      return function.getDeclarator().getName().resolveBinding() instanceof ICPPConstructor;
   }

   public static boolean isDeclConstructor(final IASTDeclaration declaration) {
      final IASTDeclarator declaratorForNode = ASTUtil.getDeclaratorForNode(declaration);

      if (!(declaratorForNode instanceof ICPPASTFunctionDeclarator)) { return false; }

      final Optional<IASTDeclSpecifier> declSpec = ASTUtil.getDeclarationSpecifier(declaration);
      if (declSpec.isPresent()) { return declSpec.get() instanceof IASTSimpleDeclSpecifier && isUnspecified((IASTSimpleDeclSpecifier) declSpec
            .get()); }

      return false;
   }

   public static boolean isUnspecified(final IASTSimpleDeclSpecifier declSpec) {
      return declSpec.getType() == IASTSimpleDeclSpecifier.t_unspecified;
   }

   public static boolean isCopyCtor(final ICPPConstructor ctor, final ICPPClassType classType) {
      final IType[] paramTypes = ctor.getType().getParameterTypes();

      if (paramTypes.length == 0) { return false; }

      if (!isFstCopyCtorParam(paramTypes[0], classType)) { return false; }

      final ICPPParameter[] params = ctor.getParameters();

      for (int i = 1; i < params.length; i++) {
         if (!params[i].hasDefaultValue()) { return false; }
      }

      return true;
   }

   private static boolean isFstCopyCtorParam(final IType paramType, final ICPPClassType classType) {
      if (!(paramType instanceof ICPPReferenceType)) { return false; }

      IType candidate = ((ICPPReferenceType) paramType).getType();

      if (candidate instanceof IQualifierType) {
         candidate = ((IQualifierType) candidate).getType();
      }

      if (candidate instanceof ICPPDeferredClassInstance) {
         candidate = ((ICPPDeferredClassInstance) candidate).getClassTemplate();
      }

      if (candidate == null) { return false; }

      return candidate.isSameType(classType);
   }

   public static boolean isDefaultCtor(final ICPPConstructor ctor) {
      final ICPPParameter[] params = ctor.getParameters();
      return params.length == 0 || params.length == 1 && isVoid(params[0]) || haveAllDefaultValue(params);
   }

   private static boolean isVoid(final ICPPParameter param) {
      if (param.getType() instanceof IBasicType) { return ((IBasicType) param.getType()).getKind().equals(IBasicType.Kind.eVoid); }
      return false;
   }

   public static boolean isVoid(final ICPPASTParameterDeclaration param) {
      return param.getDeclarator().getPointerOperators().length == 0 && isVoid(param.getDeclSpecifier());
   }

   public static boolean isVoid(final IASTDeclSpecifier specifier) {
      return specifier instanceof IASTSimpleDeclSpecifier && ((IASTSimpleDeclSpecifier) specifier).getType() == IASTSimpleDeclSpecifier.t_void;
   }

   public static IASTName getName(final IASTFunctionCallExpression callExpr) {
      if (callExpr instanceof IASTIdExpression) {
         final IASTIdExpression idExpr = (IASTIdExpression) callExpr.getFunctionNameExpression();
         return idExpr.getName();
      }

      final IASTExpression expression = callExpr.getFunctionNameExpression();

      if (expression instanceof ICPPASTFieldReference) {
         return ((ICPPASTFieldReference) expression).getFieldName();
      } else if (expression instanceof IASTIdExpression) { return ((IASTIdExpression) expression).getName().getLastName(); }

      throw new ILTISException("Was not able to determine name of function call").rethrowUnchecked();
   }

   public static IASTDeclaration[] getAllDeclarations(final IASTNode parent) {
      if (parent instanceof IASTTranslationUnit) { return ((IASTTranslationUnit) parent).getDeclarations(); }
      if (parent instanceof ICPPASTCompositeTypeSpecifier) { return ((ICPPASTCompositeTypeSpecifier) parent).getMembers(); }
      if (parent instanceof ICPPASTNamespaceDefinition) { return ((ICPPASTNamespaceDefinition) parent).getDeclarations(); }

      return new IASTDeclaration[0];
   }

   public static ICPPASTDeclSpecifier getDeclSpec(final ICPPASTFunctionDeclarator funDecl) {
      final ICPPASTFunctionDefinition funDef = ASTUtil.getAncestorOfType(funDecl, ICPPASTFunctionDefinition.class);

      if (funDef != null) { return (ICPPASTDeclSpecifier) funDef.getDeclSpecifier(); }

      final IASTSimpleDeclaration simpleDecl = ASTUtil.getAncestorOfType(funDecl, IASTSimpleDeclaration.class);
      return (ICPPASTDeclSpecifier) simpleDecl.getDeclSpecifier();
   }

   public static boolean hasConstPart(final IType type) {
      if (!(type instanceof ITypeContainer)) { return false; }

      if (type instanceof IQualifierType) {
         if (((IQualifierType) type).isConst()) { return true; }
      }

      return hasConstPart(((ITypeContainer) type).getType());
   }

   public static boolean hasVolatilePart(final IType type) {
      if (!(type instanceof ITypeContainer)) { return false; }

      if (type instanceof IQualifierType && ((IQualifierType) type).isVolatile()) { return true; }

      return hasVolatilePart(((ITypeContainer) type).getType());
   }

   public static List<ICPPASTFunctionDefinition> getFunctionDefinitions(final Collection<IASTDeclaration> publicMemFuns) {
      final List<ICPPASTFunctionDefinition> result = new ArrayList<>();

      for (final IASTDeclaration fun : publicMemFuns) {
         final ICPPASTFunctionDefinition candidate = ASTUtil.getChildOfType(fun, ICPPASTFunctionDefinition.class);

         if (candidate != null) {
            result.add(candidate);
         }
      }
      return result;
   }

   public static <T> T getAncestorOfType(final IASTNode node, final Class<? extends IASTNode> T) {
      IASTNode currentNode = node;

      while (currentNode != null) {
         if (T.isInstance(currentNode)) { return Functional.as(currentNode); }

         currentNode = currentNode.getParent();
      }
      return null;
   }

   public static <T> T getChildOfType(final IASTNode node, final Class<? extends IASTNode> T) {
      if (node == null) { return null; }

      if (T.isInstance(node)) { return Functional.as(node); }

      for (final IASTNode child : node.getChildren()) {
         final T currentNode = getChildOfType(child, T);

         if (currentNode != null) { return currentNode; }
      }

      return null;
   }

   public static boolean hasPointerOrRefType(final IASTDeclarator declarator) {
      if (declarator == null) { return false; }

      if (declarator instanceof IASTArrayDeclarator) {
         final IASTArrayDeclarator arrayDecl = (IASTArrayDeclarator) declarator;
         return arrayDecl.getPointerOperators().length > 0;
      }

      if (declarator.getPointerOperators().length > 0) { return true; }

      final IBinding declBinding = declarator.getName().resolveBinding();

      if (declBinding instanceof IVariable) {
         final IType type = ((IVariable) declBinding).getType();
         return hasPointerOrRefType(type);
      } else if (declBinding instanceof ICPPMethod) {
         final IType type = ((ICPPMethod) declBinding).getType().getReturnType();
         return hasPointerOrRefType(type);
      }

      return false;
   }

   public static boolean hasPointerOrRefType(final IType type) {
      return type instanceof ICPPReferenceType || type instanceof IPointerType;
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

   public static IASTCompoundStatement toCompoundStatement(final IASTStatement stmt) {
      if (stmt instanceof IASTCompoundStatement) { return (IASTCompoundStatement) stmt; }

      final IASTCompoundStatement compound = new CPPASTCompoundStatement();
      compound.addStatement(stmt);
      return compound;
   }

   public static IType asNonConst(IType type) {
      while (type instanceof IQualifierType && ((IQualifierType) type).isConst()) {
         type = ((IQualifierType) type).getType();
      }
      return type;
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

   public static Optional<IASTDeclSpecifier> getDeclarationSpecifier(final IASTNode node) {
      if (isPartOf(node, ICPPASTParameterDeclaration.class)) {
         final ICPPASTParameterDeclaration paramDecl = ASTUtil.getAncestorOfType(node, ICPPASTParameterDeclaration.class);
         return Optional.of(paramDecl.getDeclSpecifier());
      } else if (isPartOf(node, ICPPASTFunctionDefinition.class)) {
         final ICPPASTFunctionDefinition funDef = ASTUtil.getAncestorOfType(node, ICPPASTFunctionDefinition.class);
         return Optional.of(funDef.getDeclSpecifier());
      } else if (isPartOf(node, IASTSimpleDeclaration.class)) {
         final IASTSimpleDeclaration simpleDecl = ASTUtil.getAncestorOfType(node, IASTSimpleDeclaration.class);
         return Optional.of(simpleDecl.getDeclSpecifier());
      }
      return Optional.empty();
   }

   private static boolean haveAllDefaultValue(final ICPPParameter[] parameters) {
      for (int i = 0; i < parameters.length; i++) {
         if (!parameters[i].hasDefaultValue()) { return false; }
      }

      return true;
   }

   public static String getQfNameF(final ICPPASTCompositeTypeSpecifier clazz) {
      final IBinding clazzBinding = clazz.getName().resolveBinding();
      ILTISException.Unless.instanceOf(clazzBinding, ICPPClassType.class, "Class expected");
      return getQfName((ICPPClassType) clazzBinding);
   }

   public static String getQfName(final ICPPBinding binding) {
      try {
         return getQfName(binding.getQualifiedName());
      }
      catch (final DOMException e) {
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
      final IASTFunctionCallExpression funcCall = getAncestorOfType(name, IASTFunctionCallExpression.class);

      if (funcCall != null && funcCall.getFunctionNameExpression() instanceof IASTFieldReference) {
         final IASTFieldReference idExp = (IASTFieldReference) funcCall.getFunctionNameExpression();
         return idExp.getFieldName().toString().equals("push_back");
      }

      return false;
   }

   public static boolean isPartOf(final IASTNode node, final Class<? extends IASTNode> clazz) {
      return getAncestorOfType(node, clazz) != null;
   }

   public static IType windDownToRealType(IType type, final boolean stopAtTypeDef) {
      if (type instanceof ITypeContainer) {
         if (stopAtTypeDef && type instanceof ITypedef) { return type; }

         type = ((ITypeContainer) type).getType();
         return windDownToRealType(type, stopAtTypeDef);
      }

      return type;
   }

   public static IType getType(final IASTInitializerClause clause) {
      if (clause instanceof IASTInitializerList) {
         final IASTInitializerClause[] clauses = ((IASTInitializerList) clause).getClauses();
         if (clauses.length > 0) { return getType(clauses[0]); }

         final int noQualifiers = 0;
         return new CPPBasicType(Kind.eInt, noQualifiers);
      }

      if (clause instanceof IASTIdExpression) {
         final IASTName name = ((IASTIdExpression) clause).getName();
         final IBinding binding = name.resolveBinding();

         if (binding instanceof IVariable) { return ((IVariable) binding).getType(); }
         if (binding instanceof ICPPConstructor) { return ((ICPPConstructor) binding).getClassOwner(); }
         if (binding instanceof IFunction) { return ((IFunction) binding).getType().getReturnType(); }
         if (binding instanceof ITypedef) { return ((ITypedef) binding).getType(); }
         if (binding instanceof ICPPClassType) { return (ICPPClassType) binding; }
      }

      if (clause instanceof IASTExpression) { return ((IASTExpression) clause).getExpressionType(); }

      return new CPPBasicType(Kind.eInt, 0);
   }
}

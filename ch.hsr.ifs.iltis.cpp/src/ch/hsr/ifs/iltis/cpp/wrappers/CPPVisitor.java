package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPPlaceholderType.PlaceholderKind;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInternalBinding;

/**
 * A wrapper class for the cdt CPPVisitor. Using this wrapper reduces the amount of warnings respectively the amount of {@code @SuppressWarnings} tags
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public class CPPVisitor extends org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor {

   public static IBinding createBinding(final IASTName name) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.createBinding(name);
   }

   /**
    * Checks if the given name is the name of a friend declaration.
    *
    * @param name
    *        the name to check
    * @return {@code true} if {@code name} is the name of a friend declaration
    */
   public static boolean isNameOfFriendDeclaration(final IASTNode name) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.isNameOfFriendDeclaration(name);
   }

   public static void markRedeclaration(final ICPPInternalBinding ib) {
      org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.markRedeclaration(ib);
   }

   public static boolean isFriendDeclaration(final IASTNode decl) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.isFriendDeclaration(decl);
   }

   public static boolean isConstructor(final IScope containingScope, final IASTDeclarator declarator) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.isConstructor(containingScope, declarator);
   }

   public static boolean isConstructorDeclaration(final IASTName name) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.isConstructorDeclaration(name);
   }

   public static boolean isLastNameInUsingDeclaration(final IASTName name) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.isLastNameInUsingDeclaration(name);
   }

   public static IScope getContainingNonTemplateScope(final IASTNode inputNode) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getContainingNonTemplateScope(inputNode);
   }

   public static IScope getContainingScope(final IASTNode inputNode) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getContainingScope(inputNode);
   }

   public static IScope getContainingScope(final IASTName name) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getContainingScope(name);
   }

   public static IScope getContainingScope(final IASTStatement statement) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getContainingScope(statement);
   }

   public static IASTNode getContainingBlockItem(final IASTNode node) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getContainingBlockItem(node);
   }

   public static class CollectDeclarationsAction extends org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.CollectDeclarationsAction {

      public CollectDeclarationsAction(final IBinding binding) {
         super(binding);
      }

      @Override
      public int visit(final IASTTranslationUnit tu) {
         return super.visit(tu);
      }

      @Override
      public int visit(final IASTName name) {
         return super.visit(name);
      }

      @Override
      public IASTName[] getDeclarations() {
         return super.getDeclarations();
      }
   }

   public static class CollectReferencesAction extends org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.CollectReferencesAction {

      public CollectReferencesAction(final IBinding binding) {
         super(binding);
      }

      @Override
      public int visit(final IASTTranslationUnit tu) {
         return super.visit(tu);
      }

      @Override
      public int visit(final IASTName name) {
         return super.visit(name);
      }

      @Override
      public IASTName[] getReferences() {
         return super.getReferences();
      }
   }

   /**
    * Generate a function type for an implicit function.
    * NOTE: This does not correctly handle parameters with typedef types.
    */
   public static ICPPFunctionType createImplicitFunctionType(final IType returnType, final IParameter[] parameters, final boolean isConst,
         final boolean isVolatile) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.createImplicitFunctionType(returnType, parameters, isConst,
            isVolatile);
   }

   /**
    * Creates the type for the given type id.
    */
   public static IType createType(final IASTTypeId typeid) {
      return createType(typeid.getAbstractDeclarator());
   }

   /**
    * Creates the type for a parameter declaration.
    */
   public static IType createType(final ICPPASTParameterDeclaration pdecl, final boolean forFuncType) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.createType(pdecl, forFuncType);
   }

   /**
    * Creates an array of types for the parameters of the given function declarator.
    */
   public static IType[] createParameterTypes(final ICPPASTFunctionDeclarator fnDtor) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.createParameterTypes(fnDtor);
   }

   public static PlaceholderKind usesAuto(final IASTDeclSpecifier declSpec) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.usesAuto(declSpec);
   }

   public static IType createType(final IASTDeclarator declarator) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.createType(declarator);
   }

   public static IType createType(final IASTDeclarator declarator, final int flags) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.createType(declarator, flags);
   }

   public static IType deduceReturnType(final IASTStatement functionBody, final IASTDeclSpecifier autoDeclSpec, final IASTDeclarator autoDeclarator,
         final PlaceholderKind placeholder, final IASTNode point) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.deduceReturnType(functionBody, autoDeclSpec, autoDeclarator,
            placeholder);
   }

   public static IType createType(final IASTDeclSpecifier declSpec) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.createType(declSpec);
   }

   public static IType getImpliedObjectType(final IScope scope) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getImpliedObjectType(scope);
   }

   public static IType getPointerDiffType(final IASTNode point) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getPointerDiffType();
   }

   public static IType get_type_info(final IASTNode point) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.get_type_info();
   }

   public static IType get_SIZE_T(final IASTNode sizeofExpr) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.get_SIZE_T();
   }

   public static ICPPClassTemplate get_initializer_list(final IASTNode node) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.get_initializer_list();
   }

   public static IASTProblem[] getProblems(final IASTTranslationUnit tu) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getProblems(tu);
   }

   public static IASTName[] getReferences(final IASTTranslationUnit tu, final IBinding binding) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getReferences(tu, binding);
   }

   public static IASTName[] getImplicitReferences(final IASTTranslationUnit tu, final IBinding binding) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getImplicitReferences(tu, binding);
   }

   public static IASTName[] getDeclarations(final IASTTranslationUnit tu, final IBinding binding) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getDeclarations(tu, binding);
   }

   public static String[] getQualifiedName(final IBinding binding) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getQualifiedName(binding);
   }

   public static char[][] getQualifiedNameCharArray(final IBinding binding) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getQualifiedNameCharArray(binding);
   }

   public static boolean isExternC(final IASTNode node) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.isExternC(node);
   }

   public static boolean isExternC(final IASTNode definition, final IASTNode[] declarations) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.isExternC(definition, declarations);
   }

   /**
    * Searches for the function or class enclosing the given node. May return <code>null</code>.
    */
   public static IBinding findEnclosingFunctionOrClass(final IASTNode node) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.findEnclosingFunctionOrClass(node);
   }

   public static IBinding findNameOwner(final IASTName name, final boolean allowFunction) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.findNameOwner(name, allowFunction);
   }

   /**
    * Searches for the first class, namespace, or function, if <code>allowFunction</code>
    * is <code>true</code>, enclosing the declaration the provided node belongs to and returns
    * the binding for it. Returns <code>null</code>, if the declaration is not enclosed by any
    * of the above constructs.
    */
   public static IBinding findDeclarationOwner(final IASTNode node, final boolean allowFunction) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.findDeclarationOwner(node, allowFunction);
   }

   public static IASTName findDeclarationOwnerDefinition(final IASTNode node, final boolean allowFunction) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.findDeclarationOwnerDefinition(node, allowFunction);
   }

   public static boolean doesNotSpecifyType(final IASTDeclSpecifier declspec) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.doesNotSpecifyType(declspec);
   }

   public static ICPPASTDeclarator findInnermostDeclarator(final ICPPASTDeclarator dtor) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.findInnermostDeclarator(dtor);
   }

   /**
    * Traverses a chain of nested homogeneous left-to-right-associative binary expressions and
    * returns a list of their operands in left-to-right order. For example, for the expression
    * a + b * c + d, it will return a list containing expressions: a, b * c, and d.
    *
    * @param binaryExpression
    *        the top-level binary expression
    * @return a list of expression operands from left to right
    */
   public static IASTExpression[] getOperandsOfMultiExpression(final IASTBinaryExpression binaryExpression) {
      return org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor.getOperandsOfMultiExpression(binaryExpression);
   }

}

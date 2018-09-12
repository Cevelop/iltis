package ch.hsr.ifs.iltis.cpp.core.ast.visitor;

import java.util.function.Function;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTAttributeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTToken;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTClassVirtSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDecltypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier;


public class CallbackVisitor extends ASTVisitor {

   private Function<IASTNode, Integer> callback;

   public CallbackVisitor(Function<IASTNode, Integer> callback) {
      super(true);
      this.callback = callback;
   }

   public int visit(IASTTranslationUnit tu) {
      return callback.apply(tu);
   }

   public int visit(IASTName name) {
      return callback.apply(name);
   }

   public int visit(IASTDeclaration declaration) {
      return callback.apply(declaration);
   }

   public int visit(IASTInitializer initializer) {
      return callback.apply(initializer);
   }

   public int visit(IASTParameterDeclaration parameterDeclaration) {
      return callback.apply(parameterDeclaration);
   }

   public int visit(IASTDeclarator declarator) {
      return callback.apply(declarator);
   }

   public int visit(IASTDeclSpecifier declSpec) {
      return callback.apply(declSpec);
   }

   public int visit(IASTArrayModifier arrayModifier) {
      return callback.apply(arrayModifier);
   }

   public int visit(IASTPointerOperator ptrOperator) {
      return callback.apply(ptrOperator);
   }

   public int visit(IASTAttribute attribute) {
      return callback.apply(attribute);
   }

   public int visit(IASTAttributeSpecifier specifier) {
      return callback.apply(specifier);
   }

   public int visit(IASTToken token) {
      return callback.apply(token);
   }

   public int visit(IASTExpression expression) {
      return callback.apply(expression);
   }

   public int visit(IASTStatement statement) {
      return callback.apply(statement);
   }

   public int visit(IASTTypeId typeId) {
      return callback.apply(typeId);
   }

   public int visit(IASTEnumerator enumerator) {
      return callback.apply(enumerator);
   }

   public int visit(IASTProblem problem) {
      return callback.apply(problem);
   }

   public int visit(ICPPASTBaseSpecifier baseSpecifier) {
      return callback.apply(baseSpecifier);
   }

   public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
      return callback.apply(namespaceDefinition);
   }

   public int visit(ICPPASTTemplateParameter templateParameter) {
      return callback.apply(templateParameter);
   }

   public int visit(ICPPASTCapture capture) {
      return callback.apply(capture);
   }

   public int visit(ICASTDesignator designator) {
      return callback.apply(designator);
   }

   public int visit(ICPPASTDesignator designator) {
      return callback.apply(designator);
   }

   public int visit(ICPPASTVirtSpecifier virtSpecifier) {
      return callback.apply(virtSpecifier);
   }

   public int visit(ICPPASTClassVirtSpecifier classVirtSpecifier) {
      return callback.apply(classVirtSpecifier);
   }

   public int visit(ICPPASTDecltypeSpecifier decltypeSpecifier) {
      return callback.apply(decltypeSpecifier);
   }
}

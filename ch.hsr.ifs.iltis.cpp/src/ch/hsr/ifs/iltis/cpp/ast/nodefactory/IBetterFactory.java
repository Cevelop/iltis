package ch.hsr.ifs.iltis.cpp.ast.nodefactory;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTASMDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTAlignmentSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblemExpression;
import org.eclipse.cdt.core.dom.ast.IASTProblemStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTToken;
import org.eclipse.cdt.core.dom.ast.IASTTokenList;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdInitializerExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.ast.gnu.IGCCASTAttributeList;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTCompoundStatementExpression;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IToken;


public interface IBetterFactory extends ICPPNodeFactory {

   @Override
   ICPPASTName newName(String name);

   ICPPASTQualifiedName newQualifiedName(ICPPASTName... names);

   IASTIdExpression newIdExpression(String name);

   ICPPASTDeclarator newDeclarator(String name);

   ICPPASTDeclarator newDeclarator(String name, IASTInitializer initializer);

   ICPPASTDeclarator newPointerDeclarator(String name);

   ICPPASTDeclarator newPointerDeclarator(String name, IASTInitializerClause initClause);

   ICPPASTDeclarator newReferenceDeclarator(String name);

   ICPPASTDeclarator newReferenceDeclarator(String name, IASTInitializerClause initClause);

   IASTDeclarationStatement newDeclarationStatement(IASTDeclSpecifier declSpecifier, IASTDeclarator... declarators);

   ICPPASTFieldReference newFieldReference(String owner, String field);

   ICPPASTFieldReference newFieldReference(String owner, String field, boolean isPointerDereference);

   IASTUnaryExpression newParenthesizedExpression(IASTExpression operand);

   IASTUnaryExpression newAddressOfExpression(String operand);

   IASTUnaryExpression newAddressOfExpression(IASTExpression operand);

   IASTUnaryExpression newDereferenceExpression(String operand);

   IASTUnaryExpression newDereferenceExpression(IASTExpression operand);

   ICPPASTNewExpression newNewExpression(IASTDeclSpecifier declSpecifier, IASTInitializerClause... arguments);

   @Override
   ICPPASTFunctionCallExpression newFunctionCallExpression(IASTExpression idExpr, IASTExpression arg);

   ICPPASTFunctionCallExpression newFunctionCallExpression(IASTExpression idExpr, IASTExpression... arguments);

   ICPPASTFunctionCallExpression newFunctionCallExpression(String functionName, IASTExpression... arguments);

   IASTExpressionStatement newFunctionCallStatement(IASTExpression idExpr, IASTExpression... arguments);

   IASTExpressionStatement newFunctionCallStatement(String functionName, IASTExpression... arguments);

   ICPPASTLiteralExpression newIntegerLiteral(int n);

   ICPPASTLiteralExpression newFloatLiteral(float n);

   ICPPASTLiteralExpression newCharLiteral(char c);

   ICPPASTLiteralExpression newStringLiteral(String str);

   ICPPASTLiteralExpression newNullptr();

   ICPPASTNamedTypeSpecifier newTypedefNameSpecifier(String name);

   ICPPASTName addTemplateArgumentsToLastName(String name, IASTNode... arguments);

   ICPPASTNamedTypeSpecifier newTemplateDeclSpecifier(String name, IASTNode... arguments);

   ICPPASTCompositeTypeSpecifier newCompositeTypeSpecifier(int key, String name);

   ICPPASTFunctionDeclarator newFunctionDeclarator(String name);

   ICPPASTFunctionDeclarator newFunctionDeclarator();

   ICPPASTConstructorChainInitializer newConstructorChainInitializer(String variableName);

   IASTExpressionStatement newAssignmentStatement(IASTExpression op1, IASTExpression op2);

   IASTExpressionStatement newMemberFunctionCallStatement(String owner, String field, IASTExpression... arguments);

   ICPPASTFunctionCallExpression newMemberFunctionCallExpression(String owner, String field, IASTExpression... arguments);

   ICPPASTInitializerList newInitializerList(IASTInitializerClause... clauses);

   ICPPASTSimpleDeclSpecifier newSimpleDeclSpecifier(Kind kind);

   ICPPASTSimpleDeclSpecifier newSimpleDeclSpecifier(int type);

   ICPPASTTypeId newTypeId(IASTDeclSpecifier declSpecifier);

   IASTDeclarationStatement newDeclarationStatementFromDeclarator(IASTDeclarator declarator);

   /**
    * Creates a new template declaration with given template parameters and the
    * corresponding body of the template (the template implementation).
    * 
    * @param params
    *        Template parameters.
    * @param body
    *        Template body (implementation).
    * @return Template declaration.
    */
   IASTNode newTemplateDeclaration(IASTDeclaration body, ICPPASTSimpleTypeTemplateParameter... params);

   IASTNode newTemplateDeclaration(IASTDeclaration body, List<ICPPASTSimpleTypeTemplateParameter> params);

   /**
    * Creates a new template parameter with a typename specifier and no default
    * type, e.g. <code>typename T</code>
    * 
    * @param paramName
    *        Name of the template parameter.
    * @return Simple type template parameter.
    */
   ICPPASTSimpleTypeTemplateParameter newTemplateParameterDefinition(String paramName);

   IASTFunctionCallExpression newFunctionCallExpression(String functionName, IASTNode... args);

   IASTFunctionCallExpression newMemberFunctionCallExpression(IASTName objectName, String methodName, IASTNode... args);

   IASTBinaryExpression newAssignment(IASTExpression lhs, IASTExpression rhs);

   IASTBinaryExpression newPlusAssignment(IASTExpression lhs, IASTExpression rhs);

   IASTBinaryExpression newPlusExpression(IASTExpression lhs, IASTExpression rhs);

   IASTBinaryExpression newMinusExpression(IASTExpression lhs, IASTExpression rhs);

   IASTUnaryExpression newLogicalNotExpression(IASTExpression operand);

   IASTBinaryExpression newEqualityComparison(IASTExpression lhs, IASTExpression rhs, boolean isEqual);

   IASTUnaryExpression newDereferenceOperatorExpression(IASTExpression expression);

   IASTUnaryExpression newAdressOperatorExpression(IASTExpression expression);

   IASTUnaryExpression newNegatedExpression(IASTExpression expression);

   IASTEqualsInitializer newEqualsInitializerWithList(IASTInitializerClause... clauses);

   IASTDeclarationStatement newDeclarationStatement(String type, String varName, IASTInitializerClause initializerClause);

   IASTDeclarationStatement newDeclarationStatement(String type, String varName, IASTInitializer initializer);

   IASTCompoundStatement newCompoundStatement(IASTStatement... statements);

   IASTIfStatement newIfStatement(IASTExpression condition, IASTCompoundStatement then);

   IASTUnaryExpression newBracketedExpression(IASTExpression operand);

   ICPPASTNamedTypeSpecifier newNamedTypeSpecifier(String typeName);

   ICPPASTTypeId newIASTTypeId(ICPPASTNamedTypeSpecifier newNamedTypeSpecifier);

   IASTConditionalExpression newConditionalExpression(IASTExpression condition, IASTExpression positive, IASTExpression negative);
   
   
   
   /* INodeFactory from codan */
   /** @since 5.10 */
   public IASTAlignmentSpecifier newAlignmentSpecifier(IASTExpression expression);
   
   /** @since 5.10 */
   public IASTAlignmentSpecifier newAlignmentSpecifier(IASTTypeId typeId);
   
   public IASTArrayModifier newArrayModifier(IASTExpression expr);

   public IASTASMDeclaration newASMDeclaration(String assembly);
   
   /** @since 5.4 */
   public IASTAttribute newAttribute(char[] name, IASTToken argumentClause);
   
   public IASTBreakStatement newBreakStatement();
   
   public IASTCaseStatement newCaseStatement(IASTExpression expr);
   
   public IASTCompoundStatement newCompoundStatement();
   
   public IASTConditionalExpression newConditionalExpession(IASTExpression condition, IASTExpression positive, IASTExpression negative);

   public IASTContinueStatement newContinueStatement();

   public IASTDeclarationStatement newDeclarationStatement(IASTDeclaration declaration);

   public IASTDefaultStatement newDefaultStatement();

   public IASTDoStatement newDoStatement(IASTStatement body, IASTExpression condition);

   public IASTEnumerationSpecifier newEnumerationSpecifier(IASTName name);
   
   public IASTEnumerator newEnumerator(IASTName name, IASTExpression value);
   
   /**
    * @since 5.2
    */
   public IASTEqualsInitializer newEqualsInitializer(IASTInitializerClause initClause);

   public IASTExpressionStatement newExpressionStatement(IASTExpression expression);

   /**
    * @since 6.0
    */
   public IGCCASTAttributeList newGCCAttributeList();

   public IGNUASTCompoundStatementExpression newGNUCompoundStatementExpression(IASTCompoundStatement compoundStatement);

   public IASTGotoStatement newGotoStatement(IASTName name);

   /**
    * @since 5.8
    */
   public IASTStatement newGotoStatement(IASTExpression expression);

   public IASTIdExpression newIdExpression(IASTName name);

   public IASTLabelStatement newLabelStatement(IASTName name, IASTStatement nestedStatement);
   
   public IASTNullStatement newNullStatement();

   public IASTPointer newPointer();

   public IASTProblem newProblem(int id, char[] arg, boolean error);

   public IASTProblemDeclaration newProblemDeclaration(IASTProblem problem);
   
   public IASTProblemExpression newProblemExpression(IASTProblem problem);
   
   public IASTProblemStatement newProblemStatement(IASTProblem problem);
   
   public IASTReturnStatement newReturnStatement(IASTExpression retValue);
   
   public IASTSimpleDeclaration newSimpleDeclaration(IASTDeclSpecifier declSpecifier);

   /** @since 5.4 */
   public IASTToken newToken(int tokenType, char[] tokenImage);

   /** @since 5.4 */
   public IASTTokenList newTokenList();

   public IASTTypeIdInitializerExpression newTypeIdInitializerExpression(IASTTypeId typeId, IASTInitializer initializer);
   
   /**
    * Adjusts the end-offset of a node to be the same as the end-offset of a given node.
    * <par> May throw an exception when either one of the nodes provided was not created by this factory.
    * @param node a node created by this factory
    * @param endNode a node created by this factory defining the end for the other node.
    * @since 5.2
    */
   void setEndOffset(IASTNode node, IASTNode endNode);

   /**
    * Provides the end offset for a node. The offset is an artificial numbers that identifies the
    * position of a node in the translation unit. It is not a file-offset. You can obtain a
    * valid offset via {@link IToken#getEndOffset()} from a token provided by the scanner for 
    * this translation unit.
    * <par> May throw an exception when the node provided was not created by this factory.
    * @param node a node created by this factory
    * @param endOffset the end offset (exclusive) for the node
    * @see #newTranslationUnit(IScanner)
    * @since 5.2
    */
   void setEndOffset(IASTNode node, int endOffset);

   /**
    * Provides the offsets for a node. The offsets are artificial numbers that identify the
    * position of a node in the translation unit. They are not file-offsets. You can obtain
    * valid offsets via {@link IToken#getOffset()} or {@link IToken#getEndOffset()} from tokens
    * provided by the scanner for this translation unit.
    * <par> May throw an exception when the node provided was not created by this factory.
    * @param node a node created by this factory
    * @param offset the offset (inclusive) for the node
    * @param endOffset the end offset (exclusive) for the node
    * @see #newTranslationUnit(IScanner)
    * @since 5.2
    */
   public void setOffsets(IASTNode node, int offset, int endOffset);
}

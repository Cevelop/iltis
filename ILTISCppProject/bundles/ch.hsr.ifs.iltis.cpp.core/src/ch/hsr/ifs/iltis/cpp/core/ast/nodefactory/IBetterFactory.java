package ch.hsr.ifs.iltis.cpp.core.ast.nodefactory;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
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


public interface IBetterFactory extends ICPPNodeFactory {

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

   /**
    * Creates a new type identifier with an empty declarator
    */
   ICPPASTTypeId newTypeId(IASTDeclSpecifier declSpecifier);

   IASTDeclarationStatement newDeclarationStatementFromDeclarator(IASTDeclarator declarator);

   IASTNode newTemplateDeclaration(IASTDeclaration body, ICPPASTSimpleTypeTemplateParameter... params);

   IASTNode newTemplateDeclaration(IASTDeclaration body, List<ICPPASTSimpleTypeTemplateParameter> params);

   ICPPASTSimpleTypeTemplateParameter newTemplateParameterDefinition(String paramName);

   IASTFunctionCallExpression newFunctionCallExpression(String functionName, IASTInitializerClause... args);

   IASTFunctionCallExpression newMemberFunctionCallExpression(IASTName objectName, String methodName, IASTInitializerClause... args);

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

   /**
    * Creates a new type identifier with an empty name
    */
   ICPPASTTypeId newIASTTypeId(ICPPASTNamedTypeSpecifier newNamedTypeSpecifier);

   IASTConditionalExpression newConditionalExpression(IASTExpression condition, IASTExpression positive, IASTExpression negative);

}

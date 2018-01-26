package ch.hsr.ifs.iltis.cpp.ast.nodefactory;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
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
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;


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

}

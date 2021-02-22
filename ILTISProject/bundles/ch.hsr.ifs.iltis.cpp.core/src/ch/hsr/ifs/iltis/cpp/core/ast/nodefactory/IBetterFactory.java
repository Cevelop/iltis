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
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPBinaryOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.ICPPUnaryOperator;


public interface IBetterFactory extends ICPPNodeFactory {

    ICPPASTQualifiedName newQualifiedName(ICPPASTName... names);

    /**
     * @since 1.1
     */
    ICPPASTQualifiedName newQualifiedName(String[] qualifiers, ICPPASTName name);

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

    IASTExpressionStatement newFunctionCallStatement(IASTExpression idExpr, IASTInitializerClause... arguments);

    IASTExpressionStatement newFunctionCallStatement(String functionName, IASTInitializerClause... arguments);

    ICPPASTLiteralExpression newIntegerLiteral(int n);

    ICPPASTLiteralExpression newFloatLiteral(float n);

    ICPPASTLiteralExpression newCharLiteral(char c);

    ICPPASTLiteralExpression newStringLiteral(String str);

    ICPPASTLiteralExpression newNullptr();

    ICPPASTNamedTypeSpecifier newTypedefNameSpecifier(String name);

    ICPPASTName addTemplateArgumentsToLastName(String name, IASTNode... arguments);

    ICPPASTNamedTypeSpecifier newTemplateDeclSpecifier(String name, IASTNode... arguments);

    ICPPASTCompositeTypeSpecifier newCompositeTypeSpecifier(int key, String name);

    ICPPASTFunctionDeclarator newFunctionDeclarator();

    ICPPASTFunctionDeclarator newFunctionDeclarator(String name, IASTParameterDeclaration... paramDeclarations);

    ICPPASTFunctionDeclarator newFunctionDeclarator(IASTName name, IASTParameterDeclaration... paramDeclarations);

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

    ICPPASTTemplateDeclaration newTemplateDeclaration(IASTDeclaration body, ICPPASTTemplateParameter... params);

    ICPPASTTemplateDeclaration newTemplateDeclaration(IASTDeclaration body, List<ICPPASTTemplateParameter> params);

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

    /* Create Also Factory Methods */

    IASTCompoundStatement newCompoundStatement(Consumer<IASTCompoundStatement> also);

    IASTDeclarationStatement newDeclarationStatement(Consumer<IASTDeclarationStatement> also);

    ICPPASTDeclarator newDeclarator(IASTName name, Consumer<ICPPASTDeclarator> also);

    ICPPASTFunctionCallExpression newFunctionCallExpression(Consumer<ICPPASTFunctionCallExpression> also);

    ICPPASTFunctionDeclarator newFunctionDeclarator(Consumer<ICPPASTFunctionDeclarator> also);

    ICPPASTLambdaExpression newLambdaExpression(Consumer<ICPPASTLambdaExpression> also);

    IASTSimpleDeclaration newSimpleDeclaration(Consumer<IASTSimpleDeclaration> also);

    ICPPASTTemplateId newTemplateId(IASTName name, Consumer<ICPPASTTemplateId> also);

    /* Enum Operator Factory Methods */

    /**
     * @since 3.0
     */
    ICPPASTUnaryExpression newUnaryExpression(ICPPUnaryOperator operator, IASTExpression operand);

    /**
     * @since 3.0
     */
    ICPPASTBinaryExpression newBinaryExpression(ICPPBinaryOperator op, IASTExpression expr1, IASTExpression expr2);

    /**
     * @since 3.0
     */
    ICPPASTBinaryExpression newBinaryExpression(ICPPBinaryOperator op, IASTExpression expr1, IASTInitializerClause expr2);

    /* Magic Factory Methods */

    /**
     * Creates a new binary expression while automatically grouping the operands if necessary.
     *
     * <pre>
     * Examples:
     *
     * [a + b] * [10] -> [(a + b)] * [10]
     *
     * [a ? b : c] = [d] -> [(a ? b : c)] = [d]
     *
     * [a * b] * [c] -> no additional grouping
     * </pre>
     *
     * @param operator
     * The operator (one of {@link IASTBinaryExpression}{@code .op_...})
     * @param operand1
     * The left operand
     * @param operand2
     * The right operand
     * @return A new binary expression with correct grouping.
     * 
     * @since 3.0
     */
    IASTBinaryExpression newMagicPrecedenceBinaryExpression(ICPPBinaryOperator operator, IASTExpression operand1, IASTInitializerClause operand2);

    /**
     * Creates a new unary expression while automatically grouping the operands if necessary.
     *
     * <pre>
     * Example:
     *
     * *[c++] -> *[(c++)]
     * </pre>
     *
     * @param operator
     * The operator (one of {@link IASTUnaryExpression}{@code .op_...})
     * @param operand
     * The operand
     * @return A new unary expression with correct grouping
     * 
     * @since 3.0
     */
    IASTUnaryExpression newMagicPrecedenceUnaryExpression(ICPPUnaryOperator operator, IASTExpression operand);

    /**
     * Creates a new conditional expression while automatically grouping the operands if necessary.
     *
     * <pre>
     * Examples:
     *
     * [a + b] * [10] -> [(a + b)] * [10]
     *
     * [a] ? [b] : [c=d] ->
     * </pre>
     *
     * @param condition
     * @param positive
     * @param negative
     * @return
     */
    IASTConditionalExpression newMagicPrefedenceConditionalExpression(IASTExpression condition, IASTExpression positive, IASTExpression negative);

}

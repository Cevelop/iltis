package ch.hsr.ifs.iltis.cpp.core.ast.nodefactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
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
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarationStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;

import ch.hsr.ifs.iltis.core.core.collections.CollectionUtil;
import ch.hsr.ifs.iltis.core.core.functional.functions.Consumer;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.CPPBinaryOperator;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.CPPOtherOperators;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.operators.CPPUnaryOperator;
import ch.hsr.ifs.iltis.cpp.core.util.constants.CommonCPPConstants;


@SuppressWarnings("restriction")
public class ExtendedNodeFactory extends CPPNodeFactory implements IBetterFactory {

   private static final ExtendedNodeFactory DEFAULT_INSTANCE = new ExtendedNodeFactory();

   public static ExtendedNodeFactory getDefault() {
      return DEFAULT_INSTANCE;
   }

   @Override
   public ICPPASTName newName(final String strName) {
      final String[] components = strName.split(CommonCPPConstants.SCOPE_RES_OP);
      final ICPPASTName name = newName(components[0].toCharArray());
      if (components.length > 1) {
         final ICPPASTQualifiedName qualifiedName = newQualifiedName(name);
         for (int i = 1; i < components.length; ++i) {
            qualifiedName.addName(newName(components[i].toCharArray()));
         }
         return qualifiedName;
      }
      return name;
   }

   @Override
   public ICPPASTQualifiedName newQualifiedName(final ICPPASTName... names) {
      final ICPPASTQualifiedName qualifiedName = newQualifiedName(names[0]);
      CollectionUtil.tail(Arrays.asList(names)).forEach(qualifiedName::addName);
      return qualifiedName;
   }

   @Override
   public IASTIdExpression newIdExpression(final String name) {
      return newIdExpression(newName(name));
   }

   @Override
   public ICPPASTDeclarator newDeclarator(final String name) {
      return newDeclarator(newName(name));
   }

   @Override
   public ICPPASTDeclarator newDeclarator(final String name, final IASTInitializer initializer) {
      final ICPPASTDeclarator declarator = newDeclarator(name);
      declarator.setInitializer(initializer);
      return declarator;
   }

   @Override
   public ICPPASTDeclarator newPointerDeclarator(final String name) {
      final ICPPASTDeclarator declarator = newDeclarator(name);
      declarator.addPointerOperator(newPointer());
      return declarator;
   }

   @Override
   public ICPPASTDeclarator newPointerDeclarator(final String name, final IASTInitializerClause initClause) {
      final ICPPASTDeclarator declarator = newPointerDeclarator(name);
      declarator.setInitializer(newEqualsInitializer(initClause));
      return declarator;
   }

   @Override
   public ICPPASTDeclarator newReferenceDeclarator(final String name) {
      final ICPPASTDeclarator declarator = newDeclarator(name);
      declarator.addPointerOperator(newReferenceOperator(false));
      return declarator;
   }

   @Override
   public ICPPASTDeclarator newReferenceDeclarator(final String name, final IASTInitializerClause initClause) {
      final ICPPASTDeclarator declarator = newReferenceDeclarator(name);
      declarator.setInitializer(newEqualsInitializer(initClause));
      return declarator;
   }

   @Override
   public IASTDeclarationStatement newDeclarationStatement(final IASTDeclSpecifier declSpecifier, final IASTDeclarator... declarators) {
      final IASTSimpleDeclaration simpleDeclaration = newSimpleDeclaration(declSpecifier);
      for (final IASTDeclarator declarator : declarators) {
         simpleDeclaration.addDeclarator(declarator);
      }
      return newDeclarationStatement(simpleDeclaration);
   }

   @Override
   public IASTDeclarationStatement newDeclarationStatementFromDeclarator(IASTDeclarator declarator) {
      final IASTDeclSpecifier newDeclSpecifier = ((IASTSimpleDeclaration) declarator.getParent()).getDeclSpecifier().copy();
      final IASTSimpleDeclaration newDeclaration = newSimpleDeclaration(newDeclSpecifier);
      final IASTDeclarator newDeclarator = declarator.copy();
      newDeclaration.addDeclarator(newDeclarator);
      return newDeclarationStatement(newDeclaration);
   }

   @Override
   public ICPPASTFieldReference newFieldReference(final String owner, final String field) {
      return newFieldReference(owner, field, false);
   }

   @Override
   public ICPPASTFieldReference newFieldReference(final String owner, final String field, final boolean isPointerDereference) {
      final ICPPASTFieldReference fieldReference = newFieldReference(newName(field), newIdExpression(owner));
      fieldReference.setIsPointerDereference(isPointerDereference);
      return fieldReference;
   }

   @Override
   public IASTUnaryExpression newParenthesizedExpression(final IASTExpression operand) {
      return newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, operand);
   }

   @Override
   public IASTUnaryExpression newAddressOfExpression(final String operand) {
      return newAddressOfExpression(newIdExpression(operand));
   }

   @Override
   public IASTUnaryExpression newAddressOfExpression(final IASTExpression operand) {
      return newUnaryExpression(IASTUnaryExpression.op_amper, operand);
   }

   @Override
   public IASTUnaryExpression newDereferenceExpression(final String operand) {
      return newDereferenceExpression(newIdExpression(operand));
   }

   @Override
   public IASTUnaryExpression newDereferenceExpression(final IASTExpression operand) {
      return newUnaryExpression(IASTUnaryExpression.op_star, operand);
   }

   @Override
   public ICPPASTNewExpression newNewExpression(final IASTDeclSpecifier declSpecifier, final IASTInitializerClause... arguments) {
      final ICPPASTTypeId typeId = newTypeId(declSpecifier, null);
      final ICPPASTConstructorInitializer constructorInitializer = newConstructorInitializer(arguments);
      return newNewExpression(null, constructorInitializer, typeId);
   }

   @Override
   public ICPPASTFunctionCallExpression newFunctionCallExpression(final IASTExpression idExpr, final IASTExpression... arguments) {
      return super.newFunctionCallExpression(idExpr, arguments);
   }

   @Override
   public ICPPASTFunctionCallExpression newFunctionCallExpression(final String functionName, final IASTInitializerClause... arguments) {
      return newFunctionCallExpression(newIdExpression(functionName), arguments);
   }

   @Override
   public IASTExpressionStatement newFunctionCallStatement(final IASTExpression idExpr, final IASTInitializerClause... arguments) {
      return newExpressionStatement(newFunctionCallExpression(idExpr, arguments));
   }

   @Override
   public IASTExpressionStatement newFunctionCallStatement(final String functionName, final IASTInitializerClause... arguments) {
      return newFunctionCallStatement(newIdExpression(functionName), arguments);
   }

   @Override
   public ICPPASTLiteralExpression newIntegerLiteral(final int n) {
      return newLiteralExpression(IASTLiteralExpression.lk_integer_constant, String.valueOf(n));
   }

   @Override
   public ICPPASTLiteralExpression newFloatLiteral(float n) {
      return newLiteralExpression(IASTLiteralExpression.lk_float_constant, String.valueOf(n));
   }

   @Override
   public ICPPASTLiteralExpression newCharLiteral(char c) {
      return newLiteralExpression(IASTLiteralExpression.lk_char_constant, String.valueOf(c));
   }

   @Override
   public ICPPASTLiteralExpression newStringLiteral(final String str) {
      return newLiteralExpression(IASTLiteralExpression.lk_string_literal, str);
   }

   @Override
   public ICPPASTLiteralExpression newNullptr() {
      return newLiteralExpression(IASTLiteralExpression.lk_nullptr, CommonCPPConstants.NULLPTR);
   }

   @Override
   public ICPPASTNamedTypeSpecifier newTypedefNameSpecifier(final String name) {
      return newTypedefNameSpecifier(newName(name));
   }

   @Override
   public ICPPASTName addTemplateArgumentsToLastName(final String nameStr, final IASTNode... arguments) {
      IASTName name = newName(nameStr);
      IASTName nameToWrapInTemplateId;
      if (name instanceof ICPPASTQualifiedName) {
         ICPPASTQualifiedName qualifiedName = (ICPPASTQualifiedName) name;
         nameToWrapInTemplateId = qualifiedName.getLastName();
      } else {
         nameToWrapInTemplateId = name;
      }

      final ICPPASTTemplateId templateId = newTemplateId(nameToWrapInTemplateId);
      for (final IASTNode arg : arguments) {
         if (arg instanceof IASTTypeId) {
            final IASTTypeId typeId = (IASTTypeId) arg;
            templateId.addTemplateArgument(typeId);
         } else if (arg instanceof IASTExpression) {
            final IASTExpression expr = (IASTExpression) arg;
            templateId.addTemplateArgument(expr);
         } else if (arg instanceof IASTDeclSpecifier) {
            final IASTDeclSpecifier declSpec = (IASTDeclSpecifier) arg;
            final IASTTypeId typeId = newTypeId(declSpec, null);
            templateId.addTemplateArgument(typeId);
         } else throw new IllegalArgumentException("Template argument must either be an IASTTypeId or an IASTExpression.");
      }

      if (name instanceof ICPPASTQualifiedName) {
         ICPPASTQualifiedName qualifiedName = (ICPPASTQualifiedName) name;
         qualifiedName.setLastName(templateId);
         return qualifiedName;
      } else {
         return templateId;
      }
   }

   @Override
   public ICPPASTTemplateDeclaration newTemplateDeclaration(IASTDeclaration body, ICPPASTTemplateParameter... params) {
      ICPPASTTemplateDeclaration templateDecl = newTemplateDeclaration(body);
      for (ICPPASTTemplateParameter param : params)
         templateDecl.addTemplateParameter(param);
      return templateDecl;
   }

   @Override
   public ICPPASTTemplateDeclaration newTemplateDeclaration(IASTDeclaration body, List<ICPPASTTemplateParameter> params) {
      return newTemplateDeclaration(body, params.toArray(new ICPPASTTemplateParameter[params.size()]));
   }

   @Override
   public ICPPASTNamedTypeSpecifier newTemplateDeclSpecifier(final String name, final IASTNode... arguments) {
      return newNamedTypeSpecifier(addTemplateArgumentsToLastName(name, arguments));
   }

   @Override
   public ICPPASTSimpleTypeTemplateParameter newTemplateParameterDefinition(String paramName) {
      return new CPPASTSimpleTypeTemplateParameter(ICPPASTSimpleTypeTemplateParameter.st_typename, newName(paramName.toCharArray()), null);
   }

   @Override
   public ICPPASTCompositeTypeSpecifier newCompositeTypeSpecifier(final int key, final String name) {
      return newCompositeTypeSpecifier(key, newName(name));
   }

   @Override
   public ICPPASTFunctionDeclarator newFunctionDeclarator() {
      return super.newFunctionDeclarator(null);
   }

   @Override
   public ICPPASTFunctionDeclarator newFunctionDeclarator(final String name, final IASTParameterDeclaration... paramDeclarations) {
      return newFunctionDeclarator(newName(name), paramDeclarations);
   }

   @Override
   public ICPPASTFunctionDeclarator newFunctionDeclarator(final IASTName name, final IASTParameterDeclaration... paramDeclarations) {
      ICPPASTFunctionDeclarator declarator = newFunctionDeclarator(name);
      for (IASTParameterDeclaration paramDecl : paramDeclarations) {
         declarator.addParameterDeclaration(paramDecl);
      }
      return declarator;
   }

   @Override
   public ICPPASTConstructorChainInitializer newConstructorChainInitializer(final String variableName) {
      final IASTName memberName = newName(variableName);
      final IASTInitializerList initializerList = newInitializerList();
      initializerList.addClause(newIdExpression(variableName));
      return newConstructorChainInitializer(memberName, initializerList);
   }

   @Override
   public IASTExpressionStatement newAssignmentStatement(final IASTExpression op1, final IASTExpression op2) {
      final IASTExpression assignment = newBinaryExpression(IASTBinaryExpression.op_assign, op1, op2);
      return newExpressionStatement(assignment);
   }

   @Override
   public IASTExpressionStatement newMemberFunctionCallStatement(final String owner, final String field, final IASTExpression... arguments) {
      return newExpressionStatement(newMemberFunctionCallExpression(owner, field, arguments));
   }

   @Override
   public ICPPASTFunctionCallExpression newMemberFunctionCallExpression(final String owner, final String field, final IASTExpression... arguments) {
      final IASTFieldReference fieldReference = newFieldReference(owner, field);
      return newFunctionCallExpression(fieldReference, arguments);
   }

   @Override
   public ICPPASTInitializerList newInitializerList(final IASTInitializerClause... clauses) {
      final ICPPASTInitializerList initList = super.newInitializerList();
      for (final IASTInitializerClause clause : clauses) {
         initList.addClause(clause);
      }
      return initList;
   }

   @Override
   public ICPPASTSimpleDeclSpecifier newSimpleDeclSpecifier(final Kind kind) {
      final ICPPASTSimpleDeclSpecifier declSpec = newSimpleDeclSpecifier();
      declSpec.setType(kind);
      return declSpec;
   }

   @Override
   public ICPPASTSimpleDeclSpecifier newSimpleDeclSpecifier(final int type) {
      final ICPPASTSimpleDeclSpecifier declSpec = newSimpleDeclSpecifier();
      declSpec.setType(type);
      return declSpec;
   }

   @Override
   public ICPPASTTypeId newTypeId(final IASTDeclSpecifier declSpecifier) {
      return newTypeId(declSpecifier, newDeclarator(""));
   }

   @Override
   public IASTFunctionCallExpression newMemberFunctionCallExpression(IASTName objectName, String methodName, IASTInitializerClause... args) {
      final IASTFieldReference fieldReference = newFieldReference(newName(methodName), newIdExpression(objectName.copy()));
      return newFunctionCallExpression(fieldReference, args);
   }

   @Override
   public IASTBinaryExpression newAssignment(IASTExpression lhs, IASTExpression rhs) {
      return newBinaryExpression(IASTBinaryExpression.op_assign, lhs, rhs);
   }

   @Override
   public IASTBinaryExpression newPlusAssignment(IASTExpression lhs, IASTExpression rhs) {
      return newBinaryExpression(IASTBinaryExpression.op_plusAssign, lhs, rhs);
   }

   @Override
   public IASTBinaryExpression newPlusExpression(IASTExpression lhs, IASTExpression rhs) {
      return newBinaryExpression(IASTBinaryExpression.op_plus, lhs, rhs);
   }

   @Override
   public IASTBinaryExpression newMinusExpression(IASTExpression lhs, IASTExpression rhs) {
      return newBinaryExpression(IASTBinaryExpression.op_minus, lhs, rhs);
   }

   @Override
   public IASTUnaryExpression newLogicalNotExpression(IASTExpression operand) {
      return newUnaryExpression(IASTUnaryExpression.op_not, operand);
   }

   @Override
   public IASTBinaryExpression newEqualityComparison(IASTExpression lhs, IASTExpression rhs, boolean isEqual) {
      final int op = isEqual ? IASTBinaryExpression.op_equals : IASTBinaryExpression.op_notequals;
      return newBinaryExpression(op, lhs, rhs);
   }

   @Override
   public IASTUnaryExpression newDereferenceOperatorExpression(IASTExpression expression) {
      return newUnaryExpression(IASTUnaryExpression.op_star, expression);
   }

   @Override
   public IASTUnaryExpression newAdressOperatorExpression(IASTExpression expression) {
      return newUnaryExpression(IASTUnaryExpression.op_amper, expression);
   }

   @Override
   public IASTUnaryExpression newNegatedExpression(IASTExpression expression) {
      return newUnaryExpression(IASTUnaryExpression.op_minus, expression);
   }

   @Override
   public IASTEqualsInitializer newEqualsInitializerWithList(IASTInitializerClause... clauses) {
      return newEqualsInitializer(newInitializerList(clauses));
   }

   @Override
   public IASTDeclarationStatement newDeclarationStatement(String type, String varName, IASTInitializerClause initializerClause) {
      final IASTInitializer initializer = newEqualsInitializer(initializerClause);
      return newDeclarationStatement(type, varName, initializer);
   }

   @Override
   public IASTDeclarationStatement newDeclarationStatement(String type, String varName, IASTInitializer initializer) {
      final IASTDeclSpecifier declSpecifier = newTypedefNameSpecifier(newName(type));
      final IASTSimpleDeclaration simpleDeclaration = newSimpleDeclaration(declSpecifier);
      final IASTDeclarator declarator = newDeclarator(newName(varName));
      declarator.setInitializer(initializer);
      simpleDeclaration.addDeclarator(declarator);
      return newDeclarationStatement(simpleDeclaration);
   }

   @Override
   public IASTCompoundStatement newCompoundStatement(IASTStatement... statements) {
      final IASTCompoundStatement compoundStatement = newCompoundStatement();
      for (final IASTStatement statement : statements) {
         compoundStatement.addStatement(statement);
      }
      return compoundStatement;
   }

   @Override
   public IASTIfStatement newIfStatement(IASTExpression condition, IASTCompoundStatement then) {
      return newIfStatement(condition, then, null);
   }

   @Override
   public IASTUnaryExpression newBracketedExpression(IASTExpression operand) {
      return newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, operand);
   }

   @Override
   public ICPPASTNamedTypeSpecifier newNamedTypeSpecifier(String typeName) {
      return newTypedefNameSpecifier(newName(typeName));
   }

   @Override
   public ICPPASTTypeId newIASTTypeId(ICPPASTNamedTypeSpecifier newNamedTypeSpecifier) {
      return newTypeId(newNamedTypeSpecifier, newDeclarator(newName()));
   }

   @Override
   public IASTConditionalExpression newConditionalExpression(IASTExpression condition, IASTExpression positive, IASTExpression negative) {
      return super.newConditionalExpession(condition, positive, negative);
   }

   /* Create Also Factory Methods */

   @Override
   public IASTCompoundStatement newCompoundStatement(Consumer<IASTCompoundStatement> also) {
      return createAlso(this::newCompoundStatement, also);
   }

   @Override
   public IASTDeclarationStatement newDeclarationStatement(Consumer<IASTDeclarationStatement> also) {
      return createAlso(CPPASTDeclarationStatement::new, also);
   }

   @Override
   public ICPPASTDeclarator newDeclarator(IASTName name, Consumer<ICPPASTDeclarator> also) {
      return createAlso(() -> newDeclarator(name), also);
   }

   @Override
   public ICPPASTFunctionCallExpression newFunctionCallExpression(Consumer<ICPPASTFunctionCallExpression> also) {
      return createAlso(CPPASTFunctionCallExpression::new, also);
   }

   @Override
   public ICPPASTFunctionDeclarator newFunctionDeclarator(Consumer<ICPPASTFunctionDeclarator> also) {
      return createAlso(this::newFunctionDeclarator, also);
   }

   @Override
   public ICPPASTLambdaExpression newLambdaExpression(Consumer<ICPPASTLambdaExpression> also) {
      return createAlso(this::newLambdaExpression, also);
   }

   @Override
   public IASTSimpleDeclaration newSimpleDeclaration(Consumer<IASTSimpleDeclaration> also) {
      return createAlso(CPPASTSimpleDeclaration::new, also);
   }

   @Override
   public ICPPASTTemplateId newTemplateId(IASTName name, Consumer<ICPPASTTemplateId> also) {
      return createAlso(() -> newTemplateId(name), also);
   }

   private <T> T createAlso(Supplier<T> creator, Consumer<T> also) {
      final T t = creator.get();
      also.accept(t);
      return t;
   }

   /* Enum Operator Factory Methods */

   @Override
   public ICPPASTUnaryExpression newUnaryExpression(CPPUnaryOperator operator, IASTExpression operand) {
      return newUnaryExpression(operator.getCDTOperator(), operand);
   }

   @Override
   public ICPPASTBinaryExpression newBinaryExpression(CPPBinaryOperator op, IASTExpression expr1, IASTExpression expr2) {
      return newBinaryExpression(op.getCDTOperator(), expr1, expr2);
   }

   @Override
   public ICPPASTBinaryExpression newBinaryExpression(CPPBinaryOperator op, IASTExpression expr1, IASTInitializerClause expr2) {
      return newBinaryExpression(op.getCDTOperator(), expr1, expr2);
   }

   /* Magic Factory Methods */

   public IASTUnaryExpression newMagicPrecedenceUnaryExpression(CPPUnaryOperator op, IASTExpression operand) {
      if (op.operandNeedsWraping(operand)) {
         return newUnaryExpression(op, newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, operand));
      } else {
         return newUnaryExpression(op, operand);
      }
   }

   public IASTBinaryExpression newMagicPrecedenceBinaryExpression(CPPBinaryOperator binaryOperator, IASTExpression operand1,
         IASTInitializerClause operand2) {
      boolean leftOperandNeedsWraping = binaryOperator.leftOperandNeedsWraping(operand1);
      boolean rightOperandNeedsWraping = binaryOperator.rightOperandNeedsWraping(operand2);
      if (leftOperandNeedsWraping && rightOperandNeedsWraping) {
         return newBinaryExpression(binaryOperator, newBracketedExpression(operand1), newBracketedExpression((IASTExpression) operand2));
      } else if (leftOperandNeedsWraping) {
         return newBinaryExpression(binaryOperator, newBracketedExpression(operand1), operand2);
      } else if (rightOperandNeedsWraping) {
         return newBinaryExpression(binaryOperator, operand1, newBracketedExpression((IASTExpression) operand2));
      } else {
         return newBinaryExpression(binaryOperator, operand1, operand2);
      }
   }

   public IASTConditionalExpression newMagicPrefedenceConditionalExpression(IASTExpression condition, IASTExpression positive,
         IASTExpression negative) {
      boolean conditionNeedsWraping = CPPOtherOperators.CONDITIONAL.conditionalOperandNeedsWraping(condition);
      boolean negativeNeedsWraping = CPPOtherOperators.CONDITIONAL.elseOperandNeedsWraping(negative);
      if (conditionNeedsWraping && negativeNeedsWraping) {
         return newConditionalExpression(newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, condition), positive, newUnaryExpression(
               IASTUnaryExpression.op_bracketedPrimary, negative));
      } else if (conditionNeedsWraping) {
         return newConditionalExpression(newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, condition), positive, negative);
      } else if (negativeNeedsWraping) {
         return newConditionalExpression(condition, positive, newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, negative));
      } else {
         return newConditionalExpession(condition, positive, negative);
      }
   }

}

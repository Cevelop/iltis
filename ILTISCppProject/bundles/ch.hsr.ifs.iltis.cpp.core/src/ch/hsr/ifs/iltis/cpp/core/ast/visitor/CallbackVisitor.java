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

    private final Function<IASTNode, Integer> callback;

    public CallbackVisitor(final Function<IASTNode, Integer> callback) {
        super(true);
        this.callback = callback;
    }

    @Override
    public int visit(final IASTTranslationUnit tu) {
        return callback.apply(tu);
    }

    @Override
    public int visit(final IASTName name) {
        return callback.apply(name);
    }

    @Override
    public int visit(final IASTDeclaration declaration) {
        return callback.apply(declaration);
    }

    @Override
    public int visit(final IASTInitializer initializer) {
        return callback.apply(initializer);
    }

    @Override
    public int visit(final IASTParameterDeclaration parameterDeclaration) {
        return callback.apply(parameterDeclaration);
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        return callback.apply(declarator);
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        return callback.apply(declSpec);
    }

    @Override
    public int visit(final IASTArrayModifier arrayModifier) {
        return callback.apply(arrayModifier);
    }

    @Override
    public int visit(final IASTPointerOperator ptrOperator) {
        return callback.apply(ptrOperator);
    }

    @Override
    public int visit(final IASTAttribute attribute) {
        return callback.apply(attribute);
    }

    @Override
    public int visit(final IASTAttributeSpecifier specifier) {
        return callback.apply(specifier);
    }

    @Override
    public int visit(final IASTToken token) {
        return callback.apply(token);
    }

    @Override
    public int visit(final IASTExpression expression) {
        return callback.apply(expression);
    }

    @Override
    public int visit(final IASTStatement statement) {
        return callback.apply(statement);
    }

    @Override
    public int visit(final IASTTypeId typeId) {
        return callback.apply(typeId);
    }

    @Override
    public int visit(final IASTEnumerator enumerator) {
        return callback.apply(enumerator);
    }

    @Override
    public int visit(final IASTProblem problem) {
        return callback.apply(problem);
    }

    @Override
    public int visit(final ICPPASTBaseSpecifier baseSpecifier) {
        return callback.apply(baseSpecifier);
    }

    @Override
    public int visit(final ICPPASTNamespaceDefinition namespaceDefinition) {
        return callback.apply(namespaceDefinition);
    }

    @Override
    public int visit(final ICPPASTTemplateParameter templateParameter) {
        return callback.apply(templateParameter);
    }

    @Override
    public int visit(final ICPPASTCapture capture) {
        return callback.apply(capture);
    }

    @Override
    public int visit(final ICASTDesignator designator) {
        return callback.apply(designator);
    }

    @Override
    public int visit(final ICPPASTDesignator designator) {
        return callback.apply(designator);
    }

    @Override
    public int visit(final ICPPASTVirtSpecifier virtSpecifier) {
        return callback.apply(virtSpecifier);
    }

    @Override
    public int visit(final ICPPASTClassVirtSpecifier classVirtSpecifier) {
        return callback.apply(classVirtSpecifier);
    }

    @Override
    public int visit(final ICPPASTDecltypeSpecifier decltypeSpecifier) {
        return callback.apply(decltypeSpecifier);
    }
}

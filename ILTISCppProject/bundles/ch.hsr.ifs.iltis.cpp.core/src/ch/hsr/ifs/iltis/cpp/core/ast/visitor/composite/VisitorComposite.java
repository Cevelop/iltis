package ch.hsr.ifs.iltis.cpp.core.ast.visitor.composite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTAttributeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitDestructorName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
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

import ch.hsr.ifs.iltis.core.core.functional.functions.Function2;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter;
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor;
import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;


/**
 *
 * @author tstauber, P. Bertschi, A. Deicha
 *
 */
public class VisitorComposite<ProblemId extends Enum<ProblemId> & IProblemId, CheckerType extends AbstractIndexAstChecker & ISimpleReporter<ProblemId>>
      extends SimpleVisitor<ProblemId> {

   private final List<SimpleVisitor<?>>                          visitors;
   private final List<SimpleVisitor<?>>                          visitorsThatAborted;
   private final VisitorCache                                    cache;
   private final Function<IASTNode, ArrayList<SimpleVisitor<?>>> listFactory;

   private final IdentityHashMap<IASTNode, ArrayList<SimpleVisitor<?>>> visitorsToSkipForSubnodes = new IdentityHashMap<>();

   public VisitorComposite(final CheckerType checker, Collection<SimpleVisitor<?>> subVisitors) {
      super(checker);
      visitors = subVisitors.stream().filter(SimpleVisitor::isEnabled).collect(Collectors.toList());
      listFactory = ignored -> new ArrayList<SimpleVisitor<?>>(visitors.size());
      cache = new VisitorCache(visitors.size());
      visitorsThatAborted = listFactory.apply(null);
      visitors.forEach(this::addToCache);
   }

   public List<SimpleVisitor<?>> getVisitors() {
      return visitors;
   }

   public boolean hasVisitors() {
      return visitors.size() > 0;
   }

   private void addToCache(final SimpleVisitor<?> visitor) {
      if (visitor.shouldVisitAmbiguousNodes) {
         shouldVisitAmbiguousNodes = true;
         cache.addCache(IASTTranslationUnit.class, visitor);
      }
      if (visitor.shouldVisitArrayModifiers) {
         shouldVisitArrayModifiers = true;
         cache.addCache(IASTArrayModifier.class, visitor);
      }
      if (visitor.shouldVisitAttributes) {
         shouldVisitAttributes = true;
         cache.addCache(IASTAttribute.class, visitor);
      }
      if (visitor.shouldVisitBaseSpecifiers) {
         shouldVisitBaseSpecifiers = true;
         cache.addCache(ICPPASTBaseSpecifier.class, visitor);
      }
      if (visitor.shouldVisitCaptures) {
         shouldVisitCaptures = true;
         cache.addCache(ICPPASTCapture.class, visitor);
      }
      if (visitor.shouldVisitDeclarations) {
         shouldVisitDeclarations = true;
         cache.addCache(IASTDeclaration.class, visitor);
      }
      if (visitor.shouldVisitDeclarators) {
         shouldVisitDeclarators = true;
         cache.addCache(IASTDeclarator.class, visitor);
      }
      if (visitor.shouldVisitDeclSpecifiers) {
         shouldVisitDeclSpecifiers = true;
         cache.addCache(IASTDeclSpecifier.class, visitor);
      }
      if (visitor.shouldVisitDecltypeSpecifiers) {
         shouldVisitDecltypeSpecifiers = true;
         cache.addCache(ICPPASTDecltypeSpecifier.class, visitor);
      }
      if (visitor.shouldVisitDesignators) {
         shouldVisitDesignators = true;
         cache.addCache(ICPPASTDesignator.class, visitor);
      }
      if (visitor.shouldVisitEnumerators) {
         shouldVisitEnumerators = true;
         cache.addCache(IASTEnumerator.class, visitor);
      }
      if (visitor.shouldVisitExpressions) {
         shouldVisitExpressions = true;
         cache.addCache(IASTExpression.class, visitor);
      }
      if (visitor.shouldVisitImplicitDestructorNames) {
         shouldVisitImplicitDestructorNames = true;
         cache.addCache(IASTImplicitDestructorName.class, visitor);
      }
      if (visitor.shouldVisitImplicitNameAlternates) {
         shouldVisitImplicitNameAlternates = true;
         cache.addCache(IASTImplicitName.class, visitor);
      }
      if (visitor.shouldVisitImplicitNames) {
         shouldVisitImplicitNames = true;
         cache.addCache(IASTImplicitName.class, visitor);
      }
      if (visitor.shouldVisitInitializers) {
         shouldVisitInitializers = true;
         cache.addCache(IASTInitializer.class, visitor);
      }
      if (visitor.shouldVisitNames) {
         shouldVisitNames = true;
         cache.addCache(IASTName.class, visitor);
      }
      if (visitor.shouldVisitNamespaces) {
         shouldVisitNamespaces = true;
         cache.addCache(ICPPASTNamespaceDefinition.class, visitor);
      }
      if (visitor.shouldVisitParameterDeclarations) {
         shouldVisitParameterDeclarations = true;
         cache.addCache(IASTParameterDeclaration.class, visitor);
      }
      if (visitor.shouldVisitPointerOperators) {
         shouldVisitPointerOperators = true;
         cache.addCache(IASTPointerOperator.class, visitor);
      }
      if (visitor.shouldVisitProblems) {
         shouldVisitProblems = true;
         cache.addCache(IASTProblem.class, visitor);
      }
      if (visitor.shouldVisitStatements) {
         shouldVisitStatements = true;
         cache.addCache(IASTStatement.class, visitor);
      }
      if (visitor.shouldVisitTemplateParameters) {
         shouldVisitTemplateParameters = true;
         cache.addCache(ICPPASTTemplateParameter.class, visitor);
      }
      if (visitor.shouldVisitTokens) {
         shouldVisitTokens = true;
         cache.addCache(IASTToken.class, visitor);
      }
      if (visitor.shouldVisitTranslationUnit) {
         shouldVisitTranslationUnit = true;
         cache.addCache(IASTTranslationUnit.class, visitor);
      }
      if (visitor.shouldVisitTypeIds) {
         shouldVisitTypeIds = true;
         cache.addCache(IASTTypeId.class, visitor);
      }
      if (visitor.shouldVisitVirtSpecifiers) {
         shouldVisitVirtSpecifiers = true;
         cache.addCache(ICPPASTVirtSpecifier.class, visitor);
      }
   }

   private void processResult(final int result, final IASTNode node, final SimpleVisitor<?> visitor) {
      switch (result) {
      case PROCESS_ABORT:
         visitorsThatAborted.add(visitor);
         break;
      case PROCESS_SKIP:
         visitorsToSkipForSubnodes.computeIfAbsent(node, listFactory).add(visitor);
         visitor.enterCompositeSkipMode();
         break;
      }
   }

   /**
    * After returning from the subtree and leaving the node, all visitors which skipped the subtree are re-enabled.
    */
   private int processLeave(final IASTNode node) {
      final ArrayList<SimpleVisitor<?>> skipped = visitorsToSkipForSubnodes.remove(node);
      if (skipped != null) {
         visitors.addAll(skipped);
         visitors.forEach(SimpleVisitor::exitCompositeSkipMode);
      }

      return PROCESS_CONTINUE;
   }

   private int getCurrentResult() {
      visitors.removeAll(visitorsThatAborted);
      cache.removeAllForAnyType(visitorsThatAborted);
      visitorsThatAborted.clear();

      if (visitors.isEmpty()) {
         if (visitorsToSkipForSubnodes.isEmpty()) {
            return PROCESS_ABORT;
         } else {
            return PROCESS_SKIP;
         }
      }

      return PROCESS_CONTINUE;
   }

   @Override
   public boolean isEnabled() {
      return visitors.stream().anyMatch(SimpleVisitor::isEnabled);
   }

   @Override
   public Set<? extends IProblemId> getProblemIds() {
      return visitors.stream().flatMap(visitor -> visitor.getProblemIds().stream()).collect(Collectors.toSet());
   }

   public <Node extends IASTNode> int doVisitForNode(final Class<?> nodeType, final Node node, final Function2<ASTVisitor, Node, Integer> function) {
      //TODO Think about using one thread/job per composite to handle the nodes... this would mean to change the handling of processResult
      cache.getCache(nodeType).stream().filter(v -> v.isInCompositeSkipMode()).forEach(v -> processResult(function.apply(v, node), node, v));
      return getCurrentResult();
   }

   @Override
   public final int visit(final IASTTranslationUnit tu) {
      return doVisitForNode(IASTTranslationUnit.class, tu, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTName name) {
      return doVisitForNode(IASTName.class, name, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTDeclaration declaration) {
      return doVisitForNode(IASTDeclaration.class, declaration, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTInitializer initializer) {
      return doVisitForNode(IASTInitializer.class, initializer, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTParameterDeclaration parameterDeclaration) {
      return doVisitForNode(IASTParameterDeclaration.class, parameterDeclaration, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTDeclarator declarator) {
      return doVisitForNode(IASTDeclarator.class, declarator, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTDeclSpecifier declSpec) {
      return doVisitForNode(IASTDeclSpecifier.class, declSpec, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTArrayModifier arrayModifier) {
      return doVisitForNode(IASTArrayModifier.class, arrayModifier, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTPointerOperator ptrOperator) {
      return doVisitForNode(IASTPointerOperator.class, ptrOperator, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTAttribute attribute) {
      return doVisitForNode(IASTAttribute.class, attribute, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTAttributeSpecifier specifier) {
      return doVisitForNode(IASTAttributeSpecifier.class, specifier, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTToken token) {
      return doVisitForNode(IASTToken.class, token, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTExpression expression) {
      return doVisitForNode(IASTExpression.class, expression, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTStatement statement) {
      return doVisitForNode(IASTStatement.class, statement, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTTypeId typeId) {
      return doVisitForNode(IASTTypeId.class, typeId, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTEnumerator enumerator) {
      return doVisitForNode(IASTEnumerator.class, enumerator, ASTVisitor::visit);
   }

   @Override
   public final int visit(final IASTProblem problem) {
      return doVisitForNode(IASTProblem.class, problem, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICPPASTBaseSpecifier baseSpecifier) {
      return doVisitForNode(ICPPASTBaseSpecifier.class, baseSpecifier, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICPPASTNamespaceDefinition namespaceDefinition) {
      return doVisitForNode(ICPPASTNamespaceDefinition.class, namespaceDefinition, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICPPASTTemplateParameter templateParameter) {
      return doVisitForNode(ICPPASTTemplateParameter.class, templateParameter, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICPPASTCapture capture) {
      return doVisitForNode(ICPPASTCapture.class, capture, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICASTDesignator designator) {
      return doVisitForNode(ICASTDesignator.class, designator, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICPPASTDesignator designator) {
      return doVisitForNode(ICPPASTDesignator.class, designator, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICPPASTVirtSpecifier virtSpecifier) {
      return doVisitForNode(ICPPASTVirtSpecifier.class, virtSpecifier, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICPPASTClassVirtSpecifier classVirtSpecifier) {
      return doVisitForNode(ICPPASTClassVirtSpecifier.class, classVirtSpecifier, ASTVisitor::visit);
   }

   @Override
   public final int visit(final ICPPASTDecltypeSpecifier decltypeSpecifier) {
      return doVisitForNode(ICPPASTDecltypeSpecifier.class, decltypeSpecifier, ASTVisitor::visit);
   }

   @Override
   public final int leave(final IASTTranslationUnit tu) {
      return processLeave(tu);
   }

   @Override
   public final int leave(final IASTName name) {
      return processLeave(name);
   }

   @Override
   public final int leave(final IASTDeclaration declaration) {
      return processLeave(declaration);
   }

   @Override
   public final int leave(final IASTInitializer initializer) {
      return processLeave(initializer);
   }

   @Override
   public final int leave(final IASTParameterDeclaration parameterDeclaration) {
      return processLeave(parameterDeclaration);
   }

   @Override
   public final int leave(final IASTDeclarator declarator) {
      return processLeave(declarator);
   }

   @Override
   public final int leave(final IASTDeclSpecifier declSpec) {
      return processLeave(declSpec);
   }

   @Override
   public final int leave(final IASTArrayModifier arrayModifier) {
      return processLeave(arrayModifier);
   }

   @Override
   public final int leave(final IASTPointerOperator ptrOperator) {
      return processLeave(ptrOperator);
   }

   @Override
   public final int leave(final IASTAttribute attribute) {
      return processLeave(attribute);
   }

   @Override
   public final int leave(final IASTAttributeSpecifier specifier) {
      return processLeave(specifier);
   }

   @Override
   public final int leave(final IASTToken token) {
      return processLeave(token);
   }

   @Override
   public final int leave(final IASTExpression expression) {
      return processLeave(expression);
   }

   @Override
   public final int leave(final IASTStatement statement) {
      return processLeave(statement);
   }

   @Override
   public final int leave(final IASTTypeId typeId) {
      return processLeave(typeId);
   }

   @Override
   public final int leave(final IASTEnumerator enumerator) {
      return processLeave(enumerator);
   }

   @Override
   public final int leave(final IASTProblem problem) {
      return processLeave(problem);
   }

   @Override
   public final int leave(final ICPPASTBaseSpecifier baseSpecifier) {
      return processLeave(baseSpecifier);
   }

   @Override
   public final int leave(final ICPPASTNamespaceDefinition namespaceDefinition) {
      return processLeave(namespaceDefinition);
   }

   @Override
   public final int leave(final ICPPASTTemplateParameter templateParameter) {
      return processLeave(templateParameter);
   }

   @Override
   public final int leave(final ICPPASTCapture capture) {
      return processLeave(capture);
   }

   @Override
   public final int leave(final ICASTDesignator designator) {
      return processLeave(designator);
   }

   @Override
   public final int leave(final ICPPASTDesignator designator) {
      return processLeave(designator);
   }

   @Override
   public final int leave(final ICPPASTVirtSpecifier virtSpecifier) {
      return processLeave(virtSpecifier);
   }

   @Override
   public final int leave(final ICPPASTClassVirtSpecifier virtSpecifier) {
      return processLeave(virtSpecifier);
   }

   @Override
   public final int leave(final ICPPASTDecltypeSpecifier decltypeSpecifier) {
      return processLeave(decltypeSpecifier);
   }
}

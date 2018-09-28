package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.example.examplerefactoringtest.refactorings;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.TextEditGroup;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class DummyRenameRefactoring extends CRefactoring {

   public DummyRenameRefactoring(ICElement element, Optional<ITextSelection> selection, ICProject project) {
      super(element, selection, project);
   }

   @Override
   protected RefactoringDescriptor getRefactoringDescriptor() {
      return null;
   }

   @Override
   public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
      RefactoringStatus result = super.checkInitialConditions(pm);
      return result;
   }

   @Override
   protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
      IASTTranslationUnit ast = refactoringContext.getAST(tu, pm);

      IASTSimpleDeclaration funcDeclaration = (IASTSimpleDeclaration) ast.getDeclarations()[0];
      IASTFunctionDeclarator declarator = (IASTFunctionDeclarator) funcDeclaration.getDeclarators()[0];
      IASTFunctionDeclarator newDeclarator = declarator.copy();

      IASTName newName = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newName("b".toCharArray());
      newDeclarator.setName(newName);

      ASTRewrite rewrite = collector.rewriterForTranslationUnit(ast);
      rewrite.replace(declarator, newDeclarator, new TextEditGroup(""));
   }

   @Override
   protected RefactoringStatus checkFinalConditions(IProgressMonitor subProgressMonitor, CheckConditionsContext checkContext) throws CoreException,
         OperationCanceledException {
      RefactoringStatus result = super.checkFinalConditions(subProgressMonitor, checkContext);
      return result;
   }
}

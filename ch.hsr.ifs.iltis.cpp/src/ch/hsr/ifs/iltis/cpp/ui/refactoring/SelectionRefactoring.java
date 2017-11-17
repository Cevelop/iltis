package ch.hsr.ifs.iltis.cpp.ui.refactoring;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.jface.viewers.ISelection;

import ch.hsr.ifs.iltis.cpp.wrappers.CRefactoring;


/**
 * @author tstauber
 */
public abstract class SelectionRefactoring extends CRefactoring {

   protected final IRefactoringInfo info;

   public SelectionRefactoring(final ICElement element, final ISelection selection, final ICProject project, final IRefactoringInfo info) {
      super(element, selection, project);
      this.info = info;
   }

   public abstract IRefactoringInfo getRefactoringInfo();

   protected boolean isInSelection(final IASTNode node) {
      if (getSelectedRegion().getLength() > 0 && getRefactoringInfo().useSelection()) {
         return isInSelectionHook(node);
      } else {
         return true;
      }
   }

   // TODO write docu
   protected boolean isInSelectionHook(final IASTNode node) {
      return (getNodeStart(node) >= getSelectionStart() && getNodeEnd(node) <= getSelectionEnd());
   }

   protected int getSelectionEnd() {
      return getSelectionStart() + getSelectedRegion().getLength();
   }

   protected int getSelectionStart() {
      return getSelectedRegion().getOffset();
   }

   protected int getNodeEnd(final IASTNode node) {
      return getNodeStart(node) + node.getFileLocation().getNodeLength();
   }

   protected int getNodeStart(final IASTNode node) {
      return node.getFileLocation().getNodeOffset();
   }
}

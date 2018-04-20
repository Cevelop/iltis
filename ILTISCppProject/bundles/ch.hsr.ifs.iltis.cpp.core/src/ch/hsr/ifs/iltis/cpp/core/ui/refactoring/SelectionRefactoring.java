package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


/**
 * An abstract refactoring which can be configured to only operate on a selection.
 * 
 * @author tstauber
 */
public abstract class SelectionRefactoring extends CRefactoring {

   protected final IRefactoringInfo info;

   public SelectionRefactoring(final ICElement element, final Optional<ITextSelection> selection, final ICProject project,
                               final IRefactoringInfo info) {
      super(element, selection, project);
      this.info = info;
   }

   /**
    * @return The info for this refactoring
    */
   public abstract IRefactoringInfo getRefactoringInfo();

   /**
    * Checks if this refactoring is configured to use selection and if this is the case, if the node is inside the selection.
    * 
    * @param node
    *        The node to test
    * @return true if no selection is used, or if the node is in the selection.
    */
   protected boolean isInSelection(final IASTNode node) {
      if (selectedRegion.getLength() > 0 && getRefactoringInfo().useSelection()) {
         return isInSelectionHook(node);
      } else {
         return true;
      }
   }

   /**
    * Hook method which can be overridden for different selection check.
    * 
    * @param node
    *        The node to check
    * @return {@code true} if the node lies inside the selection
    */
   protected boolean isInSelectionHook(final IASTNode node) {
      return getNodeStart(node) >= getSelectionStart() && getNodeEnd(node) <= getSelectionEnd();
   }

   /**
    * @return The end offset of the selection
    */
   protected int getSelectionEnd() {
      return getSelectionStart() + selectedRegion.getLength();
   }

   /**
    * @return The start offset of the selection
    */
   protected int getSelectionStart() {
      return selectedRegion.getOffset();
   }

   /**
    * @return The end offset of the node
    */
   protected int getNodeEnd(final IASTNode node) {
      return getNodeStart(node) + node.getFileLocation().getNodeLength();
   }

   /**
    * @return The start offset of the node
    */
   protected int getNodeStart(final IASTNode node) {
      return node.getFileLocation().getNodeOffset();
   }
}

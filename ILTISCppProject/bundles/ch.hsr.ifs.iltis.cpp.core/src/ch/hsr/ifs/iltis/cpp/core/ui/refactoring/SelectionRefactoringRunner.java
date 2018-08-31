package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.window.IShellProvider;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.wrappers.RefactoringRunner;


/**
 * TODO
 */
public abstract class SelectionRefactoringRunner<T extends MarkerInfo<T>> extends RefactoringRunner {

   protected final T info;

   protected SelectionRefactoringRunner(ICElement element, Optional<ITextSelection> selection, IShellProvider shellProvider) {
      super(element, selection, shellProvider, element.getCProject());
      this.info = getRefactoringInfoConstructor().get();
      this.info.fileName = element.getCProject().getLocationURI().toString();
      this.info.setSelection(selection);
   }

   protected abstract Supplier<T> getRefactoringInfoConstructor();
}

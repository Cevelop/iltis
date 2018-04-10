package ch.hsr.ifs.iltis.cpp.wrappers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.refactoring.utils.EclipseObjects;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;

import ch.hsr.ifs.iltis.core.functional.OptionalUtil;


@SuppressWarnings("restriction")
public abstract class RefactoringAction extends Action {

   protected CEditor      fEditor;
   private IWorkbenchSite fSite;
   private ICElement      fElement;
   private boolean        saveRequired;

   public RefactoringAction(String label) {
      super(label);
   }

   /**
    * Sets behavior with respect to saving dirty editors.
    * 
    * @param saveRequired
    *        if <code>true</code>, dirty editors will be saved before refactoring.
    *
    * @deprecated saving of editors should be controlled by refactoring runner, not by the action.
    * @since 5.3
    */
   @Deprecated
   public void setSaveRequired(boolean saveRequired) {
      this.saveRequired = saveRequired;
   }

   public void setEditor(IEditorPart editor) {
      fEditor = null;
      fSite = null;
      if (editor instanceof CEditor) {
         fEditor = (CEditor) editor;
      }
      setEnabled(fEditor != null);
   }

   public void setSite(IWorkbenchSite site) {
      fEditor = null;
      fSite = site;
   }

   @Override
   public final void run() {
      if (saveRequired) {
         EclipseObjects.getActivePage().saveAllEditors(true);
         if (EclipseObjects.getActivePage().getDirtyEditors().length != 0) { return; }
      }
      if (fEditor != null) {
         Optional.ofNullable(CUIPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(fEditor.getEditorInput())).ifPresent(wc -> run(fEditor
               .getSite(), wc, OptionalUtil.of(fEditor.getSelectionProvider()).map(ISelectionProvider::getSelection).mapAs(ITextSelection.class)
                     .get()));
      } else if (fSite != null && fElement != null) {
         run(fSite, fElement);
      }
   }

   public void updateSelection(ICElement elem) {
      fElement = elem;
      setEnabled(elem != null);
   }

   public abstract void run(IShellProvider shellProvider, IWorkingCopy wc, Optional<ITextSelection> s);

   public abstract void run(IShellProvider shellProvider, ICElement elem);
}

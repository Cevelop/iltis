package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;


@SuppressWarnings("restriction")
public class CCompositeChange extends org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange {

   public CCompositeChange(final String name, final Change[] children) {
      super(name, children);
   }

   public CCompositeChange(final String name) {
      super(name);
   }

   @Override
   public void setDescription(final RefactoringChangeDescriptor descriptor) {
      super.setDescription(descriptor);
   }

   @Override
   public ChangeDescriptor getDescriptor() {
      return super.getDescriptor();
   }

}

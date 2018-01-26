package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
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

   public static CCompositeChange wrap(CompositeChange cng) {
      if (cng instanceof CCompositeChange) {
         return (CCompositeChange) cng;
      } else if (cng instanceof org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange) {
         return new CCompositeChange(cng.getName(), cng.getChildren());
      } else {
         return null;
      }
   }

   public static boolean isUnwrappedType(CompositeChange cng) {
      return cng instanceof org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange;
   }

   public static Class<org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange> getUnwrappedType() {
      return org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange.class;
   }

}

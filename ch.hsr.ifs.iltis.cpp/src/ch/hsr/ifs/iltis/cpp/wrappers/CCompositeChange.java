package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;


/**
 * A wrapper class for the cdt CCompositeChange. Using this wrapper reduces the amount of warnings respectively the amount of
 * {@code @SuppressWarnings} tags
 * 
 * @author tstauber
 *
 */
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

   /**
    * Used to convert a CompositeChange to a CCompositeChange. This works even if the CompositeChange is an instance of the cdt CCompositeChange.
    * 
    * @return The converted CCompositeChange
    */
   public static CCompositeChange wrap(CompositeChange cng) {
      if (cng instanceof CCompositeChange) {
         return (CCompositeChange) cng;
      } else if (cng instanceof org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange) {
         return new CCompositeChange(cng.getName(), cng.getChildren());
      } else {
         return null;
      }
   }

   /**
    * @return {@code true} if cng is instance of cdt CCompositeChange
    */
   public static boolean isUnwrappedType(CompositeChange cng) {
      return cng instanceof org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange;
   }

   /**
    * @return The class of the cdt CCompositeChange
    */
   public static Class<org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange> getUnwrappedType() {
      return org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange.class;
   }

}

package ch.hsr.ifs.iltis.cpp.core.wrappers;

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
public class CCompositeChange extends CompositeChange {

   private RefactoringChangeDescriptor desc;

   public CCompositeChange(String name, Change[] children) {
      super(name, children);
   }

   public CCompositeChange(String name) {
      super(name);
   }

   public void setDescription(RefactoringChangeDescriptor descriptor) {
      desc = descriptor;
   }

   @Override
   public ChangeDescriptor getDescriptor() {
      if (desc != null) { return desc; }
      return super.getDescriptor();
   }

}

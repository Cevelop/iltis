package ch.hsr.ifs.iltis.cpp.wrappers;

import java.util.Map;

import org.eclipse.cdt.internal.ui.refactoring.CRefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

/**
 * A wrapper class for the cdt CRefactoringContribution. Using this wrapper reduces the amount of warnings respectively the amount of {@code @SuppressWarnings} tags
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public abstract class CRefactoringContribution extends RefactoringContribution {

   public CRefactoringContribution() {
      super();
   }

   @Override
   public Map<String, String> retrieveArgumentMap(RefactoringDescriptor descriptor) {
      if (descriptor instanceof CRefactoringDescriptor) {
         CRefactoringDescriptor refDesc = (CRefactoringDescriptor) descriptor;
         return refDesc.getParameterMap();
      }
      return super.retrieveArgumentMap(descriptor);
   }
}

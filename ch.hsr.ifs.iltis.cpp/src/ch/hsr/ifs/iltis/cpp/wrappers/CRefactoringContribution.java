package ch.hsr.ifs.iltis.cpp.wrappers;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

/**
 * A wrapper class for the cdt CRefactoringContribution. Using this wrapper reduces the amount of warnings respectively the amount of {@code @SuppressWarnings} tags
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public abstract class CRefactoringContribution extends org.eclipse.cdt.internal.ui.refactoring.CRefactoringContribution {

   @Override
   public Map<String, String> retrieveArgumentMap(final RefactoringDescriptor descriptor) {
      if (descriptor instanceof CRefactoringDescriptor) {
         final CRefactoringDescriptor refDesc = (CRefactoringDescriptor) descriptor;
         return refDesc.getParameterMap();
      }
      return super.retrieveArgumentMap(descriptor);
   }

}

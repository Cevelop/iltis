package ch.hsr.ifs.iltis.cpp.wrappers;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;


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

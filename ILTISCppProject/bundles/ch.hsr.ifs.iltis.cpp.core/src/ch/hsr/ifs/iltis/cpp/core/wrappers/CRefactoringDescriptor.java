package ch.hsr.ifs.iltis.cpp.core.wrappers;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.resources.ResourceLookup;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.ids.IRefactoringId;
import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;


/**
 * A wrapper class for the cdt CRefactoringDescriptior. Using this wrapper reduces the amount of warnings respectively the amount of
 * {@code @SuppressWarnings} tags
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public abstract class CRefactoringDescriptor<RefactoringIdType extends IRefactoringId<RefactoringIdType>, MarkerInfoType extends MarkerInfo<MarkerInfoType>>
      extends RefactoringDescriptor {

   protected MarkerInfoType info;

   public CRefactoringDescriptor(RefactoringIdType id, String project, String description, String comment, int flags, MarkerInfoType info) {
      super(id.getId(), project, description, comment, flags);
      this.info = info;
   }

   @Override
   public abstract CRefactoring createRefactoring(RefactoringStatus status) throws CoreException;

   @Override
   public CRefactoringContext createRefactoringContext(RefactoringStatus status) throws CoreException {
      CRefactoring refactoring = createRefactoring(status);
      if (refactoring == null) return null;
      return new CRefactoringContext(refactoring);
   }

   protected ICProject getCProject() throws CoreException {
      String projectName = getProject();
      IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      ICProject cProject = CoreModel.getDefault().create(project);
      if (cProject == null) { throw new CoreException(new Status(IStatus.ERROR, CUIPlugin.PLUGIN_ID, String.format(
            "Project \"%s\" does not exist or is not a C/C++ project.", projectName))); }
      return cProject;
   }

   protected IFile getFile() throws CoreException {
      try {
         return ResourceLookup.selectFileForLocationURI(new URI(info.fileName), ResourcesPlugin.getWorkspace().getRoot().getProject(getProject()));
      } catch (URISyntaxException e) {
         throw new CoreException(new Status(IStatus.ERROR, CUIPlugin.PLUGIN_ID, e.getMessage(), e));
      }
   }

   protected ITranslationUnit getTranslationUnit() throws CoreException {
      try {
         return CoreModelUtil.findTranslationUnitForLocation(new URI(info.fileName), getCProject());
      } catch (URISyntaxException e) {
         throw new CoreException(new Status(IStatus.ERROR, CUIPlugin.PLUGIN_ID, e.getMessage(), e));
      }
   }
}

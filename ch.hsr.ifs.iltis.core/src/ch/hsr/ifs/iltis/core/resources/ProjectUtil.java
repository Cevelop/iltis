package ch.hsr.ifs.iltis.core.resources;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;


/**
 * Helps converting resources or extracting resources from other elements.
 *
 * @author tstauber
 */
public abstract class ProjectUtil {

   //DOC missing
   public static IProject getProject(final IFile file) {
      return file.getProject();
   }

   //DOC missing
   public static boolean isPartOfProject(final URI fileUri, final IProject project) {
      return project.getLocation().isPrefixOf(new Path(fileUri.getPath()));
   }

   //DOC missing
   public static IWorkspaceRoot getWorkspaceRoot() {
      return ResourcesPlugin.getWorkspace().getRoot();
   }

}

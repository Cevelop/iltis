package ch.hsr.ifs.iltis.core.resources;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;


/**
 * Helps converting resources or extracting resources from other elements.
 * 
 * @see ch.hsr.ifs.iltis.cpp.resources.CProjectUtil
 *
 * @author tstauber
 */
public abstract class ProjectUtil {

   /**
    * Checks if a file URI is part of a project
    * 
    * @param fileUri
    *        The file's URI
    * @param project
    *        The project
    * @return {@code true} when the file is part of the passed project
    */
   public static boolean isPartOfProject(final URI fileUri, final IProject project) {
      return project.getLocation().isPrefixOf(new Path(fileUri.getPath()));
   }

}

package ch.hsr.ifs.iltis.core.resources;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.api.tools.annotations.NoExtend;
import org.eclipse.pde.api.tools.annotations.NoInstantiate;


/**
 * Helps converting resources or extracting resources from other elements.
 *
 * <p>
 * <b>See Also</b><br>
 * &emsp;&emsp;&emsp;ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil
 * </p>
 *
 * @author tstauber
 */
@NoExtend
@NoInstantiate
public class ProjectUtil {

    /**
     * Checks if a file URI is part of a project
     *
     * @param fileUri
     * The file's URI
     * @param project
     * The project
     * @return {@code true} when the file is part of the passed project
     */
    public static boolean isPartOfProject(final URI fileUri, final IProject project) {
        return project.getLocation().isPrefixOf(new Path(fileUri.getPath()));
    }

}

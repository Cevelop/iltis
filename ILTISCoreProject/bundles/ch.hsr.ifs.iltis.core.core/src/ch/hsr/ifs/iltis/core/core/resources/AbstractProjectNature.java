package ch.hsr.ifs.iltis.core.core.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import ch.hsr.ifs.iltis.core.core.arrays.ArrayUtil;


/**
 * An abstract project nature. Provides static methods for adding and removing natures.
 * 
 * @since 2.1
 */
public abstract class AbstractProjectNature implements IProjectNature {

    protected IProject fProject;

    @Override
    public IProject getProject() {
        return fProject;
    }

    @Override
    public void setProject(IProject project) {
        this.fProject = project;
    }

    /**
     * Adds a project nature to a project if not yet present
     * 
     * @param project
     * The project to add the nature to
     * @param natureId
     * The id of the nature to add
     * @param monitor
     * A progress monitor
     * @return {@code true} iff the nature had been added.
     */
    public static boolean addNature(IProject project, String natureId, IProgressMonitor monitor) {
        IProjectDescription description;
        try {
            description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            if (ArrayUtil.contains(prevNatures, natureId) == -1) {
                description.setNatureIds(ArrayUtil.prepend(prevNatures, natureId));
                project.setDescription(description, monitor);
            }
            return true;
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a nature from a project if present
     * 
     * @param project
     * The project to remove the nature from
     * @param natureId
     * The id of the nature to remove
     * @param monitor
     * A progress monitor
     * @return {@code true} iff the nature had been removed.
     */
    public static boolean removeNature(IProject project, String natureId, IProgressMonitor monitor) {
        IProjectDescription description;
        try {
            description = project.getDescription();
            description.setNatureIds(ArrayUtil.removeAndTrim(description.getNatureIds(), natureId));
            project.setDescription(description, monitor);
            return true;
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
    }

}

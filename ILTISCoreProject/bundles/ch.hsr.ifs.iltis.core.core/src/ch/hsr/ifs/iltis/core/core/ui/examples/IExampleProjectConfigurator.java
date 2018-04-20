package ch.hsr.ifs.iltis.core.core.ui.examples;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IExampleProjectConfigurator {

   public void configureProject(IProject project, IProgressMonitor monitor);

}

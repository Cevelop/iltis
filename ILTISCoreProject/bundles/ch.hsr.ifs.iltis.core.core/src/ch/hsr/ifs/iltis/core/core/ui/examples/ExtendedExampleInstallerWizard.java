package ch.hsr.ifs.iltis.core.core.ui.examples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.ui.CommonUIPlugin;
import org.eclipse.emf.common.ui.wizard.AbstractExampleInstallerWizard;
import org.eclipse.emf.common.ui.wizard.ExampleInstallerWizard;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.core.core.ILTIS;


public class ExtendedExampleInstallerWizard extends ExampleInstallerWizard {

   @Override
   protected void loadFromExtensionPoints() {
      projectDescriptors = new ArrayList<ProjectDescriptor>();
      filesToOpen = new ArrayList<FileToOpen>();

      String wizardID = wizardConfigurationElement.getAttribute("id");

      IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(FrameworkUtil.getBundle(ILTIS.class).getSymbolicName(),
            "extendedExampleInstallerWizard");
      for (IConfigurationElement exampleElement : extensionPoint.getConfigurationElements()) {
         if ("example".equals(exampleElement.getName()) && wizardID.equals(exampleElement.getAttribute("wizardID"))) {
            for (IConfigurationElement projectDescriptorElement : exampleElement.getChildren("projectDescriptor")) {
               String projectName = projectDescriptorElement.getAttribute("name");
               if (projectName != null) {
                  String contentURI = projectDescriptorElement.getAttribute("contentURI");
                  if (contentURI != null) {
                     ExtendedProjectDescriptor projectDescriptor = new ExtendedProjectDescriptor();
                     projectDescriptor.setName(projectName);

                     URI uri = URI.createURI(contentURI);
                     if (uri.isRelative()) {
                        uri = URI.createPlatformPluginURI(projectDescriptorElement.getContributor().getName() + "/" + contentURI, true);
                     }
                     projectDescriptor.setContentURI(uri);

                     projectDescriptor.setDescription(projectDescriptorElement.getAttribute("description"));

                     String configurator = projectDescriptorElement.getAttribute("projectConfigurator");
                     if (configurator != null) projectDescriptor.setProjectConfigurator(getConfiguratorInstance(projectDescriptorElement));
                     projectDescriptors.add(projectDescriptor);
                  }

               }
            }
         }

         if (!projectDescriptors.isEmpty()) {
            for (IConfigurationElement openElement : exampleElement.getChildren("fileToOpen")) {
               String location = openElement.getAttribute("location");
               if (location != null) {
                  AbstractExampleInstallerWizard.FileToOpen fileToOpen = new AbstractExampleInstallerWizard.FileToOpen();
                  fileToOpen.setLocation(location);
                  fileToOpen.setEditorID(openElement.getAttribute("editorID"));
                  filesToOpen.add(fileToOpen);
               }
            }

            String imagePath = exampleElement.getAttribute("pageImage");
            if (imagePath != null) {
               imagePath = imagePath.replace('\\', '/');
               if (!imagePath.startsWith("/")) {
                  imagePath = "/" + imagePath;
               }

               Bundle pluginBundle = Platform.getBundle(exampleElement.getDeclaringExtension().getContributor().getName());
               try {
                  ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(pluginBundle.getEntry(imagePath));
                  setDefaultPageImageDescriptor(imageDescriptor);
               } catch (Exception e) {
                  // Ignore
               }
            }

            //Only one example per wizard
            break;
         }
      }
   }

   protected IExampleProjectConfigurator getConfiguratorInstance(IConfigurationElement exampleElement) {
      try {
         return (IExampleProjectConfigurator) exampleElement.createExecutableExtension("projectConfigurator");
      } catch (CoreException e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   protected void installExample(IProgressMonitor progressMonitor) throws Exception {
      List<ProjectDescriptor> projectDescriptors = getProjectDescriptors();
      progressMonitor.beginTask(CommonUIPlugin.INSTANCE.getString("_UI_CreatingProjects_message"), 2 * projectDescriptors.size());
      for (ProjectDescriptor projectDescriptor : projectDescriptors) {
         // TODO(tstauber - Apr 17, 2018) REMOVE AFTER TESTING 
         //       ImportOperation importOperation = createImportOperation(projectDescriptor);
         //       if (importOperation != null)
         //       {
         //         installProject(projectDescriptor, importOperation, progressMonitor);
         //       }
         //       else
         //       {
         installProject(projectDescriptor, progressMonitor);
         if (projectDescriptor instanceof ExtendedProjectDescriptor) {
            ExtendedProjectDescriptor extendedPD = ((ExtendedProjectDescriptor) projectDescriptor);
            if (extendedPD.getProjectConfigurator() != null) {
               extendedPD.getProjectConfigurator().configureProject(projectDescriptor.getProject(), BasicMonitor.subProgress(progressMonitor, 2));
            }
         }
         //       }
      }
      progressMonitor.done();
   }

   public class ExtendedProjectDescriptor extends ProjectDescriptor {

      protected IExampleProjectConfigurator projectConfigurator;

      public void setProjectConfigurator(IExampleProjectConfigurator projectConfigurator) {
         this.projectConfigurator = projectConfigurator;
      }

      public IExampleProjectConfigurator getProjectConfigurator() {
         return projectConfigurator;
      }
   }

}

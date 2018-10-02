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
        projectDescriptors = new ArrayList<>();
        filesToOpen = new ArrayList<>();

        final String wizardID = wizardConfigurationElement.getAttribute("id");

        final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(FrameworkUtil.getBundle(ILTIS.class)
                .getSymbolicName(), "extendedExampleInstallerWizard");
        for (final IConfigurationElement exampleElement : extensionPoint.getConfigurationElements()) {
            if ("example".equals(exampleElement.getName()) && wizardID.equals(exampleElement.getAttribute("wizardID"))) {
                for (final IConfigurationElement projectDescriptorElement : exampleElement.getChildren("projectDescriptor")) {
                    final String projectName = projectDescriptorElement.getAttribute("name");
                    if (projectName != null) {
                        final String contentURI = projectDescriptorElement.getAttribute("contentURI");
                        if (contentURI != null) {
                            final ExtendedProjectDescriptor projectDescriptor = new ExtendedProjectDescriptor();
                            projectDescriptor.setName(projectName);

                            URI uri = URI.createURI(contentURI);
                            if (uri.isRelative()) {
                                uri = URI.createPlatformPluginURI(projectDescriptorElement.getContributor().getName() + "/" + contentURI, true);
                            }
                            projectDescriptor.setContentURI(uri);

                            projectDescriptor.setDescription(projectDescriptorElement.getAttribute("description"));

                            final String configurator = projectDescriptorElement.getAttribute("projectConfigurator");
                            if (configurator != null) projectDescriptor.setProjectConfigurator(getConfiguratorInstance(projectDescriptorElement));
                            projectDescriptors.add(projectDescriptor);
                        }

                    }
                }
            }

            if (!projectDescriptors.isEmpty()) {
                for (final IConfigurationElement openElement : exampleElement.getChildren("fileToOpen")) {
                    final String location = openElement.getAttribute("location");
                    if (location != null) {
                        final AbstractExampleInstallerWizard.FileToOpen fileToOpen = new AbstractExampleInstallerWizard.FileToOpen();
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

                    final Bundle pluginBundle = Platform.getBundle(exampleElement.getDeclaringExtension().getContributor().getName());
                    try {
                        final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(pluginBundle.getEntry(imagePath));
                        setDefaultPageImageDescriptor(imageDescriptor);
                    } catch (final Exception e) {
                        // Ignore
                    }
                }

                //Only one example per wizard
                break;
            }
        }
    }

    protected IExampleProjectConfigurator getConfiguratorInstance(final IConfigurationElement exampleElement) {
        try {
            return (IExampleProjectConfigurator) exampleElement.createExecutableExtension("projectConfigurator");
        } catch (final CoreException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void installExample(final IProgressMonitor progressMonitor) throws Exception {
        final List<ProjectDescriptor> projectDescriptors = getProjectDescriptors();
        progressMonitor.beginTask(CommonUIPlugin.INSTANCE.getString("_UI_CreatingProjects_message"), 2 * projectDescriptors.size());
        for (final ProjectDescriptor projectDescriptor : projectDescriptors) {
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
                final ExtendedProjectDescriptor extendedPD = (ExtendedProjectDescriptor) projectDescriptor;
                if (extendedPD.getProjectConfigurator() != null) {
                    extendedPD.getProjectConfigurator().configureProject(projectDescriptor.getProject(), BasicMonitor.subProgress(progressMonitor,
                            2));
                }
            }
            //       }
        }
        progressMonitor.done();
    }

    public class ExtendedProjectDescriptor extends ProjectDescriptor {

        protected IExampleProjectConfigurator projectConfigurator;

        public void setProjectConfigurator(final IExampleProjectConfigurator projectConfigurator) {
            this.projectConfigurator = projectConfigurator;
        }

        public IExampleProjectConfigurator getProjectConfigurator() {
            return projectConfigurator;
        }
    }

}

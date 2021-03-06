package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.projectholder;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IPDOMManager;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvidersKeeper;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.testplugin.CProjectHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;

import ch.hsr.ifs.iltis.core.exception.ILTISException;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingLanguageSettingsProvider;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.helpers.FileCache;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.testsourcefile.RTSTest.Language;


public abstract class AbstractProjectHolder implements IProjectHolder {

    protected ICProject cProject;
    protected IProject  project;
    protected String    projectName;

    protected FileCache fileCache = new FileCache();

    protected ArrayList<URL> stagedFilesToImport = new ArrayList<>();
    protected Language       language;
    protected IWorkspace     workspace;
    //TODO(Tobias Stauber) cleanup
    //   protected FileManager     fileManager;

    public AbstractProjectHolder() {
        super();
    }

    @Override
    public void stageFilesForImport(Enumeration<URL> resources) {
        while (resources.hasMoreElements()) {
            stagedFilesToImport.add(resources.nextElement());
        }
    }

    @Override
    public void stageFilesForImport(Collection<URI> files) {
        for (URI uri : files) {
            try {
                stagedFilesToImport.add(uri.toURL());
            } catch (MalformedURLException e) {
                ILTISException.wrap(e).rethrowUnchecked();
            }
        }
    }

    @Override
    public IPath makeProjectAbsolutePath(final String relativePath) {
        return getProject().getLocation().append(relativePath);
    }

    @Override
    public IPath makeProjectAbsolutePath(final IPath relativePath) {
        return getProject().getLocation().append(relativePath);
    }

    @Override
    public URI makeProjectAbsoluteURI(final String relativePath) {
        return URI.create(makeProjectAbsolutePath(relativePath).toOSString());
    }

    @Override
    public URI makeProjectAbsoluteURI(final IPath relativePath) {
        return URI.create(makeProjectAbsolutePath(relativePath).toOSString());
    }

    @Override
    public IFile getFile(String filePath) {
        return getProject().getFile(filePath);
    }

    @Override
    public IFile getFile(IPath filePath) {
        return getProject().getFile(filePath);
    }

    @Override
    public IDocument getDocument(URI sourceFile) {
        return fileCache.getDocument(sourceFile);
    }

    @Override
    public IDocument getDocument(IFile sourceFile) {
        return fileCache.getDocument(sourceFile);
    }

    @Override
    public IDocument getDocumentFromRelativePath(String relativePath) {
        return getDocument(getFile(relativePath));
    }

    @Override
    public IDocument getDocumentFromRelativePath(IPath projectRelativePath) {
        return getDocument(getFile(projectRelativePath));
    }

    @Override
    public IProject getProject() {
        return project;
    }

    @Override
    public ProjectHolderJob createProjectAsync() {
        return ProjectHolderJob.create("Initializing project " + projectName, IProjectHolder.CREATE_PROJ_JOB_FAMILY, mon -> {
            createProject();
        });
    }

    @Override
    public ProjectHolderJob cleanupProjectsAsync() {
        return ProjectHolderJob.create("Cleaningup project " + projectName, IProjectHolder.CLEANUP_PROJ_JOB_FAMILY, mon -> {
            cleanupProjects();
        });
    }

    @Override
    public ProjectHolderJob importFilesAsync() {
        return ProjectHolderJob.create("Importing files into project " + projectName, IProjectHolder.IMPORT_FILES_JOB_FAMILY, mon -> {
            importFiles();
        });
    }

    @Override
    public void createProject() {
        if (CCorePlugin.getDefault() != null && CCorePlugin.getDefault().getCoreModel() != null) {
            workspace = ResourcesPlugin.getWorkspace();
            try {
                cProject = createNewProject(projectName, language);
                project = cProject.getProject();
            } catch (final CoreException ignored) {
                fail("Failed to create the project");
            }
            //TODO(Tobias Stauber) cleanup
            //         fileManager = new FileManager();
        }
    }

    protected ICProject createNewProject(String projectName, Language language) throws CoreException {
        switch (language) {
        case C:
            return createCProject(projectName);
        case CPP:
            return createCCProject(projectName);
        default:
            fail("Invalid language for this holder. Valid choices are: Language.C, Language.CPP ");
            return null;
        }
    }

    protected ICProject createCProject(String projectName) throws CoreException {
        ICProject newStyleCProject = CProjectHelper.createNewStyleCProject(projectName, IPDOMManager.ID_NO_INDEXER);

        ICProjectDescription description = CCorePlugin.getDefault().getProjectDescription(newStyleCProject.getProject(), true);
        ICConfigurationDescription[] configurations = description.getConfigurations();

        for (ICConfigurationDescription configuration : configurations) {
            if (configuration instanceof ILanguageSettingsProvidersKeeper) {
                ILanguageSettingsProvidersKeeper keeper = (ILanguageSettingsProvidersKeeper) configuration;
                Set<ILanguageSettingsProvider> providers = new HashSet<>(keeper.getLanguageSettingProviders());
                providers.add(new CDTTestingLanguageSettingsProvider());
                keeper.setLanguageSettingProviders(providers.stream().collect(Collectors.toList()));
            }
        }

        CCorePlugin.getDefault().setProjectDescription(newStyleCProject.getProject(), description);

        return newStyleCProject;
    }

    protected ICProject createCCProject(String projectName) throws CoreException {
        ICProject proj = createCProject(projectName);
        if (!proj.getProject().hasNature(CCProjectNature.CC_NATURE_ID)) {
            CProjectHelper.addNatureToProject(proj.getProject(), CCProjectNature.CC_NATURE_ID, null);
        }
        return proj;
    }

}

package ch.hsr.ifs.iltis.cpp.core.buildconfiguration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IFolderInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.tcmodification.IFolderInfoModification;
import org.eclipse.cdt.managedbuilder.tcmodification.IToolChainModificationManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;


/**
 * Configures a managed build process
 * 
 * @author tstauber
 * @since 3.0
 * 
 */
public class BuildConfigurator {

    private IProject project;

    public BuildConfigurator(IProject project) {
        this.project = project;
    }

    private static boolean isValid(IToolChain toolChain) {
        return toolChain != null && toolChain.isSupported() && !toolChain.isAbstract() && !toolChain.isSystemObject() && ManagedBuildManager
                .isPlatformOk(toolChain);
    }

    public static List<IToolChain> getValidToolChainsForPlatform() {
        List<IToolChain> validToolChains = new ArrayList<>();
        IToolChain[] toolChains = ManagedBuildManager.getExtensionToolChains();
        for (IToolChain toolChain : toolChains) {
            if (isValid(toolChain)) {
                validToolChains.add(toolChain);
            }
        }
        return validToolChains;
    }

    public void setDefaultToolChain() {
        List<IToolChain> validToolChains = BuildConfigurator.getValidToolChainsForPlatform();
        if (!validToolChains.isEmpty()) {
            IToolChain defaultToolChain = getSuperToolChain(validToolChains.get(0));
            setToolchain(defaultToolChain);
        }
    }

    private IToolChain getSuperToolChain(IToolChain toolChain) {
        IToolChain currentToolChain = toolChain;
        while (currentToolChain.getSuperClass() != null) {
            currentToolChain = currentToolChain.getSuperClass();
        }
        return currentToolChain;
    }

    public void setToolchain(IToolChain toolChain) {
        IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
        IFolderInfo folderInfo = buildInfo.getDefaultConfiguration().getRootFolderInfo();
        IToolChainModificationManager toolChainModificationManager = ManagedBuildManager.getToolChainModificationManager();
        IFolderInfoModification folderInfoModification = toolChainModificationManager.createModification(folderInfo);
        folderInfoModification.setToolChain(toolChain);

        try {
            folderInfoModification.apply();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public boolean isSharedLibraryProject() {
        IConfiguration configuration = ManagedBuildManager.getBuildInfo(project).getDefaultConfiguration();
        IBuildPropertyValue buildArtefactType = configuration.getBuildArtefactType();
        return buildArtefactType != null && buildArtefactType.getId() != null && buildArtefactType.getId().equals(
                "org.eclipse.cdt.build.core.buildArtefactType.sharedLib");
    }

    public void setArtifactExtension(String artifactExtension) {
        IConfiguration configuration = ManagedBuildManager.getBuildInfo(project).getDefaultConfiguration();
        configuration.setArtifactExtension(artifactExtension);
    }

    public static String getSharedLibraryExtensionForPlatform() {
        switch (Platform.getOS()) {
        case Platform.OS_WIN32:
            return "dll";
        case Platform.OS_MACOSX:
            return "dylib";
        default:
            return "so";
        }
    }
}

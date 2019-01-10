package ch.hsr.ifs.iltis.cpp.versionator.preferences;

import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

import ch.hsr.ifs.iltis.cpp.versionator.Activator;
import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;


public class CPPVersionProjectSetting {

    private static final String PREF_C_DIALECT     = "c_dialect";
    private static final String PREF_C_DIALECT_MIN = PREF_C_DIALECT + "_min";
    private static final String PREF_C_DIALECT_MAX = PREF_C_DIALECT + "_max";

    public static CPPVersion loadProjectVersion(IProject project) {
        IScopeContext projectScope = new ProjectScope(project);
        IEclipsePreferences projectNode = projectScope.getNode(Activator.PLUGIN_ID);
        if (projectNode != null) {
            String versionString = projectNode.get(PREF_C_DIALECT, null);
            if (versionString == null) {
                return null;
            }
            CPPVersion version = null;
            try {
                version = CPPVersion.valueOf(versionString);
            } catch (IllegalArgumentException e) {
                Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Corrupted Version File");
                Activator.getDefault().getLog().log(status);
            }
            return version;
        } else {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to get Project Preference Node");
            Activator.getDefault().getLog().log(status);
        }
        return null;
    }

    public static void saveProjectVersion(IProject project, CPPVersion version) {
        IScopeContext projectScope = new ProjectScope(project);
        IEclipsePreferences projectNode = projectScope.getNode(Activator.PLUGIN_ID);
        if (projectNode != null) {
            projectNode.put(PREF_C_DIALECT, version.toString());
        } else {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to get Project Preference Node");
            Activator.getDefault().getLog().log(status);
        }
        try {
            projectNode.flush();
        } catch (BackingStoreException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to save Project CPPVersion");
            Activator.getDefault().getLog().log(status);
        }
    }

    /**
     * Limits the available version-range for a project.
     *
     * @param project
     * The target project
     * @param min
     * The minimal version (inclusive)
     * @param max
     * The maximal version (inclusive)
     */
    public static void limitVersionRange(IProject project, CPPVersion min, CPPVersion max) {
        IScopeContext projectScope = new ProjectScope(project);
        IEclipsePreferences projectNode = projectScope.getNode(Activator.PLUGIN_ID);
        if (projectNode != null) {
            projectNode.put(PREF_C_DIALECT_MIN, min.toString());
            projectNode.put(PREF_C_DIALECT_MAX, max.toString());
        } else {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to get Project Preference Node");
            Activator.getDefault().getLog().log(status);
        }
        try {
            projectNode.flush();
        } catch (BackingStoreException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to save Project Version-Range");
            Activator.getDefault().getLog().log(status);
        }
    }

    /**
     * Returns the C++ versions available. This method considers limitations set by {@link #limitVersionRange(IProject, CPPVersion, CPPVersion)}.
     */
    public static CPPVersion[] getLimitedVersions(IProject project) {
        IScopeContext projectScope = new ProjectScope(project);
        IEclipsePreferences projectNode = projectScope.getNode(Activator.PLUGIN_ID);

        if (projectNode != null) {
            CPPVersion min = CPPVersion.valueOf(projectNode.get(PREF_C_DIALECT_MIN, Stream.of(CPPVersion.values()).min((o1, o2) -> o1.ordinal() - o2
                    .ordinal()).map(CPPVersion::toString).orElse(null)));
            CPPVersion max = CPPVersion.valueOf(projectNode.get(PREF_C_DIALECT_MAX, Stream.of(CPPVersion.values()).max((o1, o2) -> o1.ordinal() - o2
                    .ordinal()).map(CPPVersion::toString).orElse(null)));
            return Stream.of(CPPVersion.values()).filter(v -> v.ordinal() >= min.ordinal() && v.ordinal() <= max.ordinal()).toArray(
                    CPPVersion[]::new);
        } else {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to get Project Preference Node");
            Activator.getDefault().getLog().log(status);
        }
        return CPPVersion.values();
    }

}

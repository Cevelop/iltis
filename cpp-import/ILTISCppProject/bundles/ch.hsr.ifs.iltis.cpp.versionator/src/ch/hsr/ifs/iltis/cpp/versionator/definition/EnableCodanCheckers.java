package ch.hsr.ifs.iltis.cpp.versionator.definition;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.codan.core.CodanCorePlugin;
import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.PreferenceConstants;
import org.eclipse.cdt.codan.core.model.ICheckersRegistry;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemProfile;
import org.eclipse.cdt.codan.internal.core.CodanPreferencesLoader;
import org.eclipse.cdt.codan.internal.core.model.CodanProblem;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import ch.hsr.ifs.iltis.cpp.versionator.Activator;


@SuppressWarnings("restriction")
public final class EnableCodanCheckers {

    private EnableCodanCheckers() {}

    public static void enableProblems(IProject project, boolean enabled, String... ids) {
        List<String> problemIDs = Arrays.asList(ids);
        ICheckersRegistry checkersRegistry = CodanRuntime.getInstance().getCheckersRegistry();
        IProblemProfile profile = checkersRegistry.getResourceProfileWorkingCopy(project);
        IPersistentPreferenceStore prefStore = new ScopedPreferenceStore(new ProjectScope(project), CodanCorePlugin.PLUGIN_ID);
        CodanPreferencesLoader codanPreferencesLoader = new CodanPreferencesLoader();
        codanPreferencesLoader.setInput(profile);

        for (IProblem problem : profile.getProblems()) {
            String problemID = problem.getId();
            if (problemIDs.contains(problemID)) {
                ((CodanProblem) problem).setEnabled(enabled);

                String property = codanPreferencesLoader.getProperty(problemID);
                prefStore.setValue(problemID, property);
            }
        }
        checkersRegistry.updateProfile(project, profile);

        try {
            prefStore.save();
        } catch (IOException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not save codan preferences");
            Activator.getDefault().getLog().log(status);
        }
    }

    public static void setPreference_UseWorkspaceSettings(IProject project, boolean useWorkspaceSettings) {
        try {
            ProjectScope ps = new ProjectScope(project);
            ScopedPreferenceStore scoped = new ScopedPreferenceStore(ps, CodanCorePlugin.PLUGIN_ID);
            scoped.setValue(PreferenceConstants.P_USE_PARENT, useWorkspaceSettings);
            scoped.save();
        } catch (IOException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not save codan preferences");
            Activator.getDefault().getLog().log(status);
        }
    }
}

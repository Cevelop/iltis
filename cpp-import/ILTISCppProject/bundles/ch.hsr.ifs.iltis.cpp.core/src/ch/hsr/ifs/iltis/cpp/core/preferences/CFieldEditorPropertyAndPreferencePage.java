package ch.hsr.ifs.iltis.cpp.core.preferences;

import java.util.HashSet;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.ui.preferences.ProjectSelectionDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;

import ch.hsr.ifs.iltis.core.core.preferences.FieldEditorPropertyAndPreferencePage;


/**
 * A combined {@code PropertyPage} and {@code PreferencePage}. Uses {@code FieldEditor}s for easier and cleaner definition of preferences. If the page
 * acts as a {@code PropertyPage} a header-bar, containing a checkbox (to enable component specific settings) and link (to open the workspace
 * preferences), will be added.
 *
 * The header-bar's design is held consistent with the default {@code PropertyAndPreferencePage}'s design.
 *
 *
 * @author tstauber
 */
@SuppressWarnings("restriction")
public abstract class CFieldEditorPropertyAndPreferencePage extends FieldEditorPropertyAndPreferencePage implements IWorkbenchPropertyPage,
        IWorkbenchPreferencePage {

    public CFieldEditorPropertyAndPreferencePage(final int style) {
        super(style);
    }

    public CFieldEditorPropertyAndPreferencePage(final String title, final int style) {
        super(title, style);
    }

    public CFieldEditorPropertyAndPreferencePage(final String title, final ImageDescriptor image, final int style) {
        super(title, image, style);
    }

    /**
     * This method is called when the checkbox, which toggles project specific preferences, has been changed.
     */
    @Override
    protected void validationHook() {
        /* Override and do validation checks and setValid in subclass */
    }

    /**
     * Must return the {@code IProject} for which to open the property page.
     * This could be done by opening a ProjectSelectorDialog.
     *
     * @return The project for which to open the project properties
     */
    @Override
    protected ICProject getProjectForWhichToOpenProperties() {
        final HashSet<ICProject> projectsWithSpecifics = new HashSet<>();
        try {
            final ICProject[] projects = CoreModel.getDefault().getCModel().getCProjects();
            for (final ICProject curr : projects) {
                if (getPropertyAndPreferenceHelper().projectSpecificPreferencesEnabled(curr.getProject())) {
                    projectsWithSpecifics.add(curr);
                }
            }
        } catch (final CModelException ignore) {}
        final ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell(), projectsWithSpecifics);
        if (dialog.open() == Window.OK) {
            return (ICProject) dialog.getFirstResult();
        }
        return null;
    }

}

package ch.hsr.ifs.iltis.core.core.preferences;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


/**
 * The default implementation for IPropertyAndPreferenceHelper (covers most standard needs)
 *
 * @author tstauber
 *
 */
public abstract class PropertyAndPreferenceHelper implements IPropertyAndPreferenceHelper {

    private IPersistentPreferenceStore projectPreferences;
    private IProject                   currentProject;

    @Override
    public IPreferenceStore getProjectPreferences(final IProject project) {
        if (!project.equals(currentProject)) {
            if (projectPreferences != null) {
                try {
                    projectPreferences.save();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            projectPreferences = new ScopedPreferenceStore(new ProjectScope(project), getPreferenceIdQualifier());
        }
        return projectPreferences;
    }

    @Override
    public String getDefaultPreferenceIdQualifier() {
        return getPreferenceIdQualifier();
    }

    @Override
    public boolean getBoolean(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getBoolean(name);
        } else {
            return getWorkspacePreferences().getBoolean(name);
        }
    }

    @Override
    public boolean getProjectBoolean(final String name, final IProject project) {
        return getProjectPreferences(project).getBoolean(name);
    }

    @Override
    public boolean getDefaultBoolean(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getDefaultBoolean(name);
        } else {
            return getWorkspacePreferences().getDefaultBoolean(name);
        }
    }

    @Override
    public boolean getProjectDefaultBoolean(final String name, final IProject project) {
        return getProjectPreferences(project).getDefaultBoolean(name);
    }

    @Override
    public double getDouble(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getDouble(name);
        } else {
            return getWorkspacePreferences().getDouble(name);
        }
    }

    @Override
    public double getProjectDouble(final String name, final IProject project) {
        return getProjectPreferences(project).getDouble(name);
    }

    @Override
    public double getDefaultDouble(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getDefaultDouble(name);
        } else {
            return getWorkspacePreferences().getDefaultDouble(name);
        }
    }

    @Override
    public double getProjectDefaultDouble(final String name, final IProject project) {
        return getProjectPreferences(project).getDefaultDouble(name);
    }

    @Override
    public float getFloat(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getFloat(name);
        } else {
            return getWorkspacePreferences().getFloat(name);
        }
    }

    @Override
    public float getProjectFloat(final String name, final IProject project) {
        return getProjectPreferences(project).getFloat(name);
    }

    @Override
    public float getDefaultFloat(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getDefaultFloat(name);
        } else {
            return getWorkspacePreferences().getDefaultFloat(name);
        }
    }

    @Override
    public float getProjectDefaultFloat(final String name, final IProject project) {
        return getProjectPreferences(project).getDefaultFloat(name);
    }

    @Override
    public int getInt(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getInt(name);
        } else {
            return getWorkspacePreferences().getInt(name);
        }
    }

    @Override
    public int getProjectInt(final String name, final IProject project) {
        return getProjectPreferences(project).getInt(name);
    }

    @Override
    public int getDefaultInt(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getDefaultInt(name);
        } else {
            return getWorkspacePreferences().getDefaultInt(name);
        }
    }

    @Override
    public int getProjectDefaultInt(final String name, final IProject project) {
        return getProjectPreferences(project).getDefaultInt(name);
    }

    @Override
    public long getLong(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getLong(name);
        } else {
            return getWorkspacePreferences().getLong(name);
        }
    }

    @Override
    public long getProjectLong(final String name, final IProject project) {
        return getProjectPreferences(project).getLong(name);
    }

    @Override
    public long getDefaultLong(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getDefaultLong(name);
        } else {
            return getWorkspacePreferences().getDefaultLong(name);
        }
    }

    @Override
    public long getProjectDefaultLong(final String name, final IProject project) {
        return getProjectPreferences(project).getDefaultLong(name);
    }

    @Override
    public String getString(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getString(name);
        } else {
            return getWorkspacePreferences().getString(name);
        }
    }

    @Override
    public String getProjectString(final String name, final IProject project) {
        return getProjectPreferences(project).getString(name);
    }

    @Override
    public String getDefaultString(final String name, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            return getProjectPreferences(project).getDefaultString(name);
        } else {
            return getWorkspacePreferences().getDefaultString(name);
        }
    }

    @Override
    public String getProjectDefaultString(final String name, final IProject project) {
        return getProjectPreferences(project).getDefaultString(name);
    }

    /* Setters */

    @Override
    public void setValue(final String name, final boolean value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setValue(name, value);
        } else {
            getWorkspacePreferences().setValue(name, value);
        }
    }

    @Override
    public void setProjectValue(final String name, final boolean value, final IProject project) {
        getProjectPreferences(project).setValue(name, value);
    }

    @Override
    public void setDefaultValue(final String name, final boolean value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setDefault(name, value);
        } else {
            getWorkspacePreferences().setDefault(name, value);
        }
    }

    @Override
    public void setProjectDefaultValue(final String name, final boolean value, final IProject project) {
        getProjectPreferences(project).setDefault(name, value);
    }

    @Override
    public void setValue(final String name, final double value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setValue(name, value);
        } else {
            getWorkspacePreferences().setValue(name, value);
        }
    }

    @Override
    public void setProjectValue(final String name, final double value, final IProject project) {
        getProjectPreferences(project).setValue(name, value);
    }

    @Override
    public void setDefaultValue(final String name, final double value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setDefault(name, value);
        } else {
            getWorkspacePreferences().setDefault(name, value);
        }
    }

    @Override
    public void setProjectDefaultValue(final String name, final double value, final IProject project) {
        getProjectPreferences(project).setDefault(name, value);
    }

    @Override
    public void setValue(final String name, final float value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setValue(name, value);
        } else {
            getWorkspacePreferences().setValue(name, value);
        }
    }

    @Override
    public void setProjectValue(final String name, final float value, final IProject project) {
        getProjectPreferences(project).setValue(name, value);
    }

    @Override
    public void setDefaultValue(final String name, final float value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setDefault(name, value);
        } else {
            getWorkspacePreferences().setDefault(name, value);
        }
    }

    @Override
    public void setProjectDefaultValue(final String name, final float value, final IProject project) {
        getProjectPreferences(project).setDefault(name, value);
    }

    @Override
    public void setValue(final String name, final int value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setValue(name, value);
        } else {
            getWorkspacePreferences().setValue(name, value);
        }
    }

    @Override
    public void setProjectValue(final String name, final int value, final IProject project) {
        getProjectPreferences(project).setValue(name, value);
    }

    @Override
    public void setDefaultValue(final String name, final int value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setDefault(name, value);
        } else {
            getWorkspacePreferences().setDefault(name, value);
        }
    }

    @Override
    public void setProjectDefaultValue(final String name, final int value, final IProject project) {
        getProjectPreferences(project).setDefault(name, value);
    }

    @Override
    public void setValue(final String name, final long value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setValue(name, value);
        } else {
            getWorkspacePreferences().setValue(name, value);
        }
    }

    @Override
    public void setProjectValue(final String name, final long value, final IProject project) {
        getProjectPreferences(project).setValue(name, value);
    }

    @Override
    public void setDefaultValue(final String name, final long value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setDefault(name, value);
        } else {
            getWorkspacePreferences().setDefault(name, value);
        }
    }

    @Override
    public void setProjectDefaultValue(final String name, final long value, final IProject project) {
        getProjectPreferences(project).setDefault(name, value);
    }

    @Override
    public void setValue(final String name, final String value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setValue(name, value);
        } else {
            getWorkspacePreferences().setValue(name, value);
        }
    }

    @Override
    public void setProjectValue(final String name, final String value, final IProject project) {
        getProjectPreferences(project).setValue(name, value);
    }

    @Override
    public void setDefaultValue(final String name, final String value, final IProject project) {
        if (projectSpecificPreferencesEnabled(project)) {
            getProjectPreferences(project).setDefault(name, value);
        } else {
            getWorkspacePreferences().setDefault(name, value);
        }
    }

    @Override
    public void setProjectDefaultValue(final String name, final String value, final IProject project) {
        getProjectPreferences(project).setDefault(name, value);
    }

    /* Others */

    @Override
    public void setProjectSpecificPreferences(final IProject project, final boolean enabled) {
        getProjectPreferences(project).setValue(P_USE_PROJECT_PREFERENCES, enabled);
    }

    @Override
    public boolean projectSpecificPreferencesEnabled(final IProject project) {
        if (project == null) {
            return false;
        }
        final IPreferenceStore projectPreferences = getProjectPreferences(project);
        return projectPreferences.contains(P_USE_PROJECT_PREFERENCES) && projectPreferences.getBoolean(P_USE_PROJECT_PREFERENCES);
    }

    @Override
    public boolean contains(final String name, final IProject project) {
        return projectSpecificPreferencesEnabled(project) ? getProjectPreferences(project).contains(name) : getWorkspacePreferences().contains(name);
    }

}

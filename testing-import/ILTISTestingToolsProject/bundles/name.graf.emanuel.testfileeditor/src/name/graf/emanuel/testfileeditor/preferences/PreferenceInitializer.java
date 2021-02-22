package name.graf.emanuel.testfileeditor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import name.graf.emanuel.testfileeditor.helpers.IdHelper;


/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeDefaultPreferences() {
        final IPreferenceStore store = PropAndPrefHelper.getInstance().getWorkspacePreferences();
        store.setDefault(IdHelper.P_CREATE_WARNING_ON_BAD_TEST_NAME, true);
    }

}

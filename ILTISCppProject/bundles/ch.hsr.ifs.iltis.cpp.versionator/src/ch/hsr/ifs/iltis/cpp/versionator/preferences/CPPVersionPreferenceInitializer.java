package ch.hsr.ifs.iltis.cpp.versionator.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.iltis.cpp.versionator.Activator;
import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;


/**
 * Class used to initialize default preference values.
 */
public class CPPVersionPreferenceInitializer extends AbstractPreferenceInitializer {

   @Override
   public void initializeDefaultPreferences() {
      IPreferenceStore store = Activator.getDefault().getPreferenceStore();
      store.setDefault(CPPVersionPreferenceConstants.ELEVENATOR_VERSION_DEFAULT, CPPVersion.DEFAULT.name());
   }

}

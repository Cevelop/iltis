package name.graf.emanuel.testfileeditor.preferences;

import org.eclipse.jface.preference.IPreferenceStore;


import ch.hsr.ifs.iltis.core.core.preferences.PropertyAndPreferenceHelper;

import name.graf.emanuel.testfileeditor.Activator;
import name.graf.emanuel.testfileeditor.helpers.IdHelper;


public class PropAndPrefHelper extends PropertyAndPreferenceHelper {

   private static PropAndPrefHelper helper = new PropAndPrefHelper();

   public static PropAndPrefHelper getInstance() {
      return helper;
   }

   private static final IPreferenceStore workspacePreferences = Activator.getDefault().getPreferenceStore();

   @Override
   public IPreferenceStore getWorkspacePreferences() {
      return workspacePreferences;
   }

   @Override
   public String getPreferenceIdQualifier() {
      return IdHelper.DEFAULT_QUALIFIER;
   }
}

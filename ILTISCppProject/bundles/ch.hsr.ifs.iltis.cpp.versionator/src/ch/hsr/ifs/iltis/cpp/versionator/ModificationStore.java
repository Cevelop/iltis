package ch.hsr.ifs.iltis.cpp.versionator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;


public class ModificationStore {

   private Map<CPPVersion, DialectBasedSetting> settingStore = new HashMap<>();

   public DialectBasedSetting get(CPPVersion version) {
      DialectBasedSetting setting = settingStore.get(version);
      if (setting == null) {
         setting = EvaluateContributions.createSettings(version);
         setSettingsBasedOnPreferences(setting);
         settingStore.put(version, setting);
      }
      return setting;
   }

   public Collection<DialectBasedSetting> getList(CPPVersion selectedVersion) {
      DialectBasedSetting rootSetting = get(selectedVersion);
      return listSettings(rootSetting);
   }

   private Collection<DialectBasedSetting> listSettings(DialectBasedSetting setting) {
      List<DialectBasedSetting> settingList = new ArrayList<>();
      for (DialectBasedSetting subSetting : setting.getSubsettings()) {
         if (subSetting.hasSubsettings()) {
            settingList.addAll(listSettings(subSetting));
         } else {
            settingList.add(subSetting);
         }
      }
      return settingList;
   }

   private void setSettingsBasedOnPreferences(DialectBasedSetting parentSetting) {
      IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
      for (DialectBasedSetting sub : parentSetting.getSubsettings()) {
         if (!sub.hasSubsettings() && sub.getPreferenceName() != null) {
            String prefName = sub.getPreferenceName();
            if (prefStore.contains(prefName)) {
               sub.setChecked(prefStore.getBoolean(prefName));
            } else {
               sub.setChecked(sub.isCheckedByDefault());
            }
         }
         setSettingsBasedOnPreferences(sub);
      }
   }

   public void refreshFromPreferences() {
      for (DialectBasedSetting setting : settingStore.values()) {
         setSettingsBasedOnPreferences(setting);
      }
   }

   public void restoreDefaults(CPPVersion version) {
      DialectBasedSetting setting = get(version);
      restoreDefaults(setting);
   }

   private void restoreDefaults(DialectBasedSetting setting) {
      if (setting.hasSubsettings()) {
         for (DialectBasedSetting sub : setting.getSubsettings()) {
            restoreDefaults(sub);
         }
      } else {
         setting.setChecked(setting.isCheckedByDefault());
      }
   }

   public void savePreferences() {
      for (DialectBasedSetting setting : settingStore.values()) {
         storeSetting(setting);
      }
   }

   private void storeSetting(DialectBasedSetting parentSetting) {
      IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
      for (DialectBasedSetting sub : parentSetting.getSubsettings()) {
         String prefName = sub.getPreferenceName();
         if (!sub.hasSubsettings() && prefName != null) {
            prefStore.setDefault(prefName, sub.isCheckedByDefault());
            prefStore.setValue(prefName, sub.isChecked());
         }
         storeSetting(sub);
      }
      if (parentSetting.getPreferenceName() != null) {
         prefStore.setValue(parentSetting.getPreferenceName(), parentSetting.isChecked());
      }
   }
}

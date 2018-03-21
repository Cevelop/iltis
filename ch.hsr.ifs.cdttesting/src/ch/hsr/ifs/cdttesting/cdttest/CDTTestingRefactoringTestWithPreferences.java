package ch.hsr.ifs.cdttesting.cdttest;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.cdttesting.cdttest.base.ITestPreferencesMixin;


/**
 * Most of the code for this class originates from CDT's RefactoringTestBase
 * class. In our case, it executes on the more correctly set-up project/index of
 * our cdttesting framework
 */
public abstract class CDTTestingRefactoringTestWithPreferences extends CDTTestingRefactoringTest implements ITestPreferencesMixin {

   /** Expected counts of errors, warnings and info messages */
   protected int expectedInitialErrors;
   protected int expectedInitialWarnings;
   protected int expectedFinalErrors;
   protected int expectedFinalWarnings;
   protected int expectedFinalInfos;

   private final Map<String, String> prefBackup     = new HashMap<>();
   private final Map<String, String> prefEvalBackup = new HashMap<>();
   protected IPreferenceStore        preferenceStore;

   @Override
   protected void configureTest(final Properties properties) {
      final String preference = properties.getProperty(CDTTestingConfigConstants.SET_PREFERENCES);
      final String preferenceEval = properties.getProperty(CDTTestingConfigConstants.SET_PREFERENCES_EVAL);

      if (preference != null && !preference.isEmpty()) {
         if (preferenceStore == null) {
            preferenceStore = initPrefs();
         }
         final String[] splitPreferences = preference.split(",");
         final Map<String, String> preferencesMap = new HashMap<>();
         splitAndAdd(preferencesMap, splitPreferences);
         backupPreferences(prefBackup, preferencesMap);
         setPreferences(preferencesMap);
      }
      if (preferenceEval != null && !preferenceEval.isEmpty()) {

         if (preferenceStore == null) {
            preferenceStore = initPrefs();
         }
         final String[] splitPreferences = preferenceEval.split(",");
         final Map<String, String> preferencesMap = new HashMap<>();
         splitAndAdd(preferencesMap, splitPreferences);
         final Map<String, String> evaluatedMap = evaluatePreferences(preferencesMap);
         backupPreferences(prefEvalBackup, evaluatedMap);
         setPreferences(evaluatedMap);
      }

      super.configureTest(properties);
   }

   @Override
   public void tearDown() throws Exception {
      setPreferences(prefBackup);
      setPreferences(prefEvalBackup);
      super.tearDown();
      cleanupProjects();
   }

   @Override
   public void backupPreferences(final Map<String, String> backupMap, final Map<String, String> preferencesMap) {
      for (final String key : preferencesMap.keySet()) {
         final String value = preferenceStore.getString(key);
         backupMap.put(key, value);
      }
   }

   @Override
   public void setPreferences(final Map<String, String> preferencesMap) {
      for (final String key : preferencesMap.keySet()) {
         preferenceStore.setValue(key, preferencesMap.get(key));
      }
   }

}

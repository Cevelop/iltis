package ch.hsr.ifs.iltis.testing.highlevel.cdttest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsSerializableProvider;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.core.settings.model.ICSettingBase;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import ch.hsr.ifs.iltis.testing.highlevel.TestingPlugin;


/**
 * This class provides a simple language settings providers as a replacement for the Managed Build language settings provider.
 * <p>
 * CDT Testing used to be somewhat dependent on the 'Managed Build' system, since MB injects a language service provider that is capable of reading
 * resource configurations set using {@link ICProject#setRawPathEntries(IPathEntry[], IProgressMonitor)}. However, since MB will likely be phased out
 * in the future, we provide a "replacement" in order to be independent of MB.
 * </p>
 */
public class CDTTestingLanguageSettingsProvider extends LanguageSettingsSerializableProvider {

   public static final String PROVIDER_NAME = "CDTTestingLanguageSettingsProvider";
   public static final String PROVIDER_ID   = TestingPlugin.PLUGIN_ID + ".cdtTestingLanguageSettingsProvider";

   //@formatter:off
   private static final List<Integer> SUPPORTED_SETTING_KINDS = Arrays.asList(
      ICLanguageSettingEntry.INCLUDE_PATH,
      ICLanguageSettingEntry.INCLUDE_FILE,
      ICLanguageSettingEntry.MACRO,
      ICLanguageSettingEntry.MACRO_FILE,
      ICLanguageSettingEntry.LIBRARY_PATH,
      ICLanguageSettingEntry.LIBRARY_FILE,
      ICLanguageSettingEntry.OUTPUT_PATH,
      ICLanguageSettingEntry.SOURCE_PATH
   );
   //@formatter:on

   public CDTTestingLanguageSettingsProvider() {
      super(PROVIDER_NAME, PROVIDER_ID);
   }

   @Override
   public List<ICLanguageSettingEntry> getSettingEntries(ICConfigurationDescription cfgDescription, IResource resource, String languageId) {
      IPath pathInProject = resource.getProjectRelativePath();
      ICResourceDescription description = cfgDescription.getResourceDescription(pathInProject, false);
      ICLanguageSetting[] languageSettings = getLanguageSettingsFor(description);

      //@formatter:off
      return Arrays.stream(languageSettings)
         .filter(s -> s.getLanguageId() == languageId)
         .map(s -> SUPPORTED_SETTING_KINDS.stream()
                      .filter(k -> (s.getSupportedEntryKinds() & k) != 0)
                      .map(k -> s.getSettingEntriesList(k))
                      .reduce(new ArrayList<>(), (a, l) -> {
                         a.addAll(l);
                         return a;
                      })
         ).reduce(new ArrayList<>(), (a, l) -> {
            a.addAll(l);
            return a;
         });
      //@formatter:on
   }

   private ICLanguageSetting[] getLanguageSettingsFor(ICResourceDescription description) {
      if (description.getType() == ICSettingBase.SETTING_PROJECT || description
            .getType() == ICSettingBase.SETTING_FOLDER) { return ((ICFolderDescription) description).getLanguageSettings(); }

      return new ICLanguageSetting[0];
   }

}

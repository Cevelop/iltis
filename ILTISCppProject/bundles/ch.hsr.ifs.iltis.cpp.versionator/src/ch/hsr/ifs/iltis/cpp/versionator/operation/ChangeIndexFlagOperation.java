package ch.hsr.ifs.iltis.cpp.versionator.operation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsEditableProvider;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvidersKeeper;
import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsManager;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.language.settings.providers.ToolchainBuiltinSpecsDetector;
import org.eclipse.cdt.ui.newui.CDTPropertyManager;
import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;
import ch.hsr.ifs.iltis.cpp.versionator.definition.IVersionModificationOperation;


public class ChangeIndexFlagOperation implements IVersionModificationOperation {

    private final static String PARAMETER_PROPERTY = "parameter";

    @Override
    public void perform(IProject project, CPPVersion selectedVersion, boolean enabled) {
        if (!enabled) {
            return;
        }

        ICProjectDescription projectDescr = CDTPropertyManager.getProjectDescription(project);
        ICConfigurationDescription[] configurations = projectDescr.getConfigurations();
        for (ICConfigurationDescription config : configurations) {
            if (config instanceof ILanguageSettingsProvidersKeeper) {
                ILanguageSettingsProvidersKeeper providersKeeper = (ILanguageSettingsProvidersKeeper) config;
                updateConfiguration(providersKeeper, selectedVersion);
            }
        }
        CDTPropertyManager.performOk(this);
    }

    private void updateConfiguration(ILanguageSettingsProvidersKeeper providersKeeper, CPPVersion selectedVersion) {
        List<ILanguageSettingsProvider> providers = providersKeeper.getLanguageSettingProviders();
        List<ILanguageSettingsProvider> newProviders = new ArrayList<>();

        for (ILanguageSettingsProvider settingsProvider : providers) {
            ILanguageSettingsProvider rawSettingsProvider = LanguageSettingsManager.getRawProvider(settingsProvider);
            if (rawSettingsProvider instanceof ToolchainBuiltinSpecsDetector && rawSettingsProvider instanceof ILanguageSettingsEditableProvider) {
                try {
                    ILanguageSettingsEditableProvider newSettingsProvider = ((ILanguageSettingsEditableProvider) rawSettingsProvider).cloneShallow();
                    ToolchainBuiltinSpecsDetector builtinSpecsDetector = (ToolchainBuiltinSpecsDetector) newSettingsProvider;

                    String parameterProperty = builtinSpecsDetector.getProperty(PARAMETER_PROPERTY);
                    builtinSpecsDetector.setProperty(PARAMETER_PROPERTY, setIndexFlag(parameterProperty, selectedVersion));
                    LanguageSettingsManager.setStoringEntriesInProjectArea(builtinSpecsDetector, true);

                    newProviders.add(builtinSpecsDetector);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            } else {
                newProviders.add(settingsProvider);
            }
        }
        providersKeeper.setLanguageSettingProviders(newProviders);
    }

    private static String setIndexFlag(String parameterProperty, CPPVersion selectedVersion) {
        String newParameterProperty = removeSubstringToNextSpace(parameterProperty, "-std=c++");
        newParameterProperty += " -std=" + selectedVersion.getCompilerVersionString();
        return newParameterProperty;
    }

    private static String removeSubstringToNextSpace(String original, String substringToRemove) {
        StringBuilder sb = new StringBuilder(original);
        String resultingString = original;

        int start = original.indexOf(substringToRemove);
        if (start > -1) {
            int end = original.indexOf(" ", start);
            if (end > -1) {
                // + 1 at the end to also remove the space.
                // There is still a space at the beginning and we do
                // not want two spaces
                resultingString = sb.replace(start, end + 1, "").toString();
            }
        }
        return resultingString;
    }

}

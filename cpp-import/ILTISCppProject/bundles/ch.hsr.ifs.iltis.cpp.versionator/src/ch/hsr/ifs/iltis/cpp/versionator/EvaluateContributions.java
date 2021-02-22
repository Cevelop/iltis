package ch.hsr.ifs.iltis.cpp.versionator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.spi.RegistryContributor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;
import ch.hsr.ifs.iltis.cpp.versionator.definition.IVersionModificationOperation;


public class EvaluateContributions {

    private static final String IVERSIONMODIFICATOR_ID = "ch.hsr.ifs.iltis.cpp.versionator.versionmodification";

    private static final String TAG_ALL_VERSIONS         = "ALL_VERSIONS";
    private static final String TAG_NAME                 = "name";
    private static final String TAG_GROUP                = "group";
    private static final String TAG_CHECKED_BY_DEFAULT   = "checkedByDefault";
    private static final String TAG_VERSION_MODIFICATION = "versionModification";
    private static final String TAG_OPERATION_CLASS      = "operationClass";

    private static Map<String, List<DialectBasedSetting>> groupCache;

    public static DialectBasedSetting createSettings(CPPVersion selectedVersion) {

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] config = registry.getConfigurationElementsFor(IVERSIONMODIFICATOR_ID);

        DialectBasedSetting settings = new DialectBasedSetting(selectedVersion.getVersionString() + " Settings");

        groupCache = new HashMap<>();

        for (IConfigurationElement configElement : config) {

            boolean supportedVersion = false;

            String versionName = configElement.getName();
            if (versionName.equals(TAG_ALL_VERSIONS)) {
                supportedVersion = true;
            } else {
                CPPVersion version = CPPVersion.valueOf(versionName);
                supportedVersion = version.equals(selectedVersion);
            }

            if (supportedVersion) {
                Bundle contributingBundle = getContributingBundle(configElement);
                for (IConfigurationElement childElement : configElement.getChildren()) {
                    createChildSettings(childElement, settings, contributingBundle, selectedVersion.toString());
                }
            }
        }

        return settings;
    }

    private static void createChildSettings(IConfigurationElement element, DialectBasedSetting parentSettings, Bundle contributingBundle,
            String versionName) {

        DialectBasedSetting setting = createSetting(element, parentSettings, contributingBundle, versionName);

        // defaults to false if attribute not present
        setting.setCheckedByDefault(Boolean.valueOf(element.getAttribute(TAG_CHECKED_BY_DEFAULT)));

        for (IConfigurationElement childElement : element.getChildren()) {
            createChildSettings(childElement, setting, contributingBundle, versionName);
        }

    }

    private static DialectBasedSetting createSetting(IConfigurationElement element, DialectBasedSetting parentSettings, Bundle contributingBundle,
            String versionName) {
        IVersionModificationOperation versionModification = extractVersionModification(element);

        String settingName = element.getAttribute(TAG_NAME);
        String preferenceName = DialectBasedSetting.buildPreferenceName(contributingBundle, versionName, settingName);

        DialectBasedSetting setting = null;

        if (element.getName().equals(TAG_GROUP)) {
            List<DialectBasedSetting> groupList = groupCache.get(settingName);
            if (groupList != null) {
                for (DialectBasedSetting cachedGroup : groupList) {
                    if (cachedGroup.getParent().getName().equals(parentSettings.getName())) {
                        setting = cachedGroup;
                        break;
                    }
                }
            }
        }

        if (setting == null) {
            setting = new DialectBasedSetting(settingName, versionModification, preferenceName);
            parentSettings.addSubsetting(setting);
            if (element.getName().equals(TAG_GROUP)) {
                List<DialectBasedSetting> groupList = groupCache.get(settingName);
                if (groupList == null) {
                    groupList = new ArrayList<>();
                    groupCache.put(settingName, groupList);
                }
                groupList.add(setting);
            }
        }
        return setting;
    }

    private static IVersionModificationOperation extractVersionModification(IConfigurationElement element) {
        try {
            if (!element.getName().equals(TAG_VERSION_MODIFICATION)) {
                return null;
            }
            if (element.getAttribute(TAG_OPERATION_CLASS) == null) {
                return null;
            }

            final Object o = element.createExecutableExtension(TAG_OPERATION_CLASS);
            if (o instanceof IVersionModificationOperation) {
                return (IVersionModificationOperation) o;
            }
        } catch (CoreException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private static Bundle getContributingBundle(IConfigurationElement config) {
        IContributor contributor = config.getContributor();
        Bundle bundle;
        if (contributor instanceof RegistryContributor) {
            long id = Long.parseLong(((RegistryContributor) contributor).getActualId());
            Bundle thisBundle = FrameworkUtil.getBundle(EvaluateContributions.class);
            bundle = thisBundle.getBundleContext().getBundle(id);
        } else {
            bundle = Platform.getBundle(contributor.getName());
        }

        return bundle;
    }
}

package ch.hsr.ifs.iltis.cpp.versionator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;

import ch.hsr.ifs.iltis.cpp.versionator.definition.IVersionModificationOperation;
import ch.hsr.ifs.iltis.cpp.versionator.preferences.CPPVersionPreferenceConstants;


public final class DialectBasedSetting {

    private String                        name;
    private DialectBasedSetting           parent           = null;
    private String                        preferenceName;
    private List<DialectBasedSetting>     subsettings      = new ArrayList<>();
    private IVersionModificationOperation operation;
    private boolean                       checked          = false;
    private boolean                       checkedByDefault = false;

    public DialectBasedSetting(String name) {
        this(name, null);
    }

    public DialectBasedSetting(String name, IVersionModificationOperation operation) {
        this(name, operation, null);
    }

    public DialectBasedSetting(String name, IVersionModificationOperation operation, String preferenceName) {
        this.name = name;
        this.operation = operation;
        this.preferenceName = preferenceName;
    }

    private void setParent(DialectBasedSetting parent) {
        this.parent = parent;
    }

    public DialectBasedSetting getParent() {
        return parent;
    }

    public void addSubsetting(DialectBasedSetting subsetting) {
        subsetting.setParent(this);
        subsettings.add(subsetting);
    }

    public String getName() {
        return name;
    }

    public IVersionModificationOperation getOperation() {
        return operation;
    }

    public boolean hasSubsettings() {
        return !subsettings.isEmpty();
    }

    public List<DialectBasedSetting> getSubsettings() {
        return subsettings;
    }

    public boolean isCheckedByDefault() {
        return checkedByDefault;
    }

    public void setCheckedByDefault(boolean checkedByDefault) {
        this.checkedByDefault = checkedByDefault;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;

        checkedChangedDowntree(this);

        if (getParent() != null) {
            getParent().checkedChangedUptree();
        }
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    private void checkedChangedDowntree(DialectBasedSetting setting) {
        for (DialectBasedSetting subsetting : setting.getSubsettings()) {
            subsetting.checked = setting.checked;
            subsetting.checkedChangedDowntree(subsetting);
        }
    }

    private void checkedChangedUptree() {
        if (getCheckedChildCount() == getSubsettings().size()) {
            this.checked = true;
        } else {
            this.checked = false;
        }
        if (getParent() != null) {
            getParent().checkedChangedUptree();
        }
    }

    public int getCheckedChildCount() {
        int count = 0;
        for (DialectBasedSetting subsetting : getSubsettings()) {
            if (subsetting.isChecked()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return name + "(checked: " + isChecked() + ")";
    }

    public static String buildPreferenceName(Bundle contributingPlugin, String version, String settingName) {
        return MessageFormat.format(CPPVersionPreferenceConstants.ELEVENATOR_VERSION_SETTINGS_WITH_PLACEHOLDERS, contributingPlugin.getSymbolicName(),
                version, settingName);
    }
}

package ch.hsr.ifs.iltis.cpp.versionator.view;

import java.util.List;

import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import ch.hsr.ifs.iltis.cpp.versionator.DialectBasedSetting;


public final class DialectBasedSettingsProvider implements ITreeContentProvider, ILabelProvider, ICheckStateProvider {

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    @Override
    public void dispose() {}

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof DialectBasedSetting) {
            return ((DialectBasedSetting) element).hasSubsettings();
        }
        return false;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof DialectBasedSetting) {
            return ((DialectBasedSetting) element).getParent();
        }
        return null;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof DialectBasedSetting) {
            List<DialectBasedSetting> subsettings = ((DialectBasedSetting) parentElement).getSubsettings();
            return subsettings.toArray(new Object[subsettings.size()]);
        }
        return null;
    }

    @Override
    public void addListener(ILabelProviderListener listener) {}

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {}

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof DialectBasedSetting) {
            return ((DialectBasedSetting) element).getName();
        }
        return "<unknown>";
    }

    @Override
    public boolean isGrayed(Object element) {
        if (element instanceof DialectBasedSetting) {
            DialectBasedSetting setting = ((DialectBasedSetting) element);
            if (setting.getCheckedChildCount() < setting.getSubsettings().size()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isChecked(Object element) {
        if (element instanceof DialectBasedSetting) {
            DialectBasedSetting setting = ((DialectBasedSetting) element);
            if (childrenChecked(setting)) {
                return true;
            }
            return setting.isChecked();
        }
        return false;
    }

    private boolean childrenChecked(DialectBasedSetting setting) {
        if (setting.getCheckedChildCount() > 0) {
            return true;
        }
        for (DialectBasedSetting sub : setting.getSubsettings()) {
            if (childrenChecked(sub)) {
                return true;
            }
        }
        return false;
    }
}

package ch.hsr.ifs.iltis.cpp.versionator.view;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import ch.hsr.ifs.iltis.cpp.versionator.DialectBasedSetting;
import ch.hsr.ifs.iltis.cpp.versionator.view.TreeSelectionToolbar.ISelectionToolbarAction;


public class ModificationTree extends Group implements ISelectionToolbarAction {

    private CheckboxTreeViewer modificationTree;

    public ModificationTree(Composite parent, int style) {
        super(parent, style);

        setText("Modifications:");
        setLayout(new GridLayout(1, false));

        DialectBasedSettingsProvider provider = new DialectBasedSettingsProvider();

        modificationTree = createTreeViewer(this);
        modificationTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        modificationTree.setContentProvider(provider);
        modificationTree.setLabelProvider(provider);
        modificationTree.setCheckStateProvider(provider);
        modificationTree.addCheckStateListener(event -> {
            DialectBasedSetting setting = (DialectBasedSetting) event.getElement();
            setting.setChecked(event.getChecked());
            modificationTree.refresh();
        });

        TreeSelectionToolbar selectionToolbar = new TreeSelectionToolbar(this, this, SWT.NONE);
        selectionToolbar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
    }

    @Override
    public void selectAll(boolean selected) {
        DialectBasedSetting setting = (DialectBasedSetting) modificationTree.getInput();
        if (setting != null) {
            setting.setChecked(selected);
            modificationTree.refresh();
        }
    }

    protected CheckboxTreeViewer createTreeViewer(Composite parent) {
        PatternFilter filter = new PatternFilter();
        filter.setIncludeLeadingWildcard(true);
        FilteredTree filteredTree = new FilteredTree(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, filter,
                true) {

            @Override
            protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
                return new CheckboxTreeViewer(parent, style);
            }
        };
        filteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return (CheckboxTreeViewer) filteredTree.getViewer();
    }

    public void setInput(DialectBasedSetting setting) {
        modificationTree.setInput(setting);
    }

    @Override
    protected void checkSubclass() {}
}

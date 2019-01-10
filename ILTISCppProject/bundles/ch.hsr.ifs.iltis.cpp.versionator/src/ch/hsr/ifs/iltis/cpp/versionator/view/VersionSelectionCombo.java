package ch.hsr.ifs.iltis.cpp.versionator.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;


public class VersionSelectionCombo extends Composite {

    private Combo versionCombo;

    public VersionSelectionCombo(Composite parent, String labelText, int style) {
        super(parent, style);

        setFont(parent.getFont());
        setLayout(new GridLayout(2, false));

        Label label = new Label(this, SWT.NONE);
        label.setText(labelText);
        label.setFont(getFont());

        versionCombo = new Combo(this, SWT.READ_ONLY);

        for (CPPVersion cppVersion : CPPVersion.values()) {
            versionCombo.add(cppVersion.getVersionString());
            versionCombo.setData(cppVersion.getVersionString(), cppVersion);
        }

        CPPVersion versionToSelect = CPPVersion.getDefaultVersion();
        versionCombo.select(versionToSelect.ordinal());
        versionCombo.setFont(getFont());
    }

    public CPPVersion getSelectedVersion() {
        return (CPPVersion) versionCombo.getData(versionCombo.getText());
    }

    public void addSelectionListener(SelectionListener selectionListener) {
        versionCombo.addSelectionListener(selectionListener);
    }
}

package ch.hsr.ifs.iltis.cpp.versionator.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.hsr.ifs.iltis.cpp.versionator.Activator;
import ch.hsr.ifs.iltis.cpp.versionator.preferences.CPPVersionPreferenceConstants;


public class DefaultVersionSelector extends Composite {

   private Button defaultVersionButton;
   private Label  selectedDefaultVersionLabel;

   private VersionSelectionCombo versionCombo;

   public DefaultVersionSelector(Composite parent, final VersionSelectionCombo versionCombo, int style) {
      super(parent, style);

      this.versionCombo = versionCombo;

      setLayout(new GridLayout(1, false));

      defaultVersionButton = new Button(this, SWT.PUSH);
      defaultVersionButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
      defaultVersionButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Activator.getDefault().getPreferenceStore().setValue(CPPVersionPreferenceConstants.ELEVENATOR_VERSION_DEFAULT, versionCombo
                  .getSelectedVersion().toString());
            updateDefaultVersionButton();
            updateDefaultVersionLabel();
         }

      });
      updateDefaultVersionButton();

      selectedDefaultVersionLabel = new Label(this, SWT.NONE);
      selectedDefaultVersionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
      updateDefaultVersionLabel();
   }

   private void updateDefaultVersionLabel() {
      String versionString = versionCombo.getSelectedVersion().getVersionString();
      selectedDefaultVersionLabel.setText("Current Default Version is " + versionString);
   }

   public void updateDefaultVersionButton() {
      String versionString = versionCombo.getSelectedVersion().getVersionString();
      defaultVersionButton.setText("Set " + versionString + " as Default Version");

      String storedDefaultVersionString = Activator.getDefault().getPreferenceStore().getString(
            CPPVersionPreferenceConstants.ELEVENATOR_VERSION_DEFAULT);
      String selectedDefaultVersionString = versionCombo.getSelectedVersion().toString();
      boolean versionMatches = storedDefaultVersionString.equals(selectedDefaultVersionString);
      defaultVersionButton.setEnabled(!versionMatches);
   }

}

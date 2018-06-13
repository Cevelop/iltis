package ch.hsr.ifs.iltis.cpp.versionator.view;

import java.util.Collection;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ch.hsr.ifs.iltis.cpp.versionator.DialectBasedSetting;
import ch.hsr.ifs.iltis.cpp.versionator.ModificationStore;
import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;


public class SelectVersionWizardPage extends WizardPage {

   private VersionSelectionCombo versionCombo;
   private ModificationTree      modificationTree;

   private ModificationStore modificationStore = new ModificationStore();

   public SelectVersionWizardPage() {
      super("C++ version selection for project");
      setMessage("Select the C++ standard version for this project");
      setTitle("C++ Version");
      setPageComplete(true);
   }

   @Override
   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setFont(parent.getFont());
      composite.setLayout(new GridLayout(2, false));

      versionCombo = new VersionSelectionCombo(composite, "C++ Version", SWT.NONE);
      versionCombo.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            updateSettings();
         }
      });

      WorkspaceSettingsLink workspaceSettingsLink = new WorkspaceSettingsLink(composite, SWT.NONE);
      workspaceSettingsLink.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));

      modificationTree = new ModificationTree(composite, SWT.NONE);
      modificationTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

      updateSettings();

      setControl(composite);
   }

   public void refreshSettings() {
      modificationStore.refreshFromPreferences();
      updateSettings();
   }

   private void updateSettings() {
      CPPVersion selectedVersion = versionCombo.getSelectedVersion();
      DialectBasedSetting setting = modificationStore.get(selectedVersion);
      modificationTree.setInput(setting);
   }

   public Collection<DialectBasedSetting> getVersionModifications() {
      CPPVersion selectedVersion = getSelectedVersion();
      return modificationStore.getList(selectedVersion);
   }

   public CPPVersion getSelectedVersion() {
      if (versionCombo != null && !versionCombo.isDisposed()) {
         return versionCombo.getSelectedVersion();
      } else {
         return CPPVersion.getDefaultVersion();
      }
   }
}

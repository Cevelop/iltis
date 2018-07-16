package ch.hsr.ifs.iltis.cpp.versionator.view;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

import ch.hsr.ifs.iltis.cpp.versionator.preferences.CPPVersionSelectionPreferencePage;


public class WorkspaceSettingsLink extends Link {

   public WorkspaceSettingsLink(Composite parent, int style) {
      super(parent, style);

      String linkText = "Configure Workspace Settings...";

      setFont(parent.getFont());
      setText("<A>" + linkText + "</A>"); //$NON-NLS-1$//$NON-NLS-2$
      addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            PreferencesUtil.createPreferenceDialogOn(getShell(), CPPVersionSelectionPreferencePage.PAGE_ID, null, null).open();
         }
      });
   }

   @Override
   protected void checkSubclass() {}
}

package ch.hsr.ifs.iltis.cpp.versionator;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.cdt.ui.wizards.CDTCommonProjectWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;
import ch.hsr.ifs.iltis.cpp.versionator.preferences.CPPVersionProjectSetting;
import ch.hsr.ifs.iltis.cpp.versionator.view.SelectVersionWizardPage;


public class SelectVersionOperation implements IRunnableWithProgress {

   @Override
   public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
      IWizardPage[] pages = MBSCustomPageManager.getCustomPages();
      IWizard wizard = pages[0].getWizard();

      CPPVersion selectedVersion = CPPVersion.DEFAULT;
      try {
         selectedVersion = CPPVersion.getDefaultVersion();
      } catch (IllegalArgumentException e) {}

      Collection<DialectBasedSetting> modifications = null;
      // our wizard page can be anywhere, since other plug-ins can use the same extension point and add pages after
      // ours. The C++ version selection page may not be the last one in this wizard so iterate through all and do
      // not just get the last page
      for (IWizardPage page : pages) {
         if (page instanceof SelectVersionWizardPage) {
            selectedVersion = ((SelectVersionWizardPage) page).getSelectedVersion();
            modifications = ((SelectVersionWizardPage) page).getVersionModifications();
            break;
         }
      }

      if (wizard instanceof CDTCommonProjectWizard) {
         CDTCommonProjectWizard projectWizard = (CDTCommonProjectWizard) wizard;
         IProject project = projectWizard.getProject(false);

         CPPVersionProjectSetting.saveProjectVersion(project, selectedVersion);

         if (modifications != null) {
            for (DialectBasedSetting setting : modifications) {
               if (setting.getOperation() != null) {
                  // System.out.println("Executing version operation: " + setting.getName());
                  executeExtension(setting, project, selectedVersion);
               }
            }
         }
      }

   }

   private void executeExtension(final DialectBasedSetting setting, final IProject project, final CPPVersion version) {
      ISafeRunnable runnable = new ISafeRunnable() {

         @Override
         public void handleException(Throwable e) {
            System.err.println("Exception in version operation extension: " + setting.getOperation().getClass());
         }

         @Override
         public void run() throws Exception {
            setting.getOperation().perform(project, version, setting.isChecked());
         }
      };
      SafeRunner.run(runnable);
   }
}

package ch.hsr.ifs.iltis.cpp.versionator.operation;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;
import ch.hsr.ifs.iltis.cpp.versionator.definition.IVersionModificationOperation;


public class ChangeCompilerAttributeWarningsFlagOperation implements IVersionModificationOperation {

   @Override
   public void perform(IProject project, CPPVersion selectedVersion, boolean enabled) {

      if (!enabled) { return; }

      IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
      IConfiguration[] configs = info.getManagedProject().getConfigurations();
      for (IConfiguration config : configs) {
         ITool[] tools = config.getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.compiler");
         for (ITool tool : tools) {
            IOption otherFlagOption = tool.getOptionBySuperClassId("gnu.cpp.compiler.option.other.other");
            String newValue = otherFlagOption.getValue() + " -Wno-attributes";
            ManagedBuildManager.setOption(config, tool, otherFlagOption, newValue);
         }
      }
   }

}

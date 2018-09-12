package ch.hsr.ifs.iltis.cpp.versionator.definition;

import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.iltis.cpp.versionator.Activator;
import ch.hsr.ifs.iltis.cpp.versionator.preferences.CPPVersionPreferenceConstants;
import ch.hsr.ifs.iltis.cpp.versionator.preferences.CPPVersionProjectSetting;


public enum CPPVersion {

   CPP_98("C++ 98", "c++98"), CPP_03("C++ 03", "c++03"), CPP_11("C++ 11", "c++0x"), CPP_14("C++ 14", "c++1y"), CPP_17("C++ 17", "c++1z");

   private String versionString;
   private String compilerVersionString;
   // java does not know enum aliases x.x
   public static final CPPVersion DEFAULT = CPP_17;

   private CPPVersion(String versionString, String compilerVersionString) {
      this.versionString = versionString;
      this.compilerVersionString = compilerVersionString;
   }

   public String getVersionString() {
      return versionString;
   }

   public String getCompilerVersionString() {
      return compilerVersionString;
   }

   public static CPPVersion getForProject(IProject project) {
      return CPPVersionProjectSetting.loadProjectVersion(project);
   }

   public static void setForProject(IProject project, CPPVersion version) {
      CPPVersionProjectSetting.saveProjectVersion(project, version);
   }

   /**
    * @return The default C++ version configured in the preferences
    */
   public static CPPVersion getDefaultVersion() {
      return CPPVersion.valueOf(Activator.getDefault().getPreferenceStore().getString(CPPVersionPreferenceConstants.ELEVENATOR_VERSION_DEFAULT));
   }

   /**
    * This is used to limit the possible versions. If for example a cute-header version is loaded which only runs for C++ > 14, this can be expressed
    * by limiting the choices via {@code setVersionRange(project, CPP_14, null)}. Removing this limitation would be done by calling this method with
    * {@code null} for min and max.
    *
    * @param project
    *        The project to limit the version for
    * @param min
    *        The minimal version (inclusive) or {@code null} if none.
    * @param max
    *        The maximal version (inclusive) or {@code null} if none
    */
   public static void setVersionRange(IProject project, CPPVersion min, CPPVersion max) {
      // TODO(tstauber - Apr 16, 2018) implement
      throw new RuntimeException("Not jet implemented");
   }

}

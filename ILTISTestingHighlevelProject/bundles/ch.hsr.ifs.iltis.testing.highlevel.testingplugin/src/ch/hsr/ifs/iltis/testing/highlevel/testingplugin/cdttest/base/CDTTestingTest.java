package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.stream.Stream;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.CheckerLaunchMode;
import org.eclipse.cdt.codan.core.model.IProblemProfile;
import org.eclipse.cdt.codan.core.model.IProblemReporter;
import org.eclipse.cdt.codan.core.param.IProblemPreference;
import org.eclipse.cdt.codan.core.param.RootProblemPreference;
import org.eclipse.cdt.codan.internal.core.model.CodanProblem;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


@SuppressWarnings("restriction")
public abstract class CDTTestingTest extends RTSSourceFileTest {

   protected boolean checkerEnabled = false;
   protected boolean codanRun       = false;

   /**
    * Overload this function if you want to enable the checker for a single problem id.
    * For multiple problem ids use {@link #getProblemIds()}.
    */
   protected IProblemId<?> getProblemId() {
      fail("Checker can not be enabled without a problem id. Overload either getProblemId() or getProblemIds() propperly.");
      return null;
   }

   /**
    * Overload this function if you want to enable multiple CodanProblems in the checker.
    *
    * This will call {@link #getProblemId()} as a default, so if there's only a single
    * problem, you can simply overload the other function instead.
    */
   protected MutableList<IProblemId<?>> getProblemIds() {
      return Lists.mutable.of(getProblemId());
   }

   /**
    * Call to enable only the checker for the problem ids provided in {@link #getProblemIds()}
    */
   protected void enableAndConfigureChecker() {
      MutableList<IProblemId<?>> activeProblemIds = getProblemIds();
      final IProblemProfile profile = CodanRuntime.getInstance().getCheckersRegistry().getResourceProfile(getCurrentProject());
      Stream.of(profile.getProblems()).forEach(problem -> {
         final CodanProblem codanProblem = (CodanProblem) problem;
         if (activeProblemIds.anySatisfy(id -> id.equals(codanProblem))) {
            enableCodanProblem(codanProblem);
            checkerEnabled = true;
         } else {
            codanProblem.setEnabled(false);
         }
      });
      CodanRuntime.getInstance().getCheckersRegistry().updateProfile(getCurrentProject(), profile);
      assertTrue("No checker for any problem id found.", checkerEnabled);
   }

   /**
    * Enable the specified codan problem.
    * 
    * @param codanProblem
    *        The codan problem to be enabled.
    */
   protected void enableCodanProblem(final CodanProblem codanProblem) {
      final IProblemPreference preference = codanProblem.getPreference();
      if (preference instanceof RootProblemPreference) {
         final RootProblemPreference rootProblemPreference = (RootProblemPreference) preference;
         rootProblemPreference.getLaunchModePreference().enableInLaunchModes(CheckerLaunchMode.RUN_ON_FULL_BUILD);
         problemPreferenceSetup(rootProblemPreference);
      }
      codanProblem.setEnabled(true);
   }

   @SuppressWarnings("unused")
   protected void problemPreferenceSetup(final RootProblemPreference preference) {}

   /**
    * Finds markers for the type provided.
    * 
    * @param markerTypeStringToFind
    *        The super-type id of the marker to search for.
    * @return The markers of the type or any sub-type.
    * @throws CoreException
    */
   protected IMarker[] findMarkers(final String markerTypeStringToFind) throws CoreException {
      assertThatCodeWasAnalyzed();
      return getCurrentProject().findMarkers(markerTypeStringToFind, true, IResource.DEPTH_INFINITE);
   }

   /**
    * Finds generic codan-markers and its sub-types.
    * 
    * @return The markers
    * @throws CoreException
    */
   protected IMarker[] findMarkers() throws CoreException {
      return findMarkers(IProblemReporter.GENERIC_CODE_ANALYSIS_MARKER_TYPE);
   }

   /**
    * Call to run the code analysis
    * <p>
    * {@link #enableAndConfigureChecker()} must be called before calling this method
    */
   protected void runCodeAnalysis() {
      assertThatCheckerIsEnabled();
      CodanRuntime.getInstance().getBuilder().processResource(getCurrentProject(), new NullProgressMonitor());
      codanRun = true;
   }

   protected void assertThatCodeWasAnalyzed() {
      assertTrue("Code analysis was never executed", codanRun);
   }

   protected void assertThatCheckerIsEnabled() {
      assertTrue("Checker was never enabled", checkerEnabled);
   }
}

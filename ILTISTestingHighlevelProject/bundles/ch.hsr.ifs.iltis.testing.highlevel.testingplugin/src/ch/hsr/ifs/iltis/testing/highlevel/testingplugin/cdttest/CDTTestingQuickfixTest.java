package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.junit.Before;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.CDTTestingUITest;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.SourceFileBaseTest;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison.ComparisonArg;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.helpers.UIThreadSyncRunnable;


public abstract class CDTTestingQuickfixTest extends CDTTestingUITest {

   @Override
   @Before
   public void setUp() throws Exception {
      super.setUp();
      enableAndConfigureChecker();
      runCodeAnalysis();
   }

   /**
    * Subclasses must to provide marker resolution to execute
    */
   protected abstract IMarkerResolution createMarkerResolution();

   protected void runQuickFix() throws CoreException {
      final IMarker[] markers = findMarkers();
      assertThatOnlyOneMarkerWasFound(markers);
      runQuickFix(markers[0]);
   }

   protected void runQuickFix(final IMarker marker) {
      assertNotNull("Marker was null. Could not run quick fix for it. ", marker);
      UIThreadSyncRunnable.run(() -> createMarkerResolution().run(marker));
      //      saveAllEditors(); // TODO check if this is necessary, I suppose it is not necessary
   }

   protected void runQuickfixAndAssertAllEqual(final IMarker marker) {
      runQuickFix(marker);
      assertAllSourceFilesEqual(makeComparisonArguments());
   }

   protected void runQuickfixForAllMarkersAndAssertAllEqual() throws CoreException {
      IMarker[] markers = findMarkers();
      assertThatMarkersWereFound(markers);
      Stream.of(markers).forEach(this::runQuickFix);
      assertAllSourceFilesEqual(makeComparisonArguments());
   }

   /**
    * Runs the first quickfix found and then asserts all source files to be equal with the expected source. The comparison arguments can be altered by
    * overriding {@link #makeComparisonArguments()}.
    * 
    * @throws CoreException
    */
   protected void runQuickfixAndAssertAllEqual() throws CoreException {
      final IMarker[] markers = findMarkers();
      assertThatOnlyOneMarkerWasFound(markers);
      runQuickFix(markers[0]);
      assertAllSourceFilesEqual(makeComparisonArguments());
   }

   /**
    * Finds all markers and returns the one which starts at the smallest offset.
    * @return The first marker
    * @throws CoreException
    */
   protected IMarker getFirstMarker() throws CoreException {
      return Stream.of(findMarkers()).min((m1, m2) -> m1.getAttribute(IMarker.CHAR_START, Integer.MAX_VALUE) - m2.getAttribute(IMarker.CHAR_START,
            Integer.MAX_VALUE)).orElse(null);
   }

   /**
    * Can be overloaded to use specific comparison arguments in {@link #runQuickfixAndAssertAllEqual(IMarkerResolution)},
    * {@link #runQuickfixAndAssertAllEqual(IMarker, IMarkerResolution)}, and {@link #runQuickfixForAllMarkersAndAssertAllEqual(IMarkerResolution)}.
    * <p>
    * By default this uses the values stored in {@link SourceFileBaseTest#COMPARE_AST_AND_COMMENTS_AND_INCLUDES}
    * 
    * @return
    */
   protected EnumSet<ComparisonArg> makeComparisonArguments() {
      return COMPARE_AST_AND_COMMENTS_AND_INCLUDES;
   }

   public void assertThatMarkersWereFound(IMarker[] markers) {
      assertTrue("No markers found! If this behaviour is intended, please change this test to a checker test!", markers.length > 0);
   }

   public void assertThatOnlyOneMarkerWasFound(IMarker[] markers) {
      assertEquals("CDTTestingCodanQuickfixTest.runQuickFix(quickfix) is only intended to run on testcases containing exactly 1 marker. " +
                   "Use overlaod runQuickFix(marker, quickfix) for other cases. Use findMarkers-methods to find available markers.", 1,
            markers.length);
   }

}

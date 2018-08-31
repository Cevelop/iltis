package ch.hsr.ifs.iltis.cpp.core.codan.marker;

import java.util.regex.Pattern;

import org.eclipse.cdt.codan.core.model.ICodanProblemMarker;
import org.eclipse.cdt.codan.internal.core.model.CodanProblemMarker;
import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.core.core.ILTIS;
import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;


/**
 * An {@link IMarkerResolutionGenerator} that is able to handle resolutions with info element.
 *
 * @author tstauber
 */
public class InfoProblemMarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

   private static final int    CACHE_SIZE                = 42;
   private static final String EXTENSION_POINT_NAME      = "infoMarkerResolution"; //$NON-NLS-1$
   private static final String RESOLUTION_ELEMEMT_NAME   = "resolution";
   private static final String PROBLEM_ID_ATTRIBUTE      = "problemId";
   private static final String MESSAGE_PATTERN_ATTRIBUTE = "messagePattern";
   private static final String RESOLUTION_CLASS_PROPERTY = "class";
   private static final String INFO_CLASS_PROPERTY       = "infoClass";

   private static final MutableMultimap<IMarker, IMarkerResolution> resolutions = Multimaps.mutable.list.empty();

   /**
    * {@inheritDoc}
    */
   @Override
   public IMarkerResolution[] getResolutions(final IMarker marker) {
      String id = marker.getAttribute(ICodanProblemMarker.ID, null);
      if (id == null) return new IMarkerResolution[0];
      if (!resolutions.containsKey(marker)) {
         if (resolutions.size() > CACHE_SIZE) {
            resolutions.clear();
         }
         readExtensions(marker);
      }
      MutableCollection<IMarkerResolution> viableResolutions = resolutions.get(marker).select( //
            (res) -> res instanceof ICodanMarkerResolution && ((ICodanMarkerResolution) res).isApplicable(marker));
      return viableResolutions.toArray(new IMarkerResolution[viableResolutions.size()]);
   }

   private static synchronized void readExtensions(final IMarker marker) {
      final IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(FrameworkUtil.getBundle(InfoProblemMarkerResolutionGenerator.class)
            .getSymbolicName(), EXTENSION_POINT_NAME);
      if (ep == null) { return; }
      try {
         // process categories
         for (final IConfigurationElement configurationElement : ep.getConfigurationElements()) {
            processResolution(configurationElement, marker);
         }
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   /**
    * @param configurationElement
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   private static void processResolution(final IConfigurationElement configurationElement, final IMarker marker) {
      if (RESOLUTION_ELEMEMT_NAME.equals(configurationElement.getName())) { //$NON-NLS-1$
         final String id = configurationElement.getAttribute(PROBLEM_ID_ATTRIBUTE); //$NON-NLS-1$
         if (!isApplicable(marker, id)) return;
         final String messagePattern = configurationElement.getAttribute(MESSAGE_PATTERN_ATTRIBUTE); //$NON-NLS-1$
         if (id == null && messagePattern == null) {
            ILTIS.log(NLS.bind(Messages.PMRG_NotDefined, EXTENSION_POINT_NAME));
            return;
         }
         IInfoMarkerResolution res;
         try {
            res = (IInfoMarkerResolution) configurationElement.createExecutableExtension(RESOLUTION_CLASS_PROPERTY);//$NON-NLS-1$
            MarkerInfo<?> info = MarkerInfo.fromCodanProblemMarker(() -> {
               try {
                  return ((MarkerInfo) configurationElement.createExecutableExtension(INFO_CLASS_PROPERTY)); //$NON-NLS-1$
               } catch (CoreException e) {
                  throw ILTISException.wrap(e).rethrowUnchecked();
               }
            }, marker);
            res.configure(info);
         } catch (final CoreException e) {
            ILTIS.log(e);
            return;
         }
         if (messagePattern != null) {
            try {
               Pattern.compile(messagePattern);
            } catch (final Exception e) {
               ILTIS.log(NLS.bind(Messages.PMRG_Invalid, EXTENSION_POINT_NAME, e.getMessage()));
               return;
            }
         }
         addResolution(marker, res, messagePattern);
      }
   }

   @SuppressWarnings("restriction")
   private static boolean isApplicable(final IMarker marker, final String id) {
      String problemId = marker.getAttribute(CodanProblemMarker.ID, null);
      return problemId != null && problemId.equals(id);
   }

   public static void addResolution(final IMarker marker, final IMarkerResolution res, final String messagePattern) {
      resolutions.put(marker, res);
   }

   @Override
   public boolean hasResolutions(IMarker marker) {
      return getResolutions(marker).length > 0;
   }

}

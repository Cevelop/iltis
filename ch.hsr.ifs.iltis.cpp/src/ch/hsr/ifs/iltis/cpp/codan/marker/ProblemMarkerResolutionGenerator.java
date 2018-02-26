package ch.hsr.ifs.iltis.cpp.codan.marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.cdt.codan.core.model.ICodanProblemMarker;
import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import ch.hsr.ifs.iltis.core.ILTIS;


/**
 * An {@link IMarkerResolutionGenerator} that is able to handle label-texts in the {@code Plugin.xml}.
 *
 * @author tstauber
 */
public class ProblemMarkerResolutionGenerator implements IMarkerResolutionGenerator {

   private static final String                                     EXTENSION_POINT_NAME = "labeledMarkerResolution"; //$NON-NLS-1$
   private static final Map<String, Collection<IMarkerResolution>> resolutions          = new HashMap<>();
   private static boolean                                          resolutionsLoaded;

   /**
    * {@inheritDoc}
    */
   @Override
   public IMarkerResolution[] getResolutions(final IMarker marker) {
      if (!resolutionsLoaded) {
         readExtensions();
      }
      final String id = marker.getAttribute(ICodanProblemMarker.ID, null);
      if (id == null && resolutions.get(null) == null) { return new IMarkerResolution[0]; }
      final Collection<IMarkerResolution> collection = resolutions.get(id);
      if (collection != null) { return collection.stream().filter((res) -> res instanceof ICodanMarkerResolution && ((ICodanMarkerResolution) res)
            .isApplicable(marker)).toArray(IMarkerResolution[]::new); }
      return new IMarkerResolution[0];
   }

   private static synchronized void readExtensions() {
      final IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(ILTIS.PLUGIN_ID, EXTENSION_POINT_NAME);
      if (ep == null) { return; }
      try {
         final IConfigurationElement[] elements = ep.getConfigurationElements();
         // process categories
         for (final IConfigurationElement configurationElement : elements) {
            processResolution(configurationElement);
         }
      } finally {
         resolutionsLoaded = true;
      }
   }

   /**
    * @param configurationElement
    */
   private static void processResolution(final IConfigurationElement configurationElement) {
      if (configurationElement.getName().equals("resolution")) { //$NON-NLS-1$
         final String id = configurationElement.getAttribute("problemId"); //$NON-NLS-1$
         final String messagePattern = configurationElement.getAttribute("messagePattern"); //$NON-NLS-1$
         if (id == null && messagePattern == null) {
            ILTIS.log(NLS.bind(Messages.PMRG_NotDefined, EXTENSION_POINT_NAME));
            return;
         }
         IMarkerResolution res;
         try {
            res = (IMarkerResolution) configurationElement.createExecutableExtension("class");//$NON-NLS-1$
            if (res instanceof ILabeledMarkerResolution) {
               ((ILabeledMarkerResolution) res).setLabel(configurationElement.getAttribute("labelText")); //$NON-NLS-1$
            }

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
         addResolution(id, res, messagePattern);
      }
   }

   public static void addResolution(final String id, final IMarkerResolution res, final String messagePattern) {
      addResolution(id, res);
   }

   private static void addResolution(final String id, final IMarkerResolution res) {
      Collection<IMarkerResolution> collection = resolutions.get(id);
      if (collection == null) {
         collection = new ArrayList<>();
         resolutions.put(id, collection);
      }
      collection.add(res);
   }

}

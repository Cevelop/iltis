package ch.hsr.ifs.iltis.cpp.codan.marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.codan.core.model.ICodanProblemMarker;
import org.eclipse.cdt.codan.internal.core.model.CodanProblemMarker;
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
@SuppressWarnings("restriction")
public class ProblemMarkerResolutionGenerator implements IMarkerResolutionGenerator {

   private static final String                                         EXTENSION_POINT_NAME = "labeledMarkerResolution"; //$NON-NLS-1$
   private static final Map<String, Collection<ConditionalResolution>> resolutions          = new HashMap<>();
   private static boolean                                              resolutionsLoaded;

   static class ConditionalResolution {

      IMarkerResolution res;
      String            messagePattern;

      public ConditionalResolution(final IMarkerResolution res, final String messagePattern) {
         this.res = res;
         this.messagePattern = messagePattern;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IMarkerResolution[] getResolutions(final IMarker marker) {
      if (!resolutionsLoaded) {
         readExtensions();
      }
      final String id = marker.getAttribute(ICodanProblemMarker.ID, null);
      if (id == null && resolutions.get(null) == null) {
         return new IMarkerResolution[0];
      }
      final String message = marker.getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
      final Collection<ConditionalResolution> collection = resolutions.get(id);
      if (collection != null) {
         final ArrayList<IMarkerResolution> list = new ArrayList<>();
         for (final ConditionalResolution res : collection) {
            if (res.messagePattern != null) {
               try {
                  final Pattern pattern = Pattern.compile(res.messagePattern);
                  final Matcher matcher = pattern.matcher(message);
                  if (!matcher.matches()) {
                     continue;
                  }
                  if (id == null) {
                     setArgumentsFromPattern(matcher, marker);
                  }
               } catch (final Exception e) {
                  ILTIS.log(NLS.bind(Messages.PMRG_CannotCompile, res.messagePattern));
                  continue;
               }
            }
            if (res.res instanceof ICodanMarkerResolution && !((ICodanMarkerResolution) res.res).isApplicable(marker)) {
               continue;
            }
            list.add(res.res);
         }
         if (list.size() > 0) {
            return list.toArray(new IMarkerResolution[list.size()]);
         }
      }
      return new IMarkerResolution[0];
   }

   /**
    * @param matcher
    * @param marker
    */
   private void setArgumentsFromPattern(final Matcher matcher, final IMarker marker) {
      final int n = matcher.groupCount();
      if (n == 0) {
         return;
      }
      final String[] res = new String[n];
      for (int i = 0; i < n; i++) {
         res[i] = matcher.group(i + 1);
      }

      final String[] old = CodanProblemMarker.getProblemArguments(marker);
      if (!Arrays.deepEquals(res, old)) {
         try {
            CodanProblemMarker.setProblemArguments(marker, res);
         } catch (final CoreException e) {
            ILTIS.log(e);
         }
      }
   }

   private static synchronized void readExtensions() {
      final IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(ILTIS.PLUGIN_ID, EXTENSION_POINT_NAME);
      if (ep == null) {
         return;
      }
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
      addResolution(id, new ConditionalResolution(res, messagePattern));
   }

   private static void addResolution(final String id, final ConditionalResolution res) {
      Collection<ConditionalResolution> collection = resolutions.get(id);
      if (collection == null) {
         collection = new ArrayList<>();
         resolutions.put(id, collection);
      }
      collection.add(res);
   }

}

package ch.hsr.ifs.iltis.cpp.core.codan.marker;

import org.eclipse.cdt.codan.core.model.ICodanProblemMarker;
import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.core.core.ILTIS;
import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.core.resources.info.CompositeMarkerInfo;
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
    private static final String INFO_CLASS_PROPERTY       = "class";
    private static final String INFO_ELEMENT_NAME         = "info";

    private static final MutableMultimap<IMarker, IMarkerResolution> resolutions = Multimaps.mutable.list.empty();

    /**
     * {@inheritDoc}
     */
    @Override
    public IMarkerResolution[] getResolutions(final IMarker marker) {
        final String id = marker.getAttribute(ICodanProblemMarker.ID, null);
        if (id == null) return new IMarkerResolution[0];
        if (!resolutions.containsKey(marker)) {
            if (resolutions.size() > CACHE_SIZE) {
                resolutions.clear();
            }
            readExtensions(marker);
        }
        final MutableCollection<IMarkerResolution> viableResolutions = resolutions.get(marker).select( //
                (res) -> res instanceof ICodanMarkerResolution && ((ICodanMarkerResolution) res).isApplicable(marker));
        return viableResolutions.toArray(new IMarkerResolution[viableResolutions.size()]);
    }

    private static synchronized void readExtensions(final IMarker marker) {
        final IExtensionPoint infoMarkerResolutionEP = Platform.getExtensionRegistry().getExtensionPoint(FrameworkUtil.getBundle(
                InfoProblemMarkerResolutionGenerator.class).getSymbolicName(), EXTENSION_POINT_NAME);
        if (infoMarkerResolutionEP == null) return;

        for (final IConfigurationElement resolutionElement : infoMarkerResolutionEP.getConfigurationElements()) {
            if (RESOLUTION_ELEMEMT_NAME.equals(resolutionElement.getName())) processResolution(resolutionElement, marker);
        }
    }

    /**
     * @param resolutionElement
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void processResolution(final IConfigurationElement resolutionElement, final IMarker marker) {
        final String id = resolutionElement.getAttribute(PROBLEM_ID_ATTRIBUTE);
        if (id == null || !isApplicable(marker, id)) return;
        final IConfigurationElement[] infoElements = resolutionElement.getChildren(INFO_ELEMENT_NAME);

        final IInfoMarkerResolution res = getValidMarkerResolutionInstance(resolutionElement);
        final MarkerInfo<?> info = infoElements.length > 1 ? createCompositeMarkerInfo(marker, infoElements) : createMarkerInfo(marker,
                infoElements[0]);
        res.configure(info);

        final String messagePattern = resolutionElement.getAttribute(MESSAGE_PATTERN_ATTRIBUTE);
        addResolution(marker, res, messagePattern);
    }

    private static MarkerInfo<?> createCompositeMarkerInfo(final IMarker marker, final IConfigurationElement[] infoElements) {
        return new CompositeMarkerInfo().also(ci -> ci.infos.addAll(ArrayIterate.collect(infoElements, ie -> createMarkerInfo(marker, ie))));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static MarkerInfo<?> createMarkerInfo(final IMarker marker, final IConfigurationElement infoElement) {
        return MarkerInfo.fromCodanProblemMarker(ILTISException.sterilize(() -> ((MarkerInfo) infoElement.createExecutableExtension(
                INFO_CLASS_PROPERTY))), marker);
    }

    @SuppressWarnings("rawtypes")
    private static IInfoMarkerResolution getValidMarkerResolutionInstance(final IConfigurationElement configurationElement) {
        try {
            final Object markerResolution = configurationElement.createExecutableExtension(RESOLUTION_CLASS_PROPERTY);
            if (!(markerResolution instanceof IInfoMarkerResolution)) throw new IllegalStateException("The class " + markerResolution.getClass()
                    .getSimpleName() + " does not implement IInfoMarkerResolution!");
            return (IInfoMarkerResolution) markerResolution;
        } catch (final CoreException e) {
            ILTIS.log(e);
            return null;
        }
    }

    @SuppressWarnings("restriction")
    private static boolean isApplicable(final IMarker marker, final String id) {
        final String problemId = marker.getAttribute(ICodanProblemMarker.ID, null);
        return problemId != null && problemId.equals(id);
    }

    public static void addResolution(final IMarker marker, final IMarkerResolution res, final String messagePattern) {
        resolutions.put(marker, res);
    }

    @Override
    public boolean hasResolutions(final IMarker marker) {
        return getResolutions(marker).length > 0;
    }

}

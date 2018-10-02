package ch.hsr.ifs.iltis.cpp.core.ui.jface;

import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.internal.core.model.CodanProblemMarker;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;

import ch.hsr.ifs.iltis.core.core.ui.jface.TextSelectionUtil;


public class CTextSelectionUtil extends TextSelectionUtil {

    /**
     * Extracts the cdt IProblemLocation from the marker and tests, if it is contained in the selection.
     *
     * @param outer
     * The selection
     * @param marker
     * The marker
     * @return {@code true} iff the marker is contained in the selection
     */
    public static boolean contains(final ITextSelection outer, final IMarker marker) {
        final IProblemLocation location = CodanProblemMarker.getLocation(marker);
        return contains(outer, new TextSelection(location.getStartingChar(), location.getEndingChar() - location.getStartingChar()));
    }
}

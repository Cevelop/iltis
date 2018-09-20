package ch.hsr.ifs.iltis.core.core.ui.jface;

import org.eclipse.jface.text.ITextSelection;


/**
 * A utility class for TextSelections
 * 
 * @author tstauber
 *
 */
public class TextSelectionUtil {

    /**
     * Checks if outer contains inner
     * 
     * @param outer
     * The expected outer selection
     * @param inner
     * The expected inner selection
     * @return {@code true} iff outer fully encloses inner. Two congruent selections are considered to enclose each other.
     */
    public static boolean contains(ITextSelection outer, ITextSelection inner) {
        return outer.getOffset() <= inner.getOffset() && getEndOffset(outer) >= getEndOffset(inner);
    }

    /**
     * Checks if sel contains an offset
     * 
     * @param sel
     * The selection
     * @param offset
     * The offset to check
     * @return {@code true} iff offset lays in the selection
     */
    public static boolean contains(ITextSelection sel, int offset) {
        return sel.getOffset() <= offset && getEndOffset(sel) >= offset;
    }

    /**
     * Checks if two selections overlap. Containment does not count as overlap.
     * 
     * @param one
     * The first selection
     * @param two
     * The second selection
     * @return @{code true} iff the selection truly overlap. Containment does not count as overlapping.
     */
    public static boolean overlap(ITextSelection one, ITextSelection two) {
        /* [one {two ] } || { two [ } one ] */
        return contains(one, two.getOffset()) && contains(two, getEndOffset(one)) || contains(two, one.getOffset()) && contains(one, getEndOffset(
                two));
    }

    /**
     * Comfort method to get the end-offset.
     * 
     * @param sel
     * The selection from which to get the end-offset.
     * @return The end-offset of the selection
     */
    public static int getEndOffset(ITextSelection sel) {
        return sel.getOffset() + sel.getLength();
    }

}

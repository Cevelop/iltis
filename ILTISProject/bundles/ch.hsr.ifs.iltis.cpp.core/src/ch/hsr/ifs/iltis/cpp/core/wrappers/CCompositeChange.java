package ch.hsr.ifs.iltis.cpp.core.wrappers;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;


/**
 * A wrapper class for the cdt CCompositeChange. Using this wrapper reduces the amount of warnings respectively the amount of
 * {@code @SuppressWarnings} tags
 *
 * @author tstauber
 *
 */
public class CCompositeChange extends CompositeChange {

    private RefactoringChangeDescriptor desc;

    public CCompositeChange(final String name, final Change[] children) {
        super(name, children);
    }

    public CCompositeChange(final String name) {
        super(name);
    }

    public void setDescription(final RefactoringChangeDescriptor descriptor) {
        desc = descriptor;
    }

    /**
     * Add a Change to this composite change. Other composite changes will be
     * merged automatically.
     * 
     * @param change
     * The change to be added to this composite change
     * iff it is a CompositeChange as well, it will automatically be merged.
     * @since 1.1
     */
    public void flatAdd(Change change) {
        if (change instanceof CompositeChange) {
            merge((CompositeChange) change);
        } else {
            add(change);
        }
    }

    @Override
    public ChangeDescriptor getDescriptor() {
        if (desc != null) {
            return desc;
        }
        return super.getDescriptor();
    }

}

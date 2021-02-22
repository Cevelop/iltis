package ch.hsr.ifs.iltis.cpp.core.wrappers;

import java.util.Map;

import org.eclipse.cdt.internal.ui.refactoring.CRefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.core.functional.functions.Function;
import ch.hsr.ifs.iltis.cpp.core.ids.IRefactoringId;


/**
 * A wrapper class for the cdt CRefactoringContribution. Using this wrapper reduces the amount of warnings respectively the amount of
 * {@code @SuppressWarnings} tags
 *
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public abstract class CRefactoringContribution<T extends IRefactoringId<T>> extends RefactoringContribution {

    public CRefactoringContribution() {
        super();
    }

    public abstract RefactoringDescriptor createDescriptor(final T id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException;

    @Override
    public Map<String, String> retrieveArgumentMap(final RefactoringDescriptor descriptor) {
        if (descriptor instanceof CRefactoringDescriptor) {
            final CRefactoringDescriptor refDesc = (CRefactoringDescriptor) descriptor;
            return refDesc.getParameterMap();
        }
        return super.retrieveArgumentMap(descriptor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final RefactoringDescriptor createDescriptor(final String id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {
        return createDescriptor(getFromStringMethod().apply(id), project, description, comment, arguments, flags);
    }

    protected abstract Function<String, T> getFromStringMethod();
}

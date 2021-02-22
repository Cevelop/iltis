package ch.hsr.ifs.iltis.cpp.core.ids;

/**
 * An interface which should be implemented by types which represent a refactoring-id
 * 
 * @since 1.1
 */
public interface IRefactoringId<T extends IId<T>> extends IId<T> {

    /**
     * By default this creates a new IProblemId. This method must be overridden for enums which extend IRefactoringId.
     *
     * @param id
     * The id
     * @return An IRefactoringId holding the id of the refactoring. If an enum constant for this id exists, this method must return said enum
     * constant.
     */
    static IRefactoringId<RefactoringIdWrapper> wrap(final String id) {
        return RefactoringIdWrapper.wrap(id);
    }

    class RefactoringIdWrapper extends IIdWrapper<RefactoringIdWrapper> implements IRefactoringId<RefactoringIdWrapper> {

        protected RefactoringIdWrapper(final String id) {
            super(id);
        }

        protected static IRefactoringId<RefactoringIdWrapper> wrap(final String id) {
            return new RefactoringIdWrapper(id);
        }
    }
}

package ch.hsr.ifs.iltis.cpp.core.ast.checker.helper;

import ch.hsr.ifs.iltis.cpp.core.ids.IId;


/**
 * An interface which should be implemented by types which represent a problem-id
 * (mostly used in SimpleChecker, SimpleVisitor and cdttesting)
 *
 * @author tstauber
 */
public interface IProblemId<T extends IId<T>> extends IId<T> {

    /**
     * By default this creates a new IProblemId. This method must be overridden for enums which extend IProblemId.
     *
     * @param id
     * The id
     * @return An IProblemId holding the id of the problem. If an enum constant for this id exists, this method must return said enum constant.
     */
    static IProblemId<ProblemIdWrapper> wrap(final String id) {
        return new ProblemIdWrapper(id);
    }

    class ProblemIdWrapper extends IIdWrapper<ProblemIdWrapper> implements IProblemId<ProblemIdWrapper> {

        protected ProblemIdWrapper(final String id) {
            super(id);
        }
    }
}

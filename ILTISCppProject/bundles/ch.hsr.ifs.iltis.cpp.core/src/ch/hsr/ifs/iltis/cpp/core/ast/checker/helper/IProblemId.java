package ch.hsr.ifs.iltis.cpp.core.ast.checker.helper;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;


/**
 * An interface which should be implemented by types which represent a problem-id
 * (mostly used in SimpleChecker, SimpleVisitor and cdttesting)
 *
 * @author tstauber
 */
public interface IProblemId {

   /**
    * Returns the id as String
    *
    * @return
    */
   public String getId();

   public static IProblemId wrap(String id) {
      return new IProblemId() {

         @Override
         public String getId() {
            return id;
         }

         @Override
         public int hashCode() {
            return getId().hashCode();
         }

         @Override
         public boolean equals(Object obj) {
            if (obj instanceof IProblemId) { return getId().equals(((IProblemId) obj).getId()); }
            if (obj instanceof String) { throw new ILTISException("Tried to compare IProblemId and String, please wrap the string first")
                  .rethrowUnchecked(); }
            return false;
         }
      };
   }
}

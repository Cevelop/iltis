package ch.hsr.ifs.iltis.cpp.core.ids;

import org.eclipse.cdt.codan.core.model.IProblem;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.core.resources.info.IStringifyable;


/**
 * An interface which should be implemented by types which represent an id
 */
public interface IId<T extends IId<T> & IStringifyable<T>> extends IStringifyable<T> {

   /**
    * Returns the id as String
    *
    * @return The wrapped id string.
    */
   String getId();

   default boolean equals(T other) {
      return other != null && getId().equals(other.getId());
   }

   default boolean equals(IProblem problem) {
      return problem != null && getId().equals(problem.getId());
   }

   class IIdWrapper<T extends IIdWrapper<T>> implements IId<T> {

      private String id;

      protected IIdWrapper(String id) {
         this.id = id;
      }

      @Override
      public String getId() {
         return id;
      }

      @Override
      public int hashCode() {
         return getId().hashCode();
      }

      @Override
      public String toString() {
         return id;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof IId) { return getId().equals(((IId<?>) obj).getId()); }
         if (obj instanceof String) { throw new ILTISException("Tried to compare an IId and a String, please wrap the String first")
               .rethrowUnchecked(); }
         return false;
      }

      @Override
      public String stringify() {
         return id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T unstringify(String string) {
         return (T) new IIdWrapper<T>(string);
      }
   }
}

package ch.hsr.ifs.iltis.cpp.includes;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;


/**
 * @author tstauber
 */
public class IncludeHelper {

   private static String getIncludeStatement(final String includeName) {
      return "\n#include <" + includeName + ">\n"; // TODO enable for non system
                                                   // includes
   }

   /**
    * Creates and performs a change that inserts an include into the passed
    * {@link IASTTranslationUnit}
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void includeIfNotJetIncluded(final IASTTranslationUnit ast, final String includeName) {
      final IASTPreprocessorIncludeStatement[] includeStatements = ast.getIncludeDirectives();

      if (!isAlreadyIncluded(includeStatements, includeName)) { // $NON-NLS-1$
         final TextFileChange change = createIncludeIfNotJetIncluded(ast, includeName);
         try {
            change.perform(new NullProgressMonitor());
         }
         catch (final CoreException e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Creates and returns a TextFileChange to insert an include into the passed
    * {@link IASTTranslationUnit}
    *
    * @returns The {@link TextFileChange} or {@code null} if already included
    */
   public static TextFileChange createIncludeIfNotJetIncluded(final IASTTranslationUnit ast, final String includeName) {
      final IASTPreprocessorIncludeStatement[] includeStatements = ast.getIncludeDirectives();
      final IASTNode lastNode = getLastNode(includeStatements);

      if (!isAlreadyIncluded(includeStatements, includeName)) { // $NON-NLS-1$
         final TextFileChange change = createTextFileChange(ast.getOriginatingTranslationUnit().getFile(), createInsertEdit(lastNode, includeName));
         return change;
      } else {
         return null;
      }

   }

   /**
    * Returns the last {@link IASTPreprocessorIncludeStatement}.
    *
    * @param IASTPreprocessorIncludeStatement[]
    *        includeStatements
    * @return An {@link IASTNode} resembling the last of the include statements
    *         or null if there are no include statements
    */
   private static IASTNode getLastNode(final IASTPreprocessorIncludeStatement[] includeStatements) {
      return includeStatements.length > 0 ? includeStatements[includeStatements.length - 1] : null;
   }

   /**
    * Returns if the given header-file is already included in this file. This
    * function is not able to determine if the include is indirectly included.
    *
    * @param includeStatements
    * @param header
    * @return If the {@link IASTPreprocessorIncludeStatement} {@code header} is
    *         already included.
    */
   private static boolean isAlreadyIncluded(final IASTPreprocessorIncludeStatement[] includeStatements, final String header) {

      for (final IASTPreprocessorIncludeStatement include : includeStatements) {
         if (include.getName().toString().equals(header)) { return true; }
      }
      return false;
   }

   private static InsertEdit createInsertEdit(final IASTNode lastNode, final String includeName) {
      return new InsertEdit(getInsertOffset(lastNode), getIncludeStatement(includeName)); // $NON-NLS-1$
   }

   private static int getInsertOffset(final IASTNode lastNode) {
      return lastNode == null ? 0 : lastNode.getNodeLocations()[0].getNodeOffset() + lastNode.getNodeLocations()[0].getNodeLength();
   }

   private static TextFileChange createTextFileChange(final IFile file, final TextEdit edit) {
      final TextFileChange change = new TextFileChange("Add Include", file);
      change.setSaveMode(TextFileChange.LEAVE_DIRTY);
      change.setEdit(edit);
      return change;
   }
}

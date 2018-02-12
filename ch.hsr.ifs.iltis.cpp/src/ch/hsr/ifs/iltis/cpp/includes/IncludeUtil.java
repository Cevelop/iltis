package ch.hsr.ifs.iltis.cpp.includes;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import ch.hsr.ifs.iltis.core.functional.OptionalUtil;
import ch.hsr.ifs.iltis.core.resources.FileUtil;
import ch.hsr.ifs.iltis.cpp.util.constants.CommonCPPConstants;


/**
 * A utility class which provides static methods for adding includes.
 * 
 * @author tstauber
 */
public class IncludeUtil {

   private static StringBuffer getSystemIncludeStatement(final String includeName) {
      return new StringBuffer(CommonCPPConstants.INCLUDE_DIRECTIVE + " <" + includeName + ">");
   }

   private static StringBuffer getUserIncludeStatement(final String includeName) {
      return new StringBuffer(CommonCPPConstants.INCLUDE_DIRECTIVE + " \"" + includeName + "\"");
   }

   private static StringBuffer getIncludeStatement(final String includeName, final boolean isSystemInclude) {
      return isSystemInclude ? getSystemIncludeStatement(includeName) : getUserIncludeStatement(includeName);
   }

   /**
    * Creates and performs a change which inserts an user include into the passed
    * {@link IASTTranslationUnit}
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void userIncludeIfNotJetIncluded(final IASTTranslationUnit ast, final String includeName) {
      includeIfNotJetIncluded(ast, includeName, false);
   }

   /**
    * Creates and performs a change which inserts a system include into the passed
    * {@link IASTTranslationUnit}
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void systemIncludeIfNotJetIncluded(final IASTTranslationUnit ast, final String includeName) {
      includeIfNotJetIncluded(ast, includeName, true);
   }

   /**
    * Creates and performs a change which inserts an include into the passed
    * {@link IASTTranslationUnit}
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void includeIfNotJetIncluded(final IASTTranslationUnit ast, final String includeName, final boolean isSystemInclude) {
      final IASTPreprocessorIncludeStatement[] includeStatements = ast.getIncludeDirectives();

      if (!isAlreadyIncluded(includeStatements, includeName)) {
         try {
            OptionalUtil.doIfPresentT(createIncludeIfNotJetIncluded(ast, includeName, isSystemInclude), (change) -> change.perform(
                  new NullProgressMonitor()));
         } catch (final CoreException e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Creates and performs a change which inserts an include into the passed
    * {@link IASTTranslationUnit}.
    * 
    * @param textChangeSaveState
    *        Sets savestate of TextChange. Can be {@code TextFileChange.KEEP_SAVE_STATE}, {@code TextFileChange.FORCE_SAVE},
    *        {@code TextFileChange.LEAVE_DIRTY}
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void includeIfNotJetIncluded(final IASTTranslationUnit ast, final String includeName, final boolean isSystemInclude,
         final int textChangeSaveState) {
      final IASTPreprocessorIncludeStatement[] includeStatements = ast.getIncludeDirectives();

      if (!isAlreadyIncluded(includeStatements, includeName)) {
         try {
            OptionalUtil.doIfPresentT(createIncludeIfNotJetIncluded(ast, includeName, isSystemInclude), (change) -> {
               change.setSaveMode(textChangeSaveState);
               change.perform(new NullProgressMonitor());
            });
         } catch (final CoreException e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Creates and returns a TextFileChange to insert an include into the passed translation unit. The caller must provide the include name and the
    * information if it is a system include or a user include.
    * 
    * <pre>
    * An include name can be something like {@code vector} or {@code foo.h} 
    * </pre>
    *
    * @returns The {@link TextFileChange} or {@code null} if already included
    */
   public static Optional<TextFileChange> createIncludeIfNotJetIncluded(final IASTTranslationUnit ast, final String includeName,
         final boolean isSystemInclude) {
      final IASTPreprocessorIncludeStatement[] includeStatements = ast.getIncludeDirectives();
      boolean systemIncludesExist = Arrays.stream(includeStatements).anyMatch((stmt) -> stmt.isSystemInclude());
      boolean userIncludesExist = Arrays.stream(includeStatements).anyMatch((stmt) -> !stmt.isSystemInclude());

      IFile file = ast.getOriginatingTranslationUnit().getFile();

      if (!isAlreadyIncluded(includeStatements, includeName)) {
         final Optional<IASTNode> lastNode = getNodeAfterWhichToInsertInclude(ast, includeStatements, isSystemInclude);
         return Optional.ofNullable(createTextFileChange(file, createInsertEdit(lastNode, includeName, isSystemInclude, systemIncludesExist,
               userIncludesExist, FileUtil.getLineSeparator(file))));
      } else {
         return Optional.empty();
      }

   }

   private static Optional<IASTNode> getNodeAfterWhichToInsertInclude(final IASTTranslationUnit ast,
         final IASTPreprocessorIncludeStatement[] includeStatements, final boolean isSystemInclude) {
      if (includeStatements.length <= 0) { return findPositionForNewIncludeOfThisType(ast); }
      Optional<IASTNode> lastIncludeStatementOfType = findLastIncludeStatementOfType(ast, includeStatements, isSystemInclude);
      return lastIncludeStatementOfType.isPresent() ? lastIncludeStatementOfType : findPositionForNewIncludeOfThisType(ast);
   }

   private static Optional<IASTNode> findLastIncludeStatementOfType(final IASTTranslationUnit ast,
         final IASTPreprocessorIncludeStatement[] includeStatements, final boolean isSystemInclude) {
      return Optional.ofNullable(Arrays.stream(includeStatements).reduce(null, (prev, next) -> next.isSystemInclude() && !isSystemInclude ? prev
                                                                                                                                          : next));
   }

   private static Optional<IASTNode> findPositionForNewIncludeOfThisType(final IASTTranslationUnit ast) {
      IASTDeclaration[] declarations = ast.getDeclarations(true);
      int firstDeclaration = declarations.length > 0 ? declarations[0].getFileLocation().getNodeOffset() : -1;
      IASTPreprocessorStatement[] pPSs = ast.getAllPreprocessorStatements();
      IASTNode lastPPSBeforeFirstDeclaration = null;
      if (pPSs.length > 0) {
         for (IASTPreprocessorStatement current : pPSs) {
            if (current.getFileLocation().getNodeOffset() < firstDeclaration && !(current instanceof IASTPreprocessorIncludeStatement)) {
               lastPPSBeforeFirstDeclaration = current;
            } else {
               return Optional.ofNullable(lastPPSBeforeFirstDeclaration);
            }
         }
      }
      return Optional.ofNullable(lastPPSBeforeFirstDeclaration);
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
      return Arrays.stream(includeStatements).anyMatch((stmt) -> stmt.getName().toString().equals(header));
   }

   private static InsertEdit createInsertEdit(final Optional<IASTNode> lastNode, final String includeName, final boolean isSystemInclude,
         final boolean systemIncludesExist, final boolean userIncludesExist, final String separator) {
      StringBuffer includeStmt = getIncludeStatement(includeName, isSystemInclude);
      if (((isSystemInclude && !systemIncludesExist) || (!isSystemInclude && !userIncludesExist)) && lastNode.isPresent()) {
         includeStmt.insert(0, separator);
      }
      if (lastNode.isPresent()) {
         includeStmt.insert(0, separator);
      } else {
         includeStmt.append(separator);
      }
      if (!isSystemInclude && systemIncludesExist && !lastNode.isPresent()) {
         includeStmt.append(separator);
      }
      return new InsertEdit(getInsertOffset(lastNode), includeStmt.toString());
   }

   private static int getInsertOffset(final Optional<IASTNode> lastNode) {
      return OptionalUtil.returnIfPresentElse(lastNode, (node) -> node.getFileLocation().getNodeOffset() + node.getFileLocation().getNodeLength(), 0);
   }

   private static TextFileChange createTextFileChange(final IFile file, final TextEdit edit) {
      final TextFileChange change = new TextFileChange("Add Include", file);
      change.setSaveMode(TextFileChange.LEAVE_DIRTY);
      change.setEdit(edit);
      return change;
   }
}

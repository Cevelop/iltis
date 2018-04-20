package ch.hsr.ifs.iltis.cpp.core.includes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import ch.hsr.ifs.iltis.core.core.resources.FileUtil;
import ch.hsr.ifs.iltis.cpp.core.util.constants.CommonCPPConstants;


/**
 * A utility class which provides static methods for adding includes.
 * 
 * @author tstauber
 */
public class IncludeInsertionUtil {

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
    * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void insertUserIncludeIfNeeded(final IASTTranslationUnit ast, final String headerName) {
      includeIfNotJetIncluded(ast, headerName, false);
   }

   /**
    * Creates and performs a change which inserts a system include into the passed
    * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void insertSystemIncludeIfNeeded(final IASTTranslationUnit ast, final String headerName) {
      includeIfNotJetIncluded(ast, headerName, true);
   }

   /**
    * Creates and performs a change which inserts an include into the passed
    * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void includeIfNotJetIncluded(final IASTTranslationUnit ast, final String headerName, final boolean isSystemInclude) {
      includeIfNotJetIncluded(ast, headerName, isSystemInclude, TextFileChange.KEEP_SAVE_STATE);
   }

   private static List<IASTPreprocessorIncludeStatement> getLocalIncludeDirectives(final IASTTranslationUnit ast) {
      return Arrays.stream(ast.getIncludeDirectives()).filter(IASTNode::isPartOfTranslationUnitFile).collect(Collectors.toList());
   }

   /**
    * Creates and performs a change which inserts an include into the passed
    * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
    * 
    * @param textChangeSaveState
    *        Sets savestate of TextChange. Can be {@code TextFileChange.KEEP_SAVE_STATE}, {@code TextFileChange.FORCE_SAVE},
    *        {@code TextFileChange.LEAVE_DIRTY}
    *
    * @see #createIncludeIfNotJetIncluded(IASTTranslationUnit)
    */
   public static void includeIfNotJetIncluded(final IASTTranslationUnit ast, final String headerName, final boolean isSystemInclude,
         final int textChangeSaveState) {

      if (!isAlreadyIncluded(getLocalIncludeDirectives(ast), headerName)) {
         createIncludeIfNotJetIncluded(ast, headerName, isSystemInclude).ifPresent(change -> {
            try {
               change.setSaveMode(textChangeSaveState);
               change.perform(new NullProgressMonitor());
            } catch (CoreException e) {
               e.printStackTrace();
            }
         });
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
   public static Optional<TextFileChange> createIncludeIfNotJetIncluded(final IASTTranslationUnit ast, final String headerName,
         final boolean isSystemInclude) {
      final List<IASTPreprocessorIncludeStatement> includeStatements = getLocalIncludeDirectives(ast);
      boolean systemIncludesExist = includeStatements.stream().anyMatch(IASTPreprocessorIncludeStatement::isSystemInclude);
      boolean userIncludesExist = includeStatements.stream().anyMatch((stmt) -> !stmt.isSystemInclude());

      IFile file = ast.getOriginatingTranslationUnit().getFile();

      if (!isAlreadyIncluded(includeStatements, headerName)) {
         final Optional<IASTNode> lastNode = getNodeAfterWhichToInsertInclude(ast, includeStatements, isSystemInclude);
         return Optional.ofNullable(createTextFileChange(file, createInsertEdit(lastNode, headerName, isSystemInclude, systemIncludesExist,
               userIncludesExist, FileUtil.getLineSeparator(file))));
      } else {
         return Optional.empty();
      }

   }

   private static Optional<IASTNode> getNodeAfterWhichToInsertInclude(final IASTTranslationUnit ast,
         final List<IASTPreprocessorIncludeStatement> includeStatements, final boolean isSystemInclude) {
      if (includeStatements.size() <= 0) { return findPositionForNewIncludeOfThisType(ast); }
      Optional<IASTNode> lastIncludeStatementOfType = findLastIncludeStatementOfType(ast, includeStatements, isSystemInclude);
      return lastIncludeStatementOfType.isPresent() ? lastIncludeStatementOfType : findPositionForNewIncludeOfThisType(ast);
   }

   private static Optional<IASTNode> findLastIncludeStatementOfType(final IASTTranslationUnit ast,
         final List<IASTPreprocessorIncludeStatement> includeStatements, final boolean isSystemInclude) {
      return Optional.ofNullable(includeStatements.stream().reduce(null, (prev, next) -> next.isSystemInclude() && !isSystemInclude ? prev : next));
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
   private static boolean isAlreadyIncluded(final List<IASTPreprocessorIncludeStatement> includeStatements, final String header) {
      return includeStatements.stream().anyMatch((stmt) -> stmt.getName().toString().equals(header));
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
      return lastNode.map(node -> node.getFileLocation().getNodeOffset() + node.getFileLocation().getNodeLength()).orElse(0);
   }

   private static TextFileChange createTextFileChange(final IFile file, final TextEdit edit) {
      final TextFileChange change = new TextFileChange("Add Include", file);
      change.setSaveMode(TextFileChange.LEAVE_DIRTY);
      change.setEdit(edit);
      return change;
   }
}

package ch.hsr.ifs.iltis.cpp.core.includes;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import ch.hsr.ifs.iltis.core.core.resources.FileUtil;
import ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorScope;
import ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorStatementUtil;
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

   //TODO add method for adding an include in a specific scope

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
      includeIfNotJetIncluded(ast, headerName, isSystemInclude, textChangeSaveState, new NullProgressMonitor());
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
         final int textChangeSaveState, final IProgressMonitor pm) {

      createIncludeIfNotJetIncluded(ast, headerName, isSystemInclude, PreprocessorScope.createFrom(ast.getAllPreprocessorStatements())).ifPresent(
            change -> {
               try {
                  change.setSaveMode(textChangeSaveState);
                  change.perform(pm);
               } catch (CoreException e) {
                  e.printStackTrace();
               }
            });
   }

   /**
    * Creates and returns a TextFileChange to insert an include into the passed translation unit. The caller must provide the include name and the
    * information if it is a system include or a user include.
    * 
    * <pre>
    * An include name can be something like {@code vector} or {@code foo.h}
    * </pre>
    * 
    * @param scope
    *        For better performance, the PreprocessorScope tree should be cached if multiple operations are executed.
    *
    * @returns The {@link TextFileChange} or {@code null} if already included
    */
   public static Optional<TextFileChange> createIncludeIfNotJetIncluded(final IASTTranslationUnit ast, final String headerName,
         final boolean isSystemInclude, PreprocessorScope scope) {

      if (isAlreadyIncluded(scope, headerName)) return Optional.empty();

      Optional<? extends IASTPreprocessorStatement> previousStatement = scope.findStmtAfterWhichToAddInclude(headerName, isSystemInclude, ast);

      IFile file = ast.getOriginatingTranslationUnit().getFile();
      final TextFileChange change = new TextFileChange("Add Include " + headerName, file);
      change.setSaveMode(TextFileChange.LEAVE_DIRTY);
      change.setEdit(new MultiTextEdit());

      String lineSep = FileUtil.getLineSeparator(file);
      
      StringBuffer includeStmt = getIncludeStatement(headerName, isSystemInclude);
      includeStmt.append(lineSep);
      
      int offset = 0;

      if (previousStatement.isPresent()) {
         IASTPreprocessorStatement prevStmt = previousStatement.get();
         offset = PreprocessorStatementUtil.getOffsetToInsertAfter(previousStatement);

         if (prevStmt instanceof IASTPreprocessorIncludeStatement && ((IASTPreprocessorIncludeStatement) prevStmt)
               .isSystemInclude() != isSystemInclude) {
            includeStmt.insert(0, lineSep);
         }

         Optional<IASTPreprocessorStatement> nextStatement = Lists.immutable.of(ast.getAllPreprocessorStatements()).dropWhile(s -> s != prevStmt)
               .drop(1).getFirstOptional();

         if (nextStatement.isPresent()) {
            IASTPreprocessorStatement nextStmt = nextStatement.get();

            if (!(nextStmt instanceof IASTPreprocessorIncludeStatement && ((IASTPreprocessorIncludeStatement) nextStmt)
                  .isSystemInclude() == isSystemInclude)) {
               includeStmt.append(lineSep);
            }
         }
      } else {
         offset = 0;
         includeStmt.append(lineSep);
      }

      change.addEdit(new InsertEdit(offset, includeStmt.toString()));

      return Optional.of(change);
   }

   /**
    * Returns if the given header-file is already included in a influencing scope. This
    * function is not able to determine if the include is indirectly included.
    *
    * @return If the {@link IASTPreprocessorIncludeStatement} {@code header} is
    *         already included.
    */
   private static boolean isAlreadyIncluded(final PreprocessorScope scope, final String header) {
      return scope.streamUp().anyMatch(s -> s.getIncludeDirectives().anySatisfy((stmt) -> stmt.getName().toString().equals(header)));
   }

}

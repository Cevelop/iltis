package ch.hsr.ifs.iltis.cpp.core.includes;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.lazy.CompositeIterable;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MoveSourceEdit;
import org.eclipse.text.edits.MoveTargetEdit;
import org.eclipse.text.edits.MultiTextEdit;

import ch.hsr.ifs.iltis.core.core.collections.CollectionUtil;
import ch.hsr.ifs.iltis.core.core.resources.FileUtil;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.ITranslationUnitUtil;
import ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorScope;
import ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorStatementUtil;


/**
 * A utility class which provides static methods for reordering includes.
 * 
 * @author tstauber
 */
public class IncludeReorderUtil {

   /**
    * Creates and performs a change reordering the includes in the passed
    * {@link IASTTranslationUnit}.
    *
    */
   public static void reorderIncludeStatements(final IASTTranslationUnit ast) {
      reorderIncludeStatements(ast, TextFileChange.KEEP_SAVE_STATE, new NullProgressMonitor());
   }

   /**
    * Creates and performs a change reordering the includes in the passed
    * {@link IASTTranslationUnit}.
    * 
    * @param textChangeSaveState
    *        Sets savestate of TextChange. Can be {@code TextFileChange.KEEP_SAVE_STATE}, {@code TextFileChange.FORCE_SAVE},
    *        {@code TextFileChange.LEAVE_DIRTY}
    *
    */
   public static void reorderIncludeStatements(final IASTTranslationUnit ast, final int textChangeSaveState) {
      reorderIncludeStatements(ast, textChangeSaveState, new NullProgressMonitor());
   }

   /**
    * Creates and performs a change reordering the includes in the passed
    * {@link IASTTranslationUnit}.
    * 
    * @param textChangeSaveState
    *        Sets savestate of TextChange. Can be {@code TextFileChange.KEEP_SAVE_STATE}, {@code TextFileChange.FORCE_SAVE},
    *        {@code TextFileChange.LEAVE_DIRTY}
    *
    */
   public static void reorderIncludeStatements(final IASTTranslationUnit ast, final int textChangeSaveState, IProgressMonitor pm) {
      createReorderIncludeStatements(ast, PreprocessorScope.createFrom(ast)).ifPresent(change -> {
         try {
            change.setSaveMode(textChangeSaveState);
            change.perform(pm);
         } catch (CoreException e) {
            e.printStackTrace();
         }
      });
   }

   /**
    * Reorders all includes in this translation unit and adds them where the first include would be placed in this document.
    * 
    * @param ast
    *        The ast translation unit
    * @param rootScope
    *        For better performance, the PreprocessorScope tree should be cached if multiple operations are executed.
    * @return A TextFileChange to execute or an empty Optional.
    */
   public static Optional<TextFileChange> createReorderIncludeStatements(final IASTTranslationUnit ast, PreprocessorScope rootScope) {

      IFile file = ast.getOriginatingTranslationUnit().getFile();

      final TextFileChange change = new TextFileChange("Reorder Includes", file);
      MultiTextEdit multiEdit = new MultiTextEdit();
      change.setEdit(multiEdit);

      MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap = ITranslationUnitUtil.createLinenoOffsetContentMap(ast
            .getOriginatingTranslationUnit());

      rootScope.forEachDownWith((s, c) -> IncludeReorderUtil.createMoveEdits(s, c, FileUtil.getLineSeparator(file), linenoOffsetContentMap), change);

      return multiEdit.hasChildren() ? Optional.of(change) : Optional.empty();
   }

   @SuppressWarnings("unchecked")
   private static void createMoveEdits(PreprocessorScope scope, TextFileChange change, String lineSep,
         MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
      MutableList<IASTPreprocessorIncludeStatement> includeDirectives = scope.getIncludeDirectives();
      if (includeDirectives.isEmpty()) return;

      Optional<IASTPreprocessorStatement> previousStatement = scope.findLastNonScopeNonIncludeStatementBeforeSubScopes();

      int insertOffset = PreprocessorStatementUtil.getOffsetToInsertAfter(previousStatement, linenoOffsetContentMap);

      if (previousStatement.isPresent()) {
         if (ITranslationUnitUtil.isFollowedByAWhitespaceLine(previousStatement.get(), linenoOffsetContentMap)) {
            insertOffset = ITranslationUnitUtil.getOffsetOfNextLine(previousStatement.get().getFileLocation().getEndingLineNumber() + 1,
                  linenoOffsetContentMap);
         } else {
            change.addEdit(new InsertEdit(insertOffset, lineSep));
         }
      }

      MutableList<IASTPreprocessorIncludeStatement> userIncludesSorted = includeDirectives.reject(IASTPreprocessorIncludeStatement::isSystemInclude)
            .sortThis(PreprocessorStatementUtil::compareIncludes);
      MutableList<IASTPreprocessorIncludeStatement> systemIncludesSorted = includeDirectives.select(IASTPreprocessorIncludeStatement::isSystemInclude)
            .sortThis(PreprocessorStatementUtil::compareIncludes);

      /* Check if includes are already ordered */
      CompositeIterable<IASTPreprocessorIncludeStatement> combinedIterable = CompositeIterable.with(userIncludesSorted, systemIncludesSorted);
      if (CollectionUtil.haveSameElementsInSameOrder(combinedIterable, includeDirectives)) {
         userIncludesSorted.getLastOptional().ifPresent(s -> {
            insertEmptyLineAfterNodeIfNeccessary(change, lineSep, linenoOffsetContentMap, s);
         });
         systemIncludesSorted.getLastOptional().ifPresent(s -> {
            insertEmptyLineAfterNodeIfNeccessary(change, lineSep, linenoOffsetContentMap, s);
         });
         return;
      }

      /* Move user includes in order to the destination */
      userIncludesSorted.forEachWith((it, offset) -> {
         IASTFileLocation fileLocation = it.getFileLocation();
         MoveSourceEdit sourceEdit = new MoveSourceEdit(fileLocation.getNodeOffset(), fileLocation.getNodeLength() + lineSep.length());
         change.addEdit(sourceEdit);
         change.addEdit(new MoveTargetEdit(offset, sourceEdit));
         if (ITranslationUnitUtil.isFollowedByAWhitespaceLine(it, linenoOffsetContentMap)) {
            ITranslationUnitUtil.getOffsetAndLenghtOfLine(fileLocation.getEndingLineNumber(), linenoOffsetContentMap).ifPresent(p -> change.addEdit(
                  new DeleteEdit(p.getOne(), p.getTwo())));
         }
      }, insertOffset);

      /* Insert empty line if necessary */
      if (userIncludesSorted.size() > 0 && systemIncludesSorted.size() > 0) change.addEdit(new InsertEdit(insertOffset, lineSep));

      /* Move system includes in order to the destination */
      systemIncludesSorted.forEachWith((it, offset) -> {
         MoveSourceEdit sourceEdit = new MoveSourceEdit(it.getFileLocation().getNodeOffset(), it.getFileLocation().getNodeLength() + lineSep
               .length());
         change.addEdit(sourceEdit);
         change.addEdit(new MoveTargetEdit(offset, sourceEdit));
         if (ITranslationUnitUtil.isFollowedByAWhitespaceLine(it, linenoOffsetContentMap)) {
            ITranslationUnitUtil.getOffsetAndLenghtOfLine(it.getFileLocation().getEndingLineNumber(), linenoOffsetContentMap).ifPresent(p -> change
                  .addEdit(new DeleteEdit(p.getOne(), p.getTwo())));
         }
      }, insertOffset);

      if (systemIncludesSorted.size() > 0 || userIncludesSorted.size() > 0) change.addEdit(new InsertEdit(insertOffset, lineSep));
   }

   private static void insertEmptyLineAfterNodeIfNeccessary(TextFileChange change, String lineSep,
         MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap, IASTPreprocessorIncludeStatement s) {
      if (!ITranslationUnitUtil.isFollowedByAWhitespaceLine(s, linenoOffsetContentMap)) {
         insertEmptyLineAfterNode(s, change, lineSep, linenoOffsetContentMap);
      }
   }

   private static void insertEmptyLineAfterNode(IASTPreprocessorIncludeStatement s, TextFileChange change, String lineSep,
         MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
      change.addEdit(new InsertEdit(PreprocessorStatementUtil.getOffsetToInsertAfter(Optional.of(s), linenoOffsetContentMap), lineSep));
   }

}

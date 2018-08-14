package ch.hsr.ifs.iltis.cpp.core.includes;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Iterables;
import org.eclipse.collections.impl.lazy.CompositeIterable;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MoveSourceEdit;
import org.eclipse.text.edits.MoveTargetEdit;
import org.eclipse.text.edits.MultiTextEdit;

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

      rootScope.forEachDownWith((s, c) -> IncludeReorderUtil.createMoveEdits(s, c, linenoOffsetContentMap), change);

      return multiEdit.hasChildren() ? Optional.of(change) : Optional.empty();
   }

   @SuppressWarnings("unchecked")
   private static void createMoveEdits(PreprocessorScope scope, TextFileChange change,
         MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
      MutableList<IASTPreprocessorIncludeStatement> includeDirectives = scope.getIncludeDirectives();
      if (includeDirectives.isEmpty()) return;

      int insertOffset = PreprocessorStatementUtil.getOffsetToInsertAfter(scope.findLastNonScopeNonIncludeStatementBeforeSubScopes(),
            linenoOffsetContentMap);

      MutableList<IASTPreprocessorIncludeStatement> userIncludesSorted = includeDirectives.reject(IASTPreprocessorIncludeStatement::isSystemInclude)
            .sortThis(PreprocessorStatementUtil::compareIncludes);
      MutableList<IASTPreprocessorIncludeStatement> systemIncludesSorted = includeDirectives.select(IASTPreprocessorIncludeStatement::isSystemInclude)
            .sortThis(PreprocessorStatementUtil::compareIncludes);

      /* Check if includes are already ordered */
      if (CompositeIterable.with(userIncludesSorted, systemIncludesSorted).zip(Iterables.iList(includeDirectives)).allSatisfy(p -> p.getOne() == p
            .getTwo())) return;

      String lineSep = FileUtil.getLineSeparator(includeDirectives.getFirst().getTranslationUnit().getOriginatingTranslationUnit().getFile());

      /* Move user includes in order to the destination */
      userIncludesSorted.forEach(it -> {
         MoveSourceEdit sourceEdit = new MoveSourceEdit(it.getFileLocation().getNodeOffset(), it.getFileLocation().getNodeLength() + lineSep
               .length());
         change.addEdit(sourceEdit);
         change.addEdit(new MoveTargetEdit(insertOffset, sourceEdit));
      });

      /* Insert empty line if necessary */
      if (userIncludesSorted.size() > 0 && systemIncludesSorted.size() > 0) change.addEdit(new InsertEdit(insertOffset, lineSep));

      /* Move system includes in order to the destination */
      systemIncludesSorted.forEach(it -> {
         MoveSourceEdit sourceEdit = new MoveSourceEdit(it.getFileLocation().getNodeOffset(), it.getFileLocation().getNodeLength() + lineSep
               .length());
         change.addEdit(sourceEdit);
         change.addEdit(new MoveTargetEdit(insertOffset, sourceEdit));
      });
   }

}

package ch.hsr.ifs.iltis.cpp.core.includes;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Iterables;
import org.eclipse.collections.impl.lazy.CompositeIterable;
import org.eclipse.collections.impl.utility.ArrayIterate;
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
      createReorderIncludeStatements(ast).ifPresent(change -> {
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
    * @return A TextFileChange to execute or an empty Optional.
    */
   @SuppressWarnings("unchecked")
   public static Optional<TextFileChange> createReorderIncludeStatements(final IASTTranslationUnit ast) {

      IASTPreprocessorIncludeStatement[] includeDirectives = ast.getIncludeDirectives();
      if (includeDirectives.length == 0) return Optional.empty();

      IFile file = ast.getOriginatingTranslationUnit().getFile();

      final TextFileChange change = new TextFileChange("Reorder Includes", file);
      change.setSaveMode(TextFileChange.LEAVE_DIRTY);
      change.setEdit(new MultiTextEdit());

      int insertOffset = findNodeBeforeIncludes(ast).map(n -> n.getFileLocation().getNodeOffset()).orElse(0);

      MutableList<IASTPreprocessorIncludeStatement> userIncludesSorted = ArrayIterate.reject(includeDirectives,
            IASTPreprocessorIncludeStatement::isSystemInclude).sortThis(IncludeReorderUtil::compareTo);
      MutableList<IASTPreprocessorIncludeStatement> systemIncludesSorted = ArrayIterate.select(includeDirectives,
            IASTPreprocessorIncludeStatement::isSystemInclude).sortThis(IncludeReorderUtil::compareTo);

      /* Check if includes are already ordered */
      if (CompositeIterable.with(userIncludesSorted, systemIncludesSorted).zip(Iterables.iList(includeDirectives)).allSatisfy(p -> p.getOne() == p
            .getTwo())) return Optional.empty();

      /* Move user includes in order to the destination */
      userIncludesSorted.forEach(it -> {
         MoveSourceEdit sourceEdit = new MoveSourceEdit(it.getFileLocation().getNodeOffset(), it.getFileLocation().getNodeLength() + 1);
         change.addEdit(sourceEdit);
         change.addEdit(new MoveTargetEdit(insertOffset, sourceEdit));
      });

      /* Insert empty line if necessary */
      if (userIncludesSorted.size() > 0 && systemIncludesSorted.size() > 0) change.addEdit(new InsertEdit(insertOffset, FileUtil.getLineSeparator(
            file)));

      /* Move system includes in order to the destination */
      systemIncludesSorted.forEach(it -> {
         MoveSourceEdit sourceEdit = new MoveSourceEdit(it.getFileLocation().getNodeOffset(), it.getFileLocation().getNodeLength() + 1);
         change.addEdit(sourceEdit);
         change.addEdit(new MoveTargetEdit(insertOffset, sourceEdit));
      });

      return Optional.of(change);
   }

   private static int compareTo(IASTPreprocessorIncludeStatement l, IASTPreprocessorIncludeStatement r) {
      return l.getName().toString().compareTo(r.getName().toString());
   }

   private static Optional<? extends IASTNode> findNodeBeforeIncludes(final IASTTranslationUnit ast) {
      int firstDeclarationOffset = ArrayIterate.take(ast.getDeclarations(true), 1).getFirstOptional().map(n -> n.getFileLocation().getNodeOffset())
            .orElse(-1);

      return ArrayIterate.reject(ast.getAllPreprocessorStatements(), IASTPreprocessorStatement.class::isInstance).takeWhile(ppStmt -> ppStmt
            .getFileLocation().getNodeOffset() < firstDeclarationOffset).getLastOptional();
   }

}

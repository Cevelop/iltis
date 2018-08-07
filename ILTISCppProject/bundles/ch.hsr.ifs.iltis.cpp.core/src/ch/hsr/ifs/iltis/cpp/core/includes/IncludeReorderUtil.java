package ch.hsr.ifs.iltis.cpp.core.includes;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MoveSourceEdit;
import org.eclipse.text.edits.MoveTargetEdit;
import org.eclipse.text.edits.MultiTextEdit;


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
    * @param textChangeSaveState
    *        Sets savestate of TextChange. Can be {@code TextFileChange.KEEP_SAVE_STATE}, {@code TextFileChange.FORCE_SAVE},
    *        {@code TextFileChange.LEAVE_DIRTY}
    *
    */
   public static void reorderIncludeStatement(final IASTTranslationUnit ast, final int textChangeSaveState) {
      reorderIncludeStatement(ast, textChangeSaveState, new NullProgressMonitor());
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
   public static void reorderIncludeStatement(final IASTTranslationUnit ast, final int textChangeSaveState, IProgressMonitor pm) {
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
   public static Optional<TextFileChange> createReorderIncludeStatements(final IASTTranslationUnit ast) {

      //TODO add check if reordering is needed.

      IASTPreprocessorIncludeStatement[] includeDirectives = ast.getIncludeDirectives();
      if (includeDirectives.length == 0) return Optional.empty();

      final TextFileChange change = new TextFileChange("Reorder Includes", ast.getOriginatingTranslationUnit().getFile());
      change.setSaveMode(TextFileChange.LEAVE_DIRTY);
      change.setEdit(new MultiTextEdit());

      int insertOffset = findPositionToPutReorderedIncludes(ast).map(n -> n.getFileLocation().getNodeOffset()).orElse(0);
      ArrayIterate.select(includeDirectives, IASTPreprocessorIncludeStatement::isSystemInclude).sortThis(IncludeReorderUtil::compareTo)
            .reverseForEach(it -> {
               MoveSourceEdit sourceEdit = new MoveSourceEdit(it.getFileLocation().getNodeOffset(), it.getFileLocation().getNodeLength());
               change.addEdit(sourceEdit);
               change.addEdit(new MoveTargetEdit(insertOffset, sourceEdit));
            });

      //TODO insert empty-line if there are system includes 

      ArrayIterate.reject(includeDirectives, IASTPreprocessorIncludeStatement::isSystemInclude).sortThis(IncludeReorderUtil::compareTo)
            .reverseForEach(it -> {
               MoveSourceEdit sourceEdit = new MoveSourceEdit(it.getFileLocation().getNodeOffset(), it.getFileLocation().getNodeLength());
               change.addEdit(sourceEdit);
               change.addEdit(new MoveTargetEdit(insertOffset, sourceEdit));
            });

      return Optional.of(change);
   }

   private static int compareTo(IASTPreprocessorIncludeStatement l, IASTPreprocessorIncludeStatement r) {
      return l.getName().toString().compareTo(r.getName().toString());
   }

   private static Optional<? extends IASTNode> findPositionToPutReorderedIncludes(final IASTTranslationUnit ast) {
      int firstDeclarationOffset = ArrayIterate.take(ast.getDeclarations(true), 1).getFirstOptional().map(n -> n.getFileLocation().getNodeOffset())
            .orElse(-1);

      return ArrayIterate.reject(ast.getAllPreprocessorStatements(), IASTPreprocessorStatement.class::isInstance).takeWhile(ppStmt -> ppStmt
            .getFileLocation().getNodeOffset() < firstDeclarationOffset).getLastOptional();
   }

}

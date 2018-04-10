package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ISelection;


/**
 * A wrapper class for the cdt SelectionHelper. Using this wrapper reduces the amount of warnings respectively the amount of {@code @SuppressWarnings}
 * tags
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
@ILTISWrapper(org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.class)
public class SelectionHelper extends org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper {

   public static Region getRegion(final ISelection selection) {
      return org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.getRegion(selection);
   }

   protected static IRegion getNodeSpan(final IASTNode region) {
      return org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.getNodeSpan(region);
   }

   public static IASTSimpleDeclaration findFirstSelectedDeclaration(final IRegion textSelection, final IASTTranslationUnit translationUnit) {
      return org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.findFirstSelectedDeclaration(textSelection, translationUnit);
   }

   public static boolean doesNodeOverlapWithRegion(final IASTNode node, final IRegion region) {
      return org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.doesNodeOverlapWithRegion(node, region);
   }

   public static boolean isNodeInsideRegion(final IASTNode node, final IRegion region) {
      return org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.isNodeInsideRegion(node, region);
   }

   public static boolean isNodeInsideSelection(final IASTNode node, final IRegion selection) {
      return node.isPartOfTranslationUnitFile() && isNodeInsideRegion(node, selection);
   }

   public static boolean isSelectionInsideNode(final IASTNode node, final IRegion selection) {
      return org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.isSelectionInsideNode(node, selection);
   }

   public static boolean nodeMatchesSelection(final IASTNode node, final IRegion region) {
      return org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.nodeMatchesSelection(node, region);
   }

}

package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange;
import org.eclipse.cdt.internal.ui.refactoring.changes.CreateFileChange;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextChange;


/**
 * Wrapper for ModificationCollector. For duck-typing reasons and the fact, that
 * ModificationCollector does not implement an interface, this class extends
 * ModificationCollector.
 *
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public class ModificationCollector extends org.eclipse.cdt.internal.ui.refactoring.ModificationCollector {

   protected CompositeChange addedChanges = new CompositeChange("");

   /**
    * {@inheritDoc}
    */
   public ModificationCollector() {
      super(null);
   }

   /**
    * {@inheritDoc}
    */
   public ModificationCollector(final IResourceChangeDescriptionFactory deltaFactory) {
      super(deltaFactory);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ASTRewrite rewriterForTranslationUnit(final IASTTranslationUnit ast) {
      return super.rewriterForTranslationUnit(ast);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addFileChange(final CreateFileChange change) {
      super.addFileChange(change);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CCompositeChange createFinalChange() {
      final CCompositeChange cchange = super.createFinalChange();
      cchange.merge(addedChanges);
      return cchange;
   }

   /**
    * Allows to add changes that will be merged into the change created by
    * {@link #createFinalChange()}
    *
    * @param change
    *        The {@link TextChange} to merge.
    */
   public void addChange(final TextChange change) {
      addedChanges.add(change);
   }

}

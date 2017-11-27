package ch.hsr.ifs.iltis.cpp.wrappers;

import java.util.List;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.core.runtime.CoreException;


@SuppressWarnings("restriction")
public class IndexToASTNameHelper extends org.eclipse.cdt.internal.ui.refactoring.IndexToASTNameHelper {

   public static List<IASTName> findNamesIn(final IASTTranslationUnit tu, final IBinding binding, final IIndex index) {
      return org.eclipse.cdt.internal.ui.refactoring.IndexToASTNameHelper.findNamesIn(tu, binding, index);
   }

   public static IASTName findMatchingASTName(final IASTTranslationUnit tu, final IName name, final IIndex index) throws CoreException {
      return org.eclipse.cdt.internal.ui.refactoring.IndexToASTNameHelper.findMatchingASTName(tu, name, index);
   }
}

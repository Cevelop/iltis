package ch.hsr.ifs.iltis.cpp.wrappers;

import java.util.Map;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoring;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * @author tstauber
 */
@SuppressWarnings("restriction")
public abstract class CRefactoringDescriptor extends org.eclipse.cdt.internal.ui.refactoring.CRefactoringDescriptor {
  public static final String FILE_NAME = ".fileName"; //$NON-NLS-1$
  public static final String SELECTION = ".selection"; //$NON-NLS-1$

  /**
   * {@inheritDoc}
   */
  public CRefactoringDescriptor(final String id, final String project, final String description, final String comment,
      final int flags, final Map<String, String> arguments) {
    super(id, project, description, comment, flags, arguments);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getParameterMap() {
    return super.getParameterMap();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract CRefactoring createRefactoring(RefactoringStatus status) throws CoreException;

  /**
   * {@inheritDoc}
   */
  @Override
  public CRefactoringContext createRefactoringContext(final RefactoringStatus status) throws CoreException {
    return super.createRefactoringContext(status);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ISelection getSelection() throws CoreException {
    return super.getSelection();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ICProject getCProject() throws CoreException {
    return super.getCProject();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IFile getFile() throws CoreException {
    return super.getFile();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ITranslationUnit getTranslationUnit() throws CoreException {
    return super.getTranslationUnit();
  }
}

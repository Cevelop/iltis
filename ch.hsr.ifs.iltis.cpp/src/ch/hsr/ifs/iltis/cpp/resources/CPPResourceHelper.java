package ch.hsr.ifs.iltis.cpp.resources;

import java.net.URI;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * Helps converting resources or extracting resources from other elements.
 *
 * @author tstauber
 */
public abstract class CPPResourceHelper {

   /**
    * Extracts the {@link IProject} from a {@link IASTNode}
    *
    * @param An
    *            {@link IASTNode} that is part of an {@link ITranslationUnit}
    * @return The {@link IProject} of which the node's translation unit is part
    *         of, or null if the node is not part of a file.
    */
   public static IProject getProject(final IASTNode node) {
      final IASTNode original = node.getOriginalNode();
      final ITranslationUnit tu = original.getTranslationUnit().getOriginatingTranslationUnit();
      final IResource res = tu.getUnderlyingResource();
      if (res == null) {
         return null;
      } else {
         return res.getProject();
      }
   }

   public static ICProject getCProject(final IFile file) {
      return CoreModel.getDefault().create(file).getCProject();
   }

   public static ICProject getCProject(final IProject project) {
      return CoreModel.getDefault().getCModel().getCProject(project.getName());
   }

   public static boolean isPartOfProject(final URI fileUri, final IProject project) {
      return project.getLocation().isPrefixOf(new Path(fileUri.getPath()));
   }

   public static IWorkspaceRoot getWorkspaceRoot() {
      return ResourcesPlugin.getWorkspace().getRoot();
   }

}

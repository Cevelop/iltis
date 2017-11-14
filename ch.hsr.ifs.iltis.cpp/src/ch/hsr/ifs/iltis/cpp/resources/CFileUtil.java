package ch.hsr.ifs.iltis.cpp.resources;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.iltis.core.resources.FileUtil;
import ch.hsr.ifs.iltis.core.resources.ProjectUtil;


public abstract class CFileUtil extends FileUtil {

   //DOC missing
   public static IFile getFile(final IASTNode node) {
      return ProjectUtil.getWorkspaceRoot().getFileForLocation(new Path(node.getFileLocation().getFileName()));
   }

   //DOC missing
   public static IASTFileLocation getNodeFileLocation(final IASTNode node) {
      return node.getFileLocation() != null ? node.getFileLocation() : getNodeFileLocation(node.getParent());
   }

}

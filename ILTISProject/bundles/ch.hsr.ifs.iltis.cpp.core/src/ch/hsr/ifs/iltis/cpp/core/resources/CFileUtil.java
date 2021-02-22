package ch.hsr.ifs.iltis.cpp.core.resources;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.iltis.core.resources.FileUtil;
import ch.hsr.ifs.iltis.core.resources.WorkspaceUtil;


/**
 * A utility class which offers static methods which use cdt.
 * Should be used in combination with {@link FileUtil}.
 *
 * @author tstauber
 */
public abstract class CFileUtil {

    /**
     * Used to get the file in which node was defined
     *
     * @return the IFile in which node was defined, or null, if the node was not created from a file.
     */
    public static IFile getFile(final IASTNode node) {
        final IASTFileLocation location = getNodeFileLocation(node);
        return location == null ? null : WorkspaceUtil.getWorkspaceRoot().getFileForLocation(new Path(location.getFileName()));
    }

    /**
     * Used to get the {@code IASTFileLocation} from a {@code IASTNode}
     *
     * @return the IASTFileLocation if node was created from a file, else null
     */
    public static IASTFileLocation getNodeFileLocation(final IASTNode node) {
        if (node == null) return null;
        return node.getFileLocation() != null ? node.getFileLocation() : getNodeFileLocation(node.getParent());
    }

}

package ch.hsr.ifs.iltis.core.core.resources;

import java.io.File;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.internal.resources.ICoreConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.core.resources.IOUtil;
import ch.hsr.ifs.iltis.core.core.resources.WorkspaceUtil;


/**
 * A utility class providing static methods to handle files. For I/O functionality please
 * refer to {@link IOUtil}
 * 
 * <p><b>See Also</b><br>
 * &emsp;&emsp;&emsp;ch.hsr.ifs.iltis.cpp.core.resources.CFileUtil</p>
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("unused")
public abstract class FileUtil {

   /* IFile */

   /**
    * Used to obtain the IFile corresponding to a Java File
    * 
    * @param file
    *        The original Java {@code File}
    * @return the IFile or null if non existent
    */
   public static IFile toIFile(final File file) {
      return toIFile(file.toURI());
   }

   /**
    * Used to obtain the IFile for a file path
    * 
    * @param filePath
    *        The file path as a {@code String}
    * @return the IFile or null if non existent
    */
   public static IFile toIFile(final String filePath) {
      return toIFile(new File(filePath));
   }

   /**
    * Used to obtain the IFile for a file path
    * 
    * @param filePath
    *        The files file {@code IPath}
    * @return the IFile or null if non existent
    */
   public static IFile toIFile(final IPath filePath) {
      return WorkspaceUtil.getWorkspaceRoot().getFile(filePath);
   }

   /**
    * Used to obtain the IFile corresponding to a URI
    * 
    * @param locationURI
    *        The location {@code URI} of the file
    * @return the IFile or null if non existent
    */
   public static IFile getIFile(final URI locationURI) {
      final IFile[] files = WorkspaceUtil.getWorkspaceRoot().findFilesForLocationURI(locationURI);

      if (files.length == 1) { return files[0]; }

      for (final IFile curfile : files) {
         if (locationURI.getPath().endsWith(curfile.getFullPath().toOSString())) { return curfile; }
      }

      return null;
   }

   /**
    * Used to create the IFile corresponding to a URI
    * 
    * @param locationURI
    *        The files location {@code URI}
    * @return the IFile
    */
   public static IFile toIFile(final URI locationURI) {
      IWorkspaceRoot wsRoot = WorkspaceUtil.getWorkspaceRoot();
      URI relativeURI = wsRoot.getLocationURI().relativize(locationURI);
      return wsRoot.getFile(Path.fromOSString(relativeURI.getPath()));
   }

   /**
    * Used to obtain the File corresponding to a IFile
    * 
    * @param file
    *        The original {@code IFile}
    * 
    * @return the File
    */
   public static File toFile(final IFile file) {
      return toFile(file.getLocation());
   }

   /**
    * Used to obtain the File corresponding to a filePath
    * 
    * @param filePath
    *        The original files {@code IPath}
    * 
    * @return the File
    */
   public static File toFile(final IPath filePath) {
      return filePath.toFile();
   }

   /**
    * Used to obtain the File corresponding to a URI
    * 
    * @param fileURI
    *        The original files {@code URI}
    * 
    * @return the File
    */
   private static File toFile(final URI fileURI) {
      return toFile(toIFile(fileURI));
   }

   /* Other */

   /**
    * Used to strip the file extension form a file name
    * 
    * @param filename
    *        The original file name as a {@code String}
    * 
    * @return the file name without extension
    */
   public static String getFilenameWithoutExtension(final String filename) {
      final int begin = filename.lastIndexOf(File.separator) + 1;
      int end = filename.lastIndexOf('.');
      if (end < 0) {
         end = filename.length();
      }
      return filename.substring(begin, end);
   }

   /**
    * Used to obtain filename with extension from a file path
    * {@code FileUtil.getFilename("/a/b/c/foo.h") -> "foo.h" }
    * 
    * @param filePath
    *        The original file path as a {@code String}
    * 
    * @throws ILTISException
    *         (unchecked) if invalid path is passed.
    * @return the filename with extension
    */
   public static String getFilename(final String filePath) {
      final Path path = new Path(filePath);
      ILTISException.Unless.isTrue("Path elements must not be empty", path.segmentCount() > 0);
      return path.segment(path.segmentCount() - 1);
   }

   /**
    * Used to obtain the file path without the filename and file extension.
    * {@code FileUtil.removeFilePart("/a/b/c/foo.h") -> "/a/b/c/" }
    * 
    * @param filePath
    *        The original file path as a {@code String}
    * 
    * @return The path without filename and extension
    */
   public static String getPathWithoutFilename(final String filePath) {
      return filePath.replaceAll("(\\w)*\\.(\\w)*", "");
   }

   /**
    * Used to obtain the the path of the folder which contains this IFile
    * 
    * @param file
    *        The original {@code IFile}
    * 
    * @return The path of the folder containing this file relative to the workspace
    */
   public static IPath getFolderPath(final IFile file) {
      return file.getParent().getFullPath();
   }

   /**
    * Used to obtain the URI from a file path
    * 
    * @param filePath
    *        The file path for which the {@code URI} should be created
    * @return the URI corresponding to the path
    */
   public static URI stringToUri(final String filePath) {
      return new File(filePath).toURI();
   }

   /**
    * Returns all those resources which have one of the file extensions specified in fileExtensions.
    *
    * @param resources
    *        The {@code IResource}s
    * @param fileExtensions
    *        The file extensions (as {@code String}s)
    * @return The filtered set of {@code IResource}s
    */
   public static Set<IResource> filterExtensions(final Set<IResource> resources, final Set<String> fileExtensions) {
      final Set<IResource> filtered = new LinkedHashSet<>();
      for (final IResource resource : resources) {
         if (fileExtensions.contains(resource.getFileExtension())) {
            filtered.add(resource);
         }
      }
      return filtered;
   }

   /**
    * Converts an IPath into its canonical form for the local file system.
    * 
    * @param path
    *        The original {@code IPath}
    * 
    * @return the canonical {@code IPath}
    */
   public static IPath canonicalPath(IPath path) {
      return org.eclipse.core.internal.utils.FileUtil.canonicalPath(path);
   }

   /**
    * For a path on a case-insensitive file system returns the path with the actual
    * case as it exists in the file system. If only a prefix of the path exists on
    * the file system, the case of remaining part of the returned path is the same
    * as in the original path. For a case-sensitive file system returns the original
    * path.
    * <p>
    * This method is similar to java.nio.file.Path.toRealPath(LinkOption.NOFOLLOW_LINKS)
    * in Java 1.7.
    * 
    * @param path
    *        The original {@code IPath}
    * 
    * @return the real {@code IPath}
    */
   public static IPath realPath(IPath path) {
      return org.eclipse.core.internal.utils.FileUtil.realPath(path);
   }

   /**
    * Converts a URI into its canonical form.
    * 
    * @param uri
    *        The original {@code URI}
    * 
    * @return the canonical {@code URI}
    */
   public static URI canonicalURI(URI uri) {
      return org.eclipse.core.internal.utils.FileUtil.canonicalURI(uri);
   }

   /**
    * Converts a URI by replacing the file system path in the URI with the path
    * with the actual case as it exists in the file system.
    *
    * @see #realPath(IPath)
    * 
    * @param uri
    *        The original {@code URI}
    * 
    * @return the real {@code URI}
    */
   public static URI realURI(URI uri) {
      return org.eclipse.core.internal.utils.FileUtil.realURI(uri);
   }

   /**
    * Returns line separator appropriate for the given file. The returned value
    * will be the first available value from the list below:
    * <ol>
    * <li>Line separator currently used in that file.
    * <li>Line separator defined in project preferences.
    * <li>Line separator defined in instance preferences.
    * <li>Line separator defined in default preferences.
    * <li>Operating system default line separator.
    * </ol>
    * 
    * @param file
    *        the file for which line separator should be returned
    * @return line separator for the given file
    */
   public static String getLineSeparator(IFile file) {
      return org.eclipse.core.internal.utils.FileUtil.getLineSeparator(file);
   }

   /**
    * Checks if two location overlap.
    * Overlap means the locations are the same, or one is a proper prefix of the other.
    * 
    * @param location1
    *        The first location's {@code URI}
    * 
    * @param location2
    *        The second location's {@code URI}
    * 
    * @return true if the given file system locations overlap, and false otherwise
    */
   public static boolean isOverlapping(URI location1, URI location2) {
      return org.eclipse.core.internal.utils.FileUtil.isOverlapping(location1, location2);
   }

   /**
    * Checks if two locations have the same or a proper prefix.
    * Returns false otherwise.
    * 
    * @param location1
    *        The first location's {@code IPath}
    * 
    * @param location2
    *        The second location's {@code IPath}
    * 
    * @return true if location1 is the same as or a proper prefix of location2
    */
   public static boolean isPrefixOf(IPath location1, IPath location2) {
      return org.eclipse.core.internal.utils.FileUtil.isPrefixOf(location1, location2);
   }

   /**
    * Checks if two locations have the same or a proper prefix.
    * Returns false otherwise.
    * 
    * @param location1
    *        The first location's {@code URI}
    * 
    * @param location2
    *        The second location's {@code URI}
    * 
    * @return true if location1 is the same as, or a proper prefix of, location2
    */
   public static boolean isPrefixOf(URI location1, URI location2) {
      return org.eclipse.core.internal.utils.FileUtil.isPrefixOf(location1, location2);
   }

   /**
    * Converts a URI to an IPath. Returns null if the URI cannot be represented
    * as an IPath.
    * <p>
    * Note this method differs from URIUtil in its handling of relative URIs
    * as being relative to path variables.
    * 
    * @param uri
    *        The original {@code URI}
    * 
    * @return the {@code URI}s {@code IPath}
    */
   public static IPath toPath(URI uri) {
      return org.eclipse.core.internal.utils.FileUtil.toPath(uri);
   }

   /**
    * Creates the folder specified in the path, including all parent folders.
    * 
    * @param path
    *        The absolute path
    * @param root
    *        The root container
    * @param isFolder
    *        Is the path a folder and not a file
    */
   public static void createFolderWithParents(IPath path, IContainer root, boolean isFolder) {
      IPath relativePath;
      if (path.isAbsolute()) {
         relativePath = path.makeRelativeTo(root.getFullPath());
      } else {
         relativePath = path;
      }
      int lastSegment = isFolder ? 0 : 1;

      for (int i = relativePath.segmentCount() - 1; i >= lastSegment; i--) {
         final IPath folderPath = relativePath.removeLastSegments(i);
         if (folderPath.segmentCount() < ICoreConstants.MINIMUM_FOLDER_SEGMENT_LENGTH && root instanceof IWorkspaceRoot) {
            final IProject project = ((IWorkspaceRoot) root).getProject(folderPath.toString());
            if (!project.exists()) {
               try {
                  project.create(new NullProgressMonitor());
               } catch (CoreException e) {
                  e.printStackTrace();
               }
            }
         } else {
            final IFolder folder = root.getFolder(folderPath);
            if (!folder.exists()) {
               try {
                  folder.create(false, true, new NullProgressMonitor());
               } catch (CoreException e) {
                  e.printStackTrace();
               }
            }
         }
      }
   }

   /**
    * Creates the folder specified in the path, including all parent folders.
    * 
    * @param folder
    *        The folder to create
    * @param root
    *        The root container
    */
   public static void createFolderWithParents(IFolder folder, IContainer root) {
      createFolderWithParents(folder.getFullPath(), root, true);
   }

   /**
    * Creates the parent folders for the file specified in the path.
    * 
    * @param file
    *        The file for which to create the parent folders
    * @param root
    *        The root container
    */
   public static void createFolderWithParents(IFile file, IContainer root) {
      createFolderWithParents(file.getFullPath(), root, false);
   }

}

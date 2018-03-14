package ch.hsr.ifs.iltis.core.resources;

import java.io.File;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.iltis.core.exception.ILTISException;


/**
 * A utility class providing static methods to handle files. For I/O functionality please
 * refer to {@link IOUtil}
 * 
 * @see ch.hsr.ifs.iltis.cpp.resources.CFileUtil
 * @author tstauber
 *
 */
@SuppressWarnings("unused")
public abstract class FileUtil {

   /* IFile */

   /**
    * Used to obtain the IFile corresponding to a java File
    * 
    * @param file
    * @return the IFile or null if non existent
    */
   public static IFile toIFile(final File file) {
      return toIFile(file.toURI());
   }

   /**
    * Used to obtain the IFile for a file path
    * 
    * @param filePath
    * @return the IFile or null if non existent
    */
   public static IFile toIFile(final String filePath) {
      return toIFile(new File(filePath));
   }

   /**
    * Used to obtain the IFile for a file path
    * 
    * @param filePath
    * @return the IFile or null if non existent
    */
   public static IFile toIFile(final IPath filePath) {
      return WorkspaceUtil.getWorkspaceRoot().getFile(filePath);
   }

   /**
    * Used to obtain the IFile corresponding to a URI
    * 
    * @param fileURI
    * @return the IFile or null if non existent
    */
   public static IFile toIFile(final URI fileURI) {
      final IFile[] files = WorkspaceUtil.getWorkspaceRoot().findFilesForLocationURI(fileURI);

      if (files.length == 1) { return files[0]; }

      for (final IFile curfile : files) {
         if (fileURI.getPath().endsWith(curfile.getFullPath().toString())) { return curfile; }
      }

      return null;
   }

   /**
    * Used to obtain the File corresponding to a IFile
    * 
    * @return the File
    */
   public static File toFile(final IFile file) {
      return toFile(file.getLocation());
   }

   /**
    * Used to obtain the File corresponding to a filePath
    * 
    * @return the File
    */
   public static File toFile(final String filePath) {
      return new File(filePath);
   }

   /**
    * Used to obtain the File corresponding to a filePath
    * 
    * @return the File
    */
   public static File toFile(final IPath filePath) {
      return filePath.toFile();
   }

   /**
    * Used to obtain the File corresponding to a URI
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
    * @throws ILTISException
    *         (unchecked) if invalid path is passed.
    * @return the filename with extension
    */
   public static String getFilename(final String filePath) {
      final Path path = new Path(filePath);
      ILTISException.Unless.isTrue(path.segmentCount() > 0, "Path elements must not be empty");
      return path.segment(path.segmentCount() - 1);
   }

   /**
    * Used to obtain the file path without the filename and file extension.
    * {@code FileUtil.removeFilePart("/a/b/c/foo.h") -> "/a/b/c/" }
    * 
    * @return the path without filename and extension
    */
   public static String getPathWithoutFilename(final String filePath) {
      return filePath.replaceAll("(\\w)*\\.(\\w)*", "");
   }

   /**
    * Used to obtain the Path corresponding to a IFile
    * 
    * @return the Path
    */
   public static Path getPath(final IFile file) {
      final String pathOfFile = file.getFullPath().toOSString();
      return new Path(getPathWithoutFilename(pathOfFile));
   }

   /**
    * Used to obtain the URI from a file path
    * 
    * @param filePath
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
    */
   public static IPath realPath(IPath path) {
      return org.eclipse.core.internal.utils.FileUtil.realPath(path);
   }

   /**
    * Converts a URI into its canonical form.
    */
   public static URI canonicalURI(URI uri) {
      return org.eclipse.core.internal.utils.FileUtil.canonicalURI(uri);
   }

   /**
    * Converts a URI by replacing the file system path in the URI with the path
    * with the actual case as it exists in the file system.
    *
    * @see #realPath(IPath)
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
    * Returns true if the given file system locations overlap, and false otherwise.
    * Overlap means the locations are the same, or one is a proper prefix of the other.
    */
   public static boolean isOverlapping(URI location1, URI location2) {
      return org.eclipse.core.internal.utils.FileUtil.isOverlapping(location1, location2);
   }

   /**
    * Returns true if location1 is the same as, or a proper prefix of, location2.
    * Returns false otherwise.
    */
   public static boolean isPrefixOf(IPath location1, IPath location2) {
      return org.eclipse.core.internal.utils.FileUtil.isPrefixOf(location1, location2);
   }

   /**
    * Returns true if location1 is the same as, or a proper prefix of, location2.
    * Returns false otherwise.
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
    */
   public static IPath toPath(URI uri) {
      return org.eclipse.core.internal.utils.FileUtil.toPath(uri);
   }
   
   
   /**
    * Creates the folder specified in the path, including all parent folders.
    * @param path The path of the folder
    * @param project The project in which to create the folder
    */
   public static void createFolderWithParents(IPath path, IProject project) {
      for (int i = path.segmentCount() - 1; i > 0; i--) {
         final IPath folderPath = path.removeLastSegments(i);
         final IFolder folder = project.getFolder(folderPath);
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

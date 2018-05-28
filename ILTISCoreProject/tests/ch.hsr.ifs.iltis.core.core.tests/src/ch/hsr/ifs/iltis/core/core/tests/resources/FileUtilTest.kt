package ch.hsr.ifs.iltis.core.core.tests.resources;

import ch.hsr.ifs.iltis.core.core.functional.StreamFactory
import ch.hsr.ifs.iltis.core.core.resources.FileUtil
import org.junit.Assert.assertEquals
import org.junit.Test
import org.osgi.framework.FrameworkUtil


public class FileUtilTest {

   @Test
   fun filePartRetrievalYieldsMockatorHeaderFile() {
      val bundle = FrameworkUtil.getBundle(javaClass);
      val files = bundle.findEntries("/externalTestResource/iltis", "*.h", false);
      val filePart = FileUtil.getFilename(StreamFactory.stream(files).findFirst().get().getFile());
      assertEquals("iltis.h", filePart);
   }

   @Test
   fun classicFileRetrieval() {
      assertEquals("ILTIS.h", FileUtil.getFilename("/headers/ILTIS.h"));
   }

   @Test
   fun fileNameWithoutExtension() {
      assertEquals("ILTISDocu", FileUtil.getFilenameWithoutExtension("ILTISDocu.h"));
      assertEquals("ILTISDocu", FileUtil.getFilenameWithoutExtension("ILTISDocu"));
   }

   @Test
   fun removeFilePartYieldsDirectory() {
      assertEquals("/a/b/c/", FileUtil.getPathWithoutFilename("/a/b/c/foo.h"));
      assertEquals("/a/b/c/", FileUtil.getPathWithoutFilename("/a/b/c/"));
   }

   @Test
   fun removeFilePartYieldsDirectoryWindows() {
      assertEquals("\\a\\b\\c\\", FileUtil.getPathWithoutFilename("\\a\\b\\c\\foo.h"));
      assertEquals("\\a\\b\\c\\", FileUtil.getPathWithoutFilename("\\a\\b\\c\\"));
   }

}

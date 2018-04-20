package ch.hsr.ifs.iltis.core.tests.resources;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Enumeration;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.core.functional.StreamFactory;
import ch.hsr.ifs.iltis.core.resources.FileUtil;


public class FileUtilTest {

   @Test
   public void filePartRetrievalYieldsMockatorHeaderFile() { 
      Bundle bundle = FrameworkUtil.getBundle(getClass());
      final Enumeration<URL> files = bundle.findEntries("/externalTestResource/iltis", "*.h", false);
      final String filePart = FileUtil.getFilename(StreamFactory.stream(files).findFirst().get().getFile());
      assertEquals("iltis.h", filePart);
   }

   @Test
   public void classicFileRetrieval() {
      assertEquals("ILTIS.h", FileUtil.getFilename("/headers/ILTIS.h"));
   }

   @Test
   public void fileNameWithoutExtension() {
      assertEquals("ILTISDocu", FileUtil.getFilenameWithoutExtension("ILTISDocu.h"));
      assertEquals("ILTISDocu", FileUtil.getFilenameWithoutExtension("ILTISDocu"));
   }

   @Test
   public void removeFilePartYieldsDirectory() {
      assertEquals("/a/b/c/", FileUtil.getPathWithoutFilename("/a/b/c/foo.h"));
      assertEquals("/a/b/c/", FileUtil.getPathWithoutFilename("/a/b/c/"));
   }

   @Test
   public void removeFilePartYieldsDirectoryWindows() {
      assertEquals("\\a\\b\\c\\", FileUtil.getPathWithoutFilename("\\a\\b\\c\\foo.h"));
      assertEquals("\\a\\b\\c\\", FileUtil.getPathWithoutFilename("\\a\\b\\c\\"));
   }

}

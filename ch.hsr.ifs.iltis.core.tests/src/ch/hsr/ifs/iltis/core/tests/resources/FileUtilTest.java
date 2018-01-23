package ch.hsr.ifs.iltis.core.tests.resources;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Enumeration;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.functional.StreamHelper;
import ch.hsr.ifs.iltis.core.resources.FileUtil;
import ch.hsr.ifs.iltis.core.tests.Activator;


public class FileUtilTest {

   @Test
   public void filePartRetrievalYieldsMockatorHeaderFile() {
      final Enumeration<URL> files = Activator.getDefault().getBundle().findEntries("/externalTestResource/iltis", "*.h", false);
      final String filePart = FileUtil.getFilePart(StreamHelper.from(files).findFirst().get().getFile());
      assertEquals("iltis.h", filePart);
   }

   @Test
   public void classicFileRetrieval() {
      assertEquals("ILTIS.h", FileUtil.getFilePart("/headers/ILTIS.h"));
   }

   @Test
   public void fileNameWithoutExtension() {
      assertEquals("ILTISDocu", FileUtil.getFilenameWithoutExtension("ILTISDocu.h"));
      assertEquals("ILTISDocu", FileUtil.getFilenameWithoutExtension("ILTISDocu"));
   }

   @Test
   public void removeFilePartYieldsDirectory() {
      assertEquals("/a/b/c/", FileUtil.removeFilePart("/a/b/c/foo.h"));
      assertEquals("/a/b/c/", FileUtil.removeFilePart("/a/b/c/"));
   }

}

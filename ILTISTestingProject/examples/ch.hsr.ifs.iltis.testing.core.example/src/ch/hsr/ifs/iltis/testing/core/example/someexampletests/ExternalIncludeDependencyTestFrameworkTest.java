/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 * 
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.iltis.testing.core.example.someexampletests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.cdt.core.model.IIncludeReference;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;

import ch.hsr.ifs.iltis.testing.core.cdttest.base.CDTTestingUITest;


public class ExternalIncludeDependencyTestFrameworkTest extends CDTTestingUITest {

   @Override
   protected void initAdditionalIncludes() throws Exception {
      stageExternalIncludePathsForBothProjects("externalFrameworkTest");
      super.initAdditionalIncludes();
   }

   @Test
   public void runTest() throws Throwable {
      IIncludeReference[] includeRefs = getCurrentCProject().getIncludeReferences();
      assertEquals(1, includeRefs.length);

      IIncludeReference externalFrameworkTestRef = includeRefs[0];
      IPath expectedExternalFrameworkTestFolderPath = externalTestResourcesHolder.makeProjectAbsolutePath("externalFrameworkTest");
      assertFolderExists(expectedExternalFrameworkTestFolderPath);
      assertEquals(expectedExternalFrameworkTestFolderPath, externalFrameworkTestRef.getPath());
   }

   private void assertFolderExists(IPath expectedFolderName) {
      File folder = expectedFolderName.toFile();
      assertTrue(folder.exists());
      assertTrue(folder.isDirectory());
   }
}

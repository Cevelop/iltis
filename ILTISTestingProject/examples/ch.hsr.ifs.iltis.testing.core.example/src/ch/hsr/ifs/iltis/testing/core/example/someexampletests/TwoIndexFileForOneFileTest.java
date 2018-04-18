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

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IndexLocationFactory;
import org.eclipse.cdt.core.model.ICProject;
import org.junit.Test;

import ch.hsr.ifs.iltis.testing.core.cdttest.base.CDTTestingUITest;


public class TwoIndexFileForOneFileTest extends CDTTestingUITest {

   private IIndex index;

   @Override
   public void setUp() throws Exception {
      super.setUp();
      index = CCorePlugin.getIndexManager().getIndex(new ICProject[] { getCurrentCProject() });
      index.acquireReadLock();
   }

   @Test
   public void runTest() throws Throwable {
      IIndexFileLocation aHIndexFileLocation = IndexLocationFactory.getIFLExpensive(getCurrentCProject(), currentProjectHolder
            .makeProjectAbsolutePath("A.h").toOSString());
      IIndexFile[] aHIndexFile = index.getFiles(aHIndexFileLocation);
      assertEquals(2, aHIndexFile.length);
      assertEquals(GPPLanguage.getDefault().getLinkageID(), aHIndexFile[0].getLinkageID());
      assertEquals(GCCLanguage.getDefault().getLinkageID(), aHIndexFile[1].getLinkageID());
   }

   @Override
   public void tearDown() throws Exception {
      try {
         index.releaseReadLock();
      } finally { // always execute super.tearDown regardless of exceptions
         super.tearDown();
      }
   }
}

/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 * 
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.example.annotationtest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.CDTTestingUITest;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.rts.junit4.RunFor;


@RunFor(rtsFile = "/resources/AnnotationTestNotDefaultLocation.rts")
public class AnnotationTest extends CDTTestingUITest {

   @Test
   public void runTest() throws Throwable {
      assertEquals("int main() { return 0; }", testFiles.get("XY.cpp").getSource());
   }
}

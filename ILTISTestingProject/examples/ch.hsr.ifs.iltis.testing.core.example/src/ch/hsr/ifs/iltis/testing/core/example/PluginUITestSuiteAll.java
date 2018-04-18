/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 * 
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.iltis.testing.core.example;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.hsr.ifs.iltis.testing.core.example.annotationtest.SomeExampleAnnotationTestsSuite;
import ch.hsr.ifs.iltis.testing.core.example.examplecodantest.ExampleCodanCheckerTest;
import ch.hsr.ifs.iltis.testing.core.example.examplecodantest.ExampleCodanQuickFixTest;
import ch.hsr.ifs.iltis.testing.core.example.examplerefactoringtest.ExampleRefactoringModificationsTest;
import ch.hsr.ifs.iltis.testing.core.example.examplerefactoringtest.ExampleRefactoringTest;
import ch.hsr.ifs.iltis.testing.core.example.examplerefactoringtest.ILTISExampleRefactoringModificationsTest;
import ch.hsr.ifs.iltis.testing.core.example.examplerefactoringtest.ILTISExampleRefactoringTest;
import ch.hsr.ifs.iltis.testing.core.example.someexampletests.SomeExampleTestsSuite;


@RunWith(Suite.class)
@SuiteClasses({
      // @formatter:off
		SomeExampleTestsSuite.class,
		SomeExampleAnnotationTestsSuite.class,
		ExampleRefactoringTest.class,
		ExampleRefactoringModificationsTest.class,
		ILTISExampleRefactoringTest.class,
		ILTISExampleRefactoringModificationsTest.class,
		ExampleCodanCheckerTest.class,
		ExampleCodanQuickFixTest.class,
		// @formatter:on
})
public class PluginUITestSuiteAll {}

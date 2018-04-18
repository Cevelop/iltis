package ch.hsr.ifs.iltis.testing.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.hsr.ifs.iltis.testing.core.tests.cdttest.tests.ASTComparisonTest;
import ch.hsr.ifs.iltis.testing.core.tests.cdttest.tests.NormalizerTest;


@RunWith(Suite.class)
// @formatter:off
@SuiteClasses({ 
   NormalizerTest.class,
   ASTComparisonTest.class, 
   })
// @formatter:on
public class PluginUITestSuiteAll {}

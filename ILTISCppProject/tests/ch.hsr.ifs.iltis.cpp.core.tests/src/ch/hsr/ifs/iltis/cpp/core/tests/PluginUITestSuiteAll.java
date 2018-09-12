package ch.hsr.ifs.iltis.cpp.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.hsr.ifs.iltis.cpp.core.tests.includes.TestSuiteIncludes;
import ch.hsr.ifs.iltis.cpp.core.tests.validators.ValidateWrappers;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
   ValidateWrappers.class,
   TestSuiteIncludes.class,
})
public class PluginUITestSuiteAll {}

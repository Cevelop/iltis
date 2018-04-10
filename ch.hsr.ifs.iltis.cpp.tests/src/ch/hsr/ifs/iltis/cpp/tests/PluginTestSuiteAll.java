package ch.hsr.ifs.iltis.cpp.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.hsr.ifs.iltis.cpp.tests.includes.TestSuiteIncludes;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({

   ValidateWrappers.class,
   TestSuiteIncludes.class,
   
})
public class PluginTestSuiteAll {

}

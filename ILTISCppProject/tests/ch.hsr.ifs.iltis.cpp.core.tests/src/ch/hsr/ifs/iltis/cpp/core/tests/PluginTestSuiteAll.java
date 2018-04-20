package ch.hsr.ifs.iltis.cpp.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.hsr.ifs.iltis.cpp.core.tests.includes.TestSuiteIncludes;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({

   ValidateWrappers.class,
//   TestSuiteIncludes.class, // FIXME https://gitlab.dev.ifs.hsr.ch/iltis/iltis-cpp/issues/1
   
})
public class PluginTestSuiteAll {

}

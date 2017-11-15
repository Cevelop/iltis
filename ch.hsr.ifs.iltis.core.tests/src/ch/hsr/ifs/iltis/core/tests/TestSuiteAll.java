package ch.hsr.ifs.iltis.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.hsr.ifs.iltis.core.tests.data.PairTest;
import ch.hsr.ifs.iltis.core.tests.exception.AssertTest;
import ch.hsr.ifs.iltis.core.tests.exception.ExceptionTest;
import ch.hsr.ifs.iltis.core.tests.functional.CastHelperTest;
import ch.hsr.ifs.iltis.core.tests.resources.FileUtilTest;
import ch.hsr.ifs.iltis.core.tests.resources.IOUtilTest;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
   PairTest.class,
   AssertTest.class,
   ExceptionTest.class,
   CastHelperTest.class,

   FileUtilTest.class,
   IOUtilTest.class,
})
public class TestSuiteAll {

}

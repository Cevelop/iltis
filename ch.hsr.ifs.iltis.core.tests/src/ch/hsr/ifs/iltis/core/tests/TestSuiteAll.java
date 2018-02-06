package ch.hsr.ifs.iltis.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.hsr.ifs.iltis.core.tests.collections.CollectionHelperTest;
import ch.hsr.ifs.iltis.core.tests.collections.StackTest;
import ch.hsr.ifs.iltis.core.tests.data.PairTest;
import ch.hsr.ifs.iltis.core.tests.data.WrapperTest;
import ch.hsr.ifs.iltis.core.tests.exception.ExceptionTest;
import ch.hsr.ifs.iltis.core.tests.exception.UnlessTest;
import ch.hsr.ifs.iltis.core.tests.functional.FunctionalTest;
import ch.hsr.ifs.iltis.core.tests.functional.OptionalUtilTest;
import ch.hsr.ifs.iltis.core.tests.functional.StreamFactoryTest;
import ch.hsr.ifs.iltis.core.tests.functional.StreamUtilTest;
import ch.hsr.ifs.iltis.core.tests.resources.FileUtilTest;
import ch.hsr.ifs.iltis.core.tests.resources.IOUtilTest;
import ch.hsr.ifs.iltis.core.tests.resources.StringUtilTest;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({

   CollectionHelperTest.class,
   StackTest.class,
   
   PairTest.class,
   WrapperTest.class,
   
   ExceptionTest.class,
   UnlessTest.class,
   
   FunctionalTest.class,
   OptionalUtilTest.class,
   StreamFactoryTest.class,
   StreamUtilTest.class,

   FileUtilTest.class,
   IOUtilTest.class,
   StringUtilTest.class,
})
public class TestSuiteAll {

}

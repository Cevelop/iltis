package ch.hsr.ifs.iltis.core.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.hsr.ifs.iltis.core.core.tests.collections.CollectionHelperTest;
import ch.hsr.ifs.iltis.core.core.tests.data.PairTest;
import ch.hsr.ifs.iltis.core.core.tests.data.WrapperTest;
import ch.hsr.ifs.iltis.core.core.tests.exception.ExceptionTest;
import ch.hsr.ifs.iltis.core.core.tests.exception.UnlessTest;
import ch.hsr.ifs.iltis.core.core.tests.functional.FunctionalTest;
import ch.hsr.ifs.iltis.core.core.tests.functional.OptionalUtilTest;
import ch.hsr.ifs.iltis.core.core.tests.functional.StreamFactoryTest;
import ch.hsr.ifs.iltis.core.core.tests.functional.StreamUtilTest;
import ch.hsr.ifs.iltis.core.core.tests.resources.IOUtilTest;
import ch.hsr.ifs.iltis.core.core.tests.resources.StringUtilTest;


@RunWith(Suite::class)
//@formatter:off
@Suite.SuiteClasses(
   CollectionHelperTest::class,
   
   PairTest::class,
   WrapperTest::class,
   
   ExceptionTest::class,
   UnlessTest::class,
   
   FunctionalTest::class,
   OptionalUtilTest::class,
   StreamFactoryTest::class,
   StreamUtilTest::class,

   IOUtilTest::class,
   StringUtilTest::class
)
//@formatter:on
class UnitTestSuiteAll
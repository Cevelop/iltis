package ch.hsr.ifs.iltis.core.tests;

import ch.hsr.ifs.iltis.core.tests.arrays.ArrayUtilTest
import ch.hsr.ifs.iltis.core.tests.collections.CollectionUtilTest
import ch.hsr.ifs.iltis.core.tests.data.PairTest
import ch.hsr.ifs.iltis.core.tests.data.WrapperTest
import ch.hsr.ifs.iltis.core.tests.exception.ExceptionTest
import ch.hsr.ifs.iltis.core.tests.exception.UnlessTest
import ch.hsr.ifs.iltis.core.tests.functional.FunctionalTest
import ch.hsr.ifs.iltis.core.tests.functional.OptionalUtilTest
import ch.hsr.ifs.iltis.core.tests.functional.StreamFactoryTest
import ch.hsr.ifs.iltis.core.tests.functional.StreamUtilTest
import ch.hsr.ifs.iltis.core.tests.resources.IOUtilTest
import ch.hsr.ifs.iltis.core.tests.resources.StringUtilTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
	ArrayUtilTest::class,//
	CollectionUtilTest::class,//

	PairTest::class,//
	WrapperTest::class,//

	ExceptionTest::class,//
	UnlessTest::class,//

	FunctionalTest::class,//
	OptionalUtilTest::class,//
	StreamFactoryTest::class,//
	StreamUtilTest::class,//

	IOUtilTest::class,//
	StringUtilTest::class//
)
class UnitTestSuiteAll
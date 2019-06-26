package ch.hsr.ifs.iltis.cpp.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.hsr.ifs.iltis.cpp.core.tests.ast.stream.ASTNodeStreamsTest;


@RunWith(Suite.class)
@SuiteClasses({ //
                ASTNodeStreamsTest.class, //
})
public class UnitTestSuiteAll {}

/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base;

import static ch.hsr.ifs.iltis.core.functional.Functional.doForT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.ITextSelection;
import org.junit.Before;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.core.data.StringInputStream;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingConfigConstants;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.projectholder.IProjectHolder;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.projectholder.IProjectHolder.ProjectHolderJob;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison.ComparisonArg;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.testsourcefile.RTSTest.Language;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.testsourcefile.TestSourceFile;


public abstract class SourceFileBaseTest extends ProjectHolderBaseTest {

    /**
     * Compares by using the AST, in addition this compares also comments and includes. On fail it prints the whole AST.
     */
    protected static final EnumSet<ComparisonArg> COMPARE_AST_AND_COMMENTS_AND_INCLUDES = EnumSet.of(ComparisonArg.COMPARE_COMMENTS,
            ComparisonArg.COMPARE_INCLUDE_DIRECTIVES, ComparisonArg.PRINT_WHOLE_ASTS_ON_FAIL);

    /**
     * Compares by using the AST, additionally this compares includes. On fail it prints the whole AST.
     */
    protected static final EnumSet<ComparisonArg> COMPARE_AST_AND_INCLUDES = EnumSet.of(ComparisonArg.COMPARE_INCLUDE_DIRECTIVES,
            ComparisonArg.PRINT_WHOLE_ASTS_ON_FAIL);

    /**
     * The name of the primary test source file. This can be set by adding primaryFile=foo.cpp to the config.
     */
    private String primaryTestSourceFileName;

    protected Language language;

    /**
     * Key: file name, value: test file
     */
    protected LinkedHashMap<String, TestSourceFile> testFiles = new LinkedHashMap<>();

    private boolean skipTest = false;

    private boolean calledConfigureTest = false;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        assumeFalse("SKIPPING the Test.", skipTest);
    }

    /**
     * Loads config, loads selections, and populates the testFiles map form the passed files.
     *
     * This method is called by the testing framework
     */
    public void initTestSourceFiles(final List<TestSourceFile> files) {
        TestSourceFile configFile = null;

        for (final TestSourceFile file : files) {
            if (isConfigFile(file)) {
                configFile = file;
            } else {
                if (primaryTestSourceFileName == null) primaryTestSourceFileName = file.getName();
                testFiles.put(file.getName(), file);
            }
        }
        initializeConfiguration(extractProperties(configFile));
    }

    private boolean isConfigFile(final TestSourceFile file) {
        return file.getName().equals(CDTTestingConfigConstants.CONFIG_FILE_NAME);
    }

    public void setLanguage(final Language language) {
        this.language = language;
    }

    private Properties extractProperties(final TestSourceFile configFile) {
        final Properties properties = new Properties();
        if (configFile != null) {
            try (InputStream is = new StringInputStream(configFile.getSource())) {
                properties.load(is);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    private void initializeConfiguration(final Properties properties) {
        initCommonFields(properties);
        configureTest(properties);
        assertAllSuperMethodsCalled(calledConfigureTest, "configureTest");
    }

    /**
     * Can be overloaded to use properties to configure the test.
     * <p>
     * Overriding methods should always call {@code super.configureTest(Properties)}
     *
     * @param properties
     * The properties from the TestSourceFile
     *
     * @noreference Do not call this method directly
     */
    protected void configureTest(final Properties properties) {
        skipTest = Boolean.valueOf(properties.getProperty(CDTTestingConfigConstants.SKIP_TEST, "false"));
        this.calledConfigureTest = true;
    };

    private void initCommonFields(final Properties properties) {
        Optional.ofNullable(properties.getProperty(CDTTestingConfigConstants.PRIMARY_FILE, null)).ifPresent(name -> primaryTestSourceFileName = name);
    }

    @Override
    protected void initProjectFiles() throws Exception {
        stageTestSourceFileForImportForBothProjects(testFiles.values());
        super.initProjectFiles();
    }

    private void formatDocumentForBothProjects(final String relativePath) throws InterruptedException {
        scheduleAndJoinBoth((holder) -> holder.formatFileAsync(holder.makeProjectAbsolutePath(relativePath)));
    }

    /**
     * @return The name of the primary test source file (the first file defined in this test-case)
     */
    protected String getNameOfPrimaryTestFile() {
        return primaryTestSourceFileName;
    }

    /**
     * Convenience method for {@code getCurrentCElement(getCurrentIFile(testSourceFileName))}
     *
     * @throws IllegalArgumentException
     * If the no file with this name exists.
     */
    protected Optional<ICElement> getCurrentCElement(final String testSourceFileName) {
        return getCurrentCElement(getCurrentIFile(testSourceFileName));
    }

    /**
     * Convenience method to get ICElement represented by this path in expectedProjectHolder
     *
     * @throws IllegalArgumentException
     * If the no file with this name exists.
     */
    protected Optional<ICElement> getExpectedCElement(final String testSourceFileName) {
        return getExpectedCElement(getExpectedIFile(testSourceFileName));
    }

    /**
     * Convenience method to get ICElement of the primary test source file
     *
     * @throws IllegalArgumentException
     * If the no primary file exists.
     */
    protected Optional<ICElement> getPrimaryCElementFromCurrentProject() {
        return getCurrentCElement(getNameOfPrimaryTestFile());
    }

    /**
     * Convenience method to get the text selection of the primary file
     */
    protected Optional<ITextSelection> getSelectionOfPrimaryTestFile() {
        return getSelection(getNameOfPrimaryTestFile());
    }

    /**
     * Convenience method to get the text selection from a file name
     */
    protected Optional<ITextSelection> getSelection(final String testSourceFileName) {
        return Optional.ofNullable(testFiles.get(testSourceFileName)).flatMap(TestSourceFile::getSelection);
    }

    /**
     * Convenience method to get the IFile of current project for the name of a test source file
     *
     * @throws IllegalArgumentException
     * If the no file with this name exists.
     */
    protected IFile getCurrentIFile(final String testSourceFileName) {
        return getIFile(testSourceFileName, currentProjectHolder);
    }

    /**
     * Convenience method to get the IFile of expected project for the name of a test source file
     *
     * @throws IllegalArgumentException
     * If the no file with this name exists.
     */
    protected IFile getExpectedIFile(final String testSourceFileName) {
        return getIFile(testSourceFileName, expectedProjectHolder);
    }

    /**
     * Convenience method to get the primary IFile of the current project
     *
     * @throws IllegalArgumentException
     * If the no file with this name exists.
     */
    protected IFile getPrimaryIFileFromCurrentProject() {
        return getIFile(getNameOfPrimaryTestFile(), currentProjectHolder);
    }

    /**
     * @throws IllegalArgumentException
     * If the no file with this name exist
     */
    private IFile getIFile(final String testSourceFileName, final IProjectHolder holder) {
        if (testFiles.containsKey(testSourceFileName)) {
            return getIFile(testFiles.get(testSourceFileName), holder);
        } else {
            throw new IllegalArgumentException("No such test file \"" + testSourceFileName + "\" found.");
        }
    }

    /**
     * Convenience method to get the {@code IFile} with was created from this test source file in the current project.
     */
    protected IFile getCurrentIFile(final TestSourceFile testSourceFile) {
        return getIFile(testSourceFile, currentProjectHolder);
    }

    /**
     * Convenience method to get the {@code IFile} with was created from this test source file in the expected project.
     */
    protected IFile getExpectedIFile(final TestSourceFile testSourceFile) {
        return getIFile(testSourceFile, expectedProjectHolder);
    }

    private IFile getIFile(final TestSourceFile testSourceFile, final IProjectHolder holder) {
        return holder.getFile(testSourceFile.getName());
    }

    /* --- Comparison --- */

    /**
     * This method uses {@code fastAssertEquals} on each pair of test source files.
     *
     * @param args
     * The comparison arguments
     */
    protected void assertAllSourceFilesEqual(final EnumSet<ComparisonArg> args) {
        for (final TestSourceFile testFile : testFiles.values()) {
            fastAssertEquals(testFile.getName(), args);
        }
    }

    /**
     * This method compares the files with this name from the current project with the one from the expected project.
     * By default this method uses AST based comparison. This can be changed by passing an EnumSet containing the value
     * {@code ComparisonArg.USE_SOURCE_COMPARISON}
     *
     * @param testSourceFileName
     * The name of the test source file
     * @param args
     * The comparison arguments
     * @throws IllegalArgumentException
     * If no test source file with this name exists.
     */
    public void fastAssertEquals(final String testSourceFileName, EnumSet<ComparisonArg> args) {
        fastAssertEquals(testSourceFileName, args, getASTComparisonParameters());
    }

    /**
     * Override to change the used AST comparison attributes.
     * 
     * @return The comparison parameters to use
     */
    protected int getASTComparisonParameters() {
        return ITranslationUnit.AST_PARSE_INACTIVE_CODE;
    }

    /**
     * This method compares the files with this name from the current project with the one from the expected project.
     * By default this method uses AST based comparison. This can be changed by passing an EnumSet containing the value
     * {@code ComparisonArg.USE_SOURCE_COMPARISON}
     *
     * @param testSourceFileName
     * The name of the test source file
     * @param args
     * The comparison arguments
     * @param astStyle
     * The style of the AST to use (used to create the index)
     * @throws IllegalArgumentException
     * If no test source file with this name exists.
     */
    public void fastAssertEquals(final String testSourceFileName, final EnumSet<ComparisonArg> args, final int astStyle) {
        if (testFiles.containsKey(testSourceFileName)) {
            if (testFiles.get(testSourceFileName).shouldBeDeleted()) {
                assertFalse("File " + testSourceFileName + " has not been deleted.", getCurrentIFile(testSourceFileName).exists());
                assertFalse("Expected Testfile " + testSourceFileName + " created, this should not happen.", //
                        getExpectedIFile(testSourceFileName).exists());
            } else {
                assertTrue("File " + testSourceFileName + " has not been created or kept.", getCurrentIFile(testSourceFileName).exists());
                assertTrue("Expected Testfile " + testSourceFileName + " NOT created, this should not happen.", //
                        getExpectedIFile(testSourceFileName).exists());
                if (args.contains(ComparisonArg.USE_SOURCE_COMPARISON)) {
                    assertEqualsWithSource(testSourceFileName, args);
                } else {
                    assertEqualsWithAST(testSourceFileName, args, astStyle);
                }
            }
        } else {
            throw new IllegalArgumentException("No such test file \"" + testSourceFileName + "\" found.");
        }
    }

    private void assertEqualsWithSource(final String testSourceFileName, final EnumSet<ComparisonArg> args) {
        String expectedSource;
        String currentSource;
        if (!args.contains(ComparisonArg.DEBUG_NO_FORMATTING)) {
            try {
                formatDocumentForBothProjects(testSourceFileName);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        expectedSource = getExpectedDocument(getExpectedIFile(testSourceFileName)).get();
        currentSource = getCurrentDocument(getCurrentIFile(testSourceFileName)).get();

        if (!args.contains(ComparisonArg.DEBUG_NO_NORMALIZING)) {
            expectedSource = ASTComparison.normalizeCPP(expectedSource);
            currentSource = ASTComparison.normalizeCPP(currentSource);
        }
        assertEquals("Textual comparison", expectedSource, currentSource);
    }

    private void assertEqualsWithAST(final String testSourceFileName, final EnumSet<ComparisonArg> args, final int astStyle) {
        final IIndex[] expectedIndex = { null };
        final IIndex[] currentIndex = { null };
        final ITranslationUnit[] expectedTU = { null };
        final ITranslationUnit[] currentTU = { null };
        final IASTTranslationUnit[] expectedAST = { null };
        final IASTTranslationUnit[] currentAST = { null };
        try {
            ProjectHolderJob expected = ProjectHolderJob.create("Create expected AST", "ch.hsr.ifs.cdttesting.comparison.buildAST.expected", mon -> {
                try {
                    expectedIndex[0] = CCorePlugin.getIndexManager().getIndex(getExpectedCProject(), IIndexManager.ADD_DEPENDENCIES &
                                                                                                     IIndexManager.ADD_DEPENDENT);
                    expectedIndex[0].acquireReadLock();
                    expectedTU[0] = CoreModelUtil.findTranslationUnit(getExpectedIFile(testSourceFileName));
                    expectedAST[0] = expectedTU[0].getAST(expectedIndex[0], astStyle);
                } catch (final Exception e) {
                    return new Status(IStatus.ERROR, FrameworkUtil.getBundle(getClass()).getSymbolicName(), "Could not create expected AST", e);
                }
                return Status.OK_STATUS;
            });

            ProjectHolderJob current = ProjectHolderJob.create("Create current AST", "ch.hsr.ifs.cdttesting.comparison.buildAST.current", mon -> {
                try {
                    currentIndex[0] = CCorePlugin.getIndexManager().getIndex(getCurrentCProject(), IIndexManager.ADD_DEPENDENCIES &
                                                                                                   IIndexManager.ADD_DEPENDENT);
                    currentIndex[0].acquireReadLock();
                    currentTU[0] = CoreModelUtil.findTranslationUnit(getCurrentIFile(testSourceFileName));
                    currentAST[0] = currentTU[0].getAST(currentIndex[0], astStyle);
                } catch (final Exception e) {
                    return new Status(IStatus.ERROR, FrameworkUtil.getBundle(getClass()).getSymbolicName(), "Could not create current AST", e);
                }
                return Status.OK_STATUS;
            });

            scheduleAndJoinBoth(current, expected, false);

            doForT(j -> {
                assertNotNull(j);
                if (j.getState() != IStatus.OK) fail(j.getResult().getException().getMessage());
            }, expected, current);

            ASTComparison.assertEqualsAST(expectedAST[0], currentAST[0], args);
        } catch (InterruptedException e) {
            fail("Thread got interrupted");
        } finally {
            if (expectedIndex[0] != null) expectedIndex[0].releaseReadLock();
            if (currentIndex[0] != null) currentIndex[0].releaseReadLock();
        }
    }

}

package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.testsourcefile;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RTSFileParser {

    private static final String FAIL_MORE_THAN_ONE_SELECTION           = "More than one selection for file [%s] in test [%s]";
    private static final String FAIL_ONLY_ONE_EXPECTED_FILE_IS_ALLOWED = "More than one expected file for file [%s] in test [%s]";
    private static final String FAIL_SELECTION_NOT_CLOSED              = "Selection not closed for file [%s] in test [%s]";
    private static final String FAIL_TEST_HAS_NO_NAME                  = "Test has no name";
    private static final String FAIL_FILE_HAS_NO_NAME                  = "File in test [%s] has no name";

    public static final String CLASS           = "//#";
    public static final String COMMENT_OPEN    = "/*";
    public static final String COMMENT_CLOSE   = "*/";
    public static final String EXPECTED        = "//=";
    public static final String FILE            = "//@";
    public static final String CREATE_FILE     = "//@[C]";
    public static final String DELETE_FILE     = "//@[D]";
    public static final String LANGUAGE        = "//%";
    public static final String TEST            = "//!";
    public static final String SELECTION_CLOSE = "/*$";
    public static final String SELECTION_OPEN  = "$*/";

    public static final String SELECTION_START_TAG_REGEX = "(?<before>.*?)(?<opening>/\\*\\$)(?<content>.*?)(?<closing>\\*/)(?<after>.*)";
    public static final String SELECTION_END_TAG_REGEX   = "(?<before>.*?)(?<opening>/\\*)(?<content>.*?)(?<closing>\\$\\*/)(?<after>.*)";

    private enum MatcherState {
        ROOT, //
        IN_TEST_CASE, //
        IN_TEST_FILE, //
        IN_FILE_SELECTION, //
        IN_FILE_WITH_SELECTION, //
        IN_EXPECTED_FILE, //
        FAIL_STATE //
    }

    private static class RTSFileParserState {

        static final String FAIL_INVALID_PARSE_STATE = "Invalid parse state!";

        RTSTest        currentTest = null;
        TestSourceFile currentFile = null;

        String failMSG = FAIL_INVALID_PARSE_STATE;

        MatcherState matcherState = MatcherState.ROOT;
    }

    public static RTSTest parse(final BufferedReader inputReader) throws Exception {
        final Matcher BEGIN_OF_SELECTION_MATCHER = Pattern.compile(SELECTION_START_TAG_REGEX).matcher("");
        final Matcher END_OF_SELECTION_MATCHER = Pattern.compile(SELECTION_END_TAG_REGEX).matcher("");

        final RTSFileParserState state = new RTSFileParserState();

        /* YES CODE DUPLICATION MUTCH, BUT FUCK THAT, IT'S FAST!! */
        String line;
        while ((line = inputReader.readLine()) != null) {

            switch (state.matcherState) {
            case ROOT:
                if (isTEST(line)) {
                    parseTest(state, line);
                }
                break;
            case IN_TEST_CASE:
                if (isFILE(line)) {
                    parseFile(state, line);
                } else if (isTEST(line)) {
                    parseTest(state, line);
                } else if (isLANGUAGE(line)) {
                    state.currentTest.setLanguage(getValue(LANGUAGE, line));
                }
                break;
            case IN_TEST_FILE:
                if (isFILE(line)) {
                    parseFile(state, line);
                } else if (isEXPECTED(line)) {
                    enterExpected(state);
                    continue;
                } else if (isTEST(line)) {
                    parseTest(state, line);
                } else if (BEGIN_OF_SELECTION_MATCHER.reset(line).find()) {
                    /* Opening tag on this line */
                    state.currentFile.setSelectionStartRelativeToNextLine(BEGIN_OF_SELECTION_MATCHER.start("opening"));
                    line = BEGIN_OF_SELECTION_MATCHER.group("before") + BEGIN_OF_SELECTION_MATCHER.group("after");
                    if (BEGIN_OF_SELECTION_MATCHER.group("content").endsWith("$")) {
                        /* Tag is opening and closing */
                        state.currentFile.setSelectionEndRelativeToNextLine(BEGIN_OF_SELECTION_MATCHER.start("opening"));
                    } else if (END_OF_SELECTION_MATCHER.reset(line).find()) {
                        /* Closing tag on this line */
                        state.currentFile.setSelectionEndRelativeToNextLine(END_OF_SELECTION_MATCHER.start("opening"));
                        line = END_OF_SELECTION_MATCHER.group("before") + END_OF_SELECTION_MATCHER.group("after");
                    } else {
                        /* Closing tag must be on another line */
                        state.matcherState = MatcherState.IN_FILE_SELECTION;
                    }
                    state.currentFile.appendLineToSource(line);
                } else {
                    state.currentFile.appendLineToSource(line);
                }
                break;
            case IN_FILE_WITH_SELECTION:
                if (isFILE(line)) {
                    parseFile(state, line);
                } else if (isEXPECTED(line)) {
                    enterExpected(state);
                    continue;
                } else if (isTEST(line)) {
                    parseTest(state, line);
                } else if (BEGIN_OF_SELECTION_MATCHER.reset(line).find()) {
                    state.failMSG = String.format(FAIL_MORE_THAN_ONE_SELECTION, state.currentFile.getName(), state.currentTest.getName());
                    state.matcherState = MatcherState.FAIL_STATE;
                } else {
                    state.currentFile.appendLineToSource(line);
                }
                break;
            case IN_FILE_SELECTION:
                if (isFILE(line) || isEXPECTED(line) || isTEST(line)) {
                    state.matcherState = MatcherState.FAIL_STATE;
                    state.failMSG = String.format(FAIL_SELECTION_NOT_CLOSED, state.currentFile.getName(), state.currentTest.getName());
                } else if (END_OF_SELECTION_MATCHER.reset(line).find()) {
                    line = END_OF_SELECTION_MATCHER.group("before") + END_OF_SELECTION_MATCHER.group("after");
                    state.currentFile.setSelectionEndRelativeToNextLine(END_OF_SELECTION_MATCHER.start("opening"));
                    state.matcherState = MatcherState.IN_FILE_WITH_SELECTION;
                }
                state.currentFile.appendLineToSource(line);
                break;
            case IN_EXPECTED_FILE:
                if (isFILE(line)) {
                    parseFile(state, line);
                } else if (isTEST(line)) {
                    parseTest(state, line);
                } else if (isEXPECTED(line)) {
                    state.failMSG = String.format(FAIL_ONLY_ONE_EXPECTED_FILE_IS_ALLOWED, state.currentFile.getName(), state.currentTest.getName());
                    state.matcherState = MatcherState.FAIL_STATE;
                    continue;
                } else {
                    state.currentFile.appendLineToExpectedSource(line);
                }
                break;
            case FAIL_STATE:
                fail(state.failMSG);
            }
        }
        return state.currentTest;
    }

    private static void enterExpected(final RTSFileParserState state) {
        state.currentFile.initExpectedSource();
        state.matcherState = MatcherState.IN_EXPECTED_FILE;
    }

    private static void parseFile(final RTSFileParserState state, final String line) {
        final boolean createFile = isFileCreation(line);
        final boolean deleteFile = isFileDeletion(line);
        final String name = getValue(deleteFile ? DELETE_FILE : createFile ? CREATE_FILE : FILE, line);
        if (name.length() == 0) {
            state.failMSG = String.format(FAIL_FILE_HAS_NO_NAME, state.currentFile.getName());
            state.matcherState = MatcherState.FAIL_STATE;
        } else {
            state.currentFile = new TestSourceFile(name);
            if (createFile) {
                state.currentFile.createFileMode();
                enterExpected(state);
            } else if (deleteFile) {
                state.currentFile.deleteFileMode();
                state.matcherState = MatcherState.IN_TEST_FILE;
            } else {
                state.matcherState = MatcherState.IN_TEST_FILE;
            }
            state.currentTest.addTestFile(state.currentFile);
        }
    }

    private static void parseTest(final RTSFileParserState state, final String line) {
        final String name = getValue(TEST, line);
        if (name.length() == 0) {
            state.failMSG = FAIL_TEST_HAS_NO_NAME;
            state.matcherState = MatcherState.FAIL_STATE;
        } else {
            state.matcherState = MatcherState.IN_TEST_CASE;
            state.currentTest = new RTSTest(name);
        }
    }

    private static String getValue(final String attribute, final String line) {
        return line.trim().substring(attribute.length()).trim();
    }

    private static boolean isFileCreation(final String line) {
        return line.trim().startsWith(CREATE_FILE);
    }

    private static boolean isFileDeletion(final String line) {
        return line.trim().startsWith(DELETE_FILE);
    }

    private static boolean isEXPECTED(final String line) {
        return line.trim().startsWith(EXPECTED);
    }

    private static boolean isTEST(final String line) {
        return line.trim().startsWith(TEST);
    }

    private static boolean isFILE(final String line) {
        return line.trim().startsWith(FILE);
    }

    private static boolean isLANGUAGE(final String line) {
        return line.trim().startsWith(LANGUAGE);
    }
}

package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.testsourcefile;

import java.util.ArrayList;
import java.util.List;


public class RTSTest {

    private final String               testName;
    private Language                   language        = Language.CPP;
    private final List<TestSourceFile> testSourceFiles = new ArrayList<>();

    public RTSTest(final String name) {
        this.testName = name;
    }

    public List<TestSourceFile> getTestSourceFiles() {
        return testSourceFiles;
    }

    public String getName() {
        return testName;
    }

    public void addTestFile(final TestSourceFile file) {
        testSourceFiles.add(file);
    }

    public void setLanguage(final String language) {
        this.language = Language.valueOf(language);
    }

    public Language getLanguage() {
        return language;
    }

    public enum Language {
        CPP("CPP"), C("C");

        String lang;

        Language(final String lang) {
            this.lang = lang;
        }

    }

}

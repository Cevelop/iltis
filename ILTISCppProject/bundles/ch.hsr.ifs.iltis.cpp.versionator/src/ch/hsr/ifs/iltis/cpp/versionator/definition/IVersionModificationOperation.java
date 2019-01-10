package ch.hsr.ifs.iltis.cpp.versionator.definition;

import org.eclipse.core.resources.IProject;


public interface IVersionModificationOperation {

    void perform(IProject project, CPPVersion selectedVersion, boolean enabled);
}

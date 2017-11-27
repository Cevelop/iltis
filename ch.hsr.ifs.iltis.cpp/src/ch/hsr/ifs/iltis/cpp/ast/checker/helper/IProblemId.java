package ch.hsr.ifs.iltis.cpp.ast.checker.helper;

/**
 * An interface which should be implemented by types which represent a problem-id
 * (mostly used in SimpleChecker, SimpleVisitor and cdttesting)
 * 
 * @author tstauber
 */
public interface IProblemId {

   public String getId();
}

package ch.hsr.ifs.iltis.cpp.core.includes;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.core.util.constants.CommonCPPConstants;


public class IncludeDirective implements Comparable<IncludeDirective> {

    private static final String SPACE_STR = " ";

    public final String      target;
    public final IncludeType type;

    private final String startSymbol;
    private final String endSymbol;

    /**
     * There are currently two ways of including a header in C and C++. One can
     * either use a system include using {@code #include <system_header>} or a user defined include
     * using {@code #include "user_include.hpp"}. The difference being that the compiler will look
     * in the system first for system includes and the current project for user includes.
     * 
     * <ul>
     * <li>{@link IncludeType.SYSTEM} for system includes, i.e. all includes except the ones from the
     * current project.
     * <li>{@link IncludeType.USER} for all includes from the current project.
     * </ul>
     */
    public enum IncludeType {
        SYSTEM, //
        USER
    }

    public IncludeDirective(final String target, IncludeType type) {
        if (target == null || target.length() == 0) throw new ILTISException("Include directives must have a name.");
        this.target = target;
        this.type = type;
        this.startSymbol = (type == IncludeType.SYSTEM) ? "<" : "\"";
        this.endSymbol = (type == IncludeType.SYSTEM) ? ">" : "\"";
    }

    @Override
    public int compareTo(IncludeDirective o) {
        if (this.type == IncludeType.USER && o.type == IncludeType.SYSTEM) return -1;
        if (this.type == IncludeType.SYSTEM && o.type == IncludeType.USER) return 1;
        return this.target.compareTo(o.target);
    }

    @Override
    public String toString() {
        StringBuffer includeString = new StringBuffer(CommonCPPConstants.INCLUDE_DIRECTIVE);
        includeString.append(SPACE_STR);
        includeString.append(startSymbol);
        includeString.append(target);
        includeString.append(endSymbol);
        return includeString.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IncludeDirective other = (IncludeDirective) obj;
        if (target == null) {
            if (other.target != null) return false;
        } else if (!target.equals(other.target)) return false;
        if (type != other.type) return false;
        return true;
    }
}

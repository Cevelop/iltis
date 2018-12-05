package ch.hsr.ifs.iltis.cpp.core.includes;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.core.util.constants.CommonCPPConstants;


/**
 * TODO(Hansruedi) Please write javadoc
 * 
 * @author hpatzen
 * 
 * @since 1.1
 *
 */
public class IncludeDirective implements Comparable<IncludeDirective> {

    private static final char SPACE = ' ';

    public final String      target;
    public final IncludeType type;

    private final char startSymbol;
    private final char endSymbol;

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
        this.startSymbol = (type == IncludeType.SYSTEM) ? '<' : '"';
        this.endSymbol = (type == IncludeType.SYSTEM) ? '>' : '"';
    }

    /**
     * Static factory method creating a new system include
     * 
     * @param target
     * The target to include
     * @return A new system include to the target
     */
    public static IncludeDirective newSys(final String target) {
        return new IncludeDirective(target, IncludeType.SYSTEM);
    }

    /**
     * Static factory method creating a new user include
     * 
     * @param target
     * The target to include
     * @return A new user include to the target
     */
    public static IncludeDirective newUsr(final String target) {
        return new IncludeDirective(target, IncludeType.USER);
    }

    @Override
    public int compareTo(IncludeDirective o) {
        if (this.type == IncludeType.USER && o.type == IncludeType.SYSTEM) return -1;
        if (this.type == IncludeType.SYSTEM && o.type == IncludeType.USER) return 1;
        return this.target.compareTo(o.target);
    }

    @Override
    public String toString() {
        StringBuilder includeString = new StringBuilder(CommonCPPConstants.INCLUDE_DIRECTIVE);
        includeString.append(SPACE);
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

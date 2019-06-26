package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompoundStatement;


/**
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 * @since 3.0
 *
 */
public class ASTStatementUtil extends ASTNodeUtil{

    /**
     * @param stmt
     * The statement to test.
     * @return{@code true} iff the statement is either a for-statement, a while-statement, or a do-statement
     */
    public static boolean isLoop(IASTStatement stmt) {
        return stmt instanceof IASTForStatement || stmt instanceof IASTWhileStatement || stmt instanceof IASTDoStatement;
    }

    public static IASTCompoundStatement wrapInCompountStmtIfNecessary(final IASTStatement stmt) {
        if (stmt instanceof IASTCompoundStatement) return (IASTCompoundStatement) stmt;
    
        final IASTCompoundStatement compound = new CPPASTCompoundStatement();
        compound.addStatement(stmt);
        return compound;
    }
}

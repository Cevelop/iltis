package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.CVTYPE;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.REF;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.TDEF;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.getNestedType;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPEvaluation;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalTypeId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.LookupData;


/**
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 * @since 3.0
 *
 */
@SuppressWarnings("restriction")
public class ASTExpressionUtil extends ASTInitializerClauseUtil {

    /* FUNCTIONCALLEXPRESSIONS */

    /**
     * TODO handle other functionNameExpressions than idExpressions
     * 
     * @param funCall
     * @return
     */
    public static boolean isConstructorCall(ICPPASTFunctionCallExpression funCall) {
        funCall.getImplicitNames();
        //TODO check for PDOMCPPCONSTRUCTOR for simpler expression.
        IASTExpression funNameExpr = funCall.getFunctionNameExpression();
        if (funNameExpr instanceof IASTIdExpression) {
            ICPPEvaluation eval = funCall.getEvaluation();
            if (eval instanceof EvalTypeId) {
                if (!eval.isTypeDependent()) {
                    IType t = getNestedType(((EvalTypeId) eval).getInputType(), TDEF | CVTYPE | REF);
                    if (t instanceof ICPPClassType && !(t instanceof ICPPUnknownBinding)) {
                        ICPPClassType cls = (ICPPClassType) t;
                        try {
                            ICPPConstructor[] constructors = cls.getConstructors();
                            LookupData data = CPPSemantics.createLookupData(((IASTIdExpression) funNameExpr).getName());
                            IBinding b = CPPSemantics.resolveFunction(data, constructors, true, false);
                            return b != null && b instanceof ICPPConstructor;
                        } catch (DOMException e) {}
                    }
                }
            }
        }
        return false;
    }

    /**
     * TODO write doc
     * 
     * @param funCall
     * @return
     */
    public static Optional<IASTName> tryToExtractNameOfFunctionDefinition(ICPPASTFunctionCallExpression funCall) {
        if (funCall instanceof IASTIdExpression) return Optional.ofNullable(((IASTIdExpression) funCall).getName());

        //        ICPPEvaluation eval = ((ICPPASTInitializerClause) funCall.getFunctionNameExpression()).getEvaluation();

        /*
         * TODO slice back the variable krnlPtr until the assignment of a name was found
         * void (*krnlPtr)(int, float*, float*) = add;
         * krnlPtr<<<smallGridDim, dim3 (1,2)>>>(N, x, y);
         */

        return Optional.empty();
    }
}

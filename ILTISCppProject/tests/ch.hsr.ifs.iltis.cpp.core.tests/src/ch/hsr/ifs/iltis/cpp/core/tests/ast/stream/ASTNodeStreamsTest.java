package ch.hsr.ifs.iltis.cpp.core.tests.ast.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.stream.Stream;

import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.IGCCASTAttributeList;
import org.junit.Test;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ExtendedNodeFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.stream.ASTNodeStreams;


public class ASTNodeStreamsTest {

    public static IBetterFactory FACTORY = ExtendedNodeFactory.getDefault();

    //__global__ void add4567891123() {
    //    int intdint = 10;
    //    long longdong = (long) intdint;
    // }
    //

    @Test
    public void testStream() {
        IASTIdExpression childNode = FACTORY.newIdExpression(FACTORY.newName("intdint"));

        ICPPASTTranslationUnit expectedAST = FACTORY.newTranslationUnit(null);
        /* __global__ void add(float* x) { } */
        ICPPASTSimpleDeclSpecifier declspec = FACTORY.newSimpleDeclSpecifier(IASTSimpleDeclSpecifier.t_void);
        IGCCASTAttributeList attribute = FACTORY.newGCCAttributeList();
        attribute.addAttribute(FACTORY.newAttribute("global".toCharArray(), null));
        declspec.addAttributeSpecifier(attribute);
        IASTParameterDeclaration[] paramDeclarations = new IASTParameterDeclaration[] {};
        expectedAST.addDeclaration(FACTORY.newFunctionDefinition(declspec, FACTORY.newFunctionDeclarator(FACTORY.newName("add4567891123"),
                paramDeclarations), FACTORY.newCompoundStatement(cs -> {
                    /* int intdint = 10; */
                    cs.addStatement(FACTORY.newDeclarationStatement(ds -> {
                        ds.setDeclaration(FACTORY.newSimpleDeclaration(sd -> {
                            sd.setDeclSpecifier(FACTORY.newSimpleDeclSpecifier(Kind.eInt));
                            IASTDeclarator decl = FACTORY.newDeclarator(FACTORY.newName("intdint"));
                            decl.setInitializer(FACTORY.newEqualsInitializer(FACTORY.newIntegerLiteral(10)));
                            sd.addDeclarator(decl);
                        }));
                    }));
                    /* long longdong = (long) intdint; */
                    cs.addStatement(FACTORY.newDeclarationStatement(ds -> {
                        ds.setDeclaration(FACTORY.newSimpleDeclaration(sd -> {
                            ICPPASTSimpleDeclSpecifier sds = FACTORY.newSimpleDeclSpecifier(0);
                            sds.setLong(true);
                            sd.setDeclSpecifier(sds);
                            IASTDeclarator decl = FACTORY.newDeclarator(FACTORY.newName("longdong"));
                            ICPPASTSimpleDeclSpecifier csds = FACTORY.newSimpleDeclSpecifier(0);
                            csds.setLong(true);
                            decl.setInitializer(FACTORY.newEqualsInitializer(FACTORY.newCastExpression(IASTCastExpression.op_cast, FACTORY.newTypeId(
                                    csds), childNode)));
                            sd.addDeclarator(decl);
                        }));
                    }));
                })));

        Stream<IASTNode> stream = ASTNodeStreams.parentNodeStream(childNode);
        assertEquals(9l, stream.count());
    }

    @Test
    public void testStreamLimited() {
        IASTName childNode = FACTORY.newName("intdint");
        IASTDeclarationStatement parentStmt = FACTORY.newDeclarationStatement(ds -> {
            ds.setDeclaration(FACTORY.newSimpleDeclaration(sd -> {
                ICPPASTSimpleDeclSpecifier sds = FACTORY.newSimpleDeclSpecifier(0);
                sds.setLong(true);
                sd.setDeclSpecifier(sds);
                IASTDeclarator decl = FACTORY.newDeclarator(FACTORY.newName("longdong"));
                ICPPASTSimpleDeclSpecifier csds = FACTORY.newSimpleDeclSpecifier(0);
                csds.setLong(true);
                decl.setInitializer(FACTORY.newEqualsInitializer(FACTORY.newCastExpression(IASTCastExpression.op_cast, FACTORY.newTypeId(csds),
                        FACTORY.newIdExpression(childNode))));
                sd.addDeclarator(decl);
            }));
        });

        ICPPASTTranslationUnit expectedAST = FACTORY.newTranslationUnit(null);
        /* __global__ void add(float* x) { } */
        ICPPASTSimpleDeclSpecifier declspec = FACTORY.newSimpleDeclSpecifier(IASTSimpleDeclSpecifier.t_void);
        IGCCASTAttributeList attribute = FACTORY.newGCCAttributeList();
        attribute.addAttribute(FACTORY.newAttribute("global".toCharArray(), null));
        declspec.addAttributeSpecifier(attribute);
        IASTParameterDeclaration[] paramDeclarations = new IASTParameterDeclaration[] {};
        expectedAST.addDeclaration(FACTORY.newFunctionDefinition(declspec, FACTORY.newFunctionDeclarator(FACTORY.newName("add4567891123"),
                paramDeclarations), FACTORY.newCompoundStatement(cs -> {
                    /* int intdint = 10; */
                    cs.addStatement(FACTORY.newDeclarationStatement(ds -> {
                        ds.setDeclaration(FACTORY.newSimpleDeclaration(sd -> {
                            sd.setDeclSpecifier(FACTORY.newSimpleDeclSpecifier(Kind.eInt));
                            IASTDeclarator decl = FACTORY.newDeclarator(FACTORY.newName("intdint"));
                            decl.setInitializer(FACTORY.newEqualsInitializer(FACTORY.newIntegerLiteral(10)));
                            sd.addDeclarator(decl);
                        }));
                    }));
                    /* long longdong = (long) intdint; */
                    cs.addStatement(parentStmt);
                })));

        Stream<IASTNode> stream = ASTNodeStreams.parentNodeStream(childNode, parentStmt);
        assertEquals(6l, stream.count());
    }
    
    @Test
    public void testStreamfail() {
        fail();
    }
}

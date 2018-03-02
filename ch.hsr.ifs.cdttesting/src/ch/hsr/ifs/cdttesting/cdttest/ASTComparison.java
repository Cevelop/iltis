package ch.hsr.ifs.cdttesting.cdttest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemHolder;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;

import ch.hsr.ifs.cdttesting.cdttest.ASTComparison.ComparisonAttrID;
import ch.hsr.ifs.iltis.core.collections.CollectionUtil;
import ch.hsr.ifs.iltis.core.data.AbstractPair;
import ch.hsr.ifs.iltis.core.data.Wrapper;
import ch.hsr.ifs.iltis.core.functional.Functional;
import ch.hsr.ifs.iltis.core.functional.OptionalUtil;


/**
 * A utility class providing static methods to compare two IASTNodes. This can
 * be used to compare whole ASTs if the root nodes are passed.
 * 
 * @author tstauber
 *
 */
@SuppressWarnings({ "unused", "restriction" })
public class ASTComparison {

   private static ASTWriter writer = new ASTWriter();

   /**
    * Compares the {@link IASTTranslationUnit} from the code after the QuickFix was
    * applied with the {@link IASTTranslationUnit} from the expected code. To use
    * this method the flag {@code instantiateExpectedProject} has to be set to
    * true.
    * 
    * @param expectedAST
    *        The expected translation unit
    * @param actualAST
    *        The actual translation unit
    * @param failOnProblemNode
    *        When {@code true}, comparison fails on syntactically invalid code.
    * @param ignoreIncludes
    *        When {@code true}, includes will not be compared.
    *
    * @author tstauber
    *
    */
   public static void assertEqualsAST(final IASTTranslationUnit expectedAST, final IASTTranslationUnit actualAST, final boolean failOnProblemNode,
         final boolean ignoreIncludes) {
      ComparisonResult result;

      if (!ignoreIncludes) {
         result = equalsIncludes(Arrays.stream(expectedAST.getIncludeDirectives()).filter(
               IASTPreprocessorIncludeStatement::isPartOfTranslationUnitFile), Arrays.stream(actualAST.getIncludeDirectives()).filter(
                     IASTPreprocessorIncludeStatement::isPartOfTranslationUnitFile), false);
         assertEqualsWithAttributes(result);
      }
      result = equals(expectedAST, actualAST, failOnProblemNode);
      assertEqualsWithAttributes(result);
   }

   protected static void assertEqualsWithAttributes(ComparisonResult result) {
      String lineNo = result.getAttributeString(ComparisonAttrID.LINE_NO);
      String expected = result.getAttributeString(ComparisonAttrID.EXPECTED);
      String actual = result.getAttributeString(ComparisonAttrID.ACTUAL);

      switch (result.state) {
      case EQUAL:
         break;
      case DIFFERENT_AMOUNT_OF_CHILDREN:
         String mismatch = result.getAttributeString(ComparisonAttrID.FIRST_MISMATCH);
         assertEquals(result.getStateDecription() + mismatch + lineNo, expected, actual);
         break;
      default:
         assertEquals(result.getStateDecription() + lineNo, expected, actual);
      }
   }

   /**
    * Used to compare two arrays of includes. Optionally the order can be declared
    * relevant.
    * 
    * @param expectedStmt
    *        The expected include statements
    * @param actualStmt
    *        The actual include statements
    * @param ignoreOrdering
    *        When {@code true} the includes ordering will be ignored.
    * @return A {@link ComparisonResult} containing
    *         {@link ComparisonAttrID.EXPECTED} and
    *         {@link ComparisonAttrID.ACTUAL}
    */
   public static ComparisonResult equalsIncludes(final Stream<IASTPreprocessorIncludeStatement> expectedStmt,
         final Stream<IASTPreprocessorIncludeStatement> actualStmt, final boolean ignoreOrdering) {
      StringBuffer expectedStr = new StringBuffer();
      StringBuffer actualStr = new StringBuffer();
      if (ignoreOrdering) {
         Set<String> actual = actualStmt.map((node) -> node.getRawSignature()).collect(Collectors.toSet());
         Set<String> expected = expectedStmt.map((node) -> node.getRawSignature()).collect(Collectors.toSet());
         if (actual.equals(expected)) { return new ComparisonResult(ComparisonState.EQUAL); }

         Set<String> onlyInActual = new HashSet<>(actual);
         onlyInActual.removeAll(expected);
         Set<String> onlyInExpected = new HashSet<>(expected);
         onlyInExpected.removeAll(actual);

         ComparisonAttribute[] attributes = { new ComparisonAttribute(ComparisonAttrID.EXPECTED, onlyInExpected.stream().collect(Collectors.joining(
               "\n"))), new ComparisonAttribute(ComparisonAttrID.ACTUAL, onlyInActual.stream().collect(Collectors.joining("\n"))) };
         if (!onlyInActual.isEmpty()) {
            return new ComparisonResult(ComparisonState.ADDITIONAL_INCLUDE, attributes);
         } else {
            return new ComparisonResult(ComparisonState.MISSING_INCLUDE, attributes);
         }

      } else {
         Wrapper<Integer> count = new Wrapper<>(0);
         Functional.zip(expectedStmt, actualStmt).filter((pair) -> !AbstractPair.allElementEquals(pair, ASTComparison::equalsRaw)).forEachOrdered((
               pair) -> {
            count.wrapped++;
            expectedStr.append((pair.first() == null ? "---" : pair.first().toString()).concat("\n"));
            actualStr.append((pair.second() == null ? "---" : pair.second().toString()).concat("\n"));
         });
         if (count.wrapped == 0) { return new ComparisonResult(ComparisonState.EQUAL); }
         ComparisonAttribute[] attributes = { new ComparisonAttribute(ComparisonAttrID.EXPECTED, expectedStr.toString()), new ComparisonAttribute(
               ComparisonAttrID.ACTUAL, actualStr.toString()) };
         return new ComparisonResult(ComparisonState.INCLUDE_ORDER, attributes);
      }
   }

   protected static <T extends IASTNode> boolean equalsRaw(T left, T right) {
      return left.getRawSignature().equals(right.getRawSignature());
   }

   protected static <T extends IASTNode> boolean equalsNormalizedRaw(T left, T right) {
      return normalizeCPP(left.getRawSignature()).equals(normalizeCPP(right.getRawSignature()));
   }

   /**
    * Used to compare two {@link IASTNode}s. It can be configured to fail if an {@link IASTProblemNode} is encountered.
    * 
    * @param expected
    *        The first IASTNode
    * @param actual
    *        The second IASTNode
    * @param failOnProblemNode
    *        When {@code true}, comparison fails on syntactically invalid code.
    * @return
    */
   public static ComparisonResult equals(final IASTNode expected, final IASTNode actual, final boolean failOnProblemNode) {
      final IASTNode[] lChilds = Arrays.stream(expected.getChildren()).filter(IASTNode::isPartOfTranslationUnitFile).toArray(IASTNode[]::new);
      final IASTNode[] rChilds = Arrays.stream(actual.getChildren()).filter(IASTNode::isPartOfTranslationUnitFile).toArray(IASTNode[]::new);
      final IASTFileLocation fileLocation = actual.getOriginalNode().getFileLocation();
      final String lineNo = fileLocation == null ? "?" : String.valueOf(fileLocation.getStartingLineNumber());
      List<ComparisonAttribute> attributes = CollectionUtil.list(new ComparisonAttribute(ComparisonAttrID.EXPECTED, normalizeCPP(expected
            .getRawSignature())), new ComparisonAttribute(ComparisonAttrID.ACTUAL, normalizeCPP(actual.getRawSignature())), new ComparisonAttribute(
                  ComparisonAttrID.LINE_NO, lineNo));

      if ((expected instanceof IASTProblem || expected instanceof IASTProblemHolder || actual instanceof IASTProblem ||
           actual instanceof IASTProblemHolder) && failOnProblemNode) {
         /* Problem in AST occured */
         return new ComparisonResult(ComparisonState.PROBLEM_NODE, attributes);
      }
      if (lChilds.length != rChilds.length) {
         /* Find first mismatching child */
         ComparisonResult firstMismatch = findFirstMismatchingChild(lChilds, rChilds);
         attributes.add(new ComparisonAttribute(ComparisonAttrID.FIRST_MISMATCH, normalizeCPP(firstMismatch.getAttributeString(
               ComparisonAttrID.EXPECTED))));
         return new ComparisonResult(ComparisonState.DIFFERENT_AMOUNT_OF_CHILDREN, attributes);
      } else if (!expected.getClass().equals(actual.getClass())) {
         /* Return if node types differ */
         return new ComparisonResult(ComparisonState.DIFFERENT_TYPE, attributes);
      } else if (lChilds.length != 0) {
         /* Recurse into childs */
         ComparisonResult childResult = equalsEachNode(lChilds, rChilds, failOnProblemNode);
         if (childResult.state != ComparisonState.EQUAL) { return childResult; }
      }
      if (expected instanceof IASTTranslationUnit || expected instanceof ICPPASTInitializerList ||
          expected instanceof ICPPASTCompositeTypeSpecifier) {
         /* For this types the node is equal, if the childs are equal */
         return new ComparisonResult(ComparisonState.EQUAL);
      } 
      if (!equalsNormalizedRaw(expected, actual)) {
         if (failOnProblemNode) {
            /* Use writer to create two "formatted" versions of the nodes */
            String writtenExpected = writer.write(expected);
            String writtenActual = writer.write(actual);
            if (!writtenExpected.equals(writtenActual)) {
               attributes = CollectionUtil.list(new ComparisonAttribute(ComparisonAttrID.EXPECTED, writtenExpected), new ComparisonAttribute(
                     ComparisonAttrID.ACTUAL, writtenActual), new ComparisonAttribute(ComparisonAttrID.LINE_NO, lineNo));
               /* Fail if node has different signatures */
               return new ComparisonResult(ComparisonState.DIFFERENT_SIGNATURE, attributes);
            }
         } else {
            /* Fail if node has different signatures */
            return new ComparisonResult(ComparisonState.DIFFERENT_SIGNATURE, attributes);
         }
      }
      return new ComparisonResult(ComparisonState.EQUAL);
   }

   protected static ComparisonResult equalsEachNode(IASTNode[] expected, IASTNode[] actual, boolean failOnProblemNode) {
      for (int i = 0; i < expected.length; i++) {
         final ComparisonResult childResult = equals(expected[i], actual[i], failOnProblemNode);
         if (childResult.state != ComparisonState.EQUAL) { return childResult; }
      }
      return new ComparisonResult(ComparisonState.EQUAL);
   }

   private static ComparisonResult findFirstMismatchingChild(final IASTNode[] lChilds, final IASTNode[] rChilds) {
      return Functional.zip(lChilds, rChilds).map((nodes) -> equals(nodes.first(), nodes.second(), false)).filter((
            result) -> result.state != ComparisonState.EQUAL).findFirst().get();
   }

   /**
    * Normalizes the passed {@link String} by removing all testeditor-comments,
    * removing leading/trailing whitespace and line-breaks, replacing all remaining
    * line-breaks by ↵ and reducing all groups of whitespace to a single space.
    *
    * @author tstauber
    *
    * @param in
    *        The {@link String} that should be normalized.
    *
    * @return A normalized copy of the parameter in.
    **/
   public static String normalizeCPP(final String in) {
      // @formatter:off
		return in.replaceAll("/\\*.*\\*/", "") // Remove all test-editor-comments
		      .replaceAll("(^\\s*|\\s*$)", "") // Remove all leading and trailing linebreaks
		      .replaceAll("^\\s*$", "") // Remove empty lines
				.replaceAll("\\s*(\\r?\\n)+\\s*", "\n") // Replace all linebreaks with simple newline
				.replaceAll(" +", " "); // Reduce all groups of whitespace to a single space
		// @formatter:on
   }

   public enum ComparisonState {
      //@formatter:off
      DIFFERENT_TYPE("Different type."), DIFFERENT_AMOUNT_OF_CHILDREN("Different amount of children."),
      DIFFERENT_SIGNATURE("Different normalized signatures."), EQUAL(""),
      PROBLEM_NODE("Encountered a IASTProblem node."), ADDITIONAL_INCLUDE("Additional includes found."),
      MISSING_INCLUDE("Includes are missing."), INCLUDE_ORDER("The order of the includes differs.");
      //@formatter:on

      String desc;

      ComparisonState(String desc) {
         this.desc = desc;
      }
   }

   protected enum ComparisonAttrID {
      //@formatter:off
      EXPECTED(""), ACTUAL(""), LINE_NO(" On line no: "), FIRST_MISMATCH("First mismatch: ");
      //@formatter:on

      String prefix;

      ComparisonAttrID(String prefix) {
         this.prefix = prefix;
      }
   }

   /**
    * 
    * @author tstauber
    *
    */
   protected static class ComparisonAttribute {

      public ComparisonAttrID id;
      public String           value;

      public ComparisonAttribute(final ComparisonAttrID id, String value) {
         this.id = id;
         this.value = value;
      }

      public String toString() {
         return " " + id.prefix + " " + this.value;
      }
   }

   /**
    * A helper class holding a {@link ComparisonResult} and a map of
    * {@link ComparisonAttrID} -> {@link String}
    * 
    * @author tstauber
    *
    */
   public static class ComparisonResult {

      public ComparisonState           state;
      public List<ComparisonAttribute> attributes;

      public ComparisonResult(final ComparisonState state, final ComparisonAttribute... attributes) {
         this.state = state;
         this.attributes = Arrays.asList(attributes);
      }

      public ComparisonResult(final ComparisonState state, final List<ComparisonAttribute> attributes) {
         this.state = state;
         this.attributes = attributes;
      }

      public ComparisonResult(final ComparisonState state) {
         this.state = state;
         this.attributes = new ArrayList<>();
      }

      public String getAttributeString(ComparisonAttrID id) {
         return OptionalUtil.returnIfPresentElse(attributes.stream().filter((attr) -> attr.id == id).findFirst(), (o) -> o.toString(), "");
      }

      public String getStateDecription() {
         return state.desc;
      }
   }
}

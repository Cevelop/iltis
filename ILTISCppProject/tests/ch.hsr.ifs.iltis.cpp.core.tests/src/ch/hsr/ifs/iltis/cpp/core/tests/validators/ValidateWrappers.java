package ch.hsr.ifs.iltis.cpp.core.tests.validators;

import org.junit.Test;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CPPVisitor;
import ch.hsr.ifs.iltis.cpp.core.wrappers.IndexToASTNameHelper;
import ch.hsr.ifs.iltis.cpp.core.wrappers.SelectionHelper;


public class ValidateWrappers extends WrapperValidationTest {

   @Test
   public void validateCPPVisitor() {
      validate(CPPVisitor.class);
   }

   @Test
   public void validateSelectionHelper() {
      validate(SelectionHelper.class);
   }

   @Test
   public void validateIndexToASTNameHelper() {
      validate(IndexToASTNameHelper.class);
   }

}

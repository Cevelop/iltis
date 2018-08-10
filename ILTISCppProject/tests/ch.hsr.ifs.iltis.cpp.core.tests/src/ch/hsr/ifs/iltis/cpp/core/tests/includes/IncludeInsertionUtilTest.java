package ch.hsr.ifs.iltis.cpp.core.tests.includes;

import java.io.IOException;

import org.eclipse.cdt.codan.core.tests.CodanTestCase;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.testplugin.util.TestSourceReader;
import org.eclipse.cdt.internal.core.model.ExternalTranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.core.includes.IncludeInsertionUtil;


@SuppressWarnings("restriction")
public class IncludeInsertionUtilTest extends CodanTestCase {

   @Override
   public boolean isCpp() {
      return true;
   }

   @Override
   protected StringBuilder[] getContents(int sections) {
      try {
         return TestSourceReader.getContentsForTest(FrameworkUtil.getBundle(getClass()), getSourcePrefix(), getClass(), getName(), sections);
      } catch (IOException e) {
         fail(e.getMessage());
         return null;
      }
   }

   private ITranslationUnit createTranslationUnit() {
      assertNotNull("The cproject is null, did you load the code?", cproject);
      assertNotNull("The currentIFile is null, did you load the code?", currentIFile);
      ITranslationUnit tu = CoreModel.getDefault().createTranslationUnitFrom(cproject, currentIFile.getLocationURI());
      if (tu instanceof ExternalTranslationUnit) {
         ((ExternalTranslationUnit) tu).setResource(currentIFile);
      }
      return tu;
   }

   private ITranslationUnit createTranslationUnitFrom(CharSequence code) throws CoreException {
      loadcode(code);
      return createTranslationUnit();
   }

   private void executeActionAndAssertSameAST(String headerName, boolean isSystemInclude) throws CoreException {
      CharSequence[] sections = getContents(2);
      ITranslationUnit first = createTranslationUnitFrom(sections[0]);
      IncludeInsertionUtil.includeIfNotJetIncluded(first.getAST(), headerName, isSystemInclude, TextFileChange.FORCE_SAVE);
      ITranslationUnit result = createTranslationUnit();
      ITranslationUnit second = createTranslationUnitFrom(sections[1]);
      assertEquals(second.getAST().getRawSignature(), result.getAST().getRawSignature());
   }

   //#include <cstdint>
   //
   //short foo {42};

   //#include <cstdint>
   //
   //short foo {42};
   public void testSystemIncludeExists() throws CoreException {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //short foo {9001};

   //#include <algorithm>
   //
   //short foo {9001};
   public void testSystemIncludeWasAdded() throws CoreException {
      executeActionAndAssertSameAST("algorithm", true);
   }

   //#include "foo.h"
   //#include <iostream>
   //#include <vector>
   //int main(){
   //}

   //#include "foo.h"
   //#include <iostream>
   //#include <vector>
   //#include <cstdint>
   //int main(){
   //}
   public void testSystemIncludeWasAddedIntoExistingMixedIncludes() throws CoreException {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //#include <iostream>
   //#include <vector>
   //#include <cstdint>
   //
   //int main(){
   //}

   //#include "sigmund.h"
   //
   //#include <iostream>
   //#include <vector>
   //#include <cstdint>
   //
   // int main(){
   //}
   public void testUserIncludeAddedIntoExistingMixedIncludes() throws CoreException {
      executeActionAndAssertSameAST("sigmund.h", false);
   }

   //#define ABCD 2
   //
   //#include <iostream>
   // 
   //int main()
   //{
   //
   //#ifdef ABCD
   //    std::cout << "1: yes\n";
   //#else
   //    std::cout << "1: no\n";
   //#endif
   // 
   //#ifndef ABCD
   //    std::cout << "2: no1\n";
   //#elif ABCD == 2
   //    std::cout << "2: yes\n";
   //#else
   //    std::cout << "2: no2\n";
   //#endif
   // 
   //#if !defined(DCBA) && (ABCD < 2*4-3)
   //    std::cout << "3: yes\n";
   //#endif
   //}

   //  #define ABCD 2
   //
   //  #include <iostream>
   //#include <cstdint>
   // 
   //int main()
   //{
   //
   //#ifdef ABCD
   //    std::cout << "1: yes\n";
   //#else
   //     std::cout << "1: no\n";
   //#endif
   // 
   //#ifndef ABCD
   //    std::cout << "2: no1\n";
   //#elif ABCD == 2
   //    std::cout << "2: yes\n";
   //#else
   //    std::cout << "2: no2\n";
   //#endif
   // 
   //#if !defined(DCBA) && (ABCD < 2*4-3)
   //    std::cout << "3: yes\n";
   //#endif
   //}
   public void testIsIncludePlacedWithOtherPPStatementsPresent() throws CoreException {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include <cstdint>
   //
   //struct foo {
   //    int member;
   //;
   //
   //#endif /* GRANDPARENT_H */
   public void testIsIncludePlacedWithNoOtherIncludesPresent() throws CoreException {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "bampf.h"
   //
   //struct foo {
   //    int member;
   //};
   public void testIsUserIncludePlacedWithNoOtherIncludesPresent() throws CoreException {
      executeActionAndAssertSameAST("bampf.h", false);
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "foo.h"
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "foo.h"
   //
   //#include <cstdint>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */
   public void testIsSystemIncludeInsertedAtTheRightPositionWithOtherUserIncludes() throws CoreException {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "foo.h"
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "foo.h"
   //#include "bampf.h"
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */
   public void testIsUserIncludeInsertedAtTheRightPositionWithOtherUserIncludes() throws CoreException {
      executeActionAndAssertSameAST("bampf.h", false);
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include <algorithm>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include <algorithm>
   //#include <cstdint>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */
   public void testIsSystemIncludeInsertedAtTheRightPositionWithOtherSystemIncludes() throws CoreException {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include <cstdint>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "bampf.h"
   //
   //#include <cstdint>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */
   public void testIsUserIncludeInsertedAtTheRightPositionWithOtherSystemIncludes() throws CoreException {
      executeActionAndAssertSameAST("bampf.h", false);
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "schnorpsl.h"
   //
   //#include <algorithm>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "schnorpsl.h"
   //
   //#include <algorithm>
   //#include <cstdint>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */
   public void testIsSystemIncludeInsertedAtTheRightPositionWithOtherSystemAndUserIncludes() throws CoreException {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "schnorpsl.h"
   //
   //#include <cstdint>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "schnorpsl.h"
   //#include "bampf.h"
   //
   //#include <cstdint>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */
   public void testIsUserIncludeInsertedAtTheRightPositionWithOtherSystemAndUserIncludes() throws CoreException {
      executeActionAndAssertSameAST("bampf.h", false);
   }
}

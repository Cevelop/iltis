package ch.hsr.ifs.iltis.cpp.core.tests.includes;

import java.io.IOException;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.tests.ast2.AST2TestBase;
import org.eclipse.cdt.core.testplugin.util.TestSourceReader;
import org.eclipse.cdt.internal.core.parser.ParserException;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.core.includes.IncludeInsertionUtil;


@SuppressWarnings("restriction")
public class IncludeInsertionUtilTest extends AST2TestBase {

   @Override
   protected CharSequence[] getContents(int sections) throws IOException {
      return TestSourceReader.getContentsForTest(FrameworkUtil.getBundle(getClass()), "src", getClass(), getName(), sections);
   }

   private void executeActionAndAssertSameAST(String headerName, boolean isSystemInclude) throws IOException, ParserException {
      CharSequence[] sections = getContents(2);
      IASTTranslationUnit first = parse(sections[0].toString(), ParserLanguage.CPP, false, false);
      IncludeInsertionUtil.includeIfNotJetIncluded(first, headerName, isSystemInclude, TextFileChange.FORCE_SAVE);
      IASTTranslationUnit second = parse(sections[1].toString(), ParserLanguage.CPP, false, false);
      assertEquals(second.getRawSignature(), first.getRawSignature());
   }

   //#include <cstdint>
   //
   //short foo {42};

   //#include <cstdint>
   //
   //short foo {42};
   public void testSystemIncludeExists() throws Exception {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //short foo {9001};

   //#include <algorithm>
   //
   //short foo {9001};
   public void testSystemIncludeWasAdded() throws Exception {
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
   public void testSystemIncludeWasAddedIntoExistingMixedIncludes() throws Exception {
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
   public void testUserIncludeAddedIntoExistingMixedIncludes() throws Exception {
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
   public void testIsIncludePlacedWithOtherPPStatementsPresent() throws Exception {
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
   public void testIsIncludePlacedWithNoOtherIncludesPresent() throws Exception {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "bampf.h"
   //
   //   struct foo {
   //       int member;
   //   };
   public void testIsUserIncludePlacedWithNoOtherIncludesPresent() throws Exception {
      executeActionAndAssertSameAST("bampf.h", false);
   }

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "foo.h"
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "foo.h"
   //
   //   #include <cstdint>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */
   public void testIsSystemIncludeInsertedAtTheRightPositionWithOtherUserIncludes() throws Exception {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "foo.h"
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "foo.h"
   //   #include "bampf.h"
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */
   public void testIsUserIncludeInsertedAtTheRightPositionWithOtherUserIncludes() throws Exception {
      executeActionAndAssertSameAST("bampf.h", false);
   }

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include <algorithm>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include <algorithm>
   //   #include <cstdint>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */
   public void testIsSystemIncludeInsertedAtTheRightPositionWithOtherSystemIncludes() throws Exception {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include <cstdint>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "bampf.h"
   //
   //   #include <cstdint>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */
   public void testIsUserIncludeInsertedAtTheRightPositionWithOtherSystemIncludes() throws Exception {
      executeActionAndAssertSameAST("bampf.h", false);
   }

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "schnorpsl.h"
   //
   //   #include <algorithm>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "schnorpsl.h"
   //
   //   #include <algorithm>
   //   #include <cstdint>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */
   public void testIsSystemIncludeInsertedAtTheRightPositionWithOtherSystemAndUserIncludes() throws Exception {
      executeActionAndAssertSameAST("cstdint", true);
   }

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "schnorpsl.h"
   //
   //   #include <cstdint>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */

   //   #ifndef GRANDPARENT_H
   //   #define GRANDPARENT_H
   //
   //   #include "schnorpsl.h"
   //   #include "bampf.h"
   //
   //   #include <cstdint>
   //
   //   struct foo {
   //       int member;
   //   };
   //
   //   #endif /* GRANDPARENT_H */
   public void testIsUserIncludeInsertedAtTheRightPositionWithOtherSystemAndUserIncludes() throws Exception {
      executeActionAndAssertSameAST("bampf.h", false);
   }
}

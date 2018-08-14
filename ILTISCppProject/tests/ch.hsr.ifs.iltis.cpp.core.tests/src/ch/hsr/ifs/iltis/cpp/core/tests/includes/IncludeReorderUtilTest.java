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

import ch.hsr.ifs.iltis.cpp.core.includes.IncludeReorderUtil;


@SuppressWarnings("restriction")
public class IncludeReorderUtilTest extends CodanTestCase {

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

   private void executeActionAndAssertSameAST() throws CoreException {
      CharSequence[] sections = getContents(2);
      ITranslationUnit first = createTranslationUnitFrom(sections[0]);
      IncludeReorderUtil.reorderIncludeStatements(first.getAST(), TextFileChange.FORCE_SAVE);
      ITranslationUnit result = createTranslationUnit();
      ITranslationUnit second = createTranslationUnitFrom(sections[1]);
      assertEquals(second.getAST().getRawSignature(), result.getAST().getRawSignature());
   }

   //#include "foo.h"
   //#include "zar.h"
   //#include "aah.h"
   //
   //short foo {42};

   //#include "aah.h"
   //#include "foo.h"
   //#include "zar.h"
   //
   //short foo {42};
   public void testReorderOnlyUser() throws CoreException {
      executeActionAndAssertSameAST();
   }

   //#include <vector>
   //
   //#include <algorithm>
   //
   //#include <iostream>
   //
   //int main(){
   //}

   //#include <algorithm>
   //#include <iostream>
   //#include <vector>
   //
   //int main(){
   //}
   public void testReorderOnlySystem() throws CoreException {
      executeActionAndAssertSameAST();
   }

   //#include <vector>
   //#include "foo.h"
   //#include <cstdint>
   //#include "aah.h"
   //
   //short foo {42};

   //#include "aah.h"
   //#include "foo.h"
   //
   //#include <cstdint>
   //#include <vector>
   //
   //short foo {42};
   public void testReorderMixed() throws CoreException {
      executeActionAndAssertSameAST();
   }

   //#include <vector>
   //
   //#include "foo.h"
   //
   //#include <cstdint>
   //#include "aah.h"
   //
   //short foo {42};

   //#include "aah.h"
   //#include "foo.h"
   //
   //#include <cstdint>
   //#include <vector>
   //
   //short foo {42};
   public void testReorderMixedScattered() throws CoreException {
      executeActionAndAssertSameAST();
   }

   //#define ABCD 2
   //
   //#include <vector>
   //
   //#include "foo.h"
   //
   //#include <cstdint>
   //#include "aah.h"
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

   //#define ABCD 2
   //
   //#include "aah.h"
   //#include "foo.h"
   //
   //#include <cstdint>
   //#include <vector>
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
   public void testIsIncludeReorderWithOtherPPStatementsPresent() throws CoreException {
      executeActionAndAssertSameAST();
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
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */
   public void testReoderWithoutIncludeDoesNothing() throws CoreException {
      executeActionAndAssertSameAST();
   }

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //#include <vector>
   //
   //#include "foo.h"
   //
   //#include <cstdint>
   //#include "aah.h"
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */

   //#ifndef GRANDPARENT_H
   //#define GRANDPARENT_H
   //
   //#include "aah.h"
   //#include "foo.h"
   //
   //#include <cstdint>
   //#include <vector>
   //
   //struct foo {
   //    int member;
   //};
   //
   //#endif /* GRANDPARENT_H */
   public void testReorderWithHeaderGuards() throws CoreException {
      executeActionAndAssertSameAST();
   }

   //#define ABCE
   //#ifdef ABCD
   //#include <cstdint>
   //#endif

   //#define ABCE
   //#ifdef ABCD
   //
   //#include <cstdint>
   //
   //#endif
   public void testIncludesStayInScopesMin() throws CoreException {
      executeActionAndAssertSameAST();
   }

   //#define ABCD 2
   //
   //#include <vector>
   //#include <iostream>
   //
   //int main() {
   //
   //#ifdef ABCD
   //#include <vector>
   //   std::cout << "1: yes\n";
   //#else
   //#include <vector>
   //#include <algorithm>
   //   std::cout << "1: no\n";
   //#endif
   //
   //#ifndef ABCD
   //#include <cstdint>
   //#include "foo.h"
   //   std::cout << "2: no1\n";
   //#elif ABCD == 2
   //#include <algorithm>
   //#include "foo.h"
   //   std::cout << "2: yes\n";
   //#else
   //   std::cout << "2: no2\n";
   //#endif
   //
   //#if !defined(DCBA) && (ABCD < 2*4-3)
   //   std::cout << "3: yes\n";
   //#endif
   //}

   //#define ABCD 2
   //
   //#include <iostream>
   //#include <vector>
   //
   //int main() {
   //
   //#ifdef ABCD
   //
   //#include <vector>
   //
   //   std::cout << "1: yes\n";
   //#else
   //
   //#include <algorithm>
   //#include <vector>
   //
   //   std::cout << "1: no\n";
   //#endif
   //
   //#ifndef ABCD
   //
   //#include "foo.h"
   //
   //#include <cstdint>
   //
   //   std::cout << "2: no1\n";
   //#elif ABCD == 2
   //
   //#include "foo.h"
   //
   //#include <algorithm>
   //
   //   std::cout << "2: yes\n";
   //#else
   //   std::cout << "2: no2\n";
   //#endif
   //
   //#if !defined(DCBA) && (ABCD < 2*4-3)
   //   std::cout << "3: yes\n";
   //#endif
   //}
   public void testIncludesStayInScopes() throws CoreException {
      //      "Preprocessor creates wrong IASTFileLocation offsets for IASTPreprocessorIncludeStatements if they are in an inactive code branch"
      //      executeActionAndAssertSameAST();
   }
}

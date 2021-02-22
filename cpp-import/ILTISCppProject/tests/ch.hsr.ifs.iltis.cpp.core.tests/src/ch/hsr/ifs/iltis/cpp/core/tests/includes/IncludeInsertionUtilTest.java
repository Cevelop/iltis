package ch.hsr.ifs.iltis.cpp.core.tests.includes;

import java.io.IOException;

import org.eclipse.cdt.codan.core.tests.CodanTestCase;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.testplugin.util.TestSourceReader;
import org.eclipse.cdt.internal.core.model.ExternalTranslationUnit;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.core.includes.IncludeDirective;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeDirective.IncludeType;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeInsertionUtil;


@SuppressWarnings("restriction")
public class IncludeInsertionUtilTest extends CodanTestCase {

    @Override
    public boolean isCpp() {
        return true;
    }

    @Override
    protected StringBuilder[] getContents(final int sections) {
        try {
            return TestSourceReader.getContentsForTest(FrameworkUtil.getBundle(getClass()), getSourcePrefix(), getClass(), getName(), sections);
        } catch (final IOException e) {
            fail(e.getMessage());
            return null;
        }
    }

    private ITranslationUnit createTranslationUnit() {
        assertNotNull("The cproject is null, did you load the code?", cproject);
        assertNotNull("The currentIFile is null, did you load the code?", currentIFile);
        final ITranslationUnit tu = CoreModel.getDefault().createTranslationUnitFrom(cproject, currentIFile.getLocationURI());
        if (tu instanceof ExternalTranslationUnit) {
            ((ExternalTranslationUnit) tu).setResource(currentIFile);
        }
        return tu;
    }

    private ITranslationUnit createTranslationUnitFrom(final CharSequence code) throws CoreException {
        loadcode(code);
        return createTranslationUnit();
    }

    private void executeActionAndAssertSameAST(final IncludeDirective include) throws CoreException {
        final CharSequence[] sections = getContents(2);
        final ITranslationUnit first = createTranslationUnitFrom(sections[0]);
        IncludeInsertionUtil.includeIfNotYetIncluded(first.getAST(), include, TextFileChange.FORCE_SAVE);
        final ITranslationUnit result = createTranslationUnit();
        final ITranslationUnit second = createTranslationUnitFrom(sections[1]);
        assertEquals(second.getAST().getRawSignature(), result.getAST().getRawSignature());
    }

    private void executeActionAndAssertSameAST(final String headerName, final IncludeType includeType) throws CoreException {
        executeActionAndAssertSameAST(new IncludeDirective(headerName, includeType));
    }

    private void executeActionAndAssertSameAST(final MutableList<IncludeDirective> headers) throws CoreException {
        final CharSequence[] sections = getContents(2);
        final ITranslationUnit first = createTranslationUnitFrom(sections[0]);
        IncludeInsertionUtil.includeIfNotYetIncluded(first.getAST(), headers, TextFileChange.FORCE_SAVE);
        final ITranslationUnit result = createTranslationUnit();
        final ITranslationUnit second = createTranslationUnitFrom(sections[1]);
        assertEquals(second.getAST().getRawSignature(), result.getAST().getRawSignature());
    }

    //#include <cstdint>
    //
    //short foo {42};

    //#include <cstdint>
    //
    //short foo {42};
    public void testSystemIncludeExists() throws CoreException {
        executeActionAndAssertSameAST("cstdint", IncludeType.SYSTEM);
    }

    //short foo {9001};

    //#include <algorithm>
    //
    //short foo {9001};
    public void testSystemIncludeWasAdded() throws CoreException {
        executeActionAndAssertSameAST("algorithm", IncludeType.SYSTEM);
    }

    //#include "foo.h"
    //#include <iostream>
    //#include <vector>
    //int main(){
    //}

    //#include "foo.h"
    //
    //#include <cstdint>
    //#include <iostream>
    //#include <vector>
    //int main(){
    //}
    public void testSystemIncludeWasAddedIntoExistingMixedIncludes() throws CoreException {
        executeActionAndAssertSameAST("cstdint", IncludeType.SYSTEM);
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
    //int main(){
    //}
    public void testUserIncludeAddedIntoExistingMixedIncludes() throws CoreException {
        executeActionAndAssertSameAST("sigmund.h", IncludeType.USER);
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

    //#define ABCD 2
    //
    //#include <cstdint>
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
    public void testIsIncludePlacedWithOtherPPStatementsPresent() throws CoreException {
        executeActionAndAssertSameAST("cstdint", IncludeType.SYSTEM);
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
    //};
    //
    //#endif /* GRANDPARENT_H */
    public void testIsIncludePlacedWithNoOtherIncludesPresent() throws CoreException {
        executeActionAndAssertSameAST("cstdint", IncludeType.SYSTEM);
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
    //
    //#endif /* GRANDPARENT_H */
    public void testIsUserIncludePlacedWithNoOtherIncludesPresent() throws CoreException {
        executeActionAndAssertSameAST("bampf.h", IncludeType.USER);
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
        executeActionAndAssertSameAST("cstdint", IncludeType.SYSTEM);
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
    //#include "bampf.h"
    //#include "foo.h"
    //
    //struct foo {
    //    int member;
    //};
    //
    //#endif /* GRANDPARENT_H */
    public void testIsUserIncludeInsertedAtTheRightPositionWithOtherUserIncludes() throws CoreException {
        executeActionAndAssertSameAST("bampf.h", IncludeType.USER);
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
        executeActionAndAssertSameAST("cstdint", IncludeType.SYSTEM);
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
        executeActionAndAssertSameAST("bampf.h", IncludeType.USER);
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
        executeActionAndAssertSameAST("cstdint", IncludeType.SYSTEM);
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
    //#include "bampf.h"
    //#include "schnorpsl.h"
    //
    //#include <cstdint>
    //
    //struct foo {
    //    int member;
    //};
    //
    //#endif /* GRANDPARENT_H */
    public void testIsUserIncludeInsertedAtTheRightPositionWithOtherSystemAndUserIncludes() throws CoreException {
        executeActionAndAssertSameAST("bampf.h", IncludeType.USER);
    }

    //#include "foo.h"
    //
    //int main(){
    //}

    //#include "foo.h"
    //
    //#include <cstdint>
    //#include <iostream>
    //#include <vector>
    //
    //int main(){
    //}
    public void testMultipleSystemIncludes() throws CoreException {
        executeActionAndAssertSameAST(Lists.mutable.of(//
                new IncludeDirective("cstdint", IncludeType.SYSTEM), //
                new IncludeDirective("iostream", IncludeType.SYSTEM), //
                new IncludeDirective("vector", IncludeType.SYSTEM)));
    }

    //int main(){
    //}

    //#include "foo.h"
    //
    //#include <cstdint>
    //#include <iostream>
    //#include <vector>
    //
    //int main(){
    //}
    public void testMultipleMixedSystemIncludes() throws CoreException {
        executeActionAndAssertSameAST(Lists.mutable.of(//
                new IncludeDirective("iostream", IncludeType.SYSTEM), //
                new IncludeDirective("cstdint", IncludeType.SYSTEM), //
                new IncludeDirective("foo.h", IncludeType.USER), //
                new IncludeDirective("vector", IncludeType.SYSTEM)));
    }

    //int main(){
    //}

    //#include "baz.h"
    //#include "foo.h"
    //
    //#include <cstdint>
    //#include <iostream>
    //#include <vector>
    //
    //int main(){
    //}
    public void testMultipleMixedSystemAndUserIncludes() throws CoreException {
        executeActionAndAssertSameAST(Lists.mutable.of(//
                new IncludeDirective("iostream", IncludeType.SYSTEM), //
                new IncludeDirective("cstdint", IncludeType.SYSTEM), //
                new IncludeDirective("foo.h", IncludeType.USER), //
                new IncludeDirective("baz.h", IncludeType.USER), //
                new IncludeDirective("vector", IncludeType.SYSTEM)));
    }

    //#include "a.h"
    //#include "c.h"
    //
    //int main(){
    //}

    //#include "a.h"
    //#include "b.h"
    //
    //#include <iostream>
    //#include <vector>
    //
    //#include "c.h"
    //
    //int main(){
    //}
    public void testMultipleMixedSystemAndUserIncludesInsertedInBetween() throws CoreException {
        /*
         * This will currently result with the system includes in between the users includes.
         */
        executeActionAndAssertSameAST(Lists.mutable.of(//
                new IncludeDirective("vector", IncludeType.SYSTEM), //
                new IncludeDirective("b.h", IncludeType.USER), //
                new IncludeDirective("iostream", IncludeType.SYSTEM)));
    }
}

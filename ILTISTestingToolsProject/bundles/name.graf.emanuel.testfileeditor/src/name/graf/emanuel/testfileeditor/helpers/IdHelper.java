package name.graf.emanuel.testfileeditor.helpers;

import org.osgi.framework.FrameworkUtil;


/**
 * @author tstauber
 */
public class IdHelper {

    public static final String PLUGIN_ID         = FrameworkUtil.getBundle(IdHelper.class).getSymbolicName();
    public static final String DEFAULT_QUALIFIER = PLUGIN_ID;

    public static final String PREFERENCES_PREFIX                     = DEFAULT_QUALIFIER + ".preferences.";
    public static final String PROBLEMS_PREFIX                        = DEFAULT_QUALIFIER + ".problems.";
    public static final String PROPERTY_AND_PREFERENCE_PAGE_QUALIFIER = PREFERENCES_PREFIX + "PropertyAndPreferencePage";

    /* Predefined preference IDs */
    public static final String P_CREATE_WARNING_ON_BAD_TEST_NAME = PROBLEMS_PREFIX + "warningBadName"; //$NON-NLS-1$

}

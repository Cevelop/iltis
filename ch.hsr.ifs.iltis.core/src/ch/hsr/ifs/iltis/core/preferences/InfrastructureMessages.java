package ch.hsr.ifs.iltis.core.preferences;

import org.eclipse.osgi.util.NLS;

public class InfrastructureMessages extends NLS {
  private static final String BUNDLE_NAME = "com.cevelop.iltis.core.preferences.infrastructureMessages"; //$NON-NLS-1$
  public static String FieldEditorPropertyAndPreferencePage_checkbox_text;
  public static String FieldEditorPropertyAndPreferencePage_link_text;
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, InfrastructureMessages.class);
  }

  private InfrastructureMessages() {
  }
}

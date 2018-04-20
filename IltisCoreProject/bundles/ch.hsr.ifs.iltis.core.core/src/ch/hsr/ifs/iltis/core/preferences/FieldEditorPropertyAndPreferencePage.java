package ch.hsr.ifs.iltis.core.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * A combined {@code PropertyPage} and {@code PreferencePage}. Uses {@code FieldEditor}s for easier and cleaner definition of preferences. If the page
 * acts as a {@code PropertyPage} a header-bar, containing a checkbox (to enable component specific settings) and link (to open the workspace
 * preferences), will be added.
 *
 * The header-bar's design is held consistent with the default {@code PropertyAndPreferencePage}'s design.
 *
 *
 * @author tstauber
 */
public abstract class FieldEditorPropertyAndPreferencePage extends FieldEditorPropertyPage implements IWorkbenchPropertyPage,
         IWorkbenchPreferencePage {

   private static final String S_PREFERENCES_LINK_TEXT = InfrastructureMessages.FieldEditorPropertyAndPreferencePage_preference_link_text;
   private static final String S_PROPERTY_LINK_TEXT    = InfrastructureMessages.FieldEditorPropertyAndPreferencePage_property_link_text;
   private static final String S_ENABLE_PROJECT_PREF   = InfrastructureMessages.FieldEditorPropertyAndPreferencePage_checkbox_text;

   private Button headerCheckbox;
   private Link   headerLink;

   public FieldEditorPropertyAndPreferencePage(final int style) {
      super(style);
   }

   public FieldEditorPropertyAndPreferencePage(final String title, final int style) {
      super(title, style);
   }

   public FieldEditorPropertyAndPreferencePage(final String title, final ImageDescriptor image, final int style) {
      super(title, image, style);
   }

   /**
    * Returns true if this instance represents a property page
    *
    * @return true for property pages, false for preference pages
    */
   public boolean isPropertyPage() {
      return getElement() != null;
   }

   /**
    * If this is a property page, the passed projectElement will be handled by calling {@link handlePropertyPageElement(IAdaptable)}. Then the
    * controls
    * will be created and the {@link FieldEditor}s will be updated.
    *
    * @see org.eclipse.jface.preference.PreferencePage#createControl(Composite)
    */
   @Override
   public void createControl(final Composite parent) {
      super.createControl(parent);
      if (isPropertyPage()) {
         updateFieldEditors();
      }
   }

   /**
    * If this is a property page a header, containing a checkbox and a link, will be inserted at the top. Then the contained {@link FieldEditor}s will
    * be created.
    *
    * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Control createContents(final Composite parent) {
      createAndInitializeHeader(parent);
      return super.createContents(parent);
   }

   /**
    * This method is called when the checkbox, which toggles project specific preferences, has been changed.
    */
   protected void validationHook() {
      // Override and do validation checks and setValid in subclass
   }

   /**
    * Creates and initializes the header
    */
   protected void createAndInitializeHeader(final Composite parent) {
      final int numColumnsHeader = 2;
      final Composite headerComposite = createHeaderComposite(parent, numColumnsHeader);
      if (isPropertyPage()) {
         headerCheckbox = createHeaderCheckbox(headerComposite, S_ENABLE_PROJECT_PREF);
         headerLink = createHeaderLink(headerComposite, S_PREFERENCES_LINK_TEXT);
         createHeaderSeparator(headerComposite, numColumnsHeader);
         initializeHeader();
      } else {
         headerLink = createHeaderLink(headerComposite, S_PROPERTY_LINK_TEXT);
         createHeaderSeparator(headerComposite, numColumnsHeader);
      }
   }

   /* Creates the composite the header is drawn on */
   private Composite createHeaderComposite(final Composite parent, final int numColumnsHeader) {
      final Composite headerComposite = new Composite(parent, SWT.NONE);
      final GridLayout layout = new GridLayout(numColumnsHeader, false);
      layout.marginHeight = 3;
      layout.marginWidth = 3;
      headerComposite.setLayout(layout);
      headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
      return headerComposite;
   }

   /* Creates the checkbox to enable project wide preferences */
   private Button createHeaderCheckbox(final Composite parent, final String label) {
      final Button button = new Button(parent, SWT.CHECK);
      button.setText(label);
      button.setLayoutData(new GridData(SWT.LEAD, SWT.NONE, false, false));
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            headerLink.setEnabled(!projectPreferencesEnabled());
            validationHook();
            updateFieldEditors();
         }
      });
      return button;
   }

   /* Creates the link to the workspace preferences */
   private Link createHeaderLink(final Composite comp, final String text) {
      final Link link = new Link(comp, SWT.PUSH);
      link.setText(text);
      final GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      gd.horizontalAlignment = SWT.TRAIL;
      link.setLayoutData(gd);
      link.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            openCorrespondingPage();
         }
      });
      return link;
   }

   /* Draws the line to separate the header from the content */
   private void createHeaderSeparator(final Composite headerComposite, final int numColumnsHeader) {
      final Label separator = new Label(headerComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
      final GridData gd_separator = new GridData(GridData.FILL_HORIZONTAL);
      gd_separator.horizontalSpan = numColumnsHeader;
      separator.setLayoutData(gd_separator);
   }

   /* Initializes the headers components */
   private void initializeHeader() {
      final boolean projectPrefEnabled = getPropertyAndPreferenceHelper().projectSpecificPreferencesEnabled((IProject) getElement());
      headerCheckbox.setSelection(projectPrefEnabled);
      headerLink.setEnabled(!projectPrefEnabled);
   }

   /**
    * Returns the property store in case of this page being used as property page or the standard preference store in case of being a preference page
    *
    * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
    */
   @Override
   public IPreferenceStore getPreferenceStore() {
      return isPropertyPage() ? getPropertyAndPreferenceHelper().getProjectPreferences((IProject) projectElement) : getPropertyAndPreferenceHelper()
               .getWorkspacePreferences();
   }

   /**
    * Enables / disables the contained {@link FieldEditor}s and updates their {@code PreferenceStores} (Switches to property store for property page
    * and to preference store for preference page)
    */
   protected void updateFieldEditors() {
      for (final FieldEditor editor : editors) {
         editor.setEnabled(projectPreferencesEnabled(), getFieldEditorParent());
      }
   }

   /**
    * If this is a property page, this method must set a flag-value in the property store to indicate that this project should use project specific
    * preferences.
    *
    * @see org.eclipse.jface.preference.IPreferencePage#performOk()
    */
   @Override
   public boolean performOk() {
      final boolean result = super.performOk();
      if (result && isPropertyPage()) {
         getPropertyAndPreferenceHelper().setProjectSpecificPreferences((IProject) getElement(), projectPreferencesEnabled());
      }
      return result;
   }

   /**
    * Convenience method for testing if project specific preferences are enabled.
    */
   protected boolean projectPreferencesEnabled() {
      return headerCheckbox.getSelection();
   }

   /**
    * Defines what should happen after the "Restore Defaults" button was pressed. If this is a property page, the project will be reset to workspace
    * preferences and the values of the contained {@link FieldEditor}s will be reset to the values in the corresponding {@link PreferenceI}. If this
    * method is overloaded, the overload should call {@code super.performDefaults()}.
    *
    * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
    */
   @Override
   protected void performDefaults() {
      if (isPropertyPage()) {
         headerCheckbox.setSelection(false);
         headerLink.setEnabled(true);
         updateFieldEditors();
      }
      super.performDefaults();
   }

   /**
    * Must return the {@code IProject} for which to open the property page.
    * This could be done by opening a ProjectSelectorDialog.
    * 
    * If eclipse-cdt is loaded, please refer to use {@code ch.hsr.ifs.iltis.cpp.preferences.CFieldEditorPropertyAndPreferencePage}
    *
    */
   protected abstract IAdaptable getProjectForWhichToOpenProperties();

   /**
    * If this is a property page, this method opens the corresponding workspace preference page.
    */
   protected void openCorrespondingPage() {
      if (isPropertyPage()) {
         PreferencesUtil.createPreferenceDialogOn(getShell(), getPageId(), new String[] { getPageId() }, null).open();
      } else {
         PreferencesUtil.createPropertyDialogOn(getShell(), getProjectForWhichToOpenProperties(), getPageId(), null, null).open();
      }
   }

}

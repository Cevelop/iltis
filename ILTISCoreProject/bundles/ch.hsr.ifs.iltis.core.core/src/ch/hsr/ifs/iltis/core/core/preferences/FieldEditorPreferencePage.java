package ch.hsr.ifs.iltis.core.core.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * A {@code PreferencePage}. Uses {@code FieldEditor}s for easier and cleaner definition of preferences. Uses {@code IPropertyAndPreferenceHelper} for
 * accessing the preferences.
 *
 * @author tstauber
 */
public abstract class FieldEditorPreferencePage extends org.eclipse.jface.preference.FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private final IPropertyAndPreferenceHelper propertyAndPreferenceHelper = createPropertyAndPreferenceHelper();

    protected final List<FieldEditor> editors = new ArrayList<>();

    public FieldEditorPreferencePage(final int style) {
        super(style);
    }

    public FieldEditorPreferencePage(final String title, final int style) {
        super(title, style);
    }

    public FieldEditorPreferencePage(final String title, final ImageDescriptor image, final int style) {
        super(title, image, style);
    }

    /**
     * Returns the id of the current preference page as defined in plugin.xml
     *
     * Subclasses must implement.
     *
     * @return the page id as a {@code String}
     */
    abstract protected String getPageId();

    /**
     * Initially creates the {@link IPropertyAndPreferenceHelper} for this {@code FieldEditorPropertyAndPreferencePage}
     *
     * DO NOT CALL DIRECTLY - USE {@link #getPropertyAndPreferenceHelper()} INSTEAD.
     *
     * Subclasses must implement.
     *
     * @return the {@code IPropertyAndPreferenceHelper}
     */
    abstract protected IPropertyAndPreferenceHelper createPropertyAndPreferenceHelper();

    /**
     * @return the {@link IPropertyAndPreferenceHelper}
     */
    protected IPropertyAndPreferenceHelper getPropertyAndPreferenceHelper() {
        return propertyAndPreferenceHelper;
    }

    /**
     * The addField method must be overridden to store the created {@link FieldEditor}s.
     *
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#addField(org.eclipse.jface.preference.FieldEditor)
     */
    @Override
    protected void addField(final FieldEditor editor) {
        editors.add(editor);
        super.addField(editor);
    }

    /**
     * If this is a property page a header, containing a checkbox and a link, will be inserted at the top. Then the contained {@link FieldEditor}s
     * will
     * be created.
     *
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     *
     * @return the {@code Control}
     */
    @Override
    protected Control createContents(final Composite parent) {
        return super.createContents(parent);
    }

    /**
     * Returns the workspace preference store
     *
     * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
     *
     * @return the {@code IPreferenceStore}
     */
    @Override
    public IPreferenceStore getPreferenceStore() {
        return propertyAndPreferenceHelper.getWorkspacePreferences();
    }

    /**
     * Returns the {@link FieldEditor} members
     *
     * @return the {@code FieldEditor} {@code List}
     */
    protected List<FieldEditor> getFieldEditors() {
        return editors;
    }

    /**
     * Initializes the {@link FieldEditorPreferencePage}'s preference store.
     *
     * Subclass can implement, but should call {@code super.init(IWorkbench)}
     */
    @Override
    public void init(final IWorkbench workbench) {
        super.setPreferenceStore(propertyAndPreferenceHelper.getWorkspacePreferences());
    }
}

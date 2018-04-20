package ch.hsr.ifs.iltis.core.core.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPropertyPage;


/**
 * A {@code PropertyPage} which uses {@code FieldEditor}s. Uses
 * {@code FieldEditor}s for easier and cleaner definition of preferences.
 *
 * @author tstauber
 */
public abstract class FieldEditorPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

   protected IAdaptable projectElement;

   public FieldEditorPropertyPage(final int style) {
      super(style);
   }

   public FieldEditorPropertyPage(final String title, final int style) {
      super(title, style);
   }

   public FieldEditorPropertyPage(final String title, final ImageDescriptor image, final int style) {
      super(title, image, style);
   }

   /**
    * Stores the {@link IAdaptable} passed to the property page by the framework.
    *
    * @see org.eclipse.ui.IWorkbenchPropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
    */
   @Override
   public void setElement(final IAdaptable element) {
      projectElement = element.getAdapter(IResource.class);
      setPreferenceStore(getPreferenceStore());
   }

   /**
    * Delivers the object that owns the properties shown in this property page.
    *
    * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
    */
   @Override
   public IAdaptable getElement() {
      return projectElement;
   }

   /**
    * Returns the property store
    *
    * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
    */
   @Override
   public IPreferenceStore getPreferenceStore() {
      return getPropertyAndPreferenceHelper().getProjectPreferences((IProject) projectElement);
   }

}

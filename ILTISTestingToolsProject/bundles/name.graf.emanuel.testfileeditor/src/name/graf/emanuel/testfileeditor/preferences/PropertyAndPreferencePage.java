package name.graf.emanuel.testfileeditor.preferences;

import java.util.HashSet;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.preferences.ProjectSelectionDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.hsr.ifs.iltis.core.core.preferences.FieldEditorPropertyAndPreferencePage;
import ch.hsr.ifs.iltis.core.core.preferences.IPropertyAndPreferenceHelper;

import name.graf.emanuel.testfileeditor.Activator;
import name.graf.emanuel.testfileeditor.helpers.IdHelper;


/**
 * @author tstauber
 */
@SuppressWarnings("restriction")
public class PropertyAndPreferencePage extends FieldEditorPropertyAndPreferencePage {

   private static final String          U_ICON_PATH                  = "resources/icons/logo.png";
   private static final ImageDescriptor IMAGE_DESCRIPTOR_FROM_PLUGIN = AbstractUIPlugin.imageDescriptorFromPlugin(IdHelper.DEFAULT_QUALIFIER,
         U_ICON_PATH);

   public PropertyAndPreferencePage() {
      super(Activator.PLUGIN_NAME, IMAGE_DESCRIPTOR_FROM_PLUGIN, GRID);
   }

   @Override
   protected String getPageId() {
      return IdHelper.PROPERTY_AND_PREFERENCE_PAGE_QUALIFIER;
   }

   @Override
   protected IPropertyAndPreferenceHelper createPropertyAndPreferenceHelper() {
      return PropAndPrefHelper.getInstance();
   }

   @Override
   protected void createFieldEditors() {
      addField(new BooleanFieldEditor(IdHelper.P_CREATE_WARNING_ON_BAD_TEST_NAME, "Disable test-name checker", getFieldEditorParent()));
   }

   @Override
   protected IAdaptable getProjectForWhichToOpenProperties() {
      HashSet<IJavaProject> projectsWithSpecifics = new HashSet<>();
      try {
         IJavaProject[] projects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
         for (int i = 0; i < projects.length; i++) {
            IJavaProject curr = projects[i];
            if (getPropertyAndPreferenceHelper().projectSpecificPreferencesEnabled(curr.getProject())) {
               projectsWithSpecifics.add(curr);
            }
         }
      } catch (JavaModelException ignored) {}
      ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell(), projectsWithSpecifics);
      if (dialog.open() == Window.OK) { return (IJavaProject) dialog.getFirstResult(); }
      return null;
   }

}

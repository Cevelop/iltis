package ch.hsr.ifs.iltis.core.core.preferences.fieldeditor;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


/**
 * A {@link FieldEditor} that represents a SWT {@link Composite}. The
 * {@code CompositeFieldEditor} places all the FieldEditors inside on a
 * {@code Composite}. The advantage in comparison to just putting a SWT
 * {@code Group} around the elements is, that all the margins are provided. And
 * that the FieldEditors can be placed in multiple columns.
 *
 * @author tstauber
 */
public class CompositeFieldEditor extends GroupingFieldEditor {

   /**
    * Creates a {@code CompositeFieldEditor} with default value for
    * {@code margin} (8)
    *
    * @param name
    *        The name of the preference this field editor works on
    * @param parent
    *        The parent of the field editor's control
    * @param numColumnsInternal
    *        In how many columns the FieldEditors shall be aligned
    * @param numColumnsExternal
    *        In how many columns the CompositeFieldEditor shall appear for
    *        external observers
    */
   public CompositeFieldEditor(final String name, final Composite parent, final int numColumnsInternal, final int numColumnsExternal) {
      this(name, parent, numColumnsInternal, numColumnsExternal, DEFAULT_MARGIN);
   }

   /**
    * Creates a CompositeFieldEditor. This editor can contain multiple
    * FieldEditors. The number of columns defines how many rows of FieldEditors
    * shall be placed in the group.
    *
    * @param name
    *        The name of the preference this field editor works on
    * @param parent
    *        The parent of the field editor's control
    * @param numColumnsInternal
    *        In how many columns the FieldEditors shall be aligned
    * @param numColumnsExternal
    *        In how many columns the CompositeFieldEditor shall appear for
    *        external observers
    * @param marginInPx
    *        The margin between the contained field editors in pixels
    */
   public CompositeFieldEditor(final String name, final Composite parent, final int numColumnsInternal, final int numColumnsExternal,
                               final int marginInPx) {
      super(name, "composite", parent, numColumnsInternal, numColumnsExternal, marginInPx);

      composite = new Composite(parent, SWT.NONE);
      createControl(parent);
   }

}

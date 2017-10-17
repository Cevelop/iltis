package com.cevelop.iltis.core.preferences.fieldeditor;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ButtonFieldEditor extends FieldEditor {
	private Button button;
	private final SelectionAdapter selectionAdapter;

	public ButtonFieldEditor(final String label, final int style, final SelectionAdapter adapter, final Composite parent) {
		selectionAdapter = adapter;
		init("buttonFieldEditorIngored", label);
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(final int numColumns) {
	}

	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		button = getChangeControl(parent);
		final GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalSpan = numColumns;
		button.setLayoutData(gd);
		if (getLabelText() != null) {
			button.setText(getLabelText());
		}
		button.setLayoutData(new GridData(numColumns, 1));
	}

	protected Button getChangeControl(final Composite parent) {
		if (button == null) {
			button = new Button(parent, SWT.CHECK | SWT.LEFT);
			button.setFont(parent.getFont());
			button.addSelectionListener(selectionAdapter);
			button.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(final DisposeEvent event) {
					button = null;
				}
			});
		} else {
			checkParent(button, parent);
		}
		return button;
	}

	/**
	 * Buttons do not persist any preferences, so this method is empty.
	 */
	@Override
	protected void doLoad() {
	}

	/**
	 * Buttons do not persist any preferences, so this method is empty.
	 */
	@Override
	protected void doLoadDefault() {
	}

	/**
	 * Buttons do not persist any preferences, so this method is empty.
	 */
	@Override
	protected void doStore() {
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}
}

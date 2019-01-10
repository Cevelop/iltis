package ch.hsr.ifs.iltis.cpp.versionator.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class TreeSelectionToolbar extends Composite {

    public TreeSelectionToolbar(Composite parent, final ISelectionToolbarAction actionOnSelection, int style) {
        super(parent, style);

        setLayout(new RowLayout());

        ToolBar toolBar = new ToolBar(this, SWT.NONE);

        ToolItem selectAll = new ToolItem(toolBar, SWT.PUSH);
        Image selectAllIcon = new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/select_all.png"));
        selectAll.setImage(selectAllIcon);
        selectAll.setToolTipText("Select All Modifications");
        selectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                actionOnSelection.selectAll(true);
            }
        });

        ToolItem deselectAll = new ToolItem(toolBar, SWT.PUSH);
        Image deselectAllIcon = new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/deselect_all.png"));
        deselectAll.setImage(deselectAllIcon);
        deselectAll.setToolTipText("Deselect All Modifications");
        deselectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                actionOnSelection.selectAll(false);
            }
        });
    }

    public static interface ISelectionToolbarAction {

        public void selectAll(boolean selected);
    }

}

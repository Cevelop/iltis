package ch.hsr.ifs.iltis.testing.tools.showoffset.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.hsr.ifs.iltis.testing.tools.showoffset.Select;


public class GoToSelectionHanlder extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        new Select().run(HandlerUtil.getActiveWorkbenchWindow(event));
        return null;
    }
}

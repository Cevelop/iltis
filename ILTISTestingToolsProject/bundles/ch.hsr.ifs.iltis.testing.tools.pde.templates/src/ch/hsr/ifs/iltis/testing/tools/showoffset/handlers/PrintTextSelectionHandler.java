package ch.hsr.ifs.iltis.testing.tools.showoffset.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ch.hsr.ifs.iltis.testing.tools.showoffset.ShowOffset;


public class PrintTextSelectionHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        new ShowOffset().run();
        return null;
    }
}

package ch.hsr.ifs.iltis.testing.tools.pasta;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import ch.hsr.ifs.iltis.testing.tools.pasta.events.PastaEventConstants;


public class NodeView extends ViewPart {

    @Override
    public void createPartControl(final Composite parent) {
        final NodeWidget nodeWidget = new NodeWidget(parent);
        registerEventHandler(PastaEventConstants.ASTNODE, event -> {
            final IASTNode astNode = (IASTNode) event.getProperty(PastaEventConstants.ASTNODE);
            if (astNode != null) {
                nodeWidget.displayNode(astNode);
            }
        });
    }

    @Override
    public void setFocus() {}

    private void registerEventHandler(final String topic, final EventHandler handler) {
        final BundleContext ctx = FrameworkUtil.getBundle(ASTView.class).getBundleContext();
        final Dictionary<String, String> props = new Hashtable<>();
        props.put(EventConstants.EVENT_TOPIC, topic);
        ctx.registerService(EventHandler.class.getName(), handler, props);
    }
}

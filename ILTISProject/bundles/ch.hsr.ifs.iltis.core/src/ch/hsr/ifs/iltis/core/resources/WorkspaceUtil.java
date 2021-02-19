package ch.hsr.ifs.iltis.core.resources;

import java.util.Optional;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.pde.api.tools.annotations.NoExtend;
import org.eclipse.pde.api.tools.annotations.NoInstantiate;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


/**
 * A utility class providing static convenience methods for the eclipse workspace
 *
 * @author tstauber
 */
@NoExtend
@NoInstantiate
public class WorkspaceUtil {

    /**
     * Tries to get the default line separator for this workspace. If none is set the system-default will be used.
     *
     * @return A String containing a line separator
     */
    public static String getWorkspaceLineSeparator() {
        final Preferences rootNode = Platform.getPreferencesService().getRootNode();
        return getLineSeparatorFromPreferences(rootNode.node(InstanceScope.SCOPE)).orElseGet(() -> getLineSeparatorFromPreferences(rootNode.node(
                DefaultScope.SCOPE)).orElse(System.getProperty("line.separator")));
    }

    private static Optional<String> getLineSeparatorFromPreferences(final Preferences node) {
        try {
            return node.nodeExists(Platform.PI_RUNTIME) ? Optional.ofNullable(node.node(Platform.PI_RUNTIME).get(Platform.PREF_LINE_SEPARATOR, null))
                                                        : Optional.empty();
        } catch (final BackingStoreException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Convenience method to get the workspace root
     *
     * @return The workspace root
     */
    public static IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }
}

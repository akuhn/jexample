package ch.unibe.jexample.plugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JExampleCore extends Plugin {

    public static final String PLUGIN_ID = "ch.unibe.jexample";
    private static JExampleCore plugin;

    public JExampleCore() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static JExampleCore getDefault() {
        return plugin;
    }

    public IPath getPath() {
        String location = getBundle().getLocation();
        assert location.startsWith("reference:file:");
        location = location.substring("reference:file:".length());
        return new Path(location);
    }
    
}

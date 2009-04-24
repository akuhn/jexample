package ch.unibe.jexample.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class JExampleClasspathContainerInitializer extends ClasspathContainerInitializer {

    private static final String LIB_JEXAMPLE_JAR = "lib/jexample-r285.jar";
    public static final String ID = "ch.unibe.jexample.plugin.JEXAMPLE_CONTAINER";

    @Override
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
        if (isValidJExampleContainerPath(containerPath)) {
            JExampleContainer container = makeNewContainer(containerPath);
            JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project },
                    new IClasspathContainer[] { container }, null);
        }
    }

    private JExampleContainer makeNewContainer(IPath path) {
        IPath location = JExampleCore.getDefault().getPath();
        location = location.append(LIB_JEXAMPLE_JAR);
        IClasspathEntry entry = JavaCore.newLibraryEntry(
                location, 
                location, 
                null, 
                new IAccessRule[0], 
                new IClasspathAttribute[0], 
                false);
        return new JExampleContainer(path, entry);
    }

    private boolean isValidJExampleContainerPath(IPath path) {
        return path != null && path.segment(0).equals(ID);
    }

    public static class JExampleContainer implements IClasspathContainer {

        private final IClasspathEntry[] entries;
        private final IPath path;

        public JExampleContainer(IPath path, IClasspathEntry... entries) {
            this.path = path;
            this.entries = entries;
        }

        public IClasspathEntry[] getClasspathEntries() {
            return entries;
        }

        public String getDescription() {
            return "JExample";
        }

        public int getKind() {
            return IClasspathContainer.K_APPLICATION;
        }

        public IPath getPath() {
            return path;
        }

    }

}

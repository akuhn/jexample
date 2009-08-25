package ch.unibe.jexample.internal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.junit.buildpath.JUnitContainerInitializer;
import org.osgi.framework.Bundle;

@SuppressWarnings("restriction")
public class JExampleContainerInitializer extends ClasspathContainerInitializer {

    static final String JEXAMPLE_JAR = "jexample-r374.jar";
    static final String JEXAMPLE_BUNDLE = "ch.unibe.jexample.library";

    static final String JEXAMPLE_PATH = "ch.unibe.jexample.JEXAMPLE_CONTAINER";


    @Override
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
        JExampleContainer container = makeContainer(containerPath, project);
        JavaCore.setClasspathContainer(containerPath,
                new IJavaProject[] { project }, 	
                new IClasspathContainer[] { container }, 
                null);
    }

    private JExampleContainer makeContainer(IPath containerPath, IJavaProject project) throws JavaModelException {
        return new JExampleContainer(containerPath, makeLibraryEntries(project));
    }

    private IClasspathEntry[] makeLibraryEntries(IJavaProject project) throws JavaModelException {
        IClasspathEntry[] junitEntries = getJUnitLibraryEntries(project);
        IClasspathEntry[] entries = new IClasspathEntry[junitEntries.length + 1];
        System.arraycopy(junitEntries, 0, entries, 1, junitEntries.length);
        entries[0] = makeJExampleJarEntry();
        return entries;
    }

    private IClasspathEntry[] getJUnitLibraryEntries(IJavaProject project) throws JavaModelException {
        IPath junitPath = JUnitContainerInitializer.JUNIT4_PATH;
        IClasspathContainer junitClasspathContainer = JavaCore.getClasspathContainer(junitPath, project);
        if (junitClasspathContainer == null) return new IClasspathEntry[] { };
        return junitClasspathContainer.getClasspathEntries();
    }

    private IClasspathEntry makeJExampleJarEntry() {
        try {
            Bundle bundle = Platform.getBundle(JEXAMPLE_BUNDLE);
            URL url = FileLocator.find(bundle, new Path(JEXAMPLE_JAR), null);
            String fname = FileLocator.toFileURL(url).getFile();
            fname = URLDecoder.decode(fname, "UTF-8"); 
            return JavaCore.newLibraryEntry(new Path(fname), null, null);
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static class JExampleContainer implements IClasspathContainer {

        private final IClasspathEntry[] entries;
        private final IPath path;

        public JExampleContainer(IPath path, IClasspathEntry[] entries) {
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

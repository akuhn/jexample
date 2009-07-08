package ch.unibe.jexample.internal.buildpath;

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
import org.osgi.framework.Bundle;

public class JUnitContainerInitializer extends ClasspathContainerInitializer {

	@Override
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		JUnitContainer container= getNewContainer(containerPath);
		JavaCore.setClasspathContainer(containerPath,
				new IJavaProject[] { project }, 	
				new IClasspathContainer[] { container }, 
				null);
	}

	private JUnitContainer getNewContainer(IPath containerPath) {
		return new JUnitContainer(containerPath, new IClasspathEntry[] { makeJExampleLibraryEntry() });
	}

	private IClasspathEntry makeJExampleLibraryEntry() {
		try {
			Bundle bundle = Platform.getBundle("ch.unibe.jexample.library");
			URL url = FileLocator.find(bundle, new Path("jexample-r285.jar"), null);
			String fname = FileLocator.toFileURL(url).getFile();
			fname = URLDecoder.decode(fname, "UTF-8"); //$NON-NLS-1$
			return JavaCore.newLibraryEntry(new Path(fname), null, null);
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static class JUnitContainer implements IClasspathContainer {

		private final IClasspathEntry[] fEntries;
		private final IPath fPath;

		public JUnitContainer(IPath path, IClasspathEntry[] entries) {
			fPath= path;
			fEntries= entries;
		}

		public IClasspathEntry[] getClasspathEntries() {
			return fEntries;
		}

		public String getDescription() {
			return "JExample";
		}

		public int getKind() {
			return IClasspathContainer.K_APPLICATION;
		}

		public IPath getPath() {
			return fPath;
		}

	}	

}

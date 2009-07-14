package ch.unibe.jexample.internal;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.swt.graphics.Image;

public class JExampleClasspathFixProcessor extends ClasspathFixProcessor {

	@Override
	public ClasspathFixProposal[] getFixImportProposals(IJavaProject project, String name) throws CoreException {
		if (!match(name)) return null;
		return new ClasspathFixProposal[] { new JExampleClasspathFixProposal(project, 15) 	};
	}

	private boolean match(String name) {
		return name != null && (name.equals("Test") || name.equals("Given") || name.equals("RunWith")
				|| name.startsWith("org.junit.") || name.startsWith("ch.unibe.jexample."));
	}

	private static class JExampleClasspathFixProposal extends ClasspathFixProposal {

		private final int fRelevance;
		private final IJavaProject fProject;

		public JExampleClasspathFixProposal(IJavaProject project, int relevance) {
			fProject= project;
			fRelevance= relevance;
		}

		public String getAdditionalProposalInfo() {
			return "Adds the JExample library to the build path.";
		}

		public Change createChange(IProgressMonitor monitor) throws CoreException {
			if (monitor == null) {
				monitor= new NullProgressMonitor();
			}
			monitor.beginTask("Adding JExample library", 1);
			try {
				IClasspathEntry entry= null;
				entry= JavaCore.newContainerEntry(new Path(JExampleContainerInitializer.JEXAMPLE_PATH));
				IClasspathEntry[] oldEntries= fProject.getRawClasspath();
				ArrayList<IClasspathEntry> newEntries= new ArrayList<IClasspathEntry>(oldEntries.length + 1);
				boolean added= false;
				for (int i= 0; i < oldEntries.length; i++) {
					IClasspathEntry curr= oldEntries[i];
					if (curr.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
						IPath path= curr.getPath();
						if (path.equals(entry.getPath())) {
							return new NullChange(); // already on build path
						} 
					} 
					if (curr != null) {
						newEntries.add(curr);
					}
				}
				if (!added) {
					newEntries.add(entry);
				}

				final IClasspathEntry[] newCPEntries= (IClasspathEntry[]) newEntries.toArray(new IClasspathEntry[newEntries.size()]);
				Change newClasspathChange= newClasspathChange(fProject, newCPEntries, fProject.getOutputLocation());
				if (newClasspathChange != null) {
					return newClasspathChange;
				}
			} finally {
				monitor.done();
			}
			return new NullChange();
		}

		public String getDisplayString() {
			return "Add JExample library to the build path";
		}

		public Image getImage() {
			return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_LIBRARY);
		}

		public int getRelevance() {
			return fRelevance;
		}
	}
	
	
}

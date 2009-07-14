package ch.unibe.jexample.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.ui.CodeStyleConfiguration;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor.ClasspathFixProposal;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.ui.refactoring.RefactoringUI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;

public class JExampleQuickFixProcessor implements IQuickFixProcessor {

	public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		ArrayList<IJavaCompletionProposal> result = new ArrayList<IJavaCompletionProposal>(locations.length);
		for (IProblemLocation problem: locations) {
			if (IProblem.UndefinedType != problem.getProblemId()) continue;
			addJExampleToBuildPathProposals(context, problem, result); 
		}
		return result.toArray(new IJavaCompletionProposal[result.size()]);	
	}

	private void addJExampleToBuildPathProposals(IInvocationContext context, IProblemLocation location, ArrayList<IJavaCompletionProposal> proposals) throws JavaModelException {
		ICompilationUnit unit= context.getCompilationUnit();
		String qualifiedName= null;
		String s= unit.getBuffer().getText(location.getOffset(), location.getLength());
		/*if (s.equals("RunWith")) { 
			qualifiedName= "org.junit.runner.RunWith"; 
		} else if (s.equals("Test")) { 
			qualifiedName= "org.junit.Test"; 
		} else*/ if (s.equals("Given")) { 
			qualifiedName= "ch.unibe.jexample.Given"; 
		} else if (s.equals("JExample")) { 
			qualifiedName= "ch.unibe.jexample.JExample"; 
		}
		if (qualifiedName == null) return;
		IJavaProject javaProject= unit.getJavaProject();
		if (javaProject.findType(qualifiedName) != null) return;
		ClasspathFixProposal[] fixProposals= ClasspathFixProcessor.getContributedFixImportProposals(javaProject, qualifiedName, null);
		for (ClasspathFixProposal each: fixProposals) {
			proposals.add(new JExampleClasspathFixCorrectionProposal(javaProject, each, getImportRewrite(context.getASTRoot(), qualifiedName)));
		}
	}

	private ImportRewrite getImportRewrite(CompilationUnit astRoot, String typeToImport) {
		if (typeToImport != null) {
			ImportRewrite importRewrite= CodeStyleConfiguration.createImportRewrite(astRoot, true);
			importRewrite.addImport(typeToImport);
			return importRewrite;
		}
		return null;
	}	
	
	public boolean hasCorrections(ICompilationUnit unit, int problemId) {
		return problemId == IProblem.UndefinedType;
	}

	private static class JExampleClasspathFixCorrectionProposal implements IJavaCompletionProposal {

		private final ClasspathFixProposal fClasspathFixProposal;
		private final ImportRewrite fImportRewrite;
		private final IJavaProject fJavaProject;

		public JExampleClasspathFixCorrectionProposal(IJavaProject javaProject, ClasspathFixProposal cpfix, ImportRewrite rewrite) {
			fJavaProject= javaProject;
			fClasspathFixProposal= cpfix;
			fImportRewrite= rewrite;
		}

		protected Change createChange() throws CoreException {
			Change change= fClasspathFixProposal.createChange(null);
			if (fImportRewrite != null) {
				TextFileChange cuChange= new TextFileChange("Add import", (IFile) fImportRewrite.getCompilationUnit().getResource()); //$NON-NLS-1$
				cuChange.setEdit(fImportRewrite.rewriteImports(null));

				CompositeChange composite= new CompositeChange(getDisplayString());
				composite.add(change);
				composite.add(cuChange);
				return composite;
			}
			return change;
		}

		public void apply(IDocument document) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(false, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							Change change= createChange();
							change.initializeValidationData(new NullProgressMonitor());
							PerformChangeOperation op= RefactoringUI.createUIAwareChangeOperation(change);
							op.setUndoManager(RefactoringCore.getUndoManager(), getDisplayString());
							op.setSchedulingRule(fJavaProject.getProject().getWorkspace().getRoot());
							op.run(monitor);
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						} catch (OperationCanceledException e) {
							throw new InterruptedException();
						}
					}
				});
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		public String getAdditionalProposalInfo() {
			return fClasspathFixProposal.getAdditionalProposalInfo();
		}

		public int getRelevance() {
			return fClasspathFixProposal.getRelevance();
		}

		public IContextInformation getContextInformation() {
			return null;
		}

		public String getDisplayString() {
			return fClasspathFixProposal.getDisplayString();
		}

		public Image getImage() {
			return fClasspathFixProposal.getImage();
		}

		public Point getSelection(IDocument document) {
			return null;
		}
	}
	
}

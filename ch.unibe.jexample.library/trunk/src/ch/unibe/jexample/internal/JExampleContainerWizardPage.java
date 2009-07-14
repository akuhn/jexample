package ch.unibe.jexample.internal;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


public class JExampleContainerWizardPage extends WizardPage implements IClasspathContainerPage  {

	public JExampleContainerWizardPage() {
		super(JExampleContainerWizardPage.class.getName());
	}

	public boolean finish() {
		return true;
	}

	public IClasspathEntry getSelection() {
		return JavaCore.newContainerEntry(new Path(JExampleContainerInitializer.JEXAMPLE_PATH));
	}

	public void setSelection(IClasspathEntry containerEntry) {
		// ignore this
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
	}
	
	
}

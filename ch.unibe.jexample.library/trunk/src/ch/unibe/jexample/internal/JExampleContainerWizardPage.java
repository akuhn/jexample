package ch.unibe.jexample.internal;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.Bundle;

public class JExampleContainerWizardPage extends WizardPage implements IClasspathContainerPage  {

	public JExampleContainerWizardPage() {
		super(JExampleContainerWizardPage.class.getName());
		setTitle("JExample library");
		setDescription("Because well-designed tests depend on each other.");

		setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
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
		composite.setLayout(new FillLayout());
		asyncMakeBrowser(composite);
		setControl(composite);
	}

	private void asyncMakeBrowser(final Composite composite) {
		composite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				Browser browser = new Browser(composite, SWT.NONE);
				try {
					Bundle bundle = Platform.getBundle(JExampleContainerInitializer.JEXAMPLE_BUNDLE);
					URL url = FileLocator.find(bundle, new Path("wizardpage.html"), null);
					String fname = FileLocator.toFileURL(url).getFile();
					browser.setUrl("file://"+fname);
				} catch (IOException e) {
					e.printStackTrace();
				}
				composite.layout(true);
			}
		});
	}

	
}

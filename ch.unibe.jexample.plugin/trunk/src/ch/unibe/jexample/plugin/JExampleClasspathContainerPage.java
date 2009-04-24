package ch.unibe.jexample.plugin;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class JExampleClasspathContainerPage extends WizardPage implements IClasspathContainerPage {

    public JExampleClasspathContainerPage() {
        super("JExampleClasspathContainerPage");
        setTitle("JExample Wizard title.");
        setDescription("JExample Wizard description.");
        setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
    }

    public boolean finish() {
        return true;
    }

    public IClasspathEntry getSelection() {
        return JavaCore.newContainerEntry(new Path(JExampleClasspathContainerInitializer.ID));
    }

    public void setSelection(IClasspathEntry containerEntry) {
    }

    public void createControl(Composite parent) {
        Composite composite= new Composite(parent, SWT.NONE);
        setControl(composite);    
    }

}

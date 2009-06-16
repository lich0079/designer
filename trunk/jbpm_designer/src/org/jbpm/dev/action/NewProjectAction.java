package org.jbpm.dev.action;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jbpm.gd.jpdl.wizard.NewProcessProjectWizard;

public class NewProjectAction extends Action {

    private final IWorkbenchWindow window;
    
    public NewProjectAction(IWorkbenchWindow window) {
        this.window = window;
        setId("org.jbpm.dev.newProject");
        setText("新建项目");
        setActionDefinitionId("org.jbpm.dev.newProject");
        setToolTipText("新建项目");
        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                "org.jbpm.gd.jpdl", "icons/full/obj16/process_definition_enabled.gif"));
    }


    public void run() {
        NewProcessProjectWizard wizard = new NewProcessProjectWizard();
        wizard.init(PlatformUI.getWorkbench(), null);
        
        WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
        dialog.open();
    }

}

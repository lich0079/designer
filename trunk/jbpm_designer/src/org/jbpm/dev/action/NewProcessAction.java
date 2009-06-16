package org.jbpm.dev.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jbpm.gd.jpdl.wizard.NewProcessDefinitionWizard;

public class NewProcessAction extends Action {
    private final IWorkbenchWindow window;

    public NewProcessAction(IWorkbenchWindow window) {
        this.window = window;
        setId("org.jbpm.dev.newProject");
        setText("新建流程");
        setActionDefinitionId("org.jbpm.dev.newProcess");
        setToolTipText("新建流程");
        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                "org.jbpm.gd.jpdl",
                "icons/full/obj16/event_enabled.gif"));
    }

    public void run() {
        NewProcessDefinitionWizard wizard = new NewProcessDefinitionWizard();
        ISelection selection = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getSelectionService()
                .getSelection();
        if (selection != null && selection instanceof StructuredSelection) {
            wizard.init(PlatformUI.getWorkbench(),
                    (StructuredSelection) selection);
        } else {
            wizard.init(PlatformUI.getWorkbench(), null);
        }

        WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), wizard);
        dialog.open();
    }

}

//package org.jbpm.gd.jpdl.action;
//
//import org.eclipse.gef.EditPart;
//import org.eclipse.gef.commands.CommandStack;
//import org.eclipse.jface.action.IAction;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.jface.viewers.StructuredSelection;
//import org.eclipse.ui.IObjectActionDelegate;
//import org.eclipse.ui.IWorkbenchPart;
//import org.eclipse.ui.views.contentoutline.ContentOutline;
//import org.jbpm.gd.common.editor.OutlineViewer;
//import org.jbpm.gd.jpdl.command.TaskCreateCommand;
//import org.jbpm.gd.jpdl.editor.JpdlEditor;
//import org.jbpm.gd.jpdl.model.TaskContainer;
//
//public class AddTaskDelegate implements IObjectActionDelegate {
//    
//    private IWorkbenchPart targetPart;
//    private EditPart selectedPart;
//
//    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
//        this.targetPart = targetPart;
//    }
//
//    public void run(IAction action) {
//        CommandStack commandStack;
//        if (targetPart instanceof ContentOutline) {
//            commandStack = ((OutlineViewer)((ContentOutline)targetPart).getCurrentPage()).getCommandStack();            
//        } else if (targetPart instanceof JpdlEditor) {
//            commandStack = ((JpdlEditor)targetPart).getCommandStack();
//        } else return;
////        TaskContainer target = tasksSection.getTaskContainer();
////        TaskCreateCommand command = new TaskCreateCommand(target.getFactory());
////        command.setTaskContainer(target);
////        NodeAddTaskCommand command = new NodeAddTaskCommand();
////        command.setTarget((Node)selectedPart.getModel());
//        commandStack.execute(command);
//    }
//
//    public void selectionChanged(IAction action, ISelection selection) {
//        if (selection == null) return;
//        if (!(selection instanceof StructuredSelection)) return;
//        Object object = ((StructuredSelection)selection).getFirstElement();
//        if (object instanceof EditPart) {
//            selectedPart = (EditPart)object;
//        }
//    }
//    
//}
//

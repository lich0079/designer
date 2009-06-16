package org.jbpm.dev;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;

public class MyIDEWorkbenchAdvisor extends IDEWorkbenchAdvisor {
    public String getInitialWindowPerspectiveId() {
        return "org.jbpm.gd.jpdl.JpdlPerspective"; 
    }
    
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            IWorkbenchWindowConfigurer configurer) {
        return new MyIDEWorkbenchWindowAdvisor(this, configurer);
    }
}

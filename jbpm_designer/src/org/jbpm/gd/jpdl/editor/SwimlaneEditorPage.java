package org.jbpm.gd.jpdl.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.jbpm.gd.jpdl.model.ProcessDefinition;
import org.jbpm.gd.jpdl.model.Swimlane;
import org.jbpm.gd.jpdl.properties.SwimlaneConfigurationComposite;
import org.jbpm.gd.jpdl.properties.SwimlaneContainerSectionActionBarContributor;




public class SwimlaneEditorPage extends EditorPart {
    JpdlEditor editor;
    
    public SwimlaneEditorPage(JpdlEditor editor) {
        this.editor = editor;
    }

    private Composite detailsArea;  
    private Table swimlaneTable;    
    private ProcessDefinition processDefinition;
    private Swimlane selectedSwimlane;
    private SwimlaneConfigurationComposite swimlaneConfigurationComposite;
    private TabbedPropertySheetPage tabbedPropertySheetPage;    
    private SwimlaneContainerSectionActionBarContributor actionBarContributor;
    private FormToolkit toolkit;
    
    
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub
        
    }

    public void doSaveAs() {
        // TODO Auto-generated method stub
        
    }

    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        setSite(site);
        setInput(input);
    }

    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    public void createPartControl(Composite parent) {
        toolkit = new FormToolkit(parent.getDisplay());
        ScrolledForm form = toolkit.createScrolledForm(parent);
        form.setText("Swimlanes");
        setPartLayout(form);
        createMaster(toolkit, form.getBody());              
        createDetails(toolkit, form.getBody());                     
    }

    private void setPartLayout(ScrolledForm form) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        form.getBody().setLayout(layout);
    }

    public void setFocus() {
        // TODO Auto-generated method stub
        
    }
    
    private void createMaster(FormToolkit toolkit, Composite form) {
        swimlaneTable = toolkit.createTable(
                form, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        swimlaneTable.setLayoutData(createSwimlaneListLayoutData());
        swimlaneTable.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
//                handleSwimlaneListSelection();
            }           
        });
        actionBarContributor.createPopupMenu(swimlaneTable);
    }
    
    private void createDetails(FormToolkit toolkit, Composite composite) {
        detailsArea = toolkit.createComposite(composite);
        detailsArea.setLayout(new FormLayout());
        detailsArea.setLayoutData(createDetailsAreaLayoutData());       
        createSwimlaneConfigurationComposite();
    }
    
    private FormData createDetailsAreaLayoutData() {
        FormData data = new FormData();
        data.left = new FormAttachment(swimlaneTable, 0);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, 0);
        data.bottom = new FormAttachment(100, 0);
        return data;
    }
    
    private void createSwimlaneConfigurationComposite() {
//        swimlaneConfigurationComposite = SwimlaneConfigurationComposite.create(toolkit, detailsArea);
    }
    
    private FormData createSwimlaneListLayoutData() {
        FormData data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(20, 0);
        data.top = new FormAttachment(0, 0);
        data.bottom = new FormAttachment(100, 0);
        return data;
    }
}

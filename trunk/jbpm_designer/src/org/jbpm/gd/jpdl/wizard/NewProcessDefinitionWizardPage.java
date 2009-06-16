/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.gd.jpdl.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jbpm.gd.jpdl.util.ProjectFinder;

public class NewProcessDefinitionWizardPage extends WizardPage {
	
    /**
     * ȥ��ѡ��Ŀ¼�������ı� ��ť  �ͱ����ļ��������ֶ�
     */
//	private Text containerText;
	private Text processText;
//	private Button browseButton;
	
	private IWorkspaceRoot workspaceRoot;
//	private String containerName;
	//*********************************************
	/**
	 * ���һ���ֶ� ����������Ŀ��
	 */
	private  String projectName;
	//*****************************
	
	public NewProcessDefinitionWizardPage() {
		super("���̶���");
		setTitle("�������̶���");
		setDescription("�������̶���");	
		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		//����༭���Ѵ򿪣�����ͨ���������Ŀ������������ProjectFinder
		IEditorPart editor=PlatformUI.getWorkbench().getActiveWorkbenchWindow().
		                    getActivePage().getActiveEditor();
		if(editor!=null){
		    projectName=((IContainer)ProjectFinder.getCurrentProject().
		                getProject()).getName();
		}
	}

	public void init(IStructuredSelection selection) {
        if (selection != null && !selection.isEmpty()) {
            Object object = selection.getFirstElement();
            if(IProject.class.isInstance(object)){
                //���ѡ�е�����Ŀ����֮ǰû�г�ʼ����Ŀ��   ������Ŀ����������
                //����ж����Ŀ�Ļ� ���Ǳ���ͨ�����ַ�ʽ���������̣�
                //���������̻Ὠ�ڣ�ԭ���ı༭�����ڱ༭���ļ����ڵģ���Ŀ
                //���ַ�ʽ���Ա�֤�����̽���ѡ������Ŀ��
                IContainer container = (IContainer)object;
                projectName=container.getName();
                //*****************************
            } else if(JavaProject.class.isInstance(object)){
                //
                JavaProject container = (JavaProject)object;
                IProject project=container.getProject();
                IContainer container2 = (IContainer)project;
                projectName=container2.getName();
                //*****************************
                
            }
        }
//		IContainer container = null;
//		if (selection != null && !selection.isEmpty()) {
//			Object object = selection.getFirstElement();
//			if (IFile.class.isInstance(object) && !IContainer.class.isInstance(object)) {
//				container = ((IFile)object).getParent();
//			} else if(projectName==null && IProject.class.isInstance(object)){
//			    //���ѡ�е�����Ŀ ������Ŀ��
//			    container = (IContainer)object;
//                projectName=container.getName();
//                //*****************************
//            }
//			else if (IContainer.class.isInstance(object)) {
//				container = (IContainer)object;
//			}
//		}
//		initContainerName(container);//��ʼ���ļ�������  ������Ҫ
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = createClientArea(parent);		
		createLabel(composite);	
//		createContainerField(composite);//����ѡ���ļ�������� ����Ҫ
		createProcessField(composite);
		setControl(composite);
		Dialog.applyDialogFont(composite);		
		setPageComplete(false);
	}

	private void createLabel(Composite composite) {
		Label label= new Label(composite, SWT.WRAP);
		label.setText("������������.");
		GridData gd= new GridData();
		gd.widthHint= convertWidthInCharsToPixels(80);
		gd.horizontalSpan= 3;
		label.setLayoutData(gd);
	}

	private Composite createClientArea(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		layout.numColumns= 3;
		composite.setLayout(layout);
		return composite;
	}
	
//	private void createContainerField(Composite parent) {
//		Label label = new Label(parent, SWT.NONE);
//		label.setText("Դ���ļ��� : ");
//		containerText = new Text(parent, SWT.BORDER);
//		containerText.setText(containerName);
//		containerText.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				verifyContentsValid();
//			}
//		});
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		containerText.setLayoutData(gd);
//		browseButton = new Button(parent, SWT.PUSH);
//		browseButton.setText("���...");
//		browseButton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				chooseContainer();
//			}			
//		});
//		gd = new GridData();
//		gd.widthHint = convertWidthInCharsToPixels(15);
//		browseButton.setLayoutData(gd);
//	}
	
	private void createProcessField(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("������ : ");
		processText = new Text(parent, SWT.BORDER);
		processText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyContentsValid();
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		processText.setLayoutData(gd);		
	}
	
//	private void chooseContainer() {
//		WorkbenchContentProvider provider= new WorkbenchContentProvider();
//		ILabelProvider labelProvider= new WorkbenchLabelProvider(); 
//		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(getShell(), labelProvider, provider);
//		dialog.setTitle("ѡ���ļ���");
//		dialog.setMessage("ѡ���ļ���");
//		dialog.setInput(ResourcesPlugin.getWorkspace());
//		dialog.addFilter(createViewerFilter());
//		dialog.open();
//		initContainerName((IContainer)dialog.getFirstResult());
//		containerText.setText(containerName);
//	}

	private ViewerFilter createViewerFilter() {
		ViewerFilter filter= new ViewerFilter() {
			public boolean select(Viewer viewer, Object parent, Object element) {
				return IFolder.class.isInstance(element) || IProject.class.isInstance(element); 
			}
		};
		return filter;
	}
	
	
//	private void initContainerName(IContainer elem) {
//		containerName = (elem == null) ? "" : elem.getFullPath().makeRelative().toString(); 
//	}
	
	private void verifyContentsValid() {
	    /**
	     * ��Ϊ���ֶ��Ѳ����� ����������
	     */
//		if (!checkContainerPathValid()) {    
//			setErrorMessage("�ļ��в�����.");
//			setPageComplete(false);
//		} else 
	    //******************************
		    if (isProcessNameEmpty()) {
			setErrorMessage("����������.");
			setPageComplete(false);
		} else if (processExists()){
			setErrorMessage("���������Ѵ���.");
			setPageComplete(false);
		}else if(projectName==null || projectName.equals("")){
		    setErrorMessage("��ѡ����Ŀ����������.");
            setPageComplete(false);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}
	
	private boolean processExists() {
		IPath path = new Path(getProcessName());
//		IPath path = new Path(containerText.getText()).append(getProcessName());
		return workspaceRoot.getFolder(path).exists();
	}
	
	private boolean isProcessNameEmpty() {
		String str = processText.getText();
		return str == null || "".equals(str);
	}
	
//	private boolean checkContainerPathValid() {
//		if ("".equals(containerText.getText())) {
//			return false;
//		}
//		IPath path = new Path(containerText.getText());
//		return workspaceRoot.getFolder(path).exists();
//	}
	
	private String getProcessName() {
	    /**
	     * �˴��Ķ���debug�ó��Ľ�� ����Ŀ¼��ʱ�����ƽṹ������  "��Ŀ��/�ļ�����"
	     * ���ӵĻ���ֻ�� �ļ������� ����쳣  ��û�׳� ������ ����debug��
	     * ���������Ҫ�����Ŀ���Ƚ��鷳��Ŀǰ�İ취ֻ����ѡ�и���Ŀ��ͼ��ʱ �ſɵõ���Ŀ��
	     */
		return projectName+"/"+processText.getText(); // + ".par";
	}
	
	public IFolder getProcessFolder() {
		IPath path = new Path(getProcessName());
		
		return workspaceRoot.getFolder(path);
	}
	
}

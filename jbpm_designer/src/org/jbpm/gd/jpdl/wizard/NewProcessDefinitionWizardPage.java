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
     * 去掉选择目录的提升文本 按钮  和保存文件夹名的字段
     */
//	private Text containerText;
	private Text processText;
//	private Button browseButton;
	
	private IWorkspaceRoot workspaceRoot;
//	private String containerName;
	//*********************************************
	/**
	 * 添加一个字段 用来保存项目名
	 */
	private  String projectName;
	//*****************************
	
	public NewProcessDefinitionWizardPage() {
		super("流程定义");
		setTitle("创建流程定义");
		setDescription("创建流程定义");	
		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		//如果编辑器已打开，可以通过它获得项目名，方法来自ProjectFinder
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
                //如果选中的是项目并且之前没有初始化项目名   保存项目名（放弃）
                //如果有多个项目的话 还是必须通过这种方式建立新流程，
                //否则新流程会建在（原来的编辑器正在编辑的文件所在的）项目
                //这种方式可以保证新流程建在选定的项目中
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
//			    //如果选中的是项目 保存项目名
//			    container = (IContainer)object;
//                projectName=container.getName();
//                //*****************************
//            }
//			else if (IContainer.class.isInstance(object)) {
//				container = (IContainer)object;
//			}
//		}
//		initContainerName(container);//初始化文件夹名的  不在需要
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = createClientArea(parent);		
		createLabel(composite);	
//		createContainerField(composite);//创建选择文件夹组件的 不需要
		createProcessField(composite);
		setControl(composite);
		Dialog.applyDialogFont(composite);		
		setPageComplete(false);
	}

	private void createLabel(Composite composite) {
		Label label= new Label(composite, SWT.WRAP);
		label.setText("请输入流程名.");
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
//		label.setText("源码文件夹 : ");
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
//		browseButton.setText("浏览...");
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
		label.setText("流程名 : ");
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
//		dialog.setTitle("选择文件夹");
//		dialog.setMessage("选择文件夹");
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
	     * 因为该字段已不存在 所以无需检查
	     */
//		if (!checkContainerPathValid()) {    
//			setErrorMessage("文件夹不存在.");
//			setPageComplete(false);
//		} else 
	    //******************************
		    if (isProcessNameEmpty()) {
			setErrorMessage("输入流程名.");
			setPageComplete(false);
		} else if (processExists()){
			setErrorMessage("该流程名已存在.");
			setPageComplete(false);
		}else if(projectName==null || projectName.equals("")){
		    setErrorMessage("请选择项目来创建流程.");
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
	     * 此处改动是debug得出的结果 创建目录的时候名称结构必须是  "项目名/文件夹名"
	     * 不加的话就只有 文件夹名， 会出异常  但没抛出 看不到 可以debug到
	     * 在这个类中要获得项目名比较麻烦，目前的办法只有在选中该项目的图标时 才可得到项目名
	     */
		return projectName+"/"+processText.getText(); // + ".par";
	}
	
	public IFolder getProcessFolder() {
		IPath path = new Path(getProcessName());
		
		return workspaceRoot.getFolder(path);
	}
	
}

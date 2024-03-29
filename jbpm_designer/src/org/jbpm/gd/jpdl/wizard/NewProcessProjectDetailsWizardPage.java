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

import java.util.Iterator;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jbpm.gd.jpdl.prefs.PreferencesManager;

public class NewProcessProjectDetailsWizardPage extends WizardPage {
	
	Combo combo;
	Button checkbox;
	
	public NewProcessProjectDetailsWizardPage() {
		super("流程项目详细信息");
		setTitle("请选择项目详细信息");
		setDescription("请选择项目详细信息");	
	}

	public void createControl(Composite parent) {
		Composite composite = createClientArea(parent);
		createCoreVersionGroup(composite);
		createGenerateTemplateGroup(composite);
		setControl(composite);
	}
	
	private void createCoreVersionGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText("选择项目的 jBPM 路径");
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createCombo(group);
	}
	
	private void createGenerateTemplateGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText("选择是否为项目生成简单的流程");
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createCheckbox(group);
	}
	
	private void createCheckbox(Composite composite) {
		checkbox = new Button(composite, SWT.CHECK);
		checkbox.setText("生成简单流程定义动作处理器和JUnit测试");
		checkbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checkbox.setSelection(true);
	}
	
	private void createCombo(Composite composite) {
		combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	fillComboWithPreferenceRuntimes();
    	combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(combo.getSelectionIndex() != -1);
			}
    		
    	});
	}
	
	private void fillComboWithPreferenceRuntimes() {
		Iterator iterator = PreferencesManager.INSTANCE.getJbpmInstallationMap().keySet().iterator();
		int counter = 0;
		while (iterator.hasNext()) {
			counter++;
			String next = (String)iterator.next();
			combo.add(next);
			if (PreferencesManager.INSTANCE.getPreferredJbpmName().equals(next)) {
				combo.select(counter - 1);
			}
		}
	}

	private Composite createClientArea(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		layout.numColumns= 1;
		composite.setLayout(layout);
		return composite;
	}
	
	public String getCoreJbpmName() {
		String result = combo.getItem(combo.getSelectionIndex());
		return result;
	}
	
}

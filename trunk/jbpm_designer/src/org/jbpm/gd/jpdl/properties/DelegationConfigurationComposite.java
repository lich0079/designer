package org.jbpm.gd.jpdl.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.jbpm.gd.common.model.GenericElement;
import org.jbpm.gd.jpdl.dialog.ChooseDelegationClassDialog;
import org.jbpm.gd.jpdl.model.Delegation;
import org.jbpm.gd.jpdl.util.AutoResizeTableLayout;

public class DelegationConfigurationComposite implements KeyListener,
        SelectionListener, FocusListener {

    public static DelegationConfigurationComposite create(
            TabbedPropertySheetWidgetFactory widgetFactory, Composite parent,
            ChooseDelegationClassDialog dialog) {
        DelegationConfigurationComposite result = new DelegationConfigurationComposite();
        result.chooseDelegationClassDialog = dialog;
        result.widgetFactory = widgetFactory;
        result.parent = parent;
        result.create();
        return result;
    }

    private TabbedPropertySheetWidgetFactory widgetFactory;
    private Composite parent;
    private ChooseDelegationClassDialog chooseDelegationClassDialog;

    private Delegation delegation;

    private Label nameLabel;
    private Text nameText;
//    private Button searchButton;
    private Label configTypeLabel;
    private CCombo configTypeCombo;

    private HashMap configAreaPages = new HashMap();

    private LabelComposite messageLabel;
    private TextComposite constructorTextComposite;
    private TextComposite compatibilityTextComposite;
    private TableComposite fieldTableComposite;
    private TableComposite beanTableComposite;

    private DelegationConfigurationComposite() {
    }

    public void setDelegation(Delegation delegation) {
        if (this.delegation == delegation)
            return;
        unhookListeners();
        this.delegation = delegation;
        clearControls();
        if (delegation != null) {
            updateControls();
            hookListeners();
        }
    }

    private void hookListeners() {
        nameText.addKeyListener(this);
//        searchButton.addSelectionListener(this);
        configTypeCombo.addSelectionListener(this);
        constructorTextComposite.text.addFocusListener(this);
        compatibilityTextComposite.text.addFocusListener(this);
    }

    private void unhookListeners() {
        nameText.removeKeyListener(this);
//        searchButton.removeSelectionListener(this);
        configTypeCombo.removeSelectionListener(this);
        constructorTextComposite.text.removeFocusListener(this);
        compatibilityTextComposite.text.removeFocusListener(this);
    }

    private void clearControls() {
        nameText.setText("");
        configTypeCombo.setText("Field");
        showPage("Message");
        messageLabel.setText("");
        fieldTableComposite.table.removeAll();
        beanTableComposite.table.removeAll();
        constructorTextComposite.text.setText("");
        compatibilityTextComposite.text.setText("");
    }

    private void showPage(String key) {
        Iterator iter = configAreaPages.keySet().iterator();
        while (iter.hasNext()) {
            String candidate = (String) iter.next();
            ((DelegationConfigurationWidget) configAreaPages.get(candidate))
                    .setVisible(candidate.equals(key));
        }
    }

    private void updateControls() { 
        nameText.setText(getDelegationClassName());
        configTypeCombo.setText(fromConfigType(getDelegationConfigType()));
        updatePageBook();
    }

    //��Ϊ��Ҫ���ݷ��������������ֶΣ����к�type��صĶ���
    private void updatePageBook() {
//        IType type = getClassFor(nameText.getText());
//        updateFieldTableComposite(type);
//        updateBeanTableComposite(type);
//        updateConstructorTextComposite();
//        updateCompatibilityTextComposite();
//        updateVisiblePage(type != null);
        updateFieldTableComposite();
        updateBeanTableComposite();
        updateConstructorTextComposite();
        updateCompatibilityTextComposite();
//        updateVisiblePage(type != null);
        updateVisiblePage(true);
    }

    /**
     * ��ԭ���ķ�����ȣ�ԭ���ķ����Ǹ��ݷ������ж��ֶε� ��ȷ���� field������ ������ȷ����
     * ��֮�� field������ �����޷�ͨ��ԭ���ķ�ʽȷ�� Ҫ����delegation���ӽڵ���ȷ��
     * �������һ�����ڵ�һ��չʾ���ý��� �� ��������ʱ���õ�
     */
    private void updateFieldTableComposite() {
//        for (int i = 0; i < 1; i++) {
//            TableItem item = new TableItem(fieldTableComposite.table, SWT.NONE);
//        }
        
        if ("field".equals(getDelegationConfigType())) {
            for (int i = 0; i < delegation.getGenericElements().length; i++) {
                TableItem item = new TableItem(fieldTableComposite.table, SWT.NONE);
                item.setText(0, delegation.getGenericElements()[i].getName());
            }
            updateTableItems(fieldTableComposite.table.getItems());
        }
    }
    
    private void updateBeanTableComposite() {
//        for (int i = 0; i < 1; i++) {
//            TableItem item = new TableItem(beanTableComposite.table, SWT.NONE);
//        }
        
        if ("bean".equals(getDelegationConfigType())) {
            for (int i = 0; i < delegation.getGenericElements().length; i++) {
                TableItem item = new TableItem(beanTableComposite.table, SWT.NONE);
                item.setText(0, delegation.getGenericElements()[i].getName());
            }
            updateTableItems(beanTableComposite.table.getItems());
        }
    }
    
    
    
//    private void updateFieldTableComposite(IType type) {
//        if (type == null)
//            return;
//        List list = getFields(type);
//        for (int i = 0; i < list.size(); i++) {
//            TableItem item = new TableItem(fieldTableComposite.table, SWT.NONE);
//            
//        }
//        if ("field".equals(getDelegationConfigType())) {
//            updateTableItems(fieldTableComposite.table.getItems());
//        }
//    }

    private void updateTableItems(TableItem[] items) {
        //�ó��ڵ�������ӽڵ�
        GenericElement[] elements = delegation.getGenericElements();
        //�����ڵ�������ӽڵ�
        for (int i = 0; i < elements.length; i++) {
            //����table��������
            for (int j = 0; j < items.length; j++) {
                //�õ��ýڵ������  
                String name = elements[i].getName() == null ? "" : elements[i]
                        .getName();
                //�õ��ýڵ��ֵ
                String value = elements[i].getValue() == null ? ""
                        : elements[i].getValue();
                //����ýڵ����������е�������ͬ  ѡ��  ��ֵ
                if (name.equals(items[j].getText(0))) {
                    items[j].setChecked(true);
                    items[j].setText(1, value);
                    items[j].setData(elements[i]);
                    break;
                }
            }
        }
    }
    
//    private void updateBeanTableComposite(IType type) {
//        if (type == null)
//            return;
//        List list = getSetters(type);
//        for (int i = 0; i < list.size(); i++) {
//            TableItem item = new TableItem(beanTableComposite.table, SWT.NONE);
//            item.setText(0, (String) list.get(i));
//        }
//        if ("bean".equals(getDelegationConfigType())) {
//            updateTableItems(beanTableComposite.table.getItems());
//        }
//    }

    private void updateConstructorTextComposite() {
        boolean valid = "constructor".equals(getDelegationConfigType());
        constructorTextComposite.text
                .setText(valid ? getDelegationConfigString() : "");
    }

    private void updateCompatibilityTextComposite() {
        boolean valid = "configuration-property"
                .equals(getDelegationConfigType());
        constructorTextComposite.text
                .setText(valid ? getDelegationConfigString() : "");
    }

    private void updateVisiblePage(boolean validClass) {
        //���û������·���ҵ�����  ��ʾ������Ϣ
        if (!validClass) {
            showInvalidTypeMessage();
        } else {
            handleValidType();
        }
    }

    private String getDelegationConfigType() {
        return delegation.getConfigType() == null ? "field" : delegation
                .getConfigType();
    }

    private String getDelegationClassName() {
        return delegation.getClassName() == null ? "" : delegation
                .getClassName();
    }

    private String getDelegationConfigString() {
        return delegation.getConfigInfo() == null ? "" : delegation
                .getConfigInfo();
    }

    private void create() {
        nameLabel = widgetFactory.createLabel(parent, "����");
        nameText = widgetFactory.createText(parent, "");
        nameText.setSize(20, 1);
//        searchButton = widgetFactory.createButton(parent, "����...", SWT.PUSH);
        configTypeLabel = widgetFactory.createLabel(parent, "��������");
        configTypeCombo = widgetFactory.createCCombo(parent);
        configTypeCombo.setItems(getConfigurationTypes());
        configTypeCombo.setEditable(false);
        createPages(parent);
        nameLabel.setLayoutData(createNameLabelLayoutData());
        nameText.setLayoutData(createNameTextLayoutData());
//        searchButton.setLayoutData(createSearchButtonLayoutData());
        configTypeLabel.setLayoutData(createConfigTypeLabelLayoutData());
        configTypeCombo.setLayoutData(createConfigTypeComboLayoutData());
    }

    private void createPages(Composite composite) {
        messageLabel = new LabelComposite();
        messageLabel.create(composite);
        configAreaPages.put("Message", messageLabel);
        fieldTableComposite = new TableComposite();
        fieldTableComposite.create(composite);
        configAreaPages.put("Field", fieldTableComposite);
        beanTableComposite = new TableComposite();
        beanTableComposite.create(composite);
        configAreaPages.put("Bean", beanTableComposite);
        constructorTextComposite = new TextComposite();
        constructorTextComposite.create(composite);
        configAreaPages.put("Constructor", constructorTextComposite);
        compatibilityTextComposite = new TextComposite();
        compatibilityTextComposite.create(composite);
        configAreaPages.put("Compatibility", compatibilityTextComposite);
    }

    private String[] getConfigurationTypes() {
        return new String[] { "Field", "Bean", "Constructor", "Compatibility" };
    }

    private FormData createNameLabelLayoutData() {
        FormData result = new FormData();
        result.left = new FormAttachment(0, 0);
        result.top = new FormAttachment(0, 2);
        return result;
    }

    private FormData createNameTextLayoutData() {
        FormData result = new FormData();
        result.left = new FormAttachment(nameLabel, 0);
        result.right = new FormAttachment(configTypeLabel, 0);
        result.top = new FormAttachment(0, 0);
        return result;
    }

    private FormData createSearchButtonLayoutData() {
        FormData result = new FormData();
        result.right = new FormAttachment(configTypeLabel, 0);
        result.top = new FormAttachment(0, -3);
        return result;
    }

    private FormData createConfigTypeLabelLayoutData() {
        FormData result = new FormData();
        result.right = new FormAttachment(configTypeCombo, 0);
        result.top = new FormAttachment(0, 2);
        return result;
    }

    private FormData createConfigTypeComboLayoutData() {
        FormData result = new FormData();
        result.right = new FormAttachment(100, 0);
        result.top = new FormAttachment(0, -2);
        return result;
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.widget == nameText) {
            handleNameTextChange();
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    private void handleNameTextChange() {
        String newName = nameText.getText();
        // ����û�� ����
        if (newName.equals(delegation.getClassName()))
            return;
        // ��delegation������Ϊѡ�е�����
        delegation.setClassName(newName);
        // ���������Ϣ
        if (delegation.getConfigInfo() != null) {
            delegation.setConfigInfo(null);
        }
        GenericElement[] genericElements = delegation.getGenericElements();
        fieldTableComposite.table.removeAll();
        beanTableComposite.table.removeAll();
        for (int i = 0; i < genericElements.length; i++) {
            delegation.removeGenericElement(genericElements[i]);
        }
        updatePageBook();
    }

    private void handleValidType() {
        String configType = delegation.getConfigType();
        if ("field".equals(configType)) {
            handleFieldConfigType();
        } else if ("bean".equals(configType)) {
            handleBeanConfigType();
        } else if ("constructor".equals(configType)) {
            handleConstructorConfigType();
        } else if ("configuration-property".equals(configType)) {
            handleCompatibilityConfigType();
        }
    }

    private void handleFieldConfigType() {
//        if (fieldTableComposite.table.getItemCount() == 0) {
//            messageLabel.setText("��û���ֶ�");
//            showPage("Message");
//        } else {
            showPage("Field");
            restoreConfigElements(fieldTableComposite.table.getItems());
//        }
    }
    
    //ֻ�����͸ı��ʱ�� �� ˢ��ҳ��ʱ����
    private void restoreConfigElements(TableItem[] items) {
        //����ӽڵ�Ϊ0������ҳ���Ԫ�ر�ѡ�У�����ӽ����ڵ�
        //��ô���粻Ϊ0�Ļ� ����ı丸�ڵ��״̬
        if (delegation.getGenericElements().length == 0) {
            for (int i = 0; i < items.length; i++) {
                if (items[i].getChecked()) {
                    addGenericElement(items[i]);
                }
            }
        }
    }

    private void handleBeanConfigType() {
//        if (beanTableComposite.table.getItemCount() == 0) {
//            messageLabel.setText("��û��set����");
//            showPage("Message");
//        } else {
            showPage("Bean");
            restoreConfigElements(beanTableComposite.table.getItems());
//        }
    }

    
//    private List getFields(IType type) {
//        List result = new ArrayList();
//        try {
//            IField[] fields = type.getFields();
//            for (int i = 0; i < fields.length; i++) {
//                if (!Flags.isStatic(fields[i].getFlags())) {
//                    result.add(fields[i].getElementName());
//                }
//            }
//        } catch (JavaModelException e) {
//            Logger.logError("Error while getting the fields for type " + type
//                    + ".", e);
//        }
//        return result;
//    }
    
//    private List getSetters(IType type) {
//        List result = new ArrayList();
//        try {
//            IMethod[] methods = type.getMethods();
//            for (int i = 0; i < methods.length; i++) {
//                if (methods[i].getElementName().startsWith("set")) {
//                    StringBuffer buff = new StringBuffer(methods[i]
//                            .getElementName().substring(3));
//                    buff.setCharAt(0, Character.toLowerCase(buff.charAt(0)));
//                    result.add(buff.toString());
//                }
//            }
//        } catch (JavaModelException e) {
//            Logger.logError("Error while getting the setters for type " + type
//                    + ".", e);
//        }
//        return result;
//    }

    private void handleConstructorConfigType() {
        showPage("Constructor");
        if (delegation.getConfigInfo() == null) {
            delegation.setConfigInfo(constructorTextComposite.text.getText());
        } else {
            constructorTextComposite.text.setText(delegation.getConfigInfo());
        }
    }

    private void handleCompatibilityConfigType() {
        showPage("Compatibility");
        if (delegation.getConfigInfo() == null) {
            delegation.setConfigInfo(compatibilityTextComposite.text.getText());
        } else {
            compatibilityTextComposite.text.setText(delegation.getConfigInfo());
        }
    }

    private void showInvalidTypeMessage() {
        messageLabel.setText("�಻����Ŀ��·����.");
        showPage("Message");
    }

//    private IType getClassFor(String className) {
//        if (className == null)
//            return null;
//        try {
//            return ProjectFinder.getCurrentProject().findType(className);
//        } catch (JavaModelException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    //����µ�Ԫ��  ��Ԫ�ؿ������Ϊxml�е�һ���ڵ�Ԫ��  ���Լ��ļ�ֵ  ���������ӽڵ�
    private void addGenericElement(TableItem item) {
        //��ü�
        String name = item.getText(0) == null ? "" : item.getText(0);
        //���ֵ
        String value = item.getText(1) == null ? "" : item.getText(1);
        //����ģʽ����һ��Ԫ��
        GenericElement genericElement = (GenericElement) delegation
                .getFactory().createById("org.jbpm.gd.jpdl.genericElement");
        //��ֵ
        genericElement.setName(name);
        genericElement.setValue(value);
        //��������ڵ���
        delegation.addGenericElement(genericElement);
        //��view����model
        item.setData(genericElement);
    }

    private void removeGenericElement(TableItem item) {
        GenericElement genericElement = (GenericElement) item.getData();
        if (genericElement != null) {
            delegation.removeGenericElement(genericElement);
        }
    }

    public void widgetSelected(SelectionEvent e) {
//        if (e.widget == searchButton) {
//            handleSearchButtonSelected();
//        } else 
            if (e.widget == configTypeCombo) {
            handleConfigTypeComboChanged();
        }
    }

    //����ѡ���ı�ʱ���¼�
    private void handleConfigTypeComboChanged() {
        //��øı�������
        String newConfigType = toConfigType(configTypeCombo.getText());
        //������ͬ ����
        if (delegation.getConfigType().equals(newConfigType))
            return;
        //���delegation�е���Ϣ
        delegation.setConfigInfo(null);
        GenericElement[] genericElements = delegation.getGenericElements();
        for (int i = 0; i < genericElements.length; i++) {
            delegation.removeGenericElement(genericElements[i]);
        }
        delegation.setConfigType(newConfigType);
//        updateVisiblePage(getClassFor(nameText.getText()) != null);
        updateVisiblePage(true);
    }

    private void handleSearchButtonSelected() {
        FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
        fileDialog.setText("ActionHandler");
        fileDialog.setFilterExtensions(new String[] { "*.java" });
        String chosenClass = fileDialog.open();
        // String chosenClass = new
        // FileDialog(parent.getShell(),SWT.OPEN).open();
        // String chosenClass = chooseDelegationClassDialog.openDialog();
        if (chosenClass != null) {
            nameText.setText(chosenClass);
            handleNameTextChange();
        }
    }

    private String toConfigType(String configType) {
        if ("Field".equals(configType))
            return "field";
        if ("Bean".equals(configType))
            return "bean";
        if ("Constructor".equals(configType))
            return "constructor";
        if ("Compatibility".equals(configType))
            return "configuration-property";
        return null;
    }

    private String fromConfigType(String configType) {
        if ("field".equals(configType))
            return "Field";
        if ("bean".equals(configType))
            return "Bean";
        if ("constructor".equals(configType))
            return "Constructor";
        if ("configuration-property".equals(configType))
            return "Compatibility";
        return null;
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        if (e.widget == constructorTextComposite.text) {
            delegation.setConfigInfo(constructorTextComposite.text.getText());
        } else if (e.widget == compatibilityTextComposite.text) {
            delegation.setConfigInfo(compatibilityTextComposite.text.getText());
        }
    }

    public Delegation getDelegation() {
        return delegation;
    }

    private interface DelegationConfigurationWidget {
        void setVisible(boolean visible);
    }

    private class LabelComposite implements DelegationConfigurationWidget {

        private Label label;

        private void create(Composite parent) {
            label = widgetFactory.createLabel(parent, "");
            label.setLayoutData(createLabelLayoutData());
        }

        private void setText(String message) {
            label.setText(message);
        }

        public void setVisible(boolean visible) {
            label.setVisible(visible);
        }

        private FormData createLabelLayoutData() {
            FormData result = new FormData();
            result.left = new FormAttachment(0, 0);
            result.right = new FormAttachment(100, 0);
            result.top = new FormAttachment(configTypeCombo, 2);
            return result;
        }

    }

    private class TextComposite implements DelegationConfigurationWidget {

        private Label label;
        private Text text;

        private void create(Composite parent) {
            label = widgetFactory.createLabel(parent, "������Ϣ");
            text = widgetFactory.createText(parent, "", SWT.MULTI
                    | SWT.V_SCROLL);
            label.setLayoutData(createLabelLayoutData());
            text.setLayoutData(createTextLayoutData());
        }

        private FormData createLabelLayoutData() {
            FormData result = new FormData();
            result.left = new FormAttachment(0, 0);
            result.top = new FormAttachment(configTypeCombo, 2);
            return result;
        }

        private FormData createTextLayoutData() {
            FormData result = new FormData();
            result.top = new FormAttachment(configTypeCombo, 0);
            result.left = new FormAttachment(nameText, 0);
            result.left.alignment = SWT.LEFT;
            result.right = new FormAttachment(100, 0);
            result.bottom = new FormAttachment(100, 0);
            return result;
        }

        public void setVisible(boolean visible) {
            label.setVisible(visible);
            text.setVisible(visible);
        }

    }

    private class TableComposite implements FocusListener, MouseListener,
            SelectionListener, DelegationConfigurationWidget {

        private Label label;
        private Table table;
        private TableEditor valueEditor;
        private Text valueText;

        //Ϊ��ʹfield name���Կɱ༭ �������2���ֶ�
        private TableEditor nameEditor;
        private Text name4Text;
        //��� add del ��ť
        private Button addButton;
        private Button delButton;
        //����������ƥ������  ���field�����Ƿ����java�涨 
        //��Ϊԭ����field���Ǹ��ݷ����Զ����� ��Ҫ���
        //�������Լ��ֶ���д ����Ҫ���  ����д����xml�ļ�
        Pattern pattern = Pattern.compile("^[_A-Za-z][_A-Za-z0-9]*$");
        
        
        //��ʼ���������
        private void create(Composite parent) {
            //���Ͻǵı�ǩ
            label = widgetFactory.createLabel(parent, "����");
            //��
            table = widgetFactory.createTable(parent, SWT.CHECK | SWT.FULL_SELECTION | SWT.V_SCROLL);
            addButton=widgetFactory.createButton(parent, "���", SWT.PUSH);
            delButton=widgetFactory.createButton(parent, "�Ƴ�", SWT.PUSH);
            label.setLayoutData(createLabelLayoutData());
            table.setLayoutData(createTableLayoutData());
            addButton.setLayoutData(createAddButtonLayoutData());
            delButton.setLayoutData(createDelButtonLayoutData());
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.addSelectionListener(this);
            table.addMouseListener(this);
            AutoResizeTableLayout handlerConfigBeanTableLayout = new AutoResizeTableLayout(table);
            handlerConfigBeanTableLayout.addColumnData(new ColumnWeightData(40));
            handlerConfigBeanTableLayout.addColumnData(new ColumnWeightData(60));
            table.setLayout(handlerConfigBeanTableLayout);
            TableColumn handlerConfigBeanTableNameColumn = new TableColumn(table, SWT.NONE);
            handlerConfigBeanTableNameColumn.setText("����");
            TableColumn handlerConfigBeanTableValueColumn = new TableColumn(table, SWT.NONE);
            handlerConfigBeanTableValueColumn.setText("ֵ");
            createEditor();
        }

        private FormData createLabelLayoutData() {
            FormData result = new FormData();
            result.left = new FormAttachment(0, 0);
            result.top = new FormAttachment(configTypeCombo, 2);
            return result;
        }

        private FormData createTableLayoutData() {
            FormData result = new FormData();
            result.top = new FormAttachment(configTypeCombo, 0);
            result.left = new FormAttachment(nameText, 20);
            result.left.alignment = SWT.LEFT;
            result.right = new FormAttachment(100, 0);
            result.bottom = new FormAttachment(100, 0);
            return result;
        }

        private FormData createAddButtonLayoutData() {
            FormData result = new FormData();
            result.top = new FormAttachment(label, 0);
            result.left = new FormAttachment(0, 0);
            return result;
        }
        
        private FormData createDelButtonLayoutData() {
            FormData result = new FormData();
            result.top = new FormAttachment(addButton, 0);
            result.left = new FormAttachment(0, 0);
            return result;
        }
        
        private void createEditor() {
            //ֵ�༭��
            valueEditor = new TableEditor(table);
            //ֵ������
            valueText = new Text(table, SWT.NORMAL);
            valueText.setVisible(false);
            valueText.setText("");
            valueEditor.minimumWidth = valueText.getSize().x;
            valueEditor.horizontalAlignment = SWT.LEFT;
            valueEditor.grabHorizontal = true;
            
            //���Ʊ༭��
            nameEditor = new TableEditor(table);
            //���Ƶ��ı�
            name4Text = new Text(table, SWT.NORMAL);
            name4Text.setVisible(false);
            name4Text.setText("");
            nameEditor.minimumWidth = name4Text.getSize().x;
            nameEditor.horizontalAlignment = SWT.LEFT;
            nameEditor.grabHorizontal = true;
            
            addButton.addSelectionListener(new SelectionListener(){
                public void widgetDefaultSelected(SelectionEvent e) {
                }
                public void widgetSelected(SelectionEvent e) {
                    //���һ��table��item
                    TableItem item = new TableItem(table, SWT.NONE);
                }
            });
            delButton.addSelectionListener(new SelectionListener(){
                public void widgetDefaultSelected(SelectionEvent e) {
                }
                public void widgetSelected(SelectionEvent e) {
                    int index=table.getSelectionIndex();
                    if (index == -1){
                        return;
                    }
                    //�õ�ѡ�е�item  ɾ��Ԫ��  �Ƴ�item
                    TableItem selection = getSelectedTableItem();
                    removeGenericElement(selection);
                    table.remove(index);
                }
            });
        }
        //�༭ֵ
        private void doEdit() {
            //����ı��ǿ��ӵ�  ��Ϊ������  ȥ��focuslistener
            if (valueText.isVisible())
                endEdit();
            //���û��ѡ��  ����
            if (table.getSelectionIndex() == -1)
                return;
            //�õ�ѡ�е�����
            TableItem selection = table.getItem(table.getSelectionIndex());
            //�õ���2�� ��ֵ�� �ı�
            String value = selection.getText(1);
            valueText.setText(value == null ? "" : value);
            //��Ҫ�༭���ı����������������� editor
            valueEditor.setEditor(valueText, selection, 1);
            //��ʾΪ����
            valueText.setVisible(true);
            //ѡ��
            valueText.selectAll();
            //���ý���
            valueText.setFocus();
            valueText.addFocusListener(this);
            selection.setChecked(false);
        }
        
        //�༭����
        private void doEditName() {
            if (name4Text.isVisible()) endNameEdit();
            if (table.getSelectionIndex() == -1) return;
            TableItem selection = table.getItem(table.getSelectionIndex());
            //�õ���2�� �����Ƶ� �ı�
            String value = selection.getText(0);
            name4Text.setText(value == null ? "" : value);
            //��Ҫ�༭���ı����������������� editor
            nameEditor.setEditor(name4Text, selection, 0);
            name4Text.setVisible(true);
            name4Text.selectAll();
            name4Text.setFocus();
            name4Text.addFocusListener(this);
            selection.setChecked(false);
        }

        //����ı��ǿ��ӵ�  ��Ϊ������  ȥ��focuslistener
        private void endEdit() {
            valueText.setVisible(false);
            valueText.setText("");
            valueText.removeFocusListener(this);
        }
        
        private void endNameEdit() {
            name4Text.setVisible(false);
            name4Text.setText("");
            name4Text.removeFocusListener(this);
        }

        public void mouseDoubleClick(MouseEvent e) {
        }
        
        //������¼�
        public void mouseDown(MouseEvent e) {
            //�õ�Ҫ�༭���е�index
            int column = getSelectedColumn(e.x, e.y);
            if (column == -1)
                return;
            //����ǵڶ���  ���Ǳ༭ֵ
            if (column == 1) {
                doEdit();
            }else if (column == 0) {//����ǵ�һ��  ���Ǳ༭����
                doEditName();
            }
        }

        //�����ѡ�е�����һ��  ��Ҫ�༭����һ��
        private int getSelectedColumn(int x, int y) {
            int columnToEdit = -1;
            int columns = table.getColumnCount();
            TableItem tableItem = getSelectedTableItem();
            if (tableItem == null)
                return -1;
            for (int i = 0; i < columns; i++) {
                Rectangle bounds = tableItem.getBounds(i);
                if (bounds.contains(x, y)) {
                    columnToEdit = i;
                    break;
                }
            }
            return columnToEdit;
        }

        private TableItem getSelectedTableItem() {
            TableItem[] selection = table.getSelection();
            if (selection.length > 0) {
                return selection[0];
            } else {
                return null;
            }
        }

        public void mouseUp(MouseEvent e) {
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        private void applyValue() {
            //�õ�ѡ�е���һ��
            TableItem item = getSelectedTableItem();
            if (item == null)
                return;
            //�Ե�2����ֵ
            item.setText(1, valueText.getText());
            //���model
            GenericElement element = (GenericElement) item.getData();
            if (element == null)
                return;
            //����model
            //����������ָ��ĵ�ʱ���������Ԫ�ص����
            delegation.removeGenericElement(element);
            element.setValue(valueText.getText());
        }

        //���������Ƶ�ʱ������Ʊ������java�������Ĺ涨
        private void applyName() {
            TableItem item = getSelectedTableItem();
            if (item == null)
                return;
            Matcher matcher = pattern.matcher(name4Text.getText());   
            if (!matcher.matches()) {
                MessageDialog.openWarning(parent.getShell(), 
                  "����������ȷ", "��ֵ������JAVA�������������� ��\""+name4Text.getText()+"\"");
                return;
            }
            item.setText(0, name4Text.getText());
            GenericElement element = (GenericElement) item.getData();
            if (element == null)
                return;
            delegation.removeGenericElement(element);
            element.setName(name4Text.getText());
        }

        //����checkbox ��ѡ���¼�
        public void widgetSelected(SelectionEvent e) {
            if (e.widget == table) {
                if (e.detail == SWT.CHECK && e.item instanceof TableItem) {
                    handleTableItemCheck((TableItem) e.item);
                }
            }
        }
        
        //��ӻ�ɾ�� ѡ���Ԫ��
        private void handleTableItemCheck(TableItem item) {
            if (item.getChecked()) {
                if(item.getText(0)!=null && !item.getText(0).equals("")){
                    //���ѡ����item �������Ʋ�Ϊ�� ������ӽڵ�
                    addGenericElement(item);
                }else{
                    //���� ����ѡ��item
                    item.setChecked(false);
                }
            } else {
                removeGenericElement(item);
            }
            table.setSelection(item);
        }

        public void setVisible(boolean visible) {
            label.setVisible(visible);
            table.setVisible(visible);
            addButton.setVisible(visible);
            delButton.setVisible(visible);
        }

        public void focusGained(FocusEvent e) {
        }

        //��ʧȥ����
        public void focusLost(FocusEvent e) {
            if (e.widget == valueText) {
                applyValue();
                endEdit();
            }else if (e.widget == name4Text) {
                applyName();
                endNameEdit();
            }
        }
    }

}

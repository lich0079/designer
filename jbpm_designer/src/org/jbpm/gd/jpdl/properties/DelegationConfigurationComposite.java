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

    //因为不要根据反射来决定属性字段，所有和type相关的都改
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
     * 和原来的方法相比，原来的方法是根据反射来判断字段的 类确定了 field的名字 个数都确定了
     * 改之后 field的名字 个数无法通过原来的方式确定 要根据delegation的子节点来确定
     * 这个方法一般是在第一次展示配置界面 或 再生界面时调用的
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
        //拿出节点的所有子节点
        GenericElement[] elements = delegation.getGenericElements();
        //遍历节点的所有子节点
        for (int i = 0; i < elements.length; i++) {
            //遍历table的所有行
            for (int j = 0; j < items.length; j++) {
                //得到该节点的名字  
                String name = elements[i].getName() == null ? "" : elements[i]
                        .getName();
                //得到该节点的值
                String value = elements[i].getValue() == null ? ""
                        : elements[i].getValue();
                //如果该节点的名字与该行的名字相同  选中  设值
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
        //如果没有在类路径找到该类  显示错误信息
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
        nameLabel = widgetFactory.createLabel(parent, "类名");
        nameText = widgetFactory.createText(parent, "");
        nameText.setSize(20, 1);
//        searchButton = widgetFactory.createButton(parent, "搜索...", SWT.PUSH);
        configTypeLabel = widgetFactory.createLabel(parent, "配置类型");
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
        // 名字没变 返回
        if (newName.equals(delegation.getClassName()))
            return;
        // 把delegation名字设为选中的名字
        delegation.setClassName(newName);
        // 清空配置信息
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
//            messageLabel.setText("类没有字段");
//            showPage("Message");
//        } else {
            showPage("Field");
            restoreConfigElements(fieldTableComposite.table.getItems());
//        }
    }
    
    //只在类型改变的时候 或 刷新页面时调用
    private void restoreConfigElements(TableItem[] items) {
        //如果子节点为0，并且页面的元素被选中，就添加进父节点
        //那么假如不为0的话 不会改变父节点的状态
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
//            messageLabel.setText("类没有set方法");
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
        messageLabel.setText("类不在项目类路径中.");
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

    //添加新的元素  该元素可以理解为xml中的一个节点元素  有自己的键值  并可以有子节点
    private void addGenericElement(TableItem item) {
        //获得键
        String name = item.getText(0) == null ? "" : item.getText(0);
        //获得值
        String value = item.getText(1) == null ? "" : item.getText(1);
        //工厂模式创建一个元素
        GenericElement genericElement = (GenericElement) delegation
                .getFactory().createById("org.jbpm.gd.jpdl.genericElement");
        //设值
        genericElement.setName(name);
        genericElement.setValue(value);
        //添加至父节点上
        delegation.addGenericElement(genericElement);
        //给view设置model
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

    //类型选择框改变时的事件
    private void handleConfigTypeComboChanged() {
        //获得改变后的类型
        String newConfigType = toConfigType(configTypeCombo.getText());
        //类型相同 返回
        if (delegation.getConfigType().equals(newConfigType))
            return;
        //清空delegation中的信息
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
            label = widgetFactory.createLabel(parent, "配置信息");
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

        //为了使field name属性可编辑 添加如下2个字段
        private TableEditor nameEditor;
        private Text name4Text;
        //添加 add del 按钮
        private Button addButton;
        private Button delButton;
        //该属性用来匹配正则  检查field命名是否符合java规定 
        //因为原来的field名是根据反射自动生成 不要检查
        //现在是自己手动填写 所有要检查  否则写不进xml文件
        Pattern pattern = Pattern.compile("^[_A-Za-z][_A-Za-z0-9]*$");
        
        
        //初始化个个组件
        private void create(Composite parent) {
            //左上角的标签
            label = widgetFactory.createLabel(parent, "配置");
            //表单
            table = widgetFactory.createTable(parent, SWT.CHECK | SWT.FULL_SELECTION | SWT.V_SCROLL);
            addButton=widgetFactory.createButton(parent, "添加", SWT.PUSH);
            delButton=widgetFactory.createButton(parent, "移除", SWT.PUSH);
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
            handlerConfigBeanTableNameColumn.setText("名称");
            TableColumn handlerConfigBeanTableValueColumn = new TableColumn(table, SWT.NONE);
            handlerConfigBeanTableValueColumn.setText("值");
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
            //值编辑器
            valueEditor = new TableEditor(table);
            //值的文字
            valueText = new Text(table, SWT.NORMAL);
            valueText.setVisible(false);
            valueText.setText("");
            valueEditor.minimumWidth = valueText.getSize().x;
            valueEditor.horizontalAlignment = SWT.LEFT;
            valueEditor.grabHorizontal = true;
            
            //名称编辑器
            nameEditor = new TableEditor(table);
            //名称的文本
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
                    //添加一个table的item
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
                    //拿到选中的item  删除元素  移除item
                    TableItem selection = getSelectedTableItem();
                    removeGenericElement(selection);
                    table.remove(index);
                }
            });
        }
        //编辑值
        private void doEdit() {
            //如果文本是可视的  设为不可视  去掉focuslistener
            if (valueText.isVisible())
                endEdit();
            //如果没有选择  返回
            if (table.getSelectionIndex() == -1)
                return;
            //拿到选中的行数
            TableItem selection = table.getItem(table.getSelectionIndex());
            //得到第2列 即值的 文本
            String value = selection.getText(1);
            valueText.setText(value == null ? "" : value);
            //把要编辑的文本，行数，列数传给 editor
            valueEditor.setEditor(valueText, selection, 1);
            //显示为可视
            valueText.setVisible(true);
            //选中
            valueText.selectAll();
            //设置焦点
            valueText.setFocus();
            valueText.addFocusListener(this);
            selection.setChecked(false);
        }
        
        //编辑名称
        private void doEditName() {
            if (name4Text.isVisible()) endNameEdit();
            if (table.getSelectionIndex() == -1) return;
            TableItem selection = table.getItem(table.getSelectionIndex());
            //得到第2列 即名称的 文本
            String value = selection.getText(0);
            name4Text.setText(value == null ? "" : value);
            //把要编辑的文本，行数，列数传给 editor
            nameEditor.setEditor(name4Text, selection, 0);
            name4Text.setVisible(true);
            name4Text.selectAll();
            name4Text.setFocus();
            name4Text.addFocusListener(this);
            selection.setChecked(false);
        }

        //如果文本是可视的  设为不可视  去掉focuslistener
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
        
        //鼠标点击事件
        public void mouseDown(MouseEvent e) {
            //得到要编辑的行的index
            int column = getSelectedColumn(e.x, e.y);
            if (column == -1)
                return;
            //如果是第二行  就是编辑值
            if (column == 1) {
                doEdit();
            }else if (column == 0) {//如果是第一行  就是编辑名称
                doEditName();
            }
        }

        //计算出选中的是哪一列  即要编辑的哪一列
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
            //得到选中的那一行
            TableItem item = getSelectedTableItem();
            if (item == null)
                return;
            //对第2列设值
            item.setText(1, valueText.getText());
            //获得model
            GenericElement element = (GenericElement) item.getData();
            if (element == null)
                return;
            //更新model
            //不加这句会出现更改的时候添加了新元素的情况
            delegation.removeGenericElement(element);
            element.setValue(valueText.getText());
        }

        //在输入名称的时候该名称必须符合java变量名的规定
        private void applyName() {
            TableItem item = getSelectedTableItem();
            if (item == null)
                return;
            Matcher matcher = pattern.matcher(name4Text.getText());   
            if (!matcher.matches()) {
                MessageDialog.openWarning(parent.getShell(), 
                  "变量名不正确", "该值不符合JAVA变量的命名规则 ：\""+name4Text.getText()+"\"");
                return;
            }
            item.setText(0, name4Text.getText());
            GenericElement element = (GenericElement) item.getData();
            if (element == null)
                return;
            delegation.removeGenericElement(element);
            element.setName(name4Text.getText());
        }

        //处理checkbox 的选中事件
        public void widgetSelected(SelectionEvent e) {
            if (e.widget == table) {
                if (e.detail == SWT.CHECK && e.item instanceof TableItem) {
                    handleTableItemCheck((TableItem) e.item);
                }
            }
        }
        
        //添加或删除 选择的元素
        private void handleTableItemCheck(TableItem item) {
            if (item.getChecked()) {
                if(item.getText(0)!=null && !item.getText(0).equals("")){
                    //如果选中了item 并且名称不为空 添加新子节点
                    addGenericElement(item);
                }else{
                    //否则 不能选中item
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

        //当失去焦点
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

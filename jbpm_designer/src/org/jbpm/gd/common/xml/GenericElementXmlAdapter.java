package org.jbpm.gd.common.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.gd.common.model.GenericElement;
import org.jbpm.gd.common.model.SemanticElement;

public class GenericElementXmlAdapter extends XmlAdapter {
	
	private static HashMap NODE_TYPES = null;
	
	protected Map getNodeTypes() {
		if (NODE_TYPES == null) {
			NODE_TYPES = new HashMap();
			NODE_TYPES.put("genericElement", "genericElement");
		}
		return NODE_TYPES;
	}
	
	protected String getNodeType(String elementType) {
		return "genericElement";
	}
	
	public void initialize(SemanticElement jpdlElement) {
		super.initialize(jpdlElement);
		GenericElement genericElement = (GenericElement)jpdlElement;
		genericElement.setName(getNode().getNodeName());
		genericElement.setValue(getTextContent());
		genericElement.addPropertyChangeListener(this);
	}
	
	protected void initialize() {
		super.initialize();
		GenericElement genericElement = (GenericElement)getSemanticElement();
		if (genericElement != null ) {
			GenericElement[] genericElements = genericElement.getGenericElements();
			for (int i = 0; i < genericElements.length; i++) {
				addElement(genericElements[i]);
			}
			setTextContent(genericElement.getValue());
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if ("value".equals(evt.getPropertyName())) {
			setTextContent((String)evt.getNewValue());
		} else if ("genericElementAdd".equals(evt.getPropertyName())) {
			addElement((GenericElement)evt.getNewValue());
		} else if ("genericElementRemove".equals(evt.getPropertyName())) {
			removeElement((GenericElement)evt.getOldValue());
		}
	}
	
	protected void doModelUpdate(String name, String newValue) {
		GenericElement genericElement = (GenericElement)getSemanticElement();
		if ("#text".equals(name)) {
			genericElement.setValue(newValue);
		}
	}
	
	protected void doModelAdd(XmlAdapter child) {
		// a controller cannot have any child nodes
	}
	
	protected void doModelRemove(XmlAdapter child) {
		// a controller cannot have any child nodes
	}
	
	public String getElementType() {
		return "genericElement";
	}
}

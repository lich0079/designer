package org.jbpm.gd.common.notation;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.jbpm.gd.common.editor.CreationFactory;
import org.jbpm.gd.common.model.SemanticElement;

public class RootContainer extends AbstractNotationElement implements NodeContainer {
	
	Dimension dimension;
	List nodes = new ArrayList();
	
	public void setDimension(Dimension newDimension) {
		Dimension oldDimension = dimension;
		dimension = newDimension;
		firePropertyChange("dimension", oldDimension, newDimension);
	}
	
	public Dimension getDimension() {
		return dimension;
	}
	
	public void addNode(Node node) {
		nodes.add(node);
		node.setContainer(this);
		firePropertyChange("nodeAdd", null, node);
	}
	
	public void removeNode(Node node) {
		nodes.remove(node);
		node.setContainer(null);
		firePropertyChange("nodeRemove", node, null);
	}
	
	public List getNodes() {
		return nodes;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}
	
	protected void removeNode(PropertyChangeEvent evt) {
		SemanticElement jpdlElement = (SemanticElement)evt.getOldValue();
		AbstractNotationElement notationElement = getRegisteredNotationElementFor(jpdlElement);
		if (notationElement != null) {
			jpdlElement.removePropertyChangeListener(notationElement);
			removeNode((Node)notationElement);
//			notationElement.unregister();
		}
	}

	protected void addNode(PropertyChangeEvent evt) {
		SemanticElement semanticElement = (SemanticElement)evt.getNewValue();
		AbstractNotationElement notationElement = getRegisteredNotationElementFor(semanticElement);
		if (notationElement == null) {
			CreationFactory factory = new CreationFactory(semanticElement.getElementId(), semanticElement.getFactory(), getFactory());
			notationElement = (AbstractNotationElement)factory.getNewObject();
		}
		addNode((Node)notationElement);
		semanticElement.addPropertyChangeListener(notationElement);
	}
	
//	protected Editor getEditor() {
//		return (Editor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//	}
	
}

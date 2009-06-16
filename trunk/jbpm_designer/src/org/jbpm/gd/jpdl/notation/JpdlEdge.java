package org.jbpm.gd.jpdl.notation;

import java.beans.PropertyChangeEvent;

import org.jbpm.gd.common.model.SemanticElement;
import org.jbpm.gd.common.notation.Edge;
import org.jbpm.gd.common.notation.Label;
import org.jbpm.gd.common.notation.Node;
import org.jbpm.gd.jpdl.model.NodeElement;
import org.jbpm.gd.jpdl.model.ProcessDefinition;

public class JpdlEdge extends Edge {
	
	public Label getLabel() {
		Label result = super.getLabel();
		if (result == null) {
			result = (Label)getFactory().create("org.jbpm.gd.jpdl.label");
			addPropertyChangeListener(result);
			setLabel(result);
		}
		return result;
	}
	
	public void setSemanticElement(SemanticElement semanticElement) {
		super.setSemanticElement(semanticElement);
		getLabel().setSemanticElement(semanticElement);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventName = evt.getPropertyName();
		if (eventName.equals("to")) {
			if (getSource() == null) return;
			ProcessDefinition processDefinition = (ProcessDefinition)((Node)getSource()).getContainer().getSemanticElement();
			NodeElement newTarget = processDefinition.getNodeElementByName((String)evt.getNewValue());
			NodeElement oldTarget = processDefinition.getNodeElementByName((String)evt.getOldValue());
			if (oldTarget != null) {
				Node oldTargetNode = (Node)getRegisteredNotationElementFor(oldTarget);
				if (oldTargetNode != null) {
					oldTargetNode.removeArrivingEdge(this);
				}
			}
			if (newTarget != null) {
				Node targetNode = (Node)getRegisteredNotationElementFor(newTarget);
				if (targetNode != null) {
					targetNode.addArrivingEdge(this);
				}
			}
		} else {
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}	

}

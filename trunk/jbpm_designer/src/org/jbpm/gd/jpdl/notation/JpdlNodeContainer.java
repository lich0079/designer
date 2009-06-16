package org.jbpm.gd.jpdl.notation;

import java.beans.PropertyChangeEvent;
import java.util.StringTokenizer;

import org.jbpm.gd.common.editor.CreationFactory;
import org.jbpm.gd.common.model.SemanticElement;
import org.jbpm.gd.common.notation.AbstractNodeContainer;
import org.jbpm.gd.common.notation.AbstractNotationElement;
import org.jbpm.gd.common.notation.Edge;
import org.jbpm.gd.common.notation.Node;
import org.jbpm.gd.common.notation.NotationElement;
import org.jbpm.gd.jpdl.model.NodeElementContainer;
import org.jbpm.gd.jpdl.model.Transition;

public class JpdlNodeContainer extends AbstractNodeContainer {
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventName = evt.getPropertyName();
		if (eventName.equals("nodeElementAdd")) {
			SemanticElement jpdlElement = (SemanticElement)evt.getNewValue();
			NotationElement notationElement = getRegisteredNotationElementFor(jpdlElement);
			if (notationElement == null) {
				CreationFactory factory = new CreationFactory(jpdlElement.getElementId(), jpdlElement.getFactory(), getFactory());
				notationElement = (AbstractNotationElement)factory.getNewObject();
//				notationElement = getFactory().create(JpdlNotationMapping.getNotationElementId(jpdlElement.getElementId()));
//				notationElement.setSemanticElement(jpdlElement);
//				notationElement.register();
			}
			addNode((Node)notationElement);
			jpdlElement.addPropertyChangeListener(notationElement);
		} else if (eventName.equals("nodeElementRemove")) {
			SemanticElement jpdlElement = (SemanticElement)evt.getOldValue();
			NotationElement notationElement = getRegisteredNotationElementFor(jpdlElement);
			if (notationElement != null) {
				jpdlElement.removePropertyChangeListener(notationElement);
				removeNode((Node)notationElement);
//				notationElement.unregister();
			}
		} else if (eventName.equals("transitionAdd")) {
			Transition transition = (Transition)evt.getNewValue();
			Edge edge = (Edge)getRegisteredNotationElementFor(transition);
			if (edge == null) {
				CreationFactory factory = new CreationFactory(transition.getElementId(), getSemanticElement().getFactory(), getFactory());
				edge = (Edge)factory.getNewObject();
//				edge = (Edge)getFactory().create(JpdlNotationMapping.getNotationElementId(transition.getElementId()));
//				edge.setSemanticElement(transition);
//				edge.register();
			}
			addLeavingEdge(edge);
			transition.addPropertyChangeListener(edge);			
			Node targetNode = getDestinationNode(transition.getTo());
			if (targetNode != null) {
				targetNode.addArrivingEdge(edge);
			}
		} else if (eventName.equals("transitionRemove")) {
			Transition transition = (Transition)evt.getOldValue();
			Edge edge = (Edge)getRegisteredNotationElementFor(transition);
			if (edge != null) {
				Node targetNode = edge.getTarget();
				if (targetNode != null) {
					transition.removePropertyChangeListener(edge);
					targetNode.removeArrivingEdge(edge);
				}
				removeLeavingEdge(edge);
//				edge.unregister();
			}
		}
	}
	
	private Node getDestinationNode(String to) {
		NotationElement runner = getContainer();
		SemanticElement jpdlElement = null;
		StringTokenizer tokenizer = new StringTokenizer(to, "/");
		while (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();
			if ("..".equals(nextToken)) {
				if (runner != null && runner instanceof Node) {
					runner = ((Node)runner).getContainer();
				} else {
					runner = null;
					break;
				}
			} else {
				if (jpdlElement == null) {
					jpdlElement = (SemanticElement)runner.getSemanticElement();
				}
				if (jpdlElement instanceof NodeElementContainer) {
					jpdlElement = ((NodeElementContainer)jpdlElement).getNodeElementByName(nextToken);
					runner = (Node)getRegisteredNotationElementFor(jpdlElement);
				} else {
					runner = null;
					break;
				}				
			}
		}
		return (Node)runner;
	}
	
}

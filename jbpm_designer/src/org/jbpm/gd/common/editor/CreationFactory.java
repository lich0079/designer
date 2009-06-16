package org.jbpm.gd.common.editor;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.jbpm.gd.common.model.SemanticElement;
import org.jbpm.gd.common.model.SemanticElementFactory;
import org.jbpm.gd.common.notation.Node;
import org.jbpm.gd.common.notation.NotationElement;
import org.jbpm.gd.common.notation.NotationElementFactory;
import org.jbpm.gd.common.notation.NotationMapping;

public class CreationFactory implements org.eclipse.gef.requests.CreationFactory {	
	
	String elementId;
	SemanticElementFactory semanticElementFactory;
	NotationElementFactory notationElementFactory;
	
	public CreationFactory(String elementId, SemanticElementFactory semanticElementFactory, NotationElementFactory notationElementFactory) {
		this.elementId = elementId;
		this.semanticElementFactory = semanticElementFactory;
		this.notationElementFactory = notationElementFactory;
	}

	public Object getNewObject() {
		String notationElementId = NotationMapping.getNotationElementId(elementId);
		NotationElement notationElement = notationElementFactory.create(notationElementId);
		if (notationElement instanceof Node) {
			Dimension dimension = NotationMapping.getInitialDimension(elementId);
			if (dimension != null) {
				Rectangle constraint = ((Node)notationElement).getConstraint();
				constraint.setSize(new Dimension(dimension));
			}
		}
		SemanticElement jpdlElement = semanticElementFactory.createById(elementId);
		notationElement.setSemanticElement(jpdlElement);
		return notationElement;
	}

	public Object getObjectType() {
		return elementId;
	}

}

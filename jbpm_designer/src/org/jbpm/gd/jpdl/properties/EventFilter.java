package org.jbpm.gd.jpdl.properties;

import org.eclipse.jface.viewers.IFilter;
import org.jbpm.gd.common.notation.AbstractNotationElement;
import org.jbpm.gd.common.part.NotationElementGraphicalEditPart;
import org.jbpm.gd.common.part.OutlineEditPart;
import org.jbpm.gd.jpdl.model.Event;
import org.jbpm.gd.jpdl.model.Transition;

public class EventFilter implements IFilter {
	
	public boolean select(Object toTest) {
		Object input = toTest;
        if (toTest instanceof NotationElementGraphicalEditPart) {
        	AbstractNotationElement notationElement = ((NotationElementGraphicalEditPart)toTest).getNotationElement();
        	input = notationElement.getSemanticElement();
        } else if (toTest instanceof OutlineEditPart) {
        	input = ((OutlineEditPart)toTest).getModel();
        }
		return input instanceof Event && !(input instanceof Transition);
	}

}

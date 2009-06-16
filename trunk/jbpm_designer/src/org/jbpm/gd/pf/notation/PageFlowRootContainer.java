package org.jbpm.gd.pf.notation;

import java.beans.PropertyChangeEvent;

import org.jbpm.gd.common.notation.RootContainer;

public class PageFlowRootContainer extends RootContainer {
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventName = evt.getPropertyName();
		if (eventName.equals("startPageAdd") || eventName.equals("nodeElementAdd")) {
			addNode(evt);
		}
		if (eventName.equals("startPageRemove") || eventName.equals("nodeElementRemove")) {
			removeNode(evt);
		}
	}

}

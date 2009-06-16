package org.jbpm.gd.jpdl.notation;

import java.beans.PropertyChangeEvent;

import org.jbpm.gd.common.notation.RootContainer;

public class JpdlRootContainer extends RootContainer {
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventName = evt.getPropertyName();
		if (eventName.equals("startStateAdd") || eventName.equals("nodeElementAdd")) {
			addNode(evt);
		}
		if (eventName.equals("startStateRemove") || eventName.equals("nodeElementRemove")) {
			removeNode(evt);
		}
	}

}

package org.jbpm.gd.jpdl.util;

import org.jbpm.gd.jpdl.model.Assignable;

public class HandlerAssignmentMatcher implements AssignmentTypeMatcher {

	public boolean matches(Assignable assignable) {
		return assignable.getAssignment() != null && assignable.getAssignment().getClassName() != null;
	}

}

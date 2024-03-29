/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.gd.common.part;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.jbpm.gd.common.model.SemanticElement;
import org.jbpm.gd.common.notation.AbstractNotationElement;
import org.jbpm.gd.common.notation.NotationElement;

public abstract class AbstractNotationElementGraphicalEditPart 
	extends AbstractGraphicalEditPart 
	implements NotationElementGraphicalEditPart {
	
	public AbstractNotationElementGraphicalEditPart(NotationElement notationElement) {
		setModel(notationElement);
	}
	
	protected void createEditPolicies() {
	}
	
	protected SemanticElement getSemanticElement() {
		return (SemanticElement)getNotationElement().getSemanticElement();
	}
	
	public void activate() {
		if (!isActive()) {
			getNotationElement().addPropertyChangeListener(this);
			super.activate();
		}
	}
	
	public void deactivate() {
		if (isActive()) {
			getNotationElement().removePropertyChangeListener(this);
			super.deactivate();
		}
	}
			
	public AbstractNotationElement getNotationElement() {
		return (AbstractNotationElement)getModel();
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		return false;
	}
}

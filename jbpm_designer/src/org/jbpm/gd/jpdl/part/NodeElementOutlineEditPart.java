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
package org.jbpm.gd.jpdl.part;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.gd.common.part.OutlineEditPart;
import org.jbpm.gd.jpdl.model.NodeElement;

public class NodeElementOutlineEditPart extends OutlineEditPart {
	
	public NodeElementOutlineEditPart(NodeElement node) {
		super(node);
	}
	
	private NodeElement getNode() {
		return (NodeElement)getModel();
	}

	protected String getText() {
		return getNode().getName() == null ? "node" : getNode().getName();
	}
	
	protected List getModelChildren() {
		List result = new ArrayList();
		result.add(new EventListOutlineEditPart(getNode()));
		result.add(new TransitionListOutlineEditPart(getNode()));
		return result;
	}
	
}

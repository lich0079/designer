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
package org.jbpm.taskmgmt.exe;

import java.io.*;
import java.util.Set;

import org.jbpm.taskmgmt.def.*;
import org.jbpm.util.EqualsUtil;

/**
 * is a process role for a one process instance.
 */
public class SwimlaneInstance implements Serializable, Assignable {

  private static final long serialVersionUID = 1L;

  long id = 0;
  int version = 0;
  protected String name = null;
  protected String actorId = null;
  protected Set pooledActors = null;
  protected Swimlane swimlane = null;
  protected TaskMgmtInstance taskMgmtInstance = null;
  
  public SwimlaneInstance() {
  }
  
  public SwimlaneInstance(Swimlane swimlane) {
    this.name = swimlane.getName();
    this.swimlane = swimlane;
  }

  public void setPooledActors(String[] actorIds) {
    this.pooledActors = PooledActor.createPool(actorIds, this, null);
  }

  // equals ///////////////////////////////////////////////////////////////////
  // hack to support comparing hibernate proxies against the real objects
  // since this always falls back to ==, we don't need to overwrite the hashcode
  public boolean equals(Object o) {
    return EqualsUtil.equals(this, o);
  }
  
  // getters and setters //////////////////////////////////////////////////////

  public long getId() {
    return id;
  }
  public String getName() {
    return name;
  }
  public Swimlane getSwimlane() {
    return swimlane;
  }
  public String getActorId() {
    return actorId;
  }
  public void setActorId(String actorId) {
    this.actorId = actorId;
  }
  public TaskMgmtInstance getTaskMgmtInstance() {
    return taskMgmtInstance;
  }
  public void setTaskMgmtInstance(TaskMgmtInstance taskMgmtInstance) {
    this.taskMgmtInstance = taskMgmtInstance;
  }
  public Set getPooledActors() {
    return pooledActors;
  }
  public void setPooledActors(Set pooledActors) {
    this.pooledActors = pooledActors;
  }
}

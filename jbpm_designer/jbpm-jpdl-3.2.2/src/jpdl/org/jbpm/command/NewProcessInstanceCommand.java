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
package org.jbpm.command;

import java.util.Iterator;
import java.util.Map;

import org.jbpm.JbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Graph command to start a new process and create a task instance if the start
 * node has a start task definition.
 * 
 * The result of this command, if requested, is a {@link Long} value containing
 * the process instance id.
 * 
 * @author Jim Rigsbee, Tom Baeyens, Bernd Ruecker
 */
public class NewProcessInstanceCommand implements Command {

  private static final long serialVersionUID = 1L;

  private String processDefinitionName = null;
  private long processDefinitionId = 0;
  
  // TODO: This is not clear to me, what for do we need that actorId here? // Bernd Ruecker
  String actorId = null;
  private Map variables = null;
  private boolean createStartTask = false;
  
  private String key;

  public NewProcessInstanceCommand() {
  }

  public NewProcessInstanceCommand(String processDefinitionName) {
    this.processDefinitionName = processDefinitionName;
  }

  /**
   * return the id of the newly created process instance. 
   * @throws Exception 
   */
  public Object execute(JbpmContext jbpmContext) throws Exception {

    if (actorId!=null) {
      jbpmContext.setActorId(actorId);
    }
    
    ProcessInstance processInstance = null;
    if (processDefinitionName!=null) {
      processInstance = jbpmContext.newProcessInstance(processDefinitionName);
    } else {
      ProcessDefinition processDefinition = jbpmContext.getGraphSession().loadProcessDefinition(processDefinitionId);
      processInstance = new ProcessInstance(processDefinition);
    }

    if (key!=null) {
      processInstance.setKey(key);
    }

    Object result = null;
    if (createStartTask) {
      result = processInstance.getTaskMgmtInstance().createStartTaskInstance();
    } else {
      result = processInstance;
    }
    
    if (variables!=null) {
      ContextInstance contextInstance = processInstance.getContextInstance();
      Iterator iter = variables.keySet().iterator();
      while (iter.hasNext()) {
        String variableName = (String) iter.next();
        contextInstance.setVariable(variableName, variables.get(variableName));
      } 
    }   

    jbpmContext.save(processInstance);

    return result;
  }

  public String getActorId() {
    return actorId;
  }
  public void setActorId(String actorId) {
    this.actorId = actorId;
  }
  public long getProcessId() {
    return processDefinitionId;
  }
  public void setProcessId(long processId) {
    this.processDefinitionId = processId;
  }
  public String getProcessName() {
    return processDefinitionName;
  }
  public void setProcessName(String processName) {
    this.processDefinitionName = processName;
  }
  public boolean isCreateStartTask() {
    return createStartTask;
  }
  public void setCreateStartTask(boolean createStartTask) {
    this.createStartTask = createStartTask;
  }
  public Map getVariables() {
    return variables;
  }
  public void setVariables(Map variables) {
    this.variables = variables;
  }
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
}

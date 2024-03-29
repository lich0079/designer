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
package org.jbpm.graph.node;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.InvalidXPathException;
import org.dom4j.Namespace;
import org.dom4j.ProcessingInstruction;
import org.dom4j.QName;
import org.dom4j.Text;
import org.dom4j.Visitor;
import org.dom4j.XPath;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.FlyweightAttribute;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.NodeLog;
import org.jbpm.graph.log.ProcessStateLog;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Parsable;
import org.jbpm.util.Clock;

public class ProcessState extends Node implements Parsable {

  private static final long serialVersionUID = 1L;
  
  static SubProcessResolver defaultSubProcessResolver = new DbSubProcessResolver();

  public static void setDefaultSubProcessResolver(SubProcessResolver subProcessResolver) {
    defaultSubProcessResolver = subProcessResolver;
  }

  protected ProcessDefinition subProcessDefinition = null;
  protected Set variableAccesses = null;
  protected String subProcessName = null;

  // event types //////////////////////////////////////////////////////////////

  public static final String[] supportedEventTypes = new String[] { Event.EVENTTYPE_SUBPROCESS_CREATED, Event.EVENTTYPE_SUBPROCESS_END,
      Event.EVENTTYPE_NODE_ENTER, Event.EVENTTYPE_NODE_LEAVE, Event.EVENTTYPE_BEFORE_SIGNAL, Event.EVENTTYPE_AFTER_SIGNAL };

  public String[] getSupportedEventTypes() {
    return supportedEventTypes;
  }

  // xml //////////////////////////////////////////////////////////////////////

  public void read(Element processStateElement, JpdlXmlReader jpdlReader) {
    Element subProcessElement = processStateElement.element("sub-process");
    
    if (subProcessElement!=null) {
      
      String binding = subProcessElement.attributeValue("binding");
      if ("late".equalsIgnoreCase(binding)) {
        subProcessName = subProcessElement.attributeValue("name");
      } else {
        
        SubProcessResolver subProcessResolver = getSubProcessResolver();

        try {
          subProcessDefinition = subProcessResolver.findSubProcess(subProcessElement);
        } catch (JbpmException e) {
          jpdlReader.addWarning(e.getMessage());
        }

        // in case this is a self-recursive process invocation...
        if (subProcessDefinition==null) {
          String subProcessName = subProcessElement.attributeValue("name");
          if (subProcessName.equals(processDefinition.getName())) {
            subProcessDefinition = processDefinition;
          }
        }
      }
    }
    
    if (subProcessDefinition!=null) {
      log.debug("subprocess for process-state '"+name+"' bound to "+subProcessDefinition);
    } else if (subProcessName!=null ){
      log.debug("subprocess for process-state '"+name+"' will be late bound to "+subProcessName);
    } else {
      log.debug("subprocess for process-state '"+name+"' not yet bound");
    }

    this.variableAccesses = new HashSet(jpdlReader.readVariableAccesses(processStateElement));
  }

  private SubProcessResolver getSubProcessResolver() {
    SubProcessResolver subProcessResolver = defaultSubProcessResolver;
    if (JbpmConfiguration.Configs.hasObject("jbpm.sub.process.resolver")) {
      subProcessResolver = (SubProcessResolver) JbpmConfiguration.Configs.getObject("jbpm.sub.process.resolver");
    }
    return subProcessResolver;
  }
  
  public void execute(ExecutionContext executionContext) {
    Token superProcessToken = executionContext.getToken();
    
    ProcessDefinition usedSubProcessDefinition = subProcessDefinition;
    // if this process has late binding
    if ( (subProcessDefinition==null)
         && (subProcessName!=null)
       ) {
      SubProcessResolver subProcessResolver = getSubProcessResolver();
      List attributes = new ArrayList();
      String subProcessNameResolved = (String) JbpmExpressionEvaluator.evaluate(subProcessName, executionContext);
      if (log.isDebugEnabled()) {
          log.debug("SubProcessName after eval: " + subProcessNameResolved);
      }
      attributes.add(new FlyweightAttribute("name", subProcessNameResolved));
      Element subProcessElement = new DefaultElement("sub-process");
      subProcessElement.setAttributes(attributes);

      usedSubProcessDefinition = subProcessResolver.findSubProcess(subProcessElement);
    }

    // create the subprocess
    ProcessInstance subProcessInstance = superProcessToken.createSubProcessInstance(usedSubProcessDefinition);

    // fire the subprocess created event
    fireEvent(Event.EVENTTYPE_SUBPROCESS_CREATED, executionContext);

    // feed the readable variableInstances
    if ((variableAccesses != null) && (!variableAccesses.isEmpty())) {

      ContextInstance superContextInstance = executionContext.getContextInstance();
      ContextInstance subContextInstance = subProcessInstance.getContextInstance();
      subContextInstance.setTransientVariables(superContextInstance.getTransientVariables());

      // loop over all the variable accesses
      Iterator iter = variableAccesses.iterator();
      while (iter.hasNext()) {
        VariableAccess variableAccess = (VariableAccess) iter.next();
        // if this variable access is readable
        if (variableAccess.isReadable()) {
          // the variable is copied from the super process variable name
          // to the sub process mapped name
          String variableName = variableAccess.getVariableName();
          Object value = superContextInstance.getVariable(variableName, superProcessToken);
          String mappedName = variableAccess.getMappedName();
          log.debug("copying super process var '"+variableName+"' to sub process var '"+mappedName+"': "+value);
          if (value!=null) {
            subContextInstance.setVariable(mappedName, value);
          }
        }
      }
    }

    // send the signal to start the subprocess
    subProcessInstance.signal();
  }
  
  

  public void leave(ExecutionContext executionContext, Transition transition) {
    ProcessInstance subProcessInstance = executionContext.getSubProcessInstance();

    Token superProcessToken = subProcessInstance.getSuperProcessToken();

    // feed the readable variableInstances
    if ((variableAccesses != null) && (!variableAccesses.isEmpty())) {

      ContextInstance superContextInstance = executionContext.getContextInstance();
      ContextInstance subContextInstance = subProcessInstance.getContextInstance();

      // loop over all the variable accesses
      Iterator iter = variableAccesses.iterator();
      while (iter.hasNext()) {
        VariableAccess variableAccess = (VariableAccess) iter.next();
        // if this variable access is writable
        if (variableAccess.isWritable()) {
          // the variable is copied from the sub process mapped name
          // to the super process variable name
          String mappedName = variableAccess.getMappedName();
          Object value = subContextInstance.getVariable(mappedName);
          String variableName = variableAccess.getVariableName();
          log.debug("copying sub process var '"+mappedName+"' to super process var '"+variableName+"': "+value);
          if (value!=null) {
            superContextInstance.setVariable(variableName, value, superProcessToken);
          }
        }
      }
    }

    // fire the subprocess ended event
    fireEvent(Event.EVENTTYPE_SUBPROCESS_END, executionContext);

    // remove the subprocess reference
    superProcessToken.setSubProcessInstance(null);
    
    // We replaced the normal log generation of super.leave() by creating the log here
    // and overriding the addNodeLog method with an empty version 
    superProcessToken.addLog(new ProcessStateLog(this, superProcessToken.getNodeEnter(), Clock.getCurrentTime(), subProcessInstance));

    // call the subProcessEndAction
    super.leave(executionContext, getDefaultLeavingTransition());
  }

  // We replaced the normal log generation of super.leave() by creating the log above in the leave method 
  // and overriding the addNodeLog method with an empty version 
  protected void addNodeLog(Token token) {
  }

  public ProcessDefinition getSubProcessDefinition() {
    return subProcessDefinition;
  }
  public void setSubProcessDefinition(ProcessDefinition subProcessDefinition) {
    this.subProcessDefinition = subProcessDefinition;
  }
  
  private static Log log = LogFactory.getLog(ProcessState.class);
}

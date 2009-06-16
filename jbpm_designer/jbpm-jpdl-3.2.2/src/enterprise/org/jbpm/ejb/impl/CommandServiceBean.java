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
package org.jbpm.ejb.impl;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.command.Command;
import org.jbpm.command.CommandService;

/**
 * A stateless session bean implementation of the {@link org.jbpm.command.CommandService}. 
 * 
 * @author Jim Rigsbee, Tom Baeyens
 */
public class CommandServiceBean implements SessionBean, CommandService {

  private static final long serialVersionUID = 1L;

  
  JbpmConfiguration jbpmConfiguration = null;
  SessionContext sessionContext = null;

  /**
   * creates a command service that will be used to execute the commands that 
   * are passed in the execute method.  The command service will be build by 
   * creating a jbpm configuration.  In case the environment key JbpmCfgResource
   * is specified for this bean, that value will be used to resolve the jbpm 
   * configuration file as a resource.  If that key is not configured, the default 
   * jbpm configuration file will be used (jbpm.cfg.xml).
   */
  public void ejbCreate() throws CreateException {
    String jbpmCfgResource = null; 
    try {
      log.debug("getting jbpm configuration resource from the environment properties");
      Context initial = new InitialContext();
      Context environment = (Context) initial.lookup("java:comp/env");
      jbpmCfgResource = (String) environment.lookup("JbpmCfgResource");
      
    } catch (NamingException e) {
      log.debug("couldn't find configuration property JbpmCfgResource through JNDI");
    }

    if (log.isDebugEnabled()) {
      if (jbpmCfgResource==null) {
        log.debug("getting default jbpm configuration resource (jbpm.cfg.xml)");
      } else {
        log.debug("getting jbpm configuration from resource "+jbpmCfgResource);
      }
    }

    jbpmConfiguration = JbpmConfiguration.getInstance(jbpmCfgResource);
  }

  public Object execute(Command command) {
    log.debug("handing over the command execution to the command service");
    
    Object result = null;
    JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
    try {
      log.debug("executing " + command);
      result = command.execute(jbpmContext);
    } catch (Exception e) {
      throw new JbpmException("couldn't execute "+command, e);
    } finally {
      jbpmContext.close();
    }
    return result;
  }

  public void setSessionContext(SessionContext sessionContext) {
    this.sessionContext = sessionContext;
  }
  public void ejbRemove() {
    this.sessionContext = null;
  }
  public void ejbActivate() {}
  public void ejbPassivate() {}

  private static final Log log = LogFactory.getLog(CommandServiceBean.class);
}

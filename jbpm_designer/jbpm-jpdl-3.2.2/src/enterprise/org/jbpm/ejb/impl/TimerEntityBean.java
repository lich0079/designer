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

import java.rmi.RemoteException;
import java.util.Date;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.Duration;
import org.jbpm.ejb.LocalCommandService;
import org.jbpm.ejb.LocalCommandServiceHome;
import org.jbpm.ejb.LocalTimerEntity;
import org.jbpm.scheduler.ejbtimer.ExecuteTimerCommand;

/**
 * A stateless session bean implementation of the {@link org.jbpm.command.CommandService}. 
 * 
 * @author Jim Rigsbee, Tom Baeyens
 */
public class TimerEntityBean implements EntityBean, LocalTimerEntity, TimedObject {

  private static final long serialVersionUID = 1L;
  
  Long id;
  String dueDate;
  String repeat;
  
  JbpmConfiguration jbpmConfiguration = null;
  EntityContext entityContext = null;

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

  public void schedule() {
    log.debug("creating timer "+id+" in the ejb timer service");
    createTimer(dueDate);
  }

  public void createTimer(String duration) {
    TimerService timerService = entityContext.getTimerService();
    BusinessCalendar businessCalendar = new BusinessCalendar();
    Date dueDateTime = businessCalendar.add(new Date(), new Duration(duration));
    timerService.createTimer(dueDateTime, null);
  }

  public void cancel() {
  }

  public void ejbTimeout(Timer timer) {
    log.debug("ejb timer "+timer+" fires");
    String localCommandServiceJndiName = "java:comp/env/ejb/LocalCommandServiceBean";
    try {
      Context initial = new InitialContext();
      LocalCommandServiceHome localCommandServiceHome = (LocalCommandServiceHome) initial.lookup(localCommandServiceJndiName);
      LocalCommandService localCommandService = localCommandServiceHome.create();
      localCommandService.execute(new ExecuteTimerCommand(id.longValue()));
      // if the timer has repeat
      if (repeat!=null) {
        // create a new timer
        log.debug("repeating timer "+id);
        createTimer(repeat);
      }
    } catch (Exception e) {
      JbpmException jbpmException = new JbpmException("couldn't execute timer", e);
      log.error(jbpmException);
      throw jbpmException;
    }
  }
  
  public EJBLocalHome getEJBLocalHome() throws EJBException {
    return null;
  }

  public Object getPrimaryKey() throws EJBException {
    return id;
  }

  public boolean isIdentical(EJBLocalObject elo) throws EJBException {
    return false;
  }

  public void setEntityContext(EntityContext entityContext) {
    this.entityContext = entityContext;
  }
  public void unsetEntityContext() throws EJBException, RemoteException {
    this.entityContext = null;
  }
  
  public void remove() throws RemoveException, EJBException {}
  public void ejbStore() throws EJBException, RemoteException {}
  public void ejbLoad() throws EJBException, RemoteException {}
  public void ejbActivate() throws EJBException, RemoteException {}
  public void ejbPassivate() throws EJBException, RemoteException {}
  public void ejbRemove() {}

  private static final Log log = LogFactory.getLog(TimerEntityBean.class);
}

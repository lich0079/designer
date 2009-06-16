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

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.command.Command;
import org.jbpm.ejb.LocalCommandService;
import org.jbpm.ejb.LocalCommandServiceHome;

/**
 * This message-driven bean listens for ObjectMessages that contain a 
 * {@link Command} object.  The commands that are received are 
 * executed by a LocalCommandService bean (using the local interface).
 * The local command service can be configured in the deployment descriptor. 
 * The local reference used is java:comp/env/ejb and then LocalCommandServiceHome. 
 * 
 * @author Jim Rigsbee, Tom Baeyens
 */
public class CommandListenerBean implements MessageDrivenBean, MessageListener {

  private static final long serialVersionUID = 1L;
  
  protected MessageDrivenContext messageDrivenContext = null;

  public void onMessage(Message message) {
    Command command = extractCommand(message);
    if (command!=null) {
      try {
        log.debug("looking up local command service");
        Context initial = new InitialContext();
        Context environment = (Context) initial.lookup("java:comp/env");
        LocalCommandServiceHome localCommandServiceHome = (LocalCommandServiceHome) environment.lookup("ejb/LocalCommandServiceBean");
        LocalCommandService localCommandService = localCommandServiceHome.create();
        try {
          // TODO add support for sending back the result
          // not a priority because i don't see much use cases for that.
          // let me know on the dev forums if you think otherwise.
          log.debug("executing command with local command service");
          localCommandService.execute(command);
        } finally {
          localCommandService.remove();
        }
      } catch (Exception e) {
        e.printStackTrace();
        // TODO add a retry mechanism or verify how this can be configured in the MDB itself
        throw new JbpmException("command "+command+" couldn't be executed", e);
      }
    }
  }

  protected Command extractCommand(Message message) {
    Command command = null;
    if (message instanceof ObjectMessage) {
      try {
        log.debug("deserializing command from jms message...");
        ObjectMessage objectMessage = (ObjectMessage)message;
        Serializable object = objectMessage.getObject();
        if (object== null) {
          log.warn("ignoring null message");
        } else if (object instanceof Command) {
          command = (Command) object;
        } else {
          log.warn("ignoring object message cause it's not a command '"+object+"' ("+object.getClass().getName()+")");
        }
      } catch (Exception e) {
        e.printStackTrace();
        // TODO verify that such a message gets redirected to the dead letter queue
        // or something similar without retrying.
        throw new JbpmException("command listener bean only can handle object messages with Command's as the object.");
      }
    } else {
      log.warn("ignoring message '"+message+"' cause it's not an ObjectMessage ("+message.getClass().getName()+")");
    }
    return command;
  }

  public void setMessageDrivenContext(MessageDrivenContext messageDrivenContext) {
    this.messageDrivenContext = messageDrivenContext;
  }

  public void ejbRemove() throws EJBException {
    messageDrivenContext = null;
  }

  public void ejbCreate() {}

  private static final Log log = LogFactory.getLog(CommandListenerBean.class);
}

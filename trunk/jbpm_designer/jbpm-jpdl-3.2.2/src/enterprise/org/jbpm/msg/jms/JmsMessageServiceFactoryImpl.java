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
package org.jbpm.msg.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.jbpm.JbpmException;
import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;

public class JmsMessageServiceFactoryImpl implements ServiceFactory {

  private static final long serialVersionUID = 1L;
  
  String connectionFactoryJndiName = "java:/JmsXA";
  String destinationJndiName = "queue/JbpmJobQueue";
  boolean isCommitEnabled = false;

  public Service openService() {
    Connection connection = null;
    Session session = null;
    Destination destination = null;
    
    try {
      InitialContext initialContext = new InitialContext();
      ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup(connectionFactoryJndiName);
      destination = (Destination) initialContext.lookup(destinationJndiName);
      connection = connectionFactory.createConnection();
      
      // If you use an XA connection factory in JBoss, the parameters will be ignored.  It will always take part in the global JTA transaction.
      // If you use a non XQ connection factory, the first parameter specifies wether you want to have all message productions and 
      // consumptions as part of one transaction (TRUE) or wether you want all productions and consumptions to be instantanious (FALSE)
      // Of course, we never want messages to be received before the current jbpm transaction commits so we just set it to true.
      session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
      
    } catch (Exception e) {
      throw new JbpmException("couldn't open jms message session", e);
    }
    
    return new JmsMessageServiceImpl(connection, session, destination, isCommitEnabled);
  }

  public void close() {
  }

}

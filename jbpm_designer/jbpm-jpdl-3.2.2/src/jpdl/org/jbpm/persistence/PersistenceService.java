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
package org.jbpm.persistence;

import org.hibernate.SessionFactory;
import org.jbpm.db.ContextSession;
import org.jbpm.db.GraphSession;
import org.jbpm.db.JobSession;
import org.jbpm.db.LoggingSession;
import org.jbpm.db.TaskMgmtSession;
import org.jbpm.svc.Service;

public interface PersistenceService extends Service {
  
  void assignId(Object object);

  GraphSession getGraphSession();
  LoggingSession getLoggingSession();
  JobSession getJobSession();
  ContextSession getContextSession();
  TaskMgmtSession getTaskMgmtSession();

  boolean isRollbackOnly();
  void setRollbackOnly(boolean isRollbackOnly);
  void setRollbackOnly();

  void setGraphSession(GraphSession graphSession);
  void setLoggingSession(LoggingSession loggingSession);
  void setJobSession(JobSession jobSession);
  void setTaskMgmtSession(TaskMgmtSession taskMgmtSession);
  void setSessionFactory(SessionFactory sessionFactory);
}
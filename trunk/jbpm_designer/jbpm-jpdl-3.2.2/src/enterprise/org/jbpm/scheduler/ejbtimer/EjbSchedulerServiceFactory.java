package org.jbpm.scheduler.ejbtimer;

import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;

public class EjbSchedulerServiceFactory implements ServiceFactory {

  private static final long serialVersionUID = 1L;

  public Service openService() {
    return new EjbSchedulerService();
  }

  public void close() {
  }
}

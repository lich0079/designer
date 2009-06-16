package org.jbpm.scheduler.ejbtimer;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.db.JobSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.job.Timer;
import org.jbpm.scheduler.SchedulerService;

public class EjbSchedulerService implements SchedulerService {
  
  private static final long serialVersionUID = 1L;

  JobSession jobSession;
  LocalTimerService localTimerService;
  
  public EjbSchedulerService() {
    JbpmContext jbpmContext = JbpmContext.getCurrentJbpmContext();
    if (jbpmContext==null) {
      throw new JbpmException("instantiation of the EjbSchedulerService requires a current JbpmContext");
    }
    this.jobSession = jbpmContext.getJobSession();
    
    try {
      Context initial = new InitialContext();
      LocalTimerServiceHome localTimerServiceHome = (LocalTimerServiceHome) initial.lookup("java:comp/env/ejb/LocalTimerServiceBean");
      localTimerService = localTimerServiceHome.create();
    } catch (Exception e) {
      JbpmException jbpmException = new JbpmException("ejb local timer lookup problem", e);
      log.error(e);
      throw jbpmException;
    }
  }

  public void createTimer(Timer timer) {
    log.debug("creating timer "+timer);
    jobSession.saveJob(timer);
    localTimerService.createTimer(timer);
  }

  public void deleteTimersByName(String timerName, Token token) {
    log.debug("deleting timers by name "+timerName);
    jobSession.cancelTimersByName(timerName, token);
    localTimerService.cancelTimersByName(timerName, token);
  }

  public void deleteTimersByProcessInstance(ProcessInstance processInstance) {
    log.debug("deleting timers for process instance "+processInstance);
    jobSession.deleteJobsForProcessInstance(processInstance);
    localTimerService.deleteTimersForProcessInstance(processInstance);
  }

  public void close() {
    try {
      log.debug("removing the timer service session bean");
      localTimerService.remove();
    } catch (Exception e) {
      JbpmException jbpmException = new JbpmException("ejb local timer service close problem", e);
      log.error(e);
      throw jbpmException;
    }
  }
  
  private static Log log = LogFactory.getLog(EjbSchedulerService.class);
}

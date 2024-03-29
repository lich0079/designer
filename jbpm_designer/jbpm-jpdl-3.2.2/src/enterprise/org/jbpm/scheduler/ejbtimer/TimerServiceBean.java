package org.jbpm.scheduler.ejbtimer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.ejb.LocalCommandService;
import org.jbpm.ejb.LocalCommandServiceHome;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.job.Timer;

public class TimerServiceBean implements SessionBean, TimedObject {

  private static final long serialVersionUID = 1L;

  SessionContext sessionContext;
  
  public void ejbCreate() {
  }

  public void createTimer(Timer timer) {
    TimerService timerService = sessionContext.getTimerService();
    log.debug("creating timer "+timer+" in the ejb timer service");
    timerService.createTimer(timer.getDueDate(), new TimerInfo(timer));
  }

  public void cancelTimersByName(String timerName, Token token) {
    
    // TODO make the scanning of timers for cancellation optional by only deleting the timerjobs in the db.
    // of course, the corresponding ejb timer notifications have to be ignored. 

    log.debug("cancelling timers with name "+timerName+" from the ejb timer service");

    TimerService timerService = sessionContext.getTimerService();
    Iterator iter = timerService.getTimers().iterator();
    while (iter.hasNext()) {
      javax.ejb.Timer ejbTimer = (javax.ejb.Timer) iter.next();
      if (ejbTimer.getInfo() instanceof TimerInfo) {
        TimerInfo timerInfo = (TimerInfo) ejbTimer.getInfo();
        if (timerInfo.matchesName(timerName, token)) {
          ejbTimer.cancel();
        }
      }
    }
  }

  public void deleteTimersForProcessInstance(ProcessInstance processInstance) {
    
    // TODO make the scanning of timers for cancellation optional by only deleting the timerjobs in the db.
    // of course, the corresponding ejb timer notifications have to be ignored. 

    log.debug("deleting timers for process instance "+processInstance+" from the ejb timer service");

    TimerService timerService = sessionContext.getTimerService();
    Iterator iter = timerService.getTimers().iterator();
    while (iter.hasNext()) {
      javax.ejb.Timer ejbTimer = (javax.ejb.Timer) iter.next();
      if (ejbTimer.getInfo() instanceof TimerInfo) {
        TimerInfo timerInfo = (TimerInfo) ejbTimer.getInfo();
        if (timerInfo.matchesProcessInstance(processInstance)) {
          ejbTimer.cancel();
        }
      }
    }
  }

  public void ejbTimeout(javax.ejb.Timer ejbTimer) {
    log.debug("ejb timer "+ejbTimer+" fires");
    String localCommandServiceJndiName = "java:comp/env/ejb/LocalCommandServiceBean";
    try {
      Context initial = new InitialContext();
      LocalCommandServiceHome localCommandServiceHome = (LocalCommandServiceHome) initial.lookup(localCommandServiceJndiName);
      LocalCommandService localCommandService = localCommandServiceHome.create();
      Serializable info = ejbTimer.getInfo();
      if (! (info instanceof TimerInfo)) {
        if (info ==null) {
          throw new NullPointerException("timer info is null");
        } else {
          throw new ClassCastException("timer info ("+info.getClass().getName()+") is not of the expected class "+TimerInfo.class.getName());
        }
      }
      TimerInfo timerInfo = (TimerInfo) info;
      Timer timer = (Timer) localCommandService.execute(new ExecuteTimerCommand(timerInfo.getTimerId()));
      // if the timer has repeat
      if ( (timer!=null)
           && (timer.getRepeat()!=null)
         ) {
        // create a new timer
        log.debug("scheduling timer for repeat at "+timer.getDueDate());
        createTimer(timer);
      }
    } catch (Exception e) {
      JbpmException jbpmException = new JbpmException("couldn't execute timer", e);
      log.error(jbpmException);
      throw jbpmException;
    }
  }

  public void setSessionContext(SessionContext sessionContext) throws EJBException, RemoteException {
    this.sessionContext = sessionContext;
  }

  public void ejbActivate() throws EJBException, RemoteException {
  }
  public void ejbPassivate() throws EJBException, RemoteException {
  }
  public void ejbRemove() throws EJBException, RemoteException {
  }
  
  private static Log log = LogFactory.getLog(TimerServiceBean.class);
}

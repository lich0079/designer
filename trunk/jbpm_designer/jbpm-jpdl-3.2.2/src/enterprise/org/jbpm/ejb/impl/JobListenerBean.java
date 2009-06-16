package org.jbpm.ejb.impl;

import java.util.Enumeration;

import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.command.Command;

public class JobListenerBean extends CommandListenerBean {

  private static final long serialVersionUID = 1L;

  protected Command extractCommand(Message message) {
    Command command = null;
    try {
      // checking for availability of the jobId property
      boolean isJobIdAvailable = false;
      Enumeration enumeration = message.getPropertyNames();
      while ( (!isJobIdAvailable)
              && (enumeration.hasMoreElements())
            ) {
        if ("jobId".equals(enumeration.nextElement())) {
          isJobIdAvailable = true;
        }
      }

      if (isJobIdAvailable) {
        log.debug("getting job id from jms message...");
        long jobId = message.getLongProperty("jobId");
        log.debug("retrieved jobId '"+jobId+"' via jms message");
        command = new ExecuteJobCommand(jobId);
      } else {
        log.warn("JobListenerBean is ignoring message '"+message+"' that doesn't have jobId property");
      }

    } catch (Exception e) {
      e.printStackTrace();
      // TODO verify that such a message gets redirected to the dead letter queue
      // or something similar without retrying.
      throw new JbpmException("job listener bean only can handle messages with a long-property jobId that refers to a Job in the JBPM_JOB tables.");
    }
    return command;
  }

  private static Log log = LogFactory.getLog(JobListenerBean.class);
}

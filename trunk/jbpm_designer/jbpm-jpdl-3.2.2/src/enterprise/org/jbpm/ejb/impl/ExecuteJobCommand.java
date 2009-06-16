package org.jbpm.ejb.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.command.Command;
import org.jbpm.db.JobSession;
import org.jbpm.job.Job;

public class ExecuteJobCommand implements Command {

  private static final long serialVersionUID = 1L;
  
  long jobId;
  
  public ExecuteJobCommand(long jobId) {
    this.jobId = jobId;
  }

  public Object execute(JbpmContext jbpmContext) throws Exception {
    JobSession jobSession = jbpmContext.getJobSession();
    log.debug("loading job "+jobId);
    Job job = jobSession.loadJob(jobId);
    log.debug("executing job "+jobId);
    if (!job.execute(jbpmContext)) {
      log.warn("job "+jobId+" was not supposed to be deleted");
    }
    jobSession.deleteJob(job);
    return null;
  }

  private static Log log = LogFactory.getLog(ExecuteJobCommand.class);
}

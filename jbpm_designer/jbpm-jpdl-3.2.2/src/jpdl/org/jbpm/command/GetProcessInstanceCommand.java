package org.jbpm.command;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.logging.log.ProcessLog;

/**
 * This command can retrieve the matching process instances (e.g. for admin
 * client) with the given process-id, token id or task-id
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 */
public class GetProcessInstanceCommand extends AbstractGetObjectBaseCommand {

    private static final long serialVersionUID = -8436697080972165601L;

    private static final Log log = LogFactory.getLog(GetProcessInstanceCommand.class);

    private long processInstanceId;

    private long tokenId;

    private long taskInstanceId;

    public GetProcessInstanceCommand() {
    }

    public GetProcessInstanceCommand(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public GetProcessInstanceCommand(long processInstanceId, boolean includeVariables, boolean includeLogs) {
        super(true, true);
        this.processInstanceId = processInstanceId;
    }

    public Object execute(JbpmContext jbpmContext) throws Exception {
        setJbpmContext(jbpmContext);

        ProcessInstance processInstance = null;
        if (processInstanceId != 0)
            processInstance = jbpmContext.getProcessInstance(processInstanceId);
        else if (tokenId != 0)
            processInstance = jbpmContext.getToken(tokenId).getProcessInstance();
        else if (taskInstanceId != 0)
            processInstance = jbpmContext.getTaskInstance(taskInstanceId).getProcessInstance();

        if (processInstance != null) {
            processInstance = retrieveProcessInstance(processInstance);
        }
        return processInstance;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public long getTaskInstanceId() {
        return taskInstanceId;
    }

    public void setTaskInstanceId(long taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

}

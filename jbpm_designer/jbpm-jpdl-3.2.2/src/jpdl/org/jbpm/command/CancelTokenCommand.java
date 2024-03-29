package org.jbpm.command;

import java.util.Date;
import java.util.Iterator;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 * 
 */
public class CancelTokenCommand extends AbstractCancelCommand implements Command {

    private static final long serialVersionUID = 7145293049356621597L;

    private long tokenId;

    public CancelTokenCommand() {
    }

    public CancelTokenCommand(long tokenId) {
        this.tokenId = tokenId;
    }

    public Object execute(JbpmContext jbpmContext) throws Exception {
        this.jbpmContext = jbpmContext;
        cancelToken(jbpmContext.getGraphSession().loadToken(tokenId));
        this.jbpmContext = null;
        return null;
    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

}
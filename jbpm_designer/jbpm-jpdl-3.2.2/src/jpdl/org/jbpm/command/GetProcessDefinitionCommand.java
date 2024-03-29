package org.jbpm.command;

import java.util.List;

import org.jbpm.JbpmContext;

/**
 * This Command return the process definition
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 */
public class GetProcessDefinitionCommand extends AbstractGetObjectBaseCommand {
	
	private static final long serialVersionUID = -1908847549444051495L;
	
	private int version = -1;
	
	private String name;

	public GetProcessDefinitionCommand() {		
	}

	public GetProcessDefinitionCommand(String name) {
		super();
		this.name = name;
	}
	
	public GetProcessDefinitionCommand(String name, int version) {
		super();
		this.version = version;
		this.name = name;
	}
	
	public Object execute(JbpmContext jbpmContext) throws Exception {
		if (version >= 0)
			return retrieveProcessDefinition(
					jbpmContext.getGraphSession().findProcessDefinition(name, version));
		else
			return retrieveProcessDefinition(
					jbpmContext.getGraphSession().findLatestProcessDefinition(name));
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected int getVersion() {
		return version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}
}

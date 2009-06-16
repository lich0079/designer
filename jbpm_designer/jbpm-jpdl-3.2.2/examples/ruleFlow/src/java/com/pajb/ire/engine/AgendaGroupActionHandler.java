package com.pajb.ire.engine;


import org.drools.WorkingMemory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;


public class AgendaGroupActionHandler implements ActionHandler {

	private static final long serialVersionUID = 1L;
	
	 
	public String agendaGroup;
	public String workingMemoryVariableName = "workingMemoryVariable";
	

	/**
	 * The FireRulesActionHandler gets variables from the Instance, and asserts
	 * them into the Rules Engine and invokes the rules.
	 */
	public void execute(ExecutionContext executionContext) throws Exception {
      
		ContextInstance contextInstance = executionContext.getContextInstance();
		WorkingMemory workingMemory = (WorkingMemory) contextInstance.getVariable(workingMemoryVariableName);
		
		QuoteWorkingObject quoteWorkingObject = (QuoteWorkingObject) contextInstance.getVariable("quoteWorkingObject");
		   
		workingMemory.setFocus(agendaGroup);
        workingMemory.fireAllRules();
        
        quoteWorkingObject = (QuoteWorkingObject) workingMemory.getGlobal("quoteWorkingObject");
        
        System.out.println("agenda group is " + agendaGroup);
        System.out.println("declineCount is " + quoteWorkingObject.getDeclineCount());
        System.out.println("premium is " + quoteWorkingObject.getPremium());
        executionContext.getToken().signal();

        
	}
	    
	    
	    	

}

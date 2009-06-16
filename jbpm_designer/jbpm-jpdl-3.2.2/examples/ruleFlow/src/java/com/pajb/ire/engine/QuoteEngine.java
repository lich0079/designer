/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.engine;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

//import javax.ejb.Remote;
//import javax.ejb.Stateless;
// import javax.jws.WebMethod;
// import javax.jws.WebService;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import com.pajb.ire.domain.Address;
import com.pajb.ire.domain.Bike;
import com.pajb.ire.domain.Person;
import com.pajb.ire.domain.Quote;
 
/**
 * An implementation of the Quotable interface which includes the 
 * annotations required to deploy this POJO within an EJB3 
 * container in the form of a stateless session bean
 * as well as used directly.
 * <p>
 * The internal implementation makes use of the JBoss Rules 
 * rules engine to process the application. When 
 * <code>processApplication</code> is called, a 
 * <code>RuleBase</code> is 
 * instantiated and the DRL rules definition file located at 
 * <code>/InsuranceRulesEngine.drl</code> or in the location 
 * set in <code>ruleFileLocation</code> is parsed. Then 
 * a <code>WorkingMemory</code> is instantiated, the 
 * <code>Person</code> array; the <code>Bike</code> array and the 
 * <code>Address</code> are asserted into the 
 * <code>WorkingMemory</code> and the rules run against these 
 * objects.
 * <p>
 * This implementation also asserts a 
 * <code>QuoteWorkingObject</code> into the working memory and 
 * expects the rules defined in the DRL file to work with this 
 * object so that once <code>WorkingMemory.fireAllRules</code> 
 * has returned, the <code>QuoteWorkingObject.getQuote</code> 
 * method returns the results of the application as a 
 * <code>Quote</code> object, which is returned.
 * <p>
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 * 
 * @see com.pajb.ire.domain.Person
 * @see com.pajb.ire.domain.Bike
 * @see com.pajb.ire.domain.Address
 * @see com.pajb.ire.domain.Quote
 * @see com.pajb.ire.engine.QuoteWorkingObject
 * @see org.drools.RuleBase;
 * @see org.drools.WorkingMemory;
 */
//@Stateless
//// @WebService(serviceName="QuoteEngineWS")
//@Remote(Quotable.class)
public class QuoteEngine
   implements Quotable
{  
   /** A JBoss Rules RuleBase */
   private RuleBase ruleBase;
   
   /** A JBoss Rules WorkingMemory */
   private WorkingMemory workingMemory;
   
   /** The location of the rules file to be parsed */
   private String rulesFileLocation = "/InsuranceRules.drl";
   
   /** A List of all the FactHandles for objects asserted into
    *  the JBoss Rules working memory.
    */
   private List<FactHandle> factHandleList 
      = new ArrayList<FactHandle>();
   
   // @WebMethod()
   public Quote processApplication(Person[] people,
                                   Bike[]   bikes,
                                   Address  address)
   {
      long startTime = System.currentTimeMillis();
           
      workingMemory = getWorkingMemory();
      // file logger does not appear to work with jBPM when workingMemory is stored as jBPM context variable
 //     final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( workingMemory );
 //     logger.setFileName( "log/insurance" );
     System.out.println("Working Memory is: " + workingMemory);
     
      // Assert all the arguments into the working memory
      for (Person person : people) assertObject(person);
      for (Bike bike : bikes)      assertObject(bike);
                                   assertObject(address);
      
      // Create a new working object for the Rules Engine
      QuoteWorkingObject quoteWorkingObject
         = new QuoteWorkingObject();
      
      // And assert this into the engine
      //assertObject(quoteWorkingObject);
      workingMemory.setGlobal("quoteWorkingObject", quoteWorkingObject);
      
       ProcessDefinition processDefinition = ProcessDefinition.parseParResource("InsuranceRuleFlow.par");
 
       ProcessInstance processInstance = new ProcessInstance(processDefinition);
       processInstance.getContextInstance().setVariable("workingMemoryVariable", workingMemory);    
       processInstance.getContextInstance().setVariable("quoteWorkingObject", quoteWorkingObject);    
       System.out.println("Working Memory is: " + workingMemory);
       Token token = processInstance.getRootToken();
    	    
       token.signal();


      // Build a QuoteObject from the QuoteWorkingObject
      Quote quote = quoteWorkingObject.getQuote();
      
      // Remove all the arguments from the working memory
      retractAllObjects();

      System.out.println("Elapsed Time : "+(System.currentTimeMillis() - startTime)+"ms.");
      
 //     logger.writeToDisk();

      // Return the QuoteObject we have built
      return quote;
   }
   
   /**
    * Return the location of the file that will be parsed to 
    * configure the <code>RuleBase</code> for JBoss Rules. This
    * file should be in the DRL format.
    * 
    * @return the location of the DRL file
    */
   public String getRulesFileLocation()
   {
      return rulesFileLocation;
   }

   /**
    * Set the location of the file that will be parsed to 
    * configure the <code>RuleBase</code> for JBoss Rules. This
    * file should be in the DRL format.
    * 
    * @param rulesFileLocation the location of the DRL file
    */
   public void setRulesFileLocation(String rulesFileLocation)
   {
      this.rulesFileLocation = rulesFileLocation;
      
      workingMemory = null;
      ruleBase = null;
   }
   
   /**
    * Assert an Object into the WorkingMemory and add the returned
    * FactHandle to the maintained list
    * 
    * @param object the Object to assert
    * @return the FactHandle from JBoss Rules
    */
   private FactHandle assertObject(Object object)
   {
      FactHandle factHandle = getWorkingMemory().assertObject(object);
      
      factHandleList.add(factHandle);
   
      return factHandle;
   }
      
   /**
    * Retract all the objects which have been asserted into the 
    * JBoss Rules <code>WorkingMemory</code> using 
    * <code>assertObject</code>.
    */
   private void retractAllObjects()
   {
      for (FactHandle factHandle : factHandleList)
      {
         getWorkingMemory().retractObject(factHandle);
      }
      
      factHandleList.clear();
   }
   
   
   /**
    * Return a <code>WorkingMemory</code> from the 
    * <code>RuleBase</code>, creating it if required or returning
    * the existing one if it has already been created.
    * 
    * @return a WorkingMemory for the RuleBase
    *  RuntimeException if the DRL file cannot be loaded
    */
   private WorkingMemory getWorkingMemory()
   {
      if (workingMemory == null)
      {
         workingMemory = getRuleBase().newWorkingMemory();
      }
      
      return workingMemory;
   }
   
   /**
    * Return a <code>RuleBase</code> from the DRL file specified 
    * in the <code>rulesFileLocation</code> property. Creates this 
    * RuleBase if the DRL file has not yet been parsed, otherwise 
    * parses the DRL file to produce the RuleBase.
    * 
    * @return a RuleBase from the DRL file
    *  RuntimeException if the DRL file cannot be loaded
    */
   private RuleBase getRuleBase()
   {
      if (ruleBase == null)
      {
         ruleBase = loadRuleBase();
      }
      
      return ruleBase;
   }
   
   /**
    * Loads the <code>RuleBase</code> from the DRL.
    * 
    * @return the RuleBase from the DRL file
    *  RuntimeException if the DRL file cannot be loaded
    */
   private RuleBase loadRuleBase()
   {
      try
      {      
       //read in the source
       Reader source 
          = new InputStreamReader(getClass().getResourceAsStream(getRulesFileLocation()) );

       //optionally read in the DSL (if you are using it).
       //Reader dsl = new InputStreamReader( DroolsTest.class.getResourceAsStream( "/mylang.dsl" ) );

       //Use package builder to build up a rule package.
       //An alternative lower level class called "DrlParser" can also be used...
       PackageBuilder builder = new PackageBuilder();

       //this wil parse and compile in one step
       //NOTE: There are 2 methods here, the one argument one is for normal DRL.
       builder.addPackageFromDrl( source );

       //Use the following instead of above if you are using a DSL:
       //builder.addPackageFromDrl( source, dsl );
       
       //get the compiled package (which is serializable)
       Package pkg = builder.getPackage();
       
       //add the package to a rulebase (deploy the rule package).
       RuleBase ruleBase = RuleBaseFactory.newRuleBase();
       ruleBase.addPackage( pkg );
       
       return ruleBase;
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         throw new RuntimeException("Unable to Load Rules Base", ex);
      }      
   }
}

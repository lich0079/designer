/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.client;

import java.math.BigDecimal;

import com.pajb.ire.domain.Address;
import com.pajb.ire.domain.AddressObject;
import com.pajb.ire.domain.Bike;
import com.pajb.ire.domain.BikeObject;
import com.pajb.ire.domain.Person;
import com.pajb.ire.domain.PersonObject;
import com.pajb.ire.domain.Quote;
//import com.pajb.ire.ejb3.EjbHelper;
import com.pajb.ire.engine.Quotable;
import com.pajb.ire.engine.QuoteEngine;

public class IreClient
{
   // Different client types
   public enum ClientType { LOCAL, EJB3, WS};
   
   // Our client type
   private ClientType clientType = ClientType.LOCAL;
   
   // A reference to the actual service that implements quoting
   private Quotable quotingService;  
   
   private static String CLIENT_TYPE_ARG = "-clientType=";
   
   private IreGui gui = null;
   
   public IreClient(String[] args)
   {
      for (String arg : args)
      {
         if (arg.startsWith(CLIENT_TYPE_ARG))
         {
            String clientTypeParam 
               = arg.substring(CLIENT_TYPE_ARG.length());
         
            if (clientTypeParam.equals("local"))
            {
               clientType = ClientType.LOCAL;
            }
            else if (clientTypeParam.equals("ejb3"))
            {
               clientType = ClientType.EJB3;
            }
            else if (clientTypeParam.equals("ws"))
            {
               clientType = ClientType.WS;
            }
            else
            {
               System.out.println("Unknown Client Type: "+clientTypeParam);
            }
         }
         else
         {
            System.out.println("Unknown Argument: "+arg);
         }
      }
      
      gui = new IreGui(getQuotingService());
   }
   
   public void setClientType(ClientType clientType)
   {
      this.clientType = clientType;
      quotingService = null;
   }
   
   public Quotable getQuotingService()
   {
      if (quotingService == null)
      {
         // Decide on the client type
         if (clientType.equals(ClientType.LOCAL))
         {
            quotingService = new QuoteEngine();
         }
//         else if (clientType.equals(ClientType.EJB3))
//         {
//            quotingService
//               = (Quotable) EjbHelper.getInstance().getService("QuoteEngine/remote");
//         }
//         else if (clientType.equals(ClientType.WS))
//         {
//            throw new RuntimeException("WS not currently supported");
//         }
//         else
//         {
//            throw new RuntimeException("Unsupported Client Type "+clientType);
//         }
      }
      
      return quotingService;
   }
   
   public Quote getQuote(Person[] person,
                         Bike[] bike,
                         Address address)
   {
      return getQuotingService()
            .processApplication(person, bike, address);
   }
   
   /**
    * Takes the following arugments
    * -clientType=local instantiate a local QuoteEngine
    * -clientType=ejb3 access a remote QuoteEngine by EJB3
    * -clientType=ws access a remote QuoteEngine by WS
    */
   public static void main(String[] args)
   {
      // Produce an ire client object
      IreClient ireClient = new IreClient(args);
      
      /**
      Person[] persons = new Person[2];
      persons[0] = new PersonObject(45, Person.Gender.MALE, true);
      persons[1] = new PersonObject(23, Person.Gender.FEMALE, false);
      
      Bike[] bikes = new Bike[1];
      bikes[0] = new BikeObject(24, Bike.Colour.BLACK, 1000);
      
      Address address = new AddressObject("CB1", Address.MosaicCode.A, Address.DwellingType.FLAT);
   
      for (int i=0; i<2; i++)
      {
         System.out.println("Testing QuoteEngine");
         Quote quote = ireClient.getQuote(persons, bikes, address);
         System.out.println(quote);
         System.out.println("Test Complete");
      }
      
      bikes[0] = new BikeObject(24, Bike.Colour.GREEN, 500);
      
      for (int i=0; i<2; i++)
      {
         System.out.println("Testing QuoteEngine");
         Quote quote = ireClient.getQuote(persons, bikes, address);
         System.out.println(quote);
         System.out.println("Test Complete");
      }      
   **/
   }
}

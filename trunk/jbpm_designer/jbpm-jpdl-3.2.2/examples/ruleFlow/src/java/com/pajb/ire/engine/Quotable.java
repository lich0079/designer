/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.engine;

import com.pajb.ire.domain.Address;
import com.pajb.ire.domain.Bike;
import com.pajb.ire.domain.Person;
import com.pajb.ire.domain.Quote;

/**
 * A plain old Java interface that defines the business method by 
 * which an application for Insurance is processed and a Quote 
 * generated.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public interface Quotable
{
   /**
    * Process an Application for insurance from a potential customer 
    * and return a Quote object encapsulating the results of the 
    * application.
    * <p>
    * 
    * @param people an array of the People who would be insured 
    *   as a part of the application
    * @param bikes an array of the Bikes who would be insured as 
    *   a part of the application
    * @param address the single address at which the people in the 
    *   application live and the bikes are generally stored
    * @return a Quote object containing the results of the 
    *   application
    *   
    * @see com.pajb.ire.domain.Person
    * @see com.pajb.ire.domain.Bike
    * @see com.pajb.ire.domain.Address
    * @see com.pajb.ire.domain.Quote
    */
   public Quote processApplication(Person[] people,
                                   Bike[]   bikes,
                                   Address  address);
}

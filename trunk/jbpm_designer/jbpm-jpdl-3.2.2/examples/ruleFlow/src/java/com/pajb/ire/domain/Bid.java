/**
 * 
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;

import java.math.BigDecimal;

/**
 * A business interface defining a Bid from an insurance provider
 * in response to a request for a Quote from the insurance engine
 * by a potential customer.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public interface Bid
{
   /**
    * Returns a String representing the name of the partner who 
    * is making the Bid.
    * 
    * @return the partner name in the form of a String
    */
   public String getPartnerName();
   
   /**
    * Sets a String representing the name of the partner who 
    * is making the Bid.
    * 
    * @param partnerName the partner name in the form of a String
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setPartnerName(String partnerName);
   
   /**
    * Returns the premium price for this Bid
    * 
    * @return the price
    */
   public double getPremiumPrice();
   
   /**
    * Sets the premium price for this Bid
    * 
    * @param premiumPrice the price
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */   
   public void setPremiumPrice(double premiumPrice);
}
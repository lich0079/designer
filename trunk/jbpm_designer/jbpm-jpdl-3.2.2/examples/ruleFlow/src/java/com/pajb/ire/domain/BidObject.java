/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A concrete read/write implementation of the Bid interface
 * which is Serializable to allow it to be passed across a 
 * network interface.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public class BidObject
   implements Bid, 
              Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -21739884362425334L;
  
   private String partnerName;
   private double premiumPrice;
   
   public BidObject(String partnerName, double premiumPrice)
   {
      this.partnerName = partnerName;
      this.premiumPrice = premiumPrice;
   }
   
   public String getPartnerName()
   {
      return partnerName;
   }
   
   public void setPartnerName(String partnerName)
   {
      this.partnerName = partnerName;
   }
   
   public double getPremiumPrice()
   {
      return premiumPrice;
   }
   
   public void setPremiumPrice(double premiumPrice)
   {
      this.premiumPrice = premiumPrice;
   }
   
   public String toString()
   {
      return "Bid[partnerName="+getPartnerName()+", premiumPrice="+getPremiumPrice()+"]";
   }
   
   public boolean equals(BidObject other)
   {
      return (other.getPartnerName() == getPartnerName() &&
              other.getPremiumPrice() == getPremiumPrice() );
   }
   
   public int hashCode()
   {
      return getPartnerName().hashCode() * 17 >>>
             ((Double) getPremiumPrice()).hashCode();
   }   
}

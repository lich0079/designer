/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;

import java.io.Serializable;

/**
 * A concrete read/write implementation of the Quote interface
 * which is Serializable to allow it to be passed across a 
 * network interface.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public class QuoteObject 
   implements Quote, 
              Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -3098800454674716918L;
   
   private boolean declined;
   private String[] reasonsForDeclined;
   private Bid[] bids;
   
   public QuoteObject(boolean declined, String[] reasonsForDeclined, Bid[] bids)
   {
      this.declined = declined;
      this.reasonsForDeclined = reasonsForDeclined;
      this.bids = bids;
   }

   public Bid[] getBids()
   {
      return bids;
   }
   
   public void setBids(Bid[] bids)
   {
      this.bids = bids;
   }
   
   public boolean isDeclined()
   {
      return declined;
   }
   
   public void setDeclined(boolean declined)
   {
      this.declined = declined;
   }
   
   public String[] getReasonsForDeclined()
   {
      return reasonsForDeclined;
   }
   
   public void setReasonsForDeclined(String[] reasonsForDeclined)
   {
      this.reasonsForDeclined = reasonsForDeclined;
   }
   
   public String toString()
   {
      StringBuffer stringBuffer = new StringBuffer();
      
      if (isDeclined())
      {
         stringBuffer.append("DECLINED: ");
         for (int i=0; getReasonsForDeclined()!=null && i<getReasonsForDeclined().length; i++)
         {
            stringBuffer.append("["+getReasonsForDeclined()[i]+"] ");
         }
      }
      else
      {
         stringBuffer.append("ACCEPTED: ");
         for (int i=0; getBids()!=null && i<getBids().length; i++)
         {
            stringBuffer.append("["+getBids()[i]+"] ");
         }         
      }
      
      return stringBuffer.toString();
   }
}

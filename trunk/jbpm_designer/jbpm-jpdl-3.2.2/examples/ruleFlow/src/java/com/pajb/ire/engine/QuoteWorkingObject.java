/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.rule.Rule;

import com.pajb.ire.domain.Bid;
import com.pajb.ire.domain.BidObject;
import com.pajb.ire.domain.Quote;
import com.pajb.ire.domain.QuoteObject;

public class QuoteWorkingObject
   implements Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 2075891925265010916L;

   /** A List of the Rules that have fired declining the quote */
   private List<Rule> declineRules = new ArrayList<Rule>();
   
   /** A List of the String descriptions of why the quote has been declined */
   private List<String> declineReasons = new ArrayList<String>();
   
   /** The current premium */
   private double premium = 0.0D;

   public QuoteWorkingObject()
   {
      
   }
   
   public double getPremium()
   {
      return premium;
   }

   public void setPremium(double premium)
   {
      this.premium = premium;
   }
   
   public void multiplyPremium(double multiplier)
   {
      setPremium(getPremium()*multiplier);
   }
   
   public void addPremium(double delta)
   {
      setPremium(getPremium()+delta);
   }
   
   public Quote getQuote()
   {
      boolean declined = declineReasons.size() > 0;
      
      String[] reasons = null;
      Bid[] bids = null;
      if (declined)
      {
         reasons = new String[declineReasons.size()];
         reasons = declineReasons.toArray(reasons);
      }
      else
      {
         bids = new Bid[1];
         bids[0] = new BidObject("XYZ Insurers", getPremium());
      }
      
      return new QuoteObject(declined, reasons, bids);
   }
   
   /**
    * Adds a reason to the system to decline the quote.
    * 
    * @param rule the rule that caused the system to decline the quote
    * @param reason a descriptive String as to why the quote was declined
    */
   public void decline(Rule rule, String reason)
   {
      declineRules.add(rule);
      declineReasons.add(reason);
   }
   
   /**
    * Returns the number of rules the quote has hit that have caused 
    * it to be declined
    */
   public int getDeclineCount()
   {
      return declineRules.size();
   }
   
   
}

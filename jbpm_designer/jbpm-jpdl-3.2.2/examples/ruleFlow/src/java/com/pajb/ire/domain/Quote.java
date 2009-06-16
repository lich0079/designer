/*
 * 
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;

/**
 * A business interface defining the results of a request for 
 * insurance from the insurance quote engine.
 * <p>
 * The request will either be declined or not declined.
 * <p>
 * If the request has been declined, then the 
 * <code>getReasonsForDeclined</code> method will return an 
 * array of Strings specifying the reason(s) the request 
 * was declined and an empty array of Bids.
 * <p>
 * If the request has not been declined, then the 
 * <code>getBids</code> method will return an array of the Bids
 * made in response to the request and an empty array of 
 * reasons for decline.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public interface Quote
{
   /**
    * If <code>isDeclined</code> is <code>false</code>, then this 
    * method returns an array of the Bid objects representing 
    * responses to the request.
    * 
    * @return an array of Bids or an empty array
    */
   public Bid[] getBids();
   
   /**
    * Sets the array of Bids offered in response to this request.
    * 
    * @param bids an array of Bids
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setBids(Bid[] bids);

   /**
    * Returns the status of the insurance engines response to the 
    * request.
    * 
    * @return true if the request has generated one or more bids, 
    *   false if it has been declined for one or more reasons
    */
   public boolean isDeclined();
   
   /**
    * Sets the status of the insurance engines response to the 
    * request.
    * 
    * @param declined true if the request has generated one or more bids, 
    *   false if it has been declined for one or more reasons
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setDeclined(boolean declined);

   /**
    * Returns an array of the reasons why the insurance request 
    * has been declined.
    * 
    * @return a String array of reasons why the request has been 
    *   declined
    */
   public String[] getReasonsForDeclined();
   
   /**
    * Sets an array of the reasons why the insurance request 
    * has been declined.
    * 
    * @param reasonsForDeclined a String array of reasons why the request has been 
    *   declined
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setReasonsForDeclined(String[] reasonsForDeclined);
}
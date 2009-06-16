/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;


/**
 * A business interface defining a Bike which a customer is aiming
 * to ensure.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public interface Bike
{
   /**
    * An enumeration defining the possible Colour classifications for
    * the Bike.
    */
   public enum Colour {RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET, WHITE, BLACK};

   /**
    * Returns the Colour of this Bike
    * 
    * @return the Colour of this Bike
    */
   public Colour getColour();
   
   /**
    * Sets the Colour of this Bike
    * 
    * @param colour the Colour of this Bike
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setColour(Colour colour);

   /**
    * Returns the number of gears this Bike has
    * 
    * @return the number of gears
    */
   public int getGears();

   /**
    * Sets the number of gears this Bike has
    * 
    * @param gears the number of gears
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setGears(int gears);

   /**
    * Returns the price of this Bike in pounds Sterling.
    * 
    * @return the price of this Bike
    */
   public int getValue();

   /**
    * Sets the price of this Bike in pounds Sterling
    * 
    * @param price the price of this Bike
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setValue(int value);
}
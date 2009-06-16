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
 * A concrete read/write implementation of the Bike interface
 * which is Serializable to allow it to be passed across a 
 * network interface.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public class BikeObject 
   implements Bike, 
              Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -684225556295655138L;
   
   private int gears;
   private Colour colour;
   private int value;
   
   public BikeObject(int gears, 
                     Bike.Colour colour, 
                     int value)
   {
      this.gears = gears;
      this.colour = colour;
      this.value = value;
   }
   
   public Colour getColour()
   {
      return colour;
   }
   
   public void setColour(Colour colour)   
   {
      this.colour = colour;
   }
   
   public int getGears()
   {
      return gears;
   }
   
   public void setGears(int gears)
   {
      this.gears = gears;
   }
   
   public int getValue()
   {
      return value;
   }
   
   public void setValue(int value)
   {
      this.value = value;
   }
   
   public String toString()
   {
      return "Bike[colour="+getColour()+", gears="+getGears()+", price="+getValue()+"]";
   }
   
   public boolean equals(BikeObject other)
   {
      return (other.getColour() == getColour() &&
              other.getGears() == getGears() &&
              other.getValue() == getValue() );
   }
   
   public int hashCode()
   {
      return getColour().hashCode() * 17 >>>
             getGears() * 17 >>>
             ((Integer) getValue()).hashCode();
   }    
}

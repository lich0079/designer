/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;

/**
 * A business interface defining a Person which a customer is aiming
 * to ensure.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public interface Person
{
   /**
    * Defines the two possible Genders as an enumeration.
    */
   enum Gender {MALE, FEMALE};
   
   /**
    * Returns the age of the Person at their last birthday.
    * 
    * @return an age in years
    */
   public int getAge();
   
   /**
    * Sets the age of the Person at their last birthday.
    * 
    * @param age the age in years
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setAge(int age);
   
   /**
    * Returns an indication of whether the person has passed a 
    * standard fitness test.
    * 
    * @return true if the person has been declared fit after the test
    */
   public boolean isDeclaredFit();
   
   /**
    * Sets the result of a standard fitness test on the Person
    * 
    * @param declaredFit set to true when the person has taken 
    *   and passed a fitness test
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */  
   public void setDeclaredFit(boolean declaredFit);
   
   /**
    * Returns the Gender of the person
    * 
    * @return the Gender of the person
    */
   public Gender getGender();

   /**
    * Sets the Gender of the person
    * 
    * @param gender the Gender of the person
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setGender(Gender gender);
}



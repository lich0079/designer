/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;

import java.io.Serializable;

/**
 * A concrete read/write implementation of the Person interface
 * which is Serializable to allow it to be passed across a 
 * network interface.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public class PersonObject
   implements Person, 
              Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 7233733598264675747L;
   
   private int age;
   private Person.Gender gender;
   private boolean declaredFit;
   
   public PersonObject(int age, Person.Gender gender, boolean declaredFit)
   {
      super();
      this.age = age;
      this.gender = gender;
      this.declaredFit = declaredFit;
   }

   public int getAge()
   {
      return age;
   }
   
   public void setAge(int age)
   {
      this.age = age;
   }
   
   public boolean isDeclaredFit()
   {
      return declaredFit;
   }
   
   public void setDeclaredFit(boolean declaredFit)
   {
      this.declaredFit = declaredFit;
   }
   
   public Person.Gender getGender()
   {
      return gender;
   }
   
   public void setGender(Person.Gender gender)
   {
      this.gender = gender;
   }
   
   public String toString()
   {
      return "Person[age="+getAge()+", gender="+getGender()+", declaredFit="+isDeclaredFit()+"]";
   }
   
   public boolean equals(PersonObject other)
   {
      return (other.getAge() == getAge() &&
              other.getGender() == getGender() &&
              other.isDeclaredFit() == isDeclaredFit() );
   }
   
   public int hashCode()
   {
      return getAge() * 17 >>>
             getGender().hashCode() * (isDeclaredFit() ? 17 : 23);
   }   
}

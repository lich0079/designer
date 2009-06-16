/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;

import java.io.Serializable;

/**
 * A concrete read/write implementation of the Address interface
 * which is Serializable to allow it to be passed across a 
 * network interface.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public class AddressObject 
   implements Address, 
              Serializable
{   
   /** The serialVersionUID */
   private static final long serialVersionUID = 6887287016900670180L;
   
   private String postcodeArea;
   private MosaicCode mosaicCode;
   private DwellingType dwellingType;
   
   public AddressObject(String postcodeArea, MosaicCode mosaicCode, DwellingType dwellingType)
   {
      this.postcodeArea = postcodeArea;
      this.mosaicCode = mosaicCode;
      this.dwellingType = dwellingType;
   }
   
   public DwellingType getDwellingType()
   {
      return dwellingType;
   }
   
   public void setDwellingType(DwellingType dwellingType)
   {
      this.dwellingType = dwellingType;
   }
   
   public MosaicCode getMosaicCode()
   {
      return mosaicCode;
   }
   
   public void setMosaicCode(MosaicCode mosaicCode)
   {
      this.mosaicCode = mosaicCode;
   }
   
   public String getPostcodeArea()
   {
      return postcodeArea;
   }
   
   public void setPostcodeArea(String postcodeArea)
   {
      this.postcodeArea = postcodeArea;
   }
   
   public String toString()
   {
      return "Address[postcodeArea="+postcodeArea+", mosaicCode="+mosaicCode+", dwellingType="+dwellingType+"]";
   }
   
   public boolean equals(AddressObject other)
   {
      return (other.getPostcodeArea() == getPostcodeArea() &&
              other.getMosaicCode() == getMosaicCode() &&
              other.getDwellingType() == getDwellingType() );
   }
   
   public int hashCode()
   {
      return getPostcodeArea().hashCode() * 17 >>>
             getMosaicCode().hashCode() * 17 >>>
             getDwellingType().hashCode();
   }
}

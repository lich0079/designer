/**
 * 
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.domain;

/**
 * A business interface defining an Address for a potential customer
 * requesting a Quote from the insurance engine.
 * 
 * @author <a href="pete.bennett@jboss.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public interface Address
{
   /**
    * An enumeration of the four possible MosaicCodes used in this
    * system.
    */
   public enum MosaicCode {A, B, C, D};
   
   /**
    * An enumeration of the four possible dwelling types used in 
    * this system.
    */
   public enum DwellingType {FLAT, TERRACED, SEMIDETACHED, DETACHED};

   /**
    * Returns the DwellingType for this address.
    * 
    * @return one of the possible DwellingTypes
    */
   public DwellingType getDwellingType();
   
   /**
    * Sets the DwellingType for this address.
    * 
    * @param dwellingType one of the possible DwellingType objects
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */
   public void setDwellingType(DwellingType dwellingType);

   /**
    * Returns the MosaicCode for this address.
    * 
    * @return one of the possible MosaicCode
    */
   public MosaicCode getMosaicCode();

   /**
    * Sets the MosaicCode for this address.
    * 
    * @param mosaicCode one of the possible MosaicCode objects
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */   
   public void setMosaicCode(MosaicCode mosaicCode);
   
   /**
    * Returns the Postcode Area for this address. This is the first
    * one or two letters followed by one or two numbers.
    * 
    * @return the Postcode Area
    */
   public String getPostcodeArea();
   
   /**
    * Sets the UK Postcode Area for this address. This is the first
    * one or two letters followed by one or two numbers.
    * 
    * @param postcodeArea the new Postcode Area
    * @throws UnsupportedOperationException if the implementation of 
    *   the interface is read-only with respect to this property
    */   public void setPostcodeArea(String postcodeArea);
}
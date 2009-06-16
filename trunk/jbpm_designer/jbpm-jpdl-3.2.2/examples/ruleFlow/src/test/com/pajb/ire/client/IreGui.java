/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.pajb.ire.client;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pajb.ire.domain.Address;
import com.pajb.ire.domain.AddressObject;
import com.pajb.ire.domain.Bid;
import com.pajb.ire.domain.Bike;
import com.pajb.ire.domain.BikeObject;
import com.pajb.ire.domain.Person;
import com.pajb.ire.domain.PersonObject;
import com.pajb.ire.domain.Quote;
import com.pajb.ire.engine.Quotable;

public class IreGui
{
   public static String FRAME_TITLE = "Insurance Rules Engine Client";
   
   private Bike[] bikeArray 
      = {
         new BikeObject(24, Bike.Colour.GREEN, 500)
        };
   private Address address 
      = new AddressObject("CM23", Address.MosaicCode.A, Address.DwellingType.DETACHED);
   private Person[] personArray
      = {
           new PersonObject(27, Person.Gender.MALE, false),
           new PersonObject(32, Person.Gender.FEMALE, true)
        };    
   
   public IreGui(final Quotable quotable)
   {
      JFrame mainFrame = new JFrame(FRAME_TITLE);
      mainFrame.setSize(800, 600);
      mainFrame.setLayout(new GridLayout(1+bikeArray.length+personArray.length+1, 1));
      mainFrame.add(new AddressView(address));
      for (Bike bike : bikeArray) 
      {
         mainFrame.add(new BikeView(bike));
      }
      for (Person person : personArray)
      {
         mainFrame.add(new PersonView(person));
      }
      JButton quoteButton = new JButton("Quote!");
      mainFrame.add(quoteButton);
      
      quoteButton.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed(ActionEvent ev)
            {
               Quote quote 
                  = quotable.processApplication(personArray, bikeArray, address);
               System.out.println(quote);
               QuoteDialog.showQuote(quote);
            }
         }
      );
      
      mainFrame.pack();
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainFrame.setVisible(true);
   }
}

class BikeView
   extends JPanel
{
   private JSlider gearSlider= new JSlider(0, 40);
   private JComboBox colourComboBox = new JComboBox(Bike.Colour.values());
   private JSlider valueSlider = new JSlider(0, 5000);
   
   public BikeView(final Bike bike)
   {
      add(new JLabel("BIKE Gears : "));
      gearSlider.setValue(bike.getGears());

      gearSlider.setMajorTickSpacing(5);
      gearSlider.setMinorTickSpacing(1);
      gearSlider.setPaintTicks(true);
      gearSlider.setPaintLabels(true);
      gearSlider.addChangeListener
      (
            new ChangeListener()
            {
               public void stateChanged(ChangeEvent ev)
               {
                  bike.setGears(gearSlider.getValue());
               }
            }
      );
      add(gearSlider);
      add(new JLabel("Colour : "));
      colourComboBox.setSelectedItem(bike.getColour());
      colourComboBox.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed(ActionEvent ev)
            {
               bike.setColour((Bike.Colour) colourComboBox.getSelectedItem());
            }
         }
      );
      add(colourComboBox);
      add(new JLabel("Value : "));
      valueSlider.setValue(bike.getValue());
      valueSlider.setMajorTickSpacing(1000);
      valueSlider.setMinorTickSpacing(100);
      valueSlider.setPaintTicks(true);
      valueSlider.setPaintLabels(true);
      valueSlider.addChangeListener
      (
            new ChangeListener()
            {
               public void stateChanged(ChangeEvent ev)
               {
                  bike.setValue(valueSlider.getValue());
               }
            }
      );
      add(valueSlider);
   }
}

class AddressView
   extends JPanel
{
   private Address address = null;
   private JTextField postCodeTextField = new JTextField(4);
   private JComboBox mosaicCodeComboBox = new JComboBox(Address.MosaicCode.values());;
   private JComboBox dwellingTypeComboBox = new JComboBox(Address.DwellingType.values());;
   
   public AddressView(final Address address)
   {
      add(new JLabel("ADDRESS Post Code Area : "));
      postCodeTextField.setText(address.getPostcodeArea());
      // TODO add change listener for post code
      add(postCodeTextField);
      add(new JLabel("Mosaic Code : "));
      mosaicCodeComboBox.setSelectedItem(address.getMosaicCode());
      mosaicCodeComboBox.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed(ActionEvent ev)
            {
               address.setMosaicCode((Address.MosaicCode) mosaicCodeComboBox.getSelectedItem());
            }
         }
      );
      add(mosaicCodeComboBox);
      add(new JLabel("Dwelling Type : "));
      dwellingTypeComboBox.setSelectedItem(address.getDwellingType());
      dwellingTypeComboBox.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed(ActionEvent ev)
            {
               address.setDwellingType((Address.DwellingType) dwellingTypeComboBox.getSelectedItem());
            }
         }
      );
      add(dwellingTypeComboBox);
   }
}

class PersonView
   extends JPanel
{
   private JSlider ageSlider = new JSlider(0, 100);
   private JComboBox genderComboBox = new JComboBox(Person.Gender.values());
   private JCheckBox declaredFitCheckBox = new JCheckBox();
   
   public PersonView(final Person person)
   {
      add(new JLabel("PERSON Age : "));
      ageSlider.setValue(person.getAge());
      ageSlider.setMajorTickSpacing(10);
      ageSlider.setMinorTickSpacing(1);
      ageSlider.setPaintTicks(true);
      ageSlider.setPaintLabels(true);
      ageSlider.addChangeListener
      (
            new ChangeListener()
            {
               public void stateChanged(ChangeEvent ev)
               {
                  person.setAge(ageSlider.getValue());
               }
            }
      );
      add(ageSlider);
      add(new JLabel("Gender : "));
      genderComboBox.setSelectedItem(person.getGender());
      genderComboBox.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed(ActionEvent ev)
            {
               person.setGender((Person.Gender) genderComboBox.getSelectedItem());
            }
         }
      );
      add(genderComboBox);
      add(new JLabel("Declared Fit : "));
      declaredFitCheckBox.setSelected(person.isDeclaredFit());
      declaredFitCheckBox.addChangeListener
      (
            new ChangeListener()
            {
               public void stateChanged(ChangeEvent ev)
               {
                  person.setDeclaredFit(declaredFitCheckBox.isSelected());
               }               
            }
      );
      add(declaredFitCheckBox);
   }
}

class QuoteDialog
{  
   public static void showQuote(Quote quote)
   {
      StringBuffer message = new StringBuffer("<html>");
      
      if (quote.isDeclined())
      {
         for (String reason : quote.getReasonsForDeclined())
         {
            message.append(reason+"<br>");
         }
         
         JOptionPane.showMessageDialog(null,
               message.toString(),
               "Application Declined",
               JOptionPane.WARNING_MESSAGE);
      }
      else
      {
         for (Bid bid : quote.getBids())
         {
            message.append(bid.getPartnerName()+" bids "+bid.getPremiumPrice()+"<br>");
         }
         
         JOptionPane.showMessageDialog(null,
               message.toString(),
               "Application Accepted",
               JOptionPane.INFORMATION_MESSAGE);         
      }
   }
}
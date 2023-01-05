/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ratemanager;
 
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.soap.SOAPElement;
import com.ota.OTAHotelInvCountNotifRQ;

@XmlRootElement(name="envelope")
public class BookingRateManageSoapEnvelope {
 @XmlAttribute(name="SOAP-ENV:encodingStyle")
 private String soapEnvEncodingStyle;
  
 @XmlElement(name="header")
 public String soapHeader;
  
 @XmlElement(name="soap:Body")
 public OTAHotelInvCountNotifRQ soapBody;
  
 public BookingRateManageSoapEnvelope() {
  soapEnvEncodingStyle = "http://www.w3.org/2001/12/soap-encoding";
  soapHeader = "";
  soapBody = new OTAHotelInvCountNotifRQ();
 }
}
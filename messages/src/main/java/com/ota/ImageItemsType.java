//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.01 at 02:26:44 PM CET 
//


package org.opentravel.ota._2003._05;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Collection of image items.
 * 
 * <p>Java class for ImageItemsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImageItemsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ImageItem" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opentravel.org/OTA/2003/05}ImageDescriptionType">
 *                 &lt;attGroup ref="{http://www.opentravel.org/OTA/2003/05}RemovalGroup"/>
 *                 &lt;attGroup ref="{http://www.opentravel.org/OTA/2003/05}ID_OptionalGroup"/>
 *                 &lt;attGroup ref="{http://www.opentravel.org/OTA/2003/05}DateTimeStampGroup"/>
 *                 &lt;attribute name="Version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageItemsType", propOrder = {
    "imageItem"
})
public class ImageItemsType {

    @XmlElement(name = "ImageItem", required = true)
    protected List<ImageItemsType.ImageItem> imageItem;

    /**
     * Gets the value of the imageItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imageItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImageItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImageItemsType.ImageItem }
     * 
     * 
     */
    public List<ImageItemsType.ImageItem> getImageItem() {
        if (imageItem == null) {
            imageItem = new ArrayList<ImageItemsType.ImageItem>();
        }
        return this.imageItem;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.opentravel.org/OTA/2003/05}ImageDescriptionType">
     *       &lt;attGroup ref="{http://www.opentravel.org/OTA/2003/05}RemovalGroup"/>
     *       &lt;attGroup ref="{http://www.opentravel.org/OTA/2003/05}ID_OptionalGroup"/>
     *       &lt;attGroup ref="{http://www.opentravel.org/OTA/2003/05}DateTimeStampGroup"/>
     *       &lt;attribute name="Version" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ImageItem
        extends ImageDescriptionType
    {

        @XmlAttribute(name = "Version")
        protected String version;
        @XmlAttribute(name = "Removal")
        protected Boolean removal;
        @XmlAttribute(name = "ID")
        protected String id;
        @XmlAttribute(name = "CreateDateTime")
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar createDateTime;
        @XmlAttribute(name = "CreatorID")
        protected String creatorID;
        @XmlAttribute(name = "LastModifyDateTime")
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar lastModifyDateTime;
        @XmlAttribute(name = "LastModifierID")
        protected String lastModifierID;
        @XmlAttribute(name = "PurgeDate")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar purgeDate;

        /**
         * Gets the value of the version property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersion() {
            return version;
        }

        /**
         * Sets the value of the version property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersion(String value) {
            this.version = value;
        }

        /**
         * Gets the value of the removal property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isRemoval() {
            return removal;
        }

        /**
         * Sets the value of the removal property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setRemoval(Boolean value) {
            this.removal = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getID() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setID(String value) {
            this.id = value;
        }

        /**
         * Gets the value of the createDateTime property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getCreateDateTime() {
            return createDateTime;
        }

        /**
         * Sets the value of the createDateTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setCreateDateTime(XMLGregorianCalendar value) {
            this.createDateTime = value;
        }

        /**
         * Gets the value of the creatorID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCreatorID() {
            return creatorID;
        }

        /**
         * Sets the value of the creatorID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCreatorID(String value) {
            this.creatorID = value;
        }

        /**
         * Gets the value of the lastModifyDateTime property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getLastModifyDateTime() {
            return lastModifyDateTime;
        }

        /**
         * Sets the value of the lastModifyDateTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setLastModifyDateTime(XMLGregorianCalendar value) {
            this.lastModifyDateTime = value;
        }

        /**
         * Gets the value of the lastModifierID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLastModifierID() {
            return lastModifierID;
        }

        /**
         * Sets the value of the lastModifierID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLastModifierID(String value) {
            this.lastModifierID = value;
        }

        /**
         * Gets the value of the purgeDate property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getPurgeDate() {
            return purgeDate;
        }

        /**
         * Sets the value of the purgeDate property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setPurgeDate(XMLGregorianCalendar value) {
            this.purgeDate = value;
        }

    }

}

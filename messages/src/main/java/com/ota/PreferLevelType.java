//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.01 at 02:26:44 PM CET 
//


package com.ota;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PreferLevelType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PreferLevelType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="Only"/>
 *     &lt;enumeration value="Unacceptable"/>
 *     &lt;enumeration value="Preferred"/>
 *     &lt;enumeration value="Required"/>
 *     &lt;enumeration value="NoPreference"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PreferLevelType")
@XmlEnum
public enum PreferLevelType {


    /**
     * Preference level that indicates request is only for a specific criterion.
     * 
     */
    @XmlEnumValue("Only")
    ONLY("Only"),

    /**
     * Preference level that indicates request is unnacceptable for a specific criterion.
     * 
     * 
     */
    @XmlEnumValue("Unacceptable")
    UNACCEPTABLE("Unacceptable"),

    /**
     * Preference level that indicates request is preferred for a specific criterion.
     * 
     * 
     */
    @XmlEnumValue("Preferred")
    PREFERRED("Preferred"),

    /**
     * Preference level that indicates request is required for a specific criterion.
     * 
     * 
     */
    @XmlEnumValue("Required")
    REQUIRED("Required"),

    /**
     * Preference level that indicates there is no preference.
     * 
     * 
     */
    @XmlEnumValue("NoPreference")
    NO_PREFERENCE("NoPreference");
    private final String value;

    PreferLevelType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PreferLevelType fromValue(String v) {
        for (PreferLevelType c: PreferLevelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

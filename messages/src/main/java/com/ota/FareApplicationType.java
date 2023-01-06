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
 * <p>Java class for FareApplicationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FareApplicationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OneWay"/>
 *     &lt;enumeration value="Return"/>
 *     &lt;enumeration value="HalfReturn"/>
 *     &lt;enumeration value="Roundtrip"/>
 *     &lt;enumeration value="OneWayOnly"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FareApplicationType")
@XmlEnum
public enum FareApplicationType {

    @XmlEnumValue("OneWay")
    ONE_WAY("OneWay"),
    @XmlEnumValue("Return")
    RETURN("Return"),
    @XmlEnumValue("HalfReturn")
    HALF_RETURN("HalfReturn"),

    /**
     * Specifies that the fare is for a roundtrip.
     * 
     */
    @XmlEnumValue("Roundtrip")
    ROUNDTRIP("Roundtrip"),

    /**
     * The fare can only be treated as one way - can not be doubled to create a roundtrip fare.
     * 
     */
    @XmlEnumValue("OneWayOnly")
    ONE_WAY_ONLY("OneWayOnly");
    private final String value;

    FareApplicationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FareApplicationType fromValue(String v) {
        for (FareApplicationType c: FareApplicationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

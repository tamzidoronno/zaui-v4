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
 * <p>Java class for FareAccessPrefType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FareAccessPrefType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="PointToPoint"/>
 *     &lt;enumeration value="Through"/>
 *     &lt;enumeration value="Joint"/>
 *     &lt;enumeration value="Private"/>
 *     &lt;enumeration value="Negotiated"/>
 *     &lt;enumeration value="Net"/>
 *     &lt;enumeration value="Historical"/>
 *     &lt;enumeration value="SecurateAir"/>
 *     &lt;enumeration value="Moneysaver"/>
 *     &lt;enumeration value="MoneysaverRoundtrip"/>
 *     &lt;enumeration value="MoneysaverNoOneWay"/>
 *     &lt;enumeration value="MoneysaverOneWayOnly"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FareAccessPrefType")
@XmlEnum
public enum FareAccessPrefType {

    @XmlEnumValue("PointToPoint")
    POINT_TO_POINT("PointToPoint"),
    @XmlEnumValue("Through")
    THROUGH("Through"),
    @XmlEnumValue("Joint")
    JOINT("Joint"),
    @XmlEnumValue("Private")
    PRIVATE("Private"),
    @XmlEnumValue("Negotiated")
    NEGOTIATED("Negotiated"),
    @XmlEnumValue("Net")
    NET("Net"),

    /**
     * To request ATPCO historical fare/rule information.
     * 
     */
    @XmlEnumValue("Historical")
    HISTORICAL("Historical"),

    /**
     * To request fares for a specified agreement.
     * 
     */
    @XmlEnumValue("SecurateAir")
    SECURATE_AIR("SecurateAir"),

    /**
     * To request all airline fares for the specified city pair, lowest to highest.
     * 
     */
    @XmlEnumValue("Moneysaver")
    MONEYSAVER("Moneysaver"),

    /**
     * All roundtrip airline fares for the specified city pair including one way fares.
     * 
     */
    @XmlEnumValue("MoneysaverRoundtrip")
    MONEYSAVER_ROUNDTRIP("MoneysaverRoundtrip"),

    /**
     * All airline fares for the specified city pair but no one way fares.
     * 
     */
    @XmlEnumValue("MoneysaverNoOneWay")
    MONEYSAVER_NO_ONE_WAY("MoneysaverNoOneWay"),

    /**
     * Only one-way fares for all airlines for the specified city pair.
     * 
     */
    @XmlEnumValue("MoneysaverOneWayOnly")
    MONEYSAVER_ONE_WAY_ONLY("MoneysaverOneWayOnly");
    private final String value;

    FareAccessPrefType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FareAccessPrefType fromValue(String v) {
        for (FareAccessPrefType c: FareAccessPrefType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

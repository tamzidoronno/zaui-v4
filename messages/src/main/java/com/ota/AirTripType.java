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
 * <p>Java class for AirTripType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AirTripType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OneWay"/>
 *     &lt;enumeration value="OneWayOnly"/>
 *     &lt;enumeration value="Return"/>
 *     &lt;enumeration value="Circle"/>
 *     &lt;enumeration value="OpenJaw"/>
 *     &lt;enumeration value="Other"/>
 *     &lt;enumeration value="Outbound"/>
 *     &lt;enumeration value="OutboundSeasonRoundtrip"/>
 *     &lt;enumeration value="Non-directional"/>
 *     &lt;enumeration value="Inbound"/>
 *     &lt;enumeration value="Roundtrip"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AirTripType")
@XmlEnum
public enum AirTripType {


    /**
     * Identifies a one way trip type.
     * 
     */
    @XmlEnumValue("OneWay")
    ONE_WAY("OneWay"),

    /**
     * Cannot be doubled to create a roundtrip. 
     * 
     */
    @XmlEnumValue("OneWayOnly")
    ONE_WAY_ONLY("OneWayOnly"),

    /**
     * Identifies a return trip type.
     * 
     */
    @XmlEnumValue("Return")
    RETURN("Return"),

    /**
     * Identifies a circle trip type.
     * 
     */
    @XmlEnumValue("Circle")
    CIRCLE("Circle"),

    /**
     * Identifies an open jaw trip type.
     * 
     */
    @XmlEnumValue("OpenJaw")
    OPEN_JAW("OpenJaw"),

    /**
     * Identifies an other trip type.
     * 
     */
    @XmlEnumValue("Other")
    OTHER("Other"),

    /**
     * The direction for the fare is outbound.
     * 
     */
    @XmlEnumValue("Outbound")
    OUTBOUND("Outbound"),

    /**
     * The direction for the fare is outbound seasonal roundtrip.
     * 
     */
    @XmlEnumValue("OutboundSeasonRoundtrip")
    OUTBOUND_SEASON_ROUNDTRIP("OutboundSeasonRoundtrip"),

    /**
     * There is no direction specified for the fare.
     * 
     */
    @XmlEnumValue("Non-directional")
    NON_DIRECTIONAL("Non-directional"),

    /**
     * The direction for the fare is inbound.
     * 
     */
    @XmlEnumValue("Inbound")
    INBOUND("Inbound"),

    /**
     * Identifies travel from one point to another point and return to the original point.  (The outbound fare shall be used also for the inbound fare component for the purpose of determing if the pricing unit is a round trip).
     * 
     */
    @XmlEnumValue("Roundtrip")
    ROUNDTRIP("Roundtrip");
    private final String value;

    AirTripType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AirTripType fromValue(String v) {
        for (AirTripType c: AirTripType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

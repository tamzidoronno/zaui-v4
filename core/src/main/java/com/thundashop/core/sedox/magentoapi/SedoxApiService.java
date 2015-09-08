/**
 * SedoxApiService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.thundashop.core.sedox.magentoapi;

public interface SedoxApiService extends javax.xml.rpc.Service {
    public java.lang.String getSedoxApiPortAddress();

    public com.thundashop.core.sedox.magentoapi.SedoxApiPort getSedoxApiPort() throws javax.xml.rpc.ServiceException;

    public com.thundashop.core.sedox.magentoapi.SedoxApiPort getSedoxApiPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

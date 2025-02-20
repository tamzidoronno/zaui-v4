/**
 * SedoxApiServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.thundashop.core.sedox.magentoapi;

public class SedoxApiServiceLocator extends org.apache.axis.client.Service implements SedoxApiService {

    public SedoxApiServiceLocator() {
    }


    public SedoxApiServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SedoxApiServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SedoxApiPort
    private java.lang.String SedoxApiPort_address = "http://www.tuningfiles.com/sedoxapi/api.php";

    public java.lang.String getSedoxApiPortAddress() {
        return SedoxApiPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SedoxApiPortWSDDServiceName = "SedoxApiPort";

    public java.lang.String getSedoxApiPortWSDDServiceName() {
        return SedoxApiPortWSDDServiceName;
    }

    public void setSedoxApiPortWSDDServiceName(java.lang.String name) {
        SedoxApiPortWSDDServiceName = name;
    }

    public com.thundashop.core.sedox.magentoapi.SedoxApiPort getSedoxApiPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SedoxApiPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSedoxApiPort(endpoint);
    }

    public com.thundashop.core.sedox.magentoapi.SedoxApiPort getSedoxApiPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.thundashop.core.sedox.magentoapi.SedoxApiBindingStub _stub = new com.thundashop.core.sedox.magentoapi.SedoxApiBindingStub(portAddress, this);
            _stub.setPortName(getSedoxApiPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSedoxApiPortEndpointAddress(java.lang.String address) {
        SedoxApiPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.thundashop.core.sedox.magentoapi.SedoxApiPort.class.isAssignableFrom(serviceEndpointInterface)) {
                com.thundashop.core.sedox.magentoapi.SedoxApiBindingStub _stub = new com.thundashop.core.sedox.magentoapi.SedoxApiBindingStub(new java.net.URL(SedoxApiPort_address), this);
                _stub.setPortName(getSedoxApiPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SedoxApiPort".equals(inputPortName)) {
            return getSedoxApiPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.tuningfiles.com/sedoxapi/api.php", "SedoxApiService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.tuningfiles.com/sedoxapi/api.php", "SedoxApiPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SedoxApiPort".equals(portName)) {
            setSedoxApiPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

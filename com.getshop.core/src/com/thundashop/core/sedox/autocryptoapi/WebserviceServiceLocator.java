/**
 * WebserviceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.thundashop.core.sedox.autocryptoapi;

public class WebserviceServiceLocator extends org.apache.axis.client.Service implements com.thundashop.core.sedox.autocryptoapi.WebserviceService {

    public WebserviceServiceLocator() {
    }


    public WebserviceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WebserviceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WebservicePort
    private java.lang.String WebservicePort_address = "http://192.168.21.30:20000/Webservice";

    public java.lang.String getWebservicePortAddress() {
        return WebservicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WebservicePortWSDDServiceName = "WebservicePort";

    public java.lang.String getWebservicePortWSDDServiceName() {
        return WebservicePortWSDDServiceName;
    }

    public void setWebservicePortWSDDServiceName(java.lang.String name) {
        WebservicePortWSDDServiceName = name;
    }

    public com.thundashop.core.sedox.autocryptoapi.Webservice getWebservicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WebservicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWebservicePort(endpoint);
    }

    public com.thundashop.core.sedox.autocryptoapi.Webservice getWebservicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.thundashop.core.sedox.autocryptoapi.WebservicePortBindingStub _stub = new com.thundashop.core.sedox.autocryptoapi.WebservicePortBindingStub(portAddress, this);
            _stub.setPortName(getWebservicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWebservicePortEndpointAddress(java.lang.String address) {
        WebservicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.thundashop.core.sedox.autocryptoapi.Webservice.class.isAssignableFrom(serviceEndpointInterface)) {
                com.thundashop.core.sedox.autocryptoapi.WebservicePortBindingStub _stub = new com.thundashop.core.sedox.autocryptoapi.WebservicePortBindingStub(new java.net.URL(WebservicePort_address), this);
                _stub.setPortName(getWebservicePortWSDDServiceName());
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
        if ("WebservicePort".equals(inputPortName)) {
            return getWebservicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.autocrypto.sedox.com/", "WebserviceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.autocrypto.sedox.com/", "WebservicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WebservicePort".equals(portName)) {
            setWebservicePortEndpointAddress(address);
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

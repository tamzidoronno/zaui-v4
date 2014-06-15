/**
 * SedoxApiPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.thundashop.core.sedox.magentoapi;

public interface SedoxApiPort extends java.rmi.Remote {

    /**
     * Checks if username and password is valid.
     */
    public java.lang.Object checkLogin(java.lang.Object username, java.lang.Object password) throws java.rmi.RemoteException;

    /**
     * Finds the correct phone number
     */
    public java.lang.Object getPhoneNumber(java.lang.Object code, java.lang.Object userid) throws java.rmi.RemoteException;

    /**
     * Finds the userdata
     */
    public java.lang.Object getUserData(java.lang.Object code, java.lang.Object userid) throws java.rmi.RemoteException;

    /**
     * Finds the correct phone number
     */
    public java.lang.Object getOrders(java.lang.Object code) throws java.rmi.RemoteException;

    /**
     * Search for customer
     */
    public java.lang.Object getCustomers(java.lang.Object code, java.lang.Object searchcriteria) throws java.rmi.RemoteException;

}

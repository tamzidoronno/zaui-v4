/**
 * Webservice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.thundashop.core.sedox.autocryptoapi;

public interface Webservice extends java.rmi.Remote {
    public boolean ping() throws java.rmi.RemoteException;
    public com.thundashop.core.sedox.autocryptoapi.FilesMessage encryptFile(com.thundashop.core.sedox.autocryptoapi.FilesMessage encryptedfile) throws java.rmi.RemoteException, com.thundashop.core.sedox.autocryptoapi.Exception;
    public com.thundashop.core.sedox.autocryptoapi.FilesMessage decryptFile(com.thundashop.core.sedox.autocryptoapi.FilesMessage encryptedfile) throws java.rmi.RemoteException, com.thundashop.core.sedox.autocryptoapi.Exception;
}

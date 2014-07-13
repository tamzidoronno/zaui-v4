/**
 * FilesMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.thundashop.core.sedox.autocryptoapi;

public class FilesMessage  implements java.io.Serializable {
    private java.lang.String file_29Bl28Fm58W;

    private java.lang.String file_encryptedFile;

    private java.lang.String file_ifo;

    private java.lang.String file_mpc5Xxflash;

    private java.lang.String file_seriale2Prom;

    public FilesMessage() {
    }

    public FilesMessage(
           java.lang.String file_29Bl28Fm58W,
           java.lang.String file_encryptedFile,
           java.lang.String file_ifo,
           java.lang.String file_mpc5Xxflash,
           java.lang.String file_seriale2Prom) {
           this.file_29Bl28Fm58W = file_29Bl28Fm58W;
           this.file_encryptedFile = file_encryptedFile;
           this.file_ifo = file_ifo;
           this.file_mpc5Xxflash = file_mpc5Xxflash;
           this.file_seriale2Prom = file_seriale2Prom;
    }


    /**
     * Gets the file_29Bl28Fm58W value for this FilesMessage.
     * 
     * @return file_29Bl28Fm58W
     */
    public java.lang.String getFile_29Bl28Fm58W() {
        return file_29Bl28Fm58W;
    }


    /**
     * Sets the file_29Bl28Fm58W value for this FilesMessage.
     * 
     * @param file_29Bl28Fm58W
     */
    public void setFile_29Bl28Fm58W(java.lang.String file_29Bl28Fm58W) {
        this.file_29Bl28Fm58W = file_29Bl28Fm58W;
    }


    /**
     * Gets the file_encryptedFile value for this FilesMessage.
     * 
     * @return file_encryptedFile
     */
    public java.lang.String getFile_encryptedFile() {
        return file_encryptedFile;
    }


    /**
     * Sets the file_encryptedFile value for this FilesMessage.
     * 
     * @param file_encryptedFile
     */
    public void setFile_encryptedFile(java.lang.String file_encryptedFile) {
        this.file_encryptedFile = file_encryptedFile;
    }


    /**
     * Gets the file_ifo value for this FilesMessage.
     * 
     * @return file_ifo
     */
    public java.lang.String getFile_ifo() {
        return file_ifo;
    }


    /**
     * Sets the file_ifo value for this FilesMessage.
     * 
     * @param file_ifo
     */
    public void setFile_ifo(java.lang.String file_ifo) {
        this.file_ifo = file_ifo;
    }


    /**
     * Gets the file_mpc5Xxflash value for this FilesMessage.
     * 
     * @return file_mpc5Xxflash
     */
    public java.lang.String getFile_mpc5Xxflash() {
        return file_mpc5Xxflash;
    }


    /**
     * Sets the file_mpc5Xxflash value for this FilesMessage.
     * 
     * @param file_mpc5Xxflash
     */
    public void setFile_mpc5Xxflash(java.lang.String file_mpc5Xxflash) {
        this.file_mpc5Xxflash = file_mpc5Xxflash;
    }


    /**
     * Gets the file_seriale2Prom value for this FilesMessage.
     * 
     * @return file_seriale2Prom
     */
    public java.lang.String getFile_seriale2Prom() {
        return file_seriale2Prom;
    }


    /**
     * Sets the file_seriale2Prom value for this FilesMessage.
     * 
     * @param file_seriale2Prom
     */
    public void setFile_seriale2Prom(java.lang.String file_seriale2Prom) {
        this.file_seriale2Prom = file_seriale2Prom;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FilesMessage)) return false;
        FilesMessage other = (FilesMessage) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.file_29Bl28Fm58W==null && other.getFile_29Bl28Fm58W()==null) || 
             (this.file_29Bl28Fm58W!=null &&
              this.file_29Bl28Fm58W.equals(other.getFile_29Bl28Fm58W()))) &&
            ((this.file_encryptedFile==null && other.getFile_encryptedFile()==null) || 
             (this.file_encryptedFile!=null &&
              this.file_encryptedFile.equals(other.getFile_encryptedFile()))) &&
            ((this.file_ifo==null && other.getFile_ifo()==null) || 
             (this.file_ifo!=null &&
              this.file_ifo.equals(other.getFile_ifo()))) &&
            ((this.file_mpc5Xxflash==null && other.getFile_mpc5Xxflash()==null) || 
             (this.file_mpc5Xxflash!=null &&
              this.file_mpc5Xxflash.equals(other.getFile_mpc5Xxflash()))) &&
            ((this.file_seriale2Prom==null && other.getFile_seriale2Prom()==null) || 
             (this.file_seriale2Prom!=null &&
              this.file_seriale2Prom.equals(other.getFile_seriale2Prom())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getFile_29Bl28Fm58W() != null) {
            _hashCode += getFile_29Bl28Fm58W().hashCode();
        }
        if (getFile_encryptedFile() != null) {
            _hashCode += getFile_encryptedFile().hashCode();
        }
        if (getFile_ifo() != null) {
            _hashCode += getFile_ifo().hashCode();
        }
        if (getFile_mpc5Xxflash() != null) {
            _hashCode += getFile_mpc5Xxflash().hashCode();
        }
        if (getFile_seriale2Prom() != null) {
            _hashCode += getFile_seriale2Prom().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FilesMessage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.autocrypto.sedox.com/", "filesMessage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file_29Bl28Fm58W");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file_29bl28fm58w"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file_encryptedFile");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file_encryptedFile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file_ifo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file_ifo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file_mpc5Xxflash");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file_mpc5xxflash"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file_seriale2Prom");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file_seriale2prom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

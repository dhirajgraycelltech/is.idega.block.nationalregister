/**
 * XMT0003Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package is.idega.block.nationalregister.webservice.client.skyrr;

public class XMT0003Response  implements java.io.Serializable {
    private is.idega.block.nationalregister.webservice.client.skyrr.Uttak XMT0003Result;

    public XMT0003Response() {
    }

    public XMT0003Response(
           is.idega.block.nationalregister.webservice.client.skyrr.Uttak XMT0003Result) {
           this.XMT0003Result = XMT0003Result;
    }


    /**
     * Gets the XMT0003Result value for this XMT0003Response.
     * 
     * @return XMT0003Result
     */
    public is.idega.block.nationalregister.webservice.client.skyrr.Uttak getXMT0003Result() {
        return XMT0003Result;
    }


    /**
     * Sets the XMT0003Result value for this XMT0003Response.
     * 
     * @param XMT0003Result
     */
    public void setXMT0003Result(is.idega.block.nationalregister.webservice.client.skyrr.Uttak XMT0003Result) {
        this.XMT0003Result = XMT0003Result;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof XMT0003Response)) return false;
        XMT0003Response other = (XMT0003Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.XMT0003Result==null && other.getXMT0003Result()==null) || 
             (this.XMT0003Result!=null &&
              this.XMT0003Result.equals(other.getXMT0003Result())));
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
        if (getXMT0003Result() != null) {
            _hashCode += getXMT0003Result().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(XMT0003Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">XMT0003Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XMT0003Result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "XMT0003Result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "Uttak"));
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

package org.jbpm.util;


public abstract class ByteUtil {

  public static String toString(byte[] bytes) {
    if (bytes==null) return "null";
    if (bytes.length==0) return "[]";
    StringBuffer buf = new StringBuffer();
    buf.append("[");
    for ( int i=0; i<bytes.length; i++ ) {
        String hexStr = Integer.toHexString( bytes[i] - Byte.MIN_VALUE );
        if ( hexStr.length()==1 ) buf.append('0');
        buf.append(hexStr);
    }
    buf.append("]");
    return buf.toString();
  }
}

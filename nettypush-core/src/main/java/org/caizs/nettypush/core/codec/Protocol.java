package org.caizs.nettypush.core.codec;

import java.io.Serializable;
import java.nio.charset.Charset;

public class Protocol implements Serializable {

    public static final String DELIMITER = "$_";

    public static final byte[] DELIMITER_BYTES = DELIMITER.getBytes(Charset.forName("UTF-8"));

    private static final byte VERSION = 1;

    public static final byte TYPE_PING = 1;
    public static final byte TYPE_CUSTOM = 100;

    //消息头，ver+type+length，6个字节数
    public static final int HEAD_BYTE_LENGTH = 1 + 1 + Integer.BYTES;

    byte ver = VERSION;//version
    byte type;//基本类型，如ping
    int length;//payload length
    byte[] payload;//消息体

     public Protocol(byte ver, byte type, int length){
         this.ver = ver;
         this.type = type;
         this.length = length;
     }


    public Protocol(byte type) {
        this.type = type;
    }

    public Protocol() {
    }

    public int getLength() {
        return length;
    }

    public byte getVer() {
        return ver;
    }

    public void setVer(byte ver) {
        this.ver = ver;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}

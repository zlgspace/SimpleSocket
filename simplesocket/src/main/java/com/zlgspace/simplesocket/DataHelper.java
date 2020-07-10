package com.zlgspace.simplesocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DataHelper {

    public static void sendMsg(OutputStream out,String msg) throws IOException {
        sendPacket(out,mkPacket(msg));
    }

    public static void sendMsg(OutputStream out,byte msg[]) throws IOException {
        sendPacket(out,mkPacket(msg));
    }

    public static byte[] rcvPacket(InputStream in) throws IOException {
        byte msgLenBuf[] = new byte[4];
        readMsgToBuf(in,msgLenBuf);
        int msgLen = (int)getUintFromBuf(msgLenBuf,0);
        System.out.println("DataHelper:rcvPacket,msgLen = "+msgLen);
        if(msgLen <= 0){
            return null;
        }

        byte msgBuffer[] = new byte[msgLen];
        readMsgToBuf(in,msgBuffer);
        return msgBuffer;
    }

    public static void sendPacket(OutputStream out,byte packet[]) throws IOException {
        out.write(packet);
    }

    public static byte[] mkPacket(String msg){
        if(msg==null||msg.length()==0)
            return null;
        return mkPacket(msg.getBytes());
    }

    public static byte[] mkPacket(byte[] msg){
        if(msg == null)
            return null;
        int msgLen = msg.length;
        byte[] packet = new byte[4+msgLen];
        byte msgLenBuf[] = uintToByte(msgLen);
        System.arraycopy(msgLenBuf,0,packet,0,4);
        System.arraycopy(msg,0,packet,4,msgLen);
        return packet;
    }

    private static byte[] uintToByte (long data){
        byte[] buf=new byte[4];
        buf[0] = (byte)((data&0xFF000000L)>>24);
        buf[1] = (byte)((data&0x00FF0000L)>>16);
        buf[2] = (byte)((data&0x0000FF00L)>>8);
        buf[3] = (byte)((data&0x000000FFL));
        return buf;
    }

    private static long getUintFromBuf (byte[] buf,int index){

        long reslut = (0x000000FF & ((int)buf[index]))<<24
                | (0x000000FF & ((int)buf[index+1]))<<16
                | (0x000000FF & ((int)buf[index+2]))<<8
                | (0x000000FF & ((int)buf[index+3]));
        return reslut;
    }

    private static byte[] readMsgToBuf(InputStream in,byte buf[]) throws IOException {
        int count = buf.length;
        if(count==0)
            return buf;
        byte tempBuf[] = new byte[count];
        int index = 0;
        while(index<count) {
            int len = in.read(tempBuf);
            if(len==-1){
                return new byte[count];
            }
            System.arraycopy(tempBuf,0,buf,index,len);
            index+=len;
            if(index!=count){
                tempBuf = new byte[count-index];
            }
        }
        return buf;
    }
}

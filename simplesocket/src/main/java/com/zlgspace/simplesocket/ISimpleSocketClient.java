package com.zlgspace.simplesocket;

import java.net.Socket;

interface ISimpleSocketClient {
    boolean isConnected();
    void connect();
    void connect(String svrIp,int svrPort);
    void close();
    void sendMsg(byte data[]);
    Socket getSocket();
}

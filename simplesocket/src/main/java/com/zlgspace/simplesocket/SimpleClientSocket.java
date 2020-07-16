package com.zlgspace.simplesocket;

import com.zlgspace.simplesocket.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import com.zlgspace.simplesocket.SimpleSocket.Callback;

class SimpleClientSocket extends Thread implements ISimpleSocketClient{

    private String svrIp;

    private int svrPort;

    private Socket socket;

    private InputStream in;

    private OutputStream out;

    private Callback callback;

    public SimpleClientSocket(){

    }

    public SimpleClientSocket(Callback callback){
        setCallback(callback);
    }

    public SimpleClientSocket(String svrIp, int svrPort, Callback callback){
        setCallback(callback);
    }

    public SimpleClientSocket(Socket socket){
        this.socket = socket;
    }

    public SimpleClientSocket(Socket socket, Callback callback){
        this.socket = socket;
        setCallback(callback);
    }

    @Override
    public boolean isConnected() {
        if(socket==null)
            return false;
        return !socket.isClosed();
    }

    public void connect(){
        if(this.isAlive())
            return;
        this.start();
    }

    public void connect(String svrIp,int svrPort){
        setSvrIp(svrIp);
        setSvrPort(svrPort);
        connect();
    }

    public void sendMsg(String msg){
        if(msg==null||msg.length()==0)
            return;
        sendMsg(msg.getBytes());
    }

    public void sendMsg(final byte msg[]){
        if(!isConnected())
            return;
        if(out==null)
            return;
        try {
            DataHelper.sendMsg(out,msg);
        } catch (IOException e) {
            if(callback!=null)
                callback.onSocketError(e);
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    public void close(){
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
            this.interrupt();
        }catch (Exception e){
            if(callback!=null)
                callback.onSocketError(e);
        }
    }


    @Override
    public void run() {
        try {
            initSocket();
            checkConnected();
            rcvMsg();
        } catch (Exception e) {
            if(callback!=null)
                callback.onSocketError(e);
        }finally {
            close();
            if(callback!=null)
                callback.onSocketDisConnected();
            if(callback!=null)
                callback.onSocketDestory();
        }
    }

    public String getSvrIp() {
        return svrIp;
    }

    public void setSvrIp(String svrIp) {
        if(!Utils.isIP(svrIp))
            throw new IllegalArgumentException("svrIp incorrect format!");
        this.svrIp = svrIp;
    }

    public int getSvrPort() {
        return svrPort;
    }

    public void setSvrPort(int svrPort) {
        this.svrPort = svrPort;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void initSocket() throws IOException {
        if(socket==null||socket.isClosed()) {
            socket = new Socket(svrIp, svrPort);
        }
        in = socket.getInputStream();
        out = socket.getOutputStream();
        if(callback!=null)
            callback.onSocketInited();
    }

    private void checkConnected() throws IOException, InterruptedException {
        Thread.sleep(300);
        boolean isConnected = socket!=null&&!socket.isClosed()&&socket.isConnected();
        if(callback!=null) {
            if (isConnected) {
                callback.onSocketConnected();
            }
        }
    }

    private void rcvMsg() throws IOException {
        while(!socket.isClosed()){
            byte packet[] = DataHelper.rcvPacket(in);
            if(packet==null||packet.length==0)
                return;
            if(callback!=null)
                callback.onSocketRcvMsg(packet);
        }
    }
}

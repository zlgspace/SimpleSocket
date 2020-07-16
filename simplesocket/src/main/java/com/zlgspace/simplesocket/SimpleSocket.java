package com.zlgspace.simplesocket;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleSocket implements ISimpleSocketClient{

    private String name;

    private SimpleClientSocket socketClient;

    private Callback callback;

    private MsgAnalysisAdapter msgAnalysisAdapter;

    private ExecutorService sendMsgExecutorService;

    private ExecutorService rcvMsgExecutorService;

    public SimpleSocket(){
        this("127.0.0.1",8080);
    }

    public SimpleSocket(Socket socket){
        socketClient = new SimpleClientSocket(socket,privateCallback);
        init();
    }

    public SimpleSocket(String svrIp,int svrPort){
        socketClient = new SimpleClientSocket(svrIp,svrPort,privateCallback);
        init();
    }

    public void setMsgAnalysisAdapter(MsgAnalysisAdapter adapter){
        if(adapter==null)
            return;
        msgAnalysisAdapter = adapter;
    }

    @Override
    public boolean isConnected() {
        return socketClient.isConnected();
    }

    public void connect(){
        socketClient.connect();
    }

    @Override
    public void connect(String svrIp, int svrPort) {
        socketClient.connect(svrIp,svrPort);
    }

    @Override
    public void close() {
        socketClient.close();
    }

    @Override
    public void sendMsg(final byte[] data) {
        if(data==null||data.length==0)
            return;
        sendMsgExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                socketClient.sendMsg(data);
            }
        });
    }

    @Override
    public Socket getSocket() {
        return socketClient.getSocket();
    }

    public void sendMsg(String msg) {
        if(msg==null||msg.length()==0)
            return;
        sendMsg(msg.getBytes());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSvrPort() {
        return socketClient.getSvrPort();
    }

    public void setSvrPort(int svrPort) {
        socketClient.setSvrPort(svrPort);
    }

    public String getSvrIp() {
        return socketClient.getSvrIp();
    }

    public void setSvrIp(String svrIp) {
        socketClient.setSvrIp(svrIp);
    }

    public void setCallback(Callback cbk){
        callback = cbk;
    }

    private void init(){
        sendMsgExecutorService = Executors.newSingleThreadExecutor();
        rcvMsgExecutorService = Executors.newSingleThreadExecutor();
        msgAnalysisAdapter = MsgAnalysisAdapter.Factory.newDefaultMsgAnalysisAdapter();
    }


    private Callback privateCallback = new Callback<byte[]>(){
        @Override
        public void onSocketInited() {
            rcvMsgExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    if(callback!=null)
                        callback.onSocketInited();
                }
            });
        }

        @Override
        public void onSocketConnected() {
            if(callback!=null)
                callback.onSocketConnected();
        }

        @Override
        public void onSocketRcvMsg(final byte[] data) {
            rcvMsgExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    if(callback!=null) {
                        callback.onSocketRcvMsg(msgAnalysisAdapter.analysisMsg(data));
                    }
                }
            });
        }

        @Override
        public void onSocketError(final Exception e) {
            rcvMsgExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    if(callback!=null)
                        callback.onSocketError(e);
                }
            });
        }

        @Override
        public void onSocketDisConnected() {
            if(callback!=null)
                callback.onSocketDisConnected();
        }

        @Override
        public void onSocketDestory() {
            rcvMsgExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    if(callback!=null)
                        callback.onSocketDestory();
                }
            });
        }
    };

    public interface Callback<T> {
        void onSocketInited();
        void onSocketConnected();
        void onSocketRcvMsg(T t);
        void onSocketError(Exception e);
        void onSocketDisConnected();
        void onSocketDestory();
    }

}

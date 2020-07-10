package com.zlgspace.simplesocket.demo;

import android.util.Log;

import com.zlgspace.simplesocket.SimpleServerSocket;
import com.zlgspace.simplesocket.SimpleSocket;

public class SimpleSvrSocketMng {

    public static final int PORT = 8086;
    private static SimpleServerSocket serverSocket;

    public static void start(){
        if(serverSocket == null)
            serverSocket = new SimpleServerSocket(PORT);
        serverSocket.setMsgAnalysisAdapter(new AnalysisAdapter());
        serverSocket.setCallback(callback);
        serverSocket.startServer();
    }

    public static void stop(){
        if(serverSocket != null)
            serverSocket.stopServer();
        serverSocket = null;
    }

    static SimpleServerSocket.Callback<String> callback = new SimpleServerSocket.Callback<String>(){
        @Override
        public void onSvrSocketInited() {
            Log.d("SimpleSvrSocketMng","onSvrSocketInited");
        }

        @Override
        public void onSvrSocketAccpetClient(SimpleSocket client) {
            Log.d("SimpleSvrSocketMng","onSvrSocketAccpetClient");
            client.sendMsg("你好，欢迎访问！");
        }

        @Override
        public void onSvrSocketRcvMsg(SimpleSocket client, String msg) {
            Log.d("SimpleSvrSocketMng","onSvrSocketRcvMsg,接收到来自客户端的消息:"+msg);
        }

        @Override
        public void onSvrSocketError(Exception e) {
            Log.e("SimpleSvrSocketMng","onSvrSocketError",e);
        }

        @Override
        public void onSvrSocketDestory() {
            Log.d("SimpleSvrSocketMng","onSvrSocketDestory");
        }
    };
}

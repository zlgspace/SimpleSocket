package com.zlgspace.simplesocket.demo;

import android.util.Log;

import com.zlgspace.simplesocket.SimpleSocket;

public class SimpleSocketMng  {

    private static SimpleSocket simpleSocket;

    public static void connect(){
        if(simpleSocket==null){
            simpleSocket = new SimpleSocket();
            simpleSocket.setSvrIp("127.0.0.1");
            simpleSocket.setSvrPort(SimpleSvrSocketMng.PORT);
            simpleSocket.setCallback(callback);
            simpleSocket.setMsgAnalysisAdapter(new AnalysisAdapter());
        }
        simpleSocket.connect();
    }

    public static void close(){
        simpleSocket.close();
        simpleSocket = null;
    }

    static SimpleSocket.Callback<String> callback = new SimpleSocket.Callback<String>(){
        @Override
        public void onSocketInited() {
            Log.d("SimpleSocketMng","onSocketInited");
        }

        @Override
        public void onSocketRcvMsg(String s) {
            Log.d("SimpleSocketMng","onSocketRcvMsg,接收到来自服务端的消息："+s);
            simpleSocket.sendMsg("谢谢！");
        }

        @Override
        public void onSocketError(Exception e) {
            Log.e("SimpleSocketMng","onSocketError",e);
        }

        @Override
        public void onSocketDestory() {
            Log.d("SimpleSocketMng","onSocketDestory");
        }
    };
}

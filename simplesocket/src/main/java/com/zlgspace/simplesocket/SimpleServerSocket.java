package com.zlgspace.simplesocket;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SimpleServerSocket extends Thread{

    private Object lock = new Object();

    private int port;

    private boolean isRunning;

    private ServerSocket serverSocket;

    private Callback callback;

    private MsgAnalysisAdapter analysisAdapter;

    private ArrayList<SocketClientHandler> clientList = new ArrayList<SocketClientHandler>();

    public SimpleServerSocket(){
        this(8080);
    }

    public SimpleServerSocket(int port){
        setPort(port);
        analysisAdapter = MsgAnalysisAdapter.Factory.newDefaultMsgAnalysisAdapter();
    }

    public void startServer(){
        isRunning = true;
        start();
    }

    public void stopServer(){
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        interrupt();
        clear();
}

    public boolean isStarted(){
        return isRunning;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setCallback(Callback cbk){
        callback = cbk;
    }

    public void setMsgAnalysisAdapter(MsgAnalysisAdapter adapter){
        analysisAdapter = adapter;
    }

    public ArrayList<SimpleSocket> getClientList(){
        ArrayList<SimpleSocket> cl = new ArrayList<>();
        synchronized (lock){
            int size = clientList.size();
            for(int i = 0;i<size;i++){
                cl.add(clientList.get(i).getSimpleSocket());
            }
        }
        return cl;
    }

    public void sendMsgToAllClient(String msg){
        if(msg==null||msg.length()==0)
            return;
        sendMsgToAllClient(msg.getBytes());
    }

    public void sendMsgToAllClient(byte msg[]){
        synchronized (lock){
            int size = clientList.size();
            for(int i=0;i<size;i++){
                clientList.get(i).sendMsg(msg);
            }
        }
    }

    @Override
    public void run() {
        try {
            initServerSocket();
            accpetClient();
        }catch(Exception e){
            if(callback!=null)
                callback.onSvrSocketError(e);
        }finally {
            if(callback!=null)
                callback.onSvrSocketDestory();
        }
    }

    private void initServerSocket() throws IOException {
        serverSocket = new ServerSocket(getPort());
        if(callback!=null)
            callback.onSvrSocketInited();
    }

    private void accpetClient() throws IOException {
        while (!serverSocket.isClosed()&&isRunning) {
            Socket socket = serverSocket.accept();
            newSocketClientHandler(socket);
        }
    }

    private void addSocketClientHandle(SocketClientHandler handler){
        synchronized (lock) {
            if (clientList.contains(handler))
                return;
            clientList.add(handler);
        }
    }

    private void removeSocketClientHandle(SocketClientHandler handler){
        synchronized (lock) {
            if (!clientList.contains(handler))
                return;
            clientList.remove(handler);
        }
    }

    private void clear(){
        synchronized (lock) {
            clientList.clear();
        }
    }

    private void newSocketClientHandler(Socket socket){
        new SocketClientHandler(socket);
        System.out.println("SimpleServerSocket:newSocketClientHandler,client.size = "+clientList.size());
    }


    public interface Callback<T>{
        void onSvrSocketInited();
        void onSvrSocketAccpetClient(SocketClientHandler handler);
        void onSvrSocketLoseClient(SocketClientHandler handler);
        void onSvrSocketRcvMsg(SocketClientHandler handler,T t);
        void onSvrSocketError(Exception e);
        void onSvrSocketDestory();
    }

    public class SocketClientHandler implements SimpleSocket.Callback<byte[]> {

        private SimpleSocket simpleSocket;

        private SimpleSocket.Callback privateCbk;

        public SocketClientHandler(Socket socket){
            addSocketClientHandle(this);
            simpleSocket = new SimpleSocket(socket);
            simpleSocket.setCallback(this);
            simpleSocket.connect();
        }

        public void sendMsg(String msg){
            simpleSocket.sendMsg(msg);
        }

        public void sendMsg(byte msg[]){
            simpleSocket.sendMsg(msg);
        }

        public SimpleSocket getSimpleSocket(){
            return simpleSocket;
        }

        public void setCallback(SimpleSocket.Callback cbk){
            privateCbk = cbk;
        }

        @Override
        public void onSocketInited() {
            addSocketClientHandle(this);
            if(callback!=null)
                callback.onSvrSocketAccpetClient(this);

            if(privateCbk!=null)
                privateCbk.onSocketInited();
        }

        @Override
        public void onSocketConnected() {
            if(privateCbk!=null)
                privateCbk.onSocketConnected();
        }

        @Override
        public void onSocketRcvMsg(byte msg[]) {
            if(callback!=null)
                callback.onSvrSocketRcvMsg(this,analysisAdapter.analysisMsg(msg));
        }

        @Override
        public void onSocketError(Exception e) {
            if(privateCbk!=null)
                privateCbk.onSocketError(e);
        }

        @Override
        public void onSocketDisConnected() {
            if(privateCbk!=null)
                privateCbk.onSocketDisConnected();

            if(callback!=null)
                callback.onSvrSocketLoseClient(this);
        }

        @Override
        public void onSocketDestory() {
            if(privateCbk!=null)
                privateCbk.onSocketDestory();
            removeSocketClientHandle(this);
        }
    }
}

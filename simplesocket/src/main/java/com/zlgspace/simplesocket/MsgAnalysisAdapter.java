package com.zlgspace.simplesocket;

public abstract class MsgAnalysisAdapter<T> {
    public abstract T analysisMsg(byte data[]);
    final static class Factory {
        private Factory(){}
        public static MsgAnalysisAdapter newDefaultMsgAnalysisAdapter(){
            return new MsgAnalysisAdapter<byte[]>(){

                @Override
                public byte[] analysisMsg(byte[] data) {
                    return data;
                }
            };
        }
    }
}


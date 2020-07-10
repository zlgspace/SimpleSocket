package com.zlgspace.simplesocket.demo;

import com.zlgspace.simplesocket.MsgAnalysisAdapter;

public class AnalysisAdapter extends MsgAnalysisAdapter<String> {
    public String analysisMsg(byte msg[]){
        if(msg==null)
            return "NULL";

        return new String(msg);
    }
}

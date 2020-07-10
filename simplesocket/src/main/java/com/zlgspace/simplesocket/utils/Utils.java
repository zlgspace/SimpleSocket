package com.zlgspace.simplesocket.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean isIP(String str){
        if(str.length()<7 || str.length() >15) return false;
        String[] arr = str.split("\\.");
        if( arr.length != 4 )    return false;
        for(int i = 0 ; i <4 ; i++ ){
            if (!isNUM(arr[i]) || arr[i].length()==0 || Integer.parseInt(arr[i])>255 || Integer.parseInt(arr[i])<0){
                return false;
            }
        }
        return true;
    }

    private static boolean isNUM(String str){
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(str);
        return m.matches();
    }
}

package com.biotag.huangpuhospital.common;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lxh on 2017/7/11.
 */

public class Log {
    public static boolean isLog = true;
    public static void i(String TAG , String msg){
        if(isLog){
            Logger.i(msg);
        }
    }
    public static void i(String TAG , String msg , Throwable throwable){
        if(isLog){

            Logger.i(TAG, msg, throwable);
        }
    }

    public static void v(String TAG , String msg){
        if(isLog){
            Logger.v(msg);
        }
    }
    public static void v(String TAG , String msg , Throwable throwable){
        if(isLog){
            Logger.v(TAG, msg, throwable);
        }
    }

    //=========================================================================================

    public static void d(String TAG , String msg){
        if(isLog){
            Logger.d(msg);
        }
    }
    public static void d(String TAG, ArrayList list){
        if(isLog){
            Logger.d(list);
        }
    }
    public static void d(String TAG, Map map){
        if(isLog){
            Logger.d(map);
        }
    }
    public static void d(String TAG, Set set){
        if(isLog){
            Logger.d(set);
        }
    }
    public static void djson(String TAG, String json){
        if(isLog){
            Logger.json(json);
        }
    }
    public static void dxml(String TAG, String xml){
        Logger.xml(xml);
    }
    public static void d(String TAG , String msg , Throwable throwable){
        if(isLog){
            Logger.d(TAG, msg, throwable);
        }
    }
    //=========================================
    public static void e(String TAG , String msg){
        if(isLog){
            Logger.e(TAG, msg);
        }
    }
    public static void e(String TAG , String msg , Throwable throwable){
        if(isLog){
            Logger.e(TAG, msg, throwable);
        }
    }
}

package com.whut.afinal.core;

import com.whut.afinal.handler.HttpHandlerTask;

/**
 * ============================================================
 * <br/>
 * 作 者 :        xyb
 * <br/>
 * 版 本 :        1.0
 * <br/>
 * 创建日期 ：     2016-05-10 下午2:47
 * <br/>
 * 描 述 ：        FinalHttp负责和http相关
 * <br/>
 * 修订历史 ：
 * <br/>
 * ============================================================
 **/
public class FinalHttp {

    private static FinalHttp mFinalHttp;

    /**
     * FinalHttp的实例获取的方法
     * @return mFinalHttp
     */
    public static FinalHttp create(){
        if(mFinalHttp == null ){
            mFinalHttp = new FinalHttp();
        }
        return mFinalHttp;
    }

    public  void post(){
        sendRequest();
    }

    private void sendRequest() {
        HttpHandlerTask httpHandlerTask = new HttpHandlerTask();

    }
}

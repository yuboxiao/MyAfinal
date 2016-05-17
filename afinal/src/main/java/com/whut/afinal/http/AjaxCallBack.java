package com.whut.afinal.http;

/**
 * ============================================================
 * <br/>
 * 作 者 :        xyb
 * <br/>
 * 版 本 :        1.0
 * <br/>
 * 创建日期 ：     2016-05-17 上午11:53
 * <br/>
 * 描 述 ：       http请求的回调接口，将服务器返回结果回调到主线程
 * <br/>
 * 修订历史 ：
 * <br/>
 * ============================================================
 **/
public abstract class AjaxCallBack<T> {

    /**
     * 请求成功
     * @param t
     */
    public void onSuccess(T t) {};

    /**
     * 请求失败
     * @param t 异常信息
     * @param errorNo   错误码
     * @param strMsg    错误信息
     */
    public void onFailure(Throwable t, int errorNo, String strMsg) {};

}

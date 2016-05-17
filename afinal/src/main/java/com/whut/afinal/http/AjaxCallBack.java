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


    private boolean progress = true;
    private int rate = 1000 * 1;//每秒


    public boolean isProgress() {
        return progress;
    }

    public int getRate() {
        return rate;
    }


    /**
     * 设置进度,而且只有设置了这个了以后，onLoading才能有效。
     * @param progress 是否启用进度显示
     * @param rate 进度更新频率
     */
    public AjaxCallBack<T> progress(boolean progress , int rate) {
        this.progress = progress;
        this.rate = rate;
        return this;
    }

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


    public void onStart(){};
    /**
     * onLoading方法有效progress
     * @param count
     * @param current
     */
    public void onLoading(long count,long current){};

}

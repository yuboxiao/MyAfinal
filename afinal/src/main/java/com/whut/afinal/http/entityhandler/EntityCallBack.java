package com.whut.afinal.http.entityhandler;

/**
 * ============================================================
 * <br/>
 * 作 者 :        xyb
 * <br/>
 * 版 本 :        1.0
 * <br/>
 * 创建日期 ：     2016-05-17 下午5:53
 * <br/>
 * 描 述 ：
 * <br/>
 * 修订历史 ：
 * <br/>
 * ============================================================
 **/
public interface EntityCallBack {

    public void callBack(long count,long current,boolean mustNoticeUI);

}

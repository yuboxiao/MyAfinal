package com.whut.afinal.http;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * * ============================================================
 * <br/>
 * 作 者 :        xyb
 * <br/>
 * 版 本 :        1.0
 * <br/>
 * 创建日期 ：     2016-05-17 上午11:58
 * <br/>
 * 描 述 ：        用来传参数的
 * <br/>
 * 修订历史 ：
 * <br/>
 * ============================================================
 */
public class AjaxParams {

    private static String ENCODING = "UTF-8";

    private ConcurrentHashMap <String,String> urlParams;


    public AjaxParams() {
        init();
    }

    private void init() {
        urlParams = new ConcurrentHashMap<>();
    }

    /****
     * 向urlParams中添加一条数据
     * @param key
     * @param value
     */
    public void put(String key, String value){
        if(key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    /**
     * 向urlParams中移除一条数据
     * @param key 关键词
     */
    public void remove(String key){
        urlParams.remove(key);
    }

    /**
     * 获取 编码为ENCODING类型的字符串
     * @return 编码为ENCODING类型的字符串
     */
    public String getParamString() {
        return URLEncodedUtils.format(getParamsList(), ENCODING);
    }

    /**
     * 获取参数列表
     * @return  参数列表
     */
    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<>();
        for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return lparams;
    }


}

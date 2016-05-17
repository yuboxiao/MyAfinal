package com.whut.afinal.core;

import com.whut.afinal.http.AjaxCallBack;
import com.whut.afinal.http.AjaxParams;
import com.whut.afinal.http.HttpHandlerTask;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

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

    private DefaultHttpClient mHttpClient ;

    private HttpContext mHttpContext;

    private String mCharset = "urf-8";


    private static FinalHttp mFinalHttp;

    //默认构造方法
    private FinalHttp (){
        mHttpClient = new DefaultHttpClient();
        mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());
    }

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

    /**
     * get 方法
     * @param url 请求路径
     * @param params 请求参数
     * @param callBack  限制泛型为object的回调接口
     */
    public  void get(String url,AjaxParams params,AjaxCallBack<? extends Object> callBack){
        HttpGet httpGet = new HttpGet(getUrlWithQueryString(url,params));

        httpGet.setHeader("JsonStub-User-Key", "0a0d2a98-1798-4349-9f3a-c2a6dc5b117c");
        httpGet.setHeader("JsonStub-Project-Key", "6646e5dc-539a-4676-8028-bb5544a1e9b5");
        httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");

        sendRequest(mHttpClient, mHttpContext, httpGet, null, callBack);
    }

    /**
     * 泛型方法,发送请求
     * @param client
     * @param httpContext
     * @param uriRequest
     * @param contentType
     * @param ajaxCallBack
     * @param <T>
     */
    private <T> void sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, AjaxCallBack<T> ajaxCallBack) {
        if(contentType != null){
            uriRequest.addHeader("Content-Type",contentType);
        }

        HttpHandlerTask httpHandlerTask = new HttpHandlerTask(client,httpContext,ajaxCallBack,mCharset);
        //使用线程池去执行
        httpHandlerTask.execute(uriRequest);
        //httpHandlerTask.executeOnExecutor()
    }

    /**
     * 给baseUrl加上查询参数
     * @param url 原来的url
     * @param params   查询参数
     * @return  示例  http://jsonstub.com/usr/login?username=123&password=123
     */
    public static String getUrlWithQueryString(String url, AjaxParams params) {
        if(params != null) {
            String paramString = params.getParamString();
            url += "?" + paramString;
        }
        return url;
    }
}

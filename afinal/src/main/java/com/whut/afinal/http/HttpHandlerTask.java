package com.whut.afinal.http;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.whut.afinal.http.entityhandler.EntityCallBack;
import com.whut.afinal.http.entityhandler.StringEntityHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * ============================================================
 * <br/>
 * 作 者 :        xyb
 * <br/>
 * 版 本 :        1.0
 * <br/>
 * 创建日期 ：     2016-05-10 下午2:53
 * <br/>
 * 描 述 ：
 * <br/>
 * 修订历史 ：
 * <br/>
 * ============================================================
 **/
public class HttpHandlerTask<T> extends AsyncTask<Object, Object, Object> implements EntityCallBack{

    private AbstractHttpClient mClient;
    private HttpContext mContext;
    private AjaxCallBack<T> mCallBack;
    private String mCharset;

    private StringEntityHandler mStrEntityHandler = new StringEntityHandler();

    public HttpHandlerTask(AbstractHttpClient client, HttpContext context, AjaxCallBack<T> callBack, String charset) {
        mClient = client;
        mContext = context;
        mCallBack = callBack;
        mCharset = charset;
    }


    @Override
    protected Object doInBackground(Object[] params) {

        HttpUriRequest request = (HttpUriRequest) params[0];

        try {
            publishProgress(UPDATE_START);
            makeRequestWithRetry(request);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 访问服务器带上重试机制
     * @param request
     */
    private void makeRequestWithRetry(HttpUriRequest request) {
        HttpRequestRetryHandler requestRetryHandler = mClient.getHttpRequestRetryHandler();

        int retryCount = 0;
        boolean retry = true;
        IOException cause = null;
        while (retry){

            try {
                HttpResponse response = mClient.execute(request, mContext);
                Header[] headers = response.getAllHeaders();
                for (Header header:headers){
                    System.out.println(header.getName() + " -------- " + header.getValue());
                }
                handleResponse(response);
                return;
            }catch (Exception e){
                cause = new IOException("Exception error",e);
                retry = requestRetryHandler.retryRequest(cause,++retryCount,mContext);
            }

        }
    }

    /**
     * 处理服务器的返回信息
     * @param response
     */
    private void handleResponse(HttpResponse response) {
        Object responseBody = null;
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if(statusCode > 300 ){
            publishProgress(UPDATE_FAILURE,new HttpResponseException(statusCode,"服务器出错!!!"));
        }else{
            try {
                HttpEntity entity = response.getEntity();
                responseBody = mStrEntityHandler.handleEntity(entity, mCharset, this);
                publishProgress(UPDATE_SUCCESS,responseBody);
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(UPDATE_FAILURE, e, 0, e.getMessage());
            }
        }
    }

    private final static int UPDATE_START = 1;
    private final static int UPDATE_LOADING = 2;
    private final static int UPDATE_FAILURE = 3;
    private final static int UPDATE_SUCCESS = 4;

    private long time;
    @Override
    public void callBack(long count, long current, boolean mustNoticeUI) {
        if(mCallBack!=null && mCallBack.isProgress()){
            if(mustNoticeUI){
                publishProgress(UPDATE_LOADING,count,current);
            }else{
                long thisTime = SystemClock.uptimeMillis();
                if(thisTime - time >= mCallBack.getRate()){
                    time = thisTime ;
                    publishProgress(UPDATE_LOADING,count,current);
                }
            }
        }
    }


    @Override
    protected void onProgressUpdate(Object... values) {
        int update = Integer.valueOf(String.valueOf(values[0]));
        switch (update) {
            case UPDATE_START:
                if(mCallBack!=null)
                    mCallBack.onStart();
                break;
            case UPDATE_LOADING:
                if(mCallBack!=null)
                    mCallBack.onLoading(Long.valueOf(String.valueOf(values[1])),Long.valueOf(String.valueOf(values[2])));
                break;
            case UPDATE_FAILURE:
                if(mCallBack!=null)
                    mCallBack.onFailure((Throwable)values[1],(Integer)values[2],(String)values[3]);
                break;
            case UPDATE_SUCCESS:
                if(mCallBack!=null)
                    mCallBack.onSuccess((T)values[1]);
                break;
            default:
                break;
        }
        super.onProgressUpdate(values);
    }
}

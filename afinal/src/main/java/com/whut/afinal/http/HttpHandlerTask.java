package com.whut.afinal.http;

import android.os.AsyncTask;

import com.whut.afinal.http.entityhandler.EntityCallBack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        String result ;
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if(statusCode > 300 ){
            publishProgress(UPDATE_FAILURE,new HttpResponseException(statusCode,"服务器出错!!!"));
        }else{
            try {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                int len ;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 4];
                while ((len=inputStream.read(buffer))!=-1){
                    baos.write(buffer,0,len);
                }
                byte[] data = baos.toByteArray();
                result = new String(data,"utf8");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final static int UPDATE_START = 1;
    private final static int UPDATE_LOADING = 2;
    private final static int UPDATE_FAILURE = 3;
    private final static int UPDATE_SUCCESS = 4;

    @Override
    public void callBack(long count, long current, boolean mustNoticeUI) {

    }
}

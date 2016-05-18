package com.whut.afinal.core;

import com.whut.afinal.http.AjaxCallBack;
import com.whut.afinal.http.AjaxParams;
import com.whut.afinal.http.HttpHandlerTask;
import com.whut.afinal.http.RetryHandler;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

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

    private String mCharset = "utf-8";


    private static FinalHttp mFinalHttp;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024; //8KB
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static int maxConnections = 10; //http请求最大并发连接数
    private static int socketTimeout = 10 * 1000; //超时时间，默认10秒
    private static int maxRetries = 5;//错误尝试次数，错误异常表请在RetryHandler添加
    private static int httpThreadCount = 3;//http线程池数量


    private final Map<String, String> clientHeaderMap;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            Thread tread = new Thread(r, "FinalHttp #" + mCount.getAndIncrement());
            tread.setPriority(Thread.NORM_PRIORITY - 1);
            return tread;
        }
    };

    private static final Executor executor = Executors.newFixedThreadPool(httpThreadCount, sThreadFactory);

    //默认构造方法
    private FinalHttp (){
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, socketTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, 10);

        HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, true);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());
        mHttpClient = new DefaultHttpClient(cm, httpParams);

        // 设置相关的压缩文件标识，在请求头的信息中
        mHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {

            @Override
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
                for (String header : clientHeaderMap.keySet()) {
                    request.addHeader(header, clientHeaderMap.get(header));
                }
            }
        });

        // 设置相应的拦截器，用于处理接收到的拦截的压缩信息
        mHttpClient.addResponseInterceptor(new HttpResponseInterceptor() {

            @Override
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        mHttpClient.setHttpRequestRetryHandler(new RetryHandler(maxRetries));

        // httpclient的请求头信息
        clientHeaderMap = new HashMap<>();

    }
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
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

        httpGet.setHeader("Transfer-Encoding", "identity");
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
        //httpHandlerTask.execute(uriRequest);
        httpHandlerTask.executeOnExecutor(executor,uriRequest);
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

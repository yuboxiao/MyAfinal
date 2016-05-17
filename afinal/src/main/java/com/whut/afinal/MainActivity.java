package com.whut.afinal;

import android.app.Activity;
import android.os.Bundle;

import com.whut.afinal.core.FinalHttp;
import com.whut.afinal.http.AjaxCallBack;
import com.whut.afinal.http.AjaxParams;


/**
 * 测试用例<br/>
 * http://jsonstub.com/usr/login?password=123&username=123
 * <br/>
 * mRequestParams.setHeader("JsonStub-User-Key", "0a0d2a98-1798-4349-9f3a-c2a6dc5b117c");
 * <br/>
 * mRequestParams.setHeader("JsonStub-Project-Key", "6646e5dc-539a-4676-8028-bb5544a1e9b5");
 * <br/>
 * mRequestParams.setHeader("Content-Type", "application/json;charset=UTF-8");
 * <br/>
 */
public class MainActivity extends Activity {

    private static FinalHttp mFinalHttp;

    private static final String URL = "http://jsonstub.com/usr/login";

    static {
        mFinalHttp = FinalHttp.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AjaxParams params = new AjaxParams();
        params.put("password", "123");
        params.put("username", "123");

        mFinalHttp.get(URL, params, new AjaxCallBack<String>() {

            @Override
            public void onStart() {
                System.out.println("------- onStart ------");
                super.onStart();
            }

            @Override
            public void onLoading(long count, long current) {
                System.out.println("------- onLoading ------ 当前/总共 :" + current +"/" + count);
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("s ---> " + s);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                System.out.println("strMsg ---> " + strMsg);
                System.out.println("errorNo ---> " + errorNo);
            }

        });
    }
}

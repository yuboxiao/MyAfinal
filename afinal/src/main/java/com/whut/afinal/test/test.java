package com.whut.afinal.test;

import android.test.AndroidTestCase;

import com.whut.afinal.http.AjaxParams;

/**
 * ============================================================
 * <br/>
 * 作 者 :        xyb
 * <br/>
 * 版 本 :        1.0
 * <br/>
 * 创建日期 ：     2016-05-17 下午3:46
 * <br/>
 * 描 述 ：       单元测试
 * <br/>
 * 修订历史 ：
 * <br/>
 * ============================================================
 **/
public class test extends AndroidTestCase{


     String mUrl = "http://jsonstub.com/usr/login";

    public void test (){
        System.out.println("-----------------");

        AjaxParams params = new AjaxParams();
        params.put("password","123");
        params.put("username", "123");
        if(params != null) {

            String paramString = params.getParamString();
            System.out.println(paramString);
            mUrl += "?" + paramString;
        }
        System.out.println(mUrl);
        System.out.println("-----------------");
    }

}

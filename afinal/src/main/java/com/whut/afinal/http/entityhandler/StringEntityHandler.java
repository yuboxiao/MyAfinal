package com.whut.afinal.http.entityhandler;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

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
 * 创建日期 ：     2016-05-17 下午8:18
 * <br/>
 * 描 述 ：
 * <br/>
 * 修订历史 ：
 * <br/>
 * ============================================================
 **/
public class StringEntityHandler {

    /**
     * 处理字符串类型的entity
     * @param entity 输入流中获取的实体对象
     * @param charset 编码格式
     * @param callBack
     * @return
     * @throws IOException
     */
    public Object handleEntity(HttpEntity entity,String charset,EntityCallBack callBack) throws IOException {
        InputStream inputStream = entity.getContent();

        int len ;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        long count = entity.getContentLength();
        System.out.println(
                +"--------");
        long curCount = 0;
        while ((len=inputStream.read(buffer))!=-1){
            baos.write(buffer, 0, len);
            curCount += len;
            callBack.callBack(count,curCount,false);
        }
        callBack.callBack(count, curCount,true);
        byte[] data = baos.toByteArray();

        return new String(data,charset);
    }

}

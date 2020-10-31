package xyz.seiyaya.jenkins.plugin.helper;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author wangjia
 * @version 1.0
 * @date 2020/10/30 10:49
 */
public class HttpHelper {

    public static String sendPostJson(String url,String text){
        String resultJson = "";
        CloseableHttpClient client = HttpClients.custom().build();
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);

            httpPost.addHeader("Content-type","application/json");
            httpPost.setEntity(new StringEntity(text,"utf-8"));
            response = client.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                resultJson = EntityUtils.toString(response.getEntity(),"utf-8");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            release(response);
        }
        return resultJson;
    }

    /**
     * 释放连接
     * @param response
     */
    private static void release(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }
}

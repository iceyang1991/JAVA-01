package com.pikachu.okhttp;

import okhttp3.*;

import java.io.IOException;

/**
 * @author yang.zhou02@hand-china.com
 * @create 2021-01-21 下午9:33
 */
public class OkHttp {
    public static void main(String[] args)  {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("http://localhost:8801")
                .build();
        Call call = client.newCall(request);


        //同步调用,返回Response,会抛出IO异常
        /*try {
            Response response = call.execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //异步调用,并设置回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                System.out.println(res);
            }
        });
    }
}
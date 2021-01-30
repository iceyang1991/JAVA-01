package com.pikachu.gateway.outbound.okhttp;

import com.pikachu.gateway.filter.HeaderHttpRequestFilter;
import com.pikachu.gateway.filter.HeaderHttpResponseFilter;
import com.pikachu.gateway.filter.HttpRequestFilter;
import com.pikachu.gateway.filter.HttpResponseFilter;
import com.pikachu.gateway.router.HttpEndpointRouter;
import com.pikachu.gateway.router.WeightRoundRobinHttpRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import okhttp3.*;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkhttpOutboundHandler {
    private List<String> backendUrls;
    private OkHttpClient okHttpClient;
    HttpEndpointRouter router=  new WeightRoundRobinHttpRouter();
    HttpRequestFilter filter = new HeaderHttpRequestFilter();
    HttpResponseFilter responseFilter = new HeaderHttpResponseFilter();
    public OkhttpOutboundHandler(List<String> backendUrls){
        this.backendUrls = backendUrls;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }
    public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        String backendUrl = router.route(this.backendUrls);
        backendUrl = backendUrl.endsWith("/")?backendUrl.substring(0,backendUrl.length()-1):backendUrl;
        String url = backendUrl + fullRequest.uri();
        System.out.println("当前请求的服务地址："+url+"------"+System.currentTimeMillis());
        System.out.println("当前请求的headers中属性mao的值："+fullRequest.headers().get("pikachu")+"------"+System.currentTimeMillis());
        filter.filter(fullRequest,ctx);
        System.out.println("过滤器在请求的headers中增加属性mao的值："+fullRequest.headers().get("pikachu")+"------"+System.currentTimeMillis());
        fetchGet(fullRequest, ctx, url);
    }
    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {

        Request request = new Request.Builder().url(url).get().
                addHeader("pikachu",inbound.headers().get("pikachu")).
                addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE).
                build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    handleResponse(inbound, ctx, response);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        });

    }
    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final Response endpointResponse) {
        FullHttpResponse response = null;
        try {
            ResponseBody responseBody = endpointResponse.body();
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(responseBody.bytes()));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.header("Content-Length")));
            responseFilter.filter(response);

        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }
    }
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

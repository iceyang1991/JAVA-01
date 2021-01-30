package com.pikachu.gateway.outbound.netty4;

import com.pikachu.gateway.filter.HeaderHttpRequestFilter;
import com.pikachu.gateway.filter.HttpRequestFilter;
import com.pikachu.gateway.router.HttpEndpointRouter;

import com.pikachu.gateway.router.WeightRoundRobinHttpRouter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URISyntaxException;
import java.util.List;


public class NettyOutboundHandler {
    private List<String> backendUrls;
    private NettyHttpClient nettyHttpClient;
    HttpEndpointRouter router = new WeightRoundRobinHttpRouter();
    HttpRequestFilter filter = new HeaderHttpRequestFilter();

    public NettyOutboundHandler(List<String> backendUrls) {
        this.backendUrls = backendUrls;
        nettyHttpClient = new NettyHttpClient();
    }

    public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx) throws InterruptedException, URISyntaxException {
        String backendUrl = router.route(this.backendUrls);
        backendUrl = backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;
        System.out.println("当前请求的服务地址：" + backendUrl + "------" + System.currentTimeMillis());
        System.out.println("当前请求的headers中属性pikachu的值：" + fullRequest.headers().get("pikachu") + "------" + System.currentTimeMillis());
        filter.filter(fullRequest, ctx);
        System.out.println("过滤器在请求的headers中增加属性pikachu的值：" + fullRequest.headers().get("pikachu") + "------" + System.currentTimeMillis());
        nettyHttpClient.connect(fullRequest, ctx, backendUrl);
    }
}

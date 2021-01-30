package com.pikachu.gateway.outbound.netty4;

import com.pikachu.gateway.filter.HeaderHttpResponseFilter;
import com.pikachu.gateway.filter.HttpResponseFilter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author yang.zhou02@hand-china.com
 * @create 2021-01-30 下午11:34
 */

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    HttpResponseFilter responseFilter = new HeaderHttpResponseFilter();
    private ChannelHandlerContext channelHandlerContext;

    public NettyClientHandler(ChannelHandlerContext ctx) {
        this.channelHandlerContext = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.err.println("client 读取数据开始");
            FullHttpResponse response = (FullHttpResponse) msg;
            responseFilter.filter(response);
            this.channelHandlerContext.write(response);
            this.channelHandlerContext.flush();
            handlerTest(this.channelHandlerContext,response.content());
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //ReferenceCountUtil.release(msg);
        }
    }
    private void handlerTest(ChannelHandlerContext ctx, ByteBuf value) {
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, value);
            response.headers().set("Content-Type", "text/html");
            response.headers().setInt("Content-Length", response.content().readableBytes());

        } catch (Exception e) {
            System.out.println("处理出错:"+e.getMessage());
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {

        }
    }

    // 数据读取完毕的处理
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.err.println("client 读取数据完毕");
    }

    // 出现异常的处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("client 读取数据出现异常");
        ctx.close();
    }

}


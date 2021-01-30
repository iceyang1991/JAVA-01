
package com.pikachu.gateway.outbound.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class NettyHttpClient {

    public void connect(FullHttpRequest fullRequest, ChannelHandlerContext ctx, String url) throws URISyntaxException, InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            URI uri = new java.net.URI(url);
            String host = uri.getHost();
            int port = uri.getPort();
            InetSocketAddress isa = new java.net.InetSocketAddress(host, port);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.remoteAddress(isa);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new HttpClientCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(1024 * 10 * 1024));
                    ch.pipeline().addLast(new HttpContentDecompressor());
                    ch.pipeline().addLast(new NettyClientHandler(ctx));
                }
            });
            ChannelFuture cf = bootstrap.connect().sync();
            cf.channel().writeAndFlush(fullRequest);
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully().sync();
        }
    }
}

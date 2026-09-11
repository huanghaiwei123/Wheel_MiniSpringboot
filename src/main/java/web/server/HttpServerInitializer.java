package web.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import web.mvc.Dispatcher;

/**
 * 用于配置pipeLine的处理链
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private final Dispatcher dispatcher;

    public HttpServerInitializer(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
//        获取该连接的处理管道
        ChannelPipeline pipeline = socketChannel.pipeline();
//        http编解码，将数据从字节流和http对象之间进行转换，数据在网络传输用的是字节流这种二进制数据
        pipeline.addLast(new HttpServerCodec());
//        http消息聚合器,最大消息体大小：512KB
        pipeline.addLast("httpAggregator", new HttpObjectAggregator(512*1024));  //这里把多个片段聚合成一个完整的httpfullrequest
//        自定义业务处理器
        pipeline.addLast(new HttpRequestHandler(dispatcher));  //处理完整的httpfullrequest，包含请请求行+请求头+请求体
    }
}

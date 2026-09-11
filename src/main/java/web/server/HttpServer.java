package web.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import web.mvc.Dispatcher;

import java.net.InetSocketAddress;

public class HttpServer {
    /**
     * http请求端口
     */
    int port;
    private final Dispatcher dispatcher;

    public HttpServer(int port, Dispatcher dispatcher) {
        this.port = port;
        this.dispatcher = dispatcher;
    }
    /**

     * 服务的启动方法
     * @throws Exception
     */
    public void start() throws Exception {
//        老板线程组，负责端口监听，等待客户端连接，连接成功转交任务给工人线程组，只需一个线程
        NioEventLoopGroup boss = new NioEventLoopGroup();
//        工人线程组，负责处理已建立连接的I/O读写操作
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
//        Netty服务启动引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)    //设置线程组
                    .handler(new LoggingHandler(LogLevel.DEBUG))   //设置服务端日志处理器
                    .channel(NioServerSocketChannel.class)   //设置通道类型，NioServerSocketChannel是服务端监听通道，作用是绑定端口，接受连接，整个服务只有一个
                    .childHandler(new HttpServerInitializer(dispatcher))  //设置子通道处理器
            ;

            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(port)).sync(); //线程阻塞直到绑定成功
            System.out.println(" server start up on port : " + port);

//        注册关闭钩子:JVM 收到停止信号(IDEA 点停止)时,优雅关闭线程组 → 进程才能退出、端口才能释放
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }));

            future.channel().closeFuture().sync();  //线程阻塞直到通道关闭
        } finally {
//        无论正常/异常退出,都释放线程组,避免进程卡死、端口一直被占
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

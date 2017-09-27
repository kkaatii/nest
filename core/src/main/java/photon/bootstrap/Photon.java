package photon.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Entry point of the program.
 */
public class Photon {

    private final Injector injector;

    public static void main(String[] args) throws Exception {
        int port = args.length == 1 ? Integer.parseInt(args[0]) : 8080;
        new Photon(
                new PhotonModule(),
                new MybatisModule()
        ).startServer(port);
        System.out.println("Photon server started running on port " + port);
    }

    public Photon(Module... modules) {
        injector = Guice.createInjector(modules);
    }

    public void startServer(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer(injector))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = bootstrap.bind(port).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

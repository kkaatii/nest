package photon.bootstrap;

import com.google.inject.Injector;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import photon.query.QueryService;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final Injector injector;

    ServerInitializer(Injector injector) {
        this.injector = injector;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("delimiter", new DelimiterBasedFrameDecoder(8192, Delimiters.nulDelimiter()));
        p.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        p.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        p.addLast(new QueryHandler(injector.getInstance(QueryService.class)));
    }
}

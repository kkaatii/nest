package photon.bootstrap;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import photon.model.Owner;
import photon.Callback;
import photon.query.QueryResult;
import photon.query.QueryService;

import java.util.function.Consumer;

public class QueryHandler extends SimpleChannelInboundHandler<String> {
    private final QueryService service;

    public QueryHandler(QueryService service) {
        this.service = service;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Consumer<String> toWriteAndFlush = s -> {
            try {
                ctx.writeAndFlush(s).addListener(ChannelFutureListener.CLOSE);
            } catch (Exception e) {
                ctx.close();
            }
        };
        Callback<QueryResult> callback = new Callback<QueryResult>() {
            @Override
            public void onSuccess(QueryResult outcome) {
                System.out.println(outcome.getSegment());
                toWriteAndFlush.accept(outcome.toString());
            }

            @Override
            public void onException(Exception e) {
                toWriteAndFlush.accept(e.getMessage());
            }
        };

        try {
            // Incoming String shall contain 3 parts, colon as the delimiter: the ID, the name of the Owner, and the
            // query json
            String[] req = msg.split(";");
            Owner owner = new Owner(Integer.parseInt(req[0]), req[1]);
            service.executeQuery(owner, req[2], callback);
        } catch (Exception e) {
            callback.onException(e);
        }
    }
}

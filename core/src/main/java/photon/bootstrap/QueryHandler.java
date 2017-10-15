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

    QueryHandler(QueryService service) {
        this.service = service;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Callback<QueryResult> callback = new Callback<QueryResult>() {
            @Override
            public void onSuccess(QueryResult outcome) {
                System.out.println(outcome.getSegment());
                writeAndFlush(ctx, outcome.getSegment().toString());
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                writeAndFlush(ctx, e.getMessage());
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

    private static void writeAndFlush(ChannelHandlerContext ctx, String msg) {
        try {
            ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            ctx.close();
            throw e;
        }
    }
}

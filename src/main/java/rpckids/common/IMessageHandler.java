package rpckids.common;

import io.netty.channel.ChannelHandlerContext;

// 函数式接口
@FunctionalInterface
public interface IMessageHandler<T> {

	void handle(ChannelHandlerContext ctx, String requestId, T message);

}

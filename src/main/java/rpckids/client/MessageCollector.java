package rpckids.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import rpckids.common.MessageInput;
import rpckids.common.MessageOutput;
import rpckids.common.MessageRegistry;

@Sharable
public class MessageCollector extends ChannelInboundHandlerAdapter {

	private final static Logger LOG = LoggerFactory.getLogger(MessageCollector.class);

	private MessageRegistry registry;
	private RPCClient client;
	// netty中的ctx，可以
	private ChannelHandlerContext context;
	private ConcurrentMap<String, RpcFuture<?>> pendingTasks = new ConcurrentHashMap<>();

	private Throwable ConnectionClosed = new Exception("rpc connection not active error");

	public MessageCollector(MessageRegistry registry, RPCClient client) {
		this.registry = registry;
		this.client = client;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.context = ctx;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.context = null;
		// 刚开始看确实惊到我了，这里也就是key,value别名，foreach lambda
		pendingTasks.forEach((__, future) -> {
			future.fail(ConnectionClosed);
		});
		// 清空map
		pendingTasks.clear();
		// 尝试重连
		ctx.channel().eventLoop().schedule(() -> {
			client.reconnect();
		}, 1, TimeUnit.SECONDS);
	}

	public <T> RpcFuture<T> send(MessageOutput output) {
		ChannelHandlerContext ctx = context;
		RpcFuture<T> future = new RpcFuture<T>();
		if (ctx != null) {
			ctx.channel().eventLoop().execute(() -> {
				// 发送请求时，将requestId和结果绑定一下
				pendingTasks.put(output.getRequestId(), future);
				// 将数据发送服务端
				ctx.writeAndFlush(output);
			});
		} else {
		    // 将error设置到rpcFuture，并打开锁
			future.fail(ConnectionClosed);
		}
		return future;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof MessageInput)) {
			return;
		}
		MessageInput input = (MessageInput) msg;
		// 业务逻辑在这里，获取到对应的Class
		Class<?> clazz = registry.get(input.getType());
		if (clazz == null) {
			LOG.error("unrecognized msg type {}", input.getType());
			return;
		}
		// 将json通过fastjson中工具转换为object
		Object o = input.getPayload(clazz);
		// 当前的感觉就是requestId对应一个RpcFuture
		@SuppressWarnings("unchecked")
		RpcFuture<Object> future = (RpcFuture<Object>) pendingTasks.remove(input.getRequestId());
		if (future == null) {
			LOG.error("future not found with type {}", input.getType());
			return;
		}
		// 将结果传递给rpcFuture，并开锁
		future.success(o);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

	}

	public void close() {
		ChannelHandlerContext ctx = context;
		if (ctx != null) {
			ctx.close();
		}
	}

}

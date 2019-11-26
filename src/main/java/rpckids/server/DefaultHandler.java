package rpckids.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import rpckids.common.IMessageHandler;
import rpckids.common.MessageInput;

/**
 * 默认处理信息处理器
 * */
public class DefaultHandler implements IMessageHandler<MessageInput> {

	private final static Logger LOG = LoggerFactory.getLogger(DefaultHandler.class);


	@Override
	public void handle(ChannelHandlerContext ctx, String requesetId, MessageInput input) {
		// 无法识别的信息来领
		LOG.error("unrecognized message type {} comes", input.getType());
		ctx.close();
	}

}

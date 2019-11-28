package rpckids.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MessageInput {
	// class的className
	private String type;
	// UUID
	private String requestId;
	// 此处的是使用JSONObject.toJSONString(payLoad)
	private String payload;

	public MessageInput(String type, String requestId, String payload) {
		this.type = type;
		this.requestId = requestId;
		this.payload = payload;
	}

	public String getType() {
		return type;
	}

	public String getRequestId() {
		return requestId;
	}

	// 将这个jsonString解析生成token
	public <T> T getPayload(Class<T> clazz) {
		if (payload == null) {
			return null;
		}
		return JSON.parseObject(payload, clazz);
	}

}

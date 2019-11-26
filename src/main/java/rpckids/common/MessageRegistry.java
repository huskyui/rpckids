package rpckids.common;

import java.util.HashMap;
import java.util.Map;


/**
 * 实现了一个 map   type : Class ，提供注册，以及根据type获取class
 * */
public class MessageRegistry {
	private Map<String, Class<?>> clazzes = new HashMap<>();

	public void register(String type, Class<?> clazz) {
		clazzes.put(type, clazz);
	}

	public Class<?> get(String type) {
		return clazzes.get(type);
	}
}

package rpckids.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpckids.client.RPCClient;
import rpckids.client.RPCException;

public class DemoClient {
	private static Logger logger = LoggerFactory.getLogger(DemoClient.class);

	private RPCClient client;

	public DemoClient(RPCClient client) {
		this.client = client;
		this.client.rpc("fib_res", Long.class).rpc("exp_res", ExpResponse.class).rpc("user_res",UserResponse.class);
	}

	public long fib(int n) {
		return (Long) client.send("fib", n);
	}

	public ExpResponse exp(int base, int exp) {
		return (ExpResponse) client.send("exp", new ExpRequest(base, exp));
	}

	public UserResponse buildUser(String name,Integer age){
		return (UserResponse)client.send("user",new UserBuildReq(name,age));
	}

	public static void main(String[] args) throws InterruptedException {
		RPCClient client = new RPCClient("localhost", 8888);
		DemoClient demo = new DemoClient(client);
//		for (int i = 0; i < 30; i++) {
//			try {
//				System.out.printf("fib(%d) = %d\n", i, demo.fib(i));
//				Thread.sleep(100);
//			} catch (RPCException e) {
//				i--; // retry
//			}
//		}
//		for (int i = 0; i < 30; i++) {
//			try {
//				ExpResponse res = demo.exp(2, i);
//				Thread.sleep(100);
//				System.out.printf("exp2(%d) = %d cost=%dns\n", i, res.getValue(), res.getCostInNanos());
//			} catch (RPCException e) {
//				i--; // retry
//			}
//		}
		for(int i = 0; i < 30; i++){
			UserResponse res = demo.buildUser("name:"+i,i);
			Thread.sleep(100);
			logger.info("user{} 生成 : {}",i,res);
		}
		client.close();
	}

}

package rpckids;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huskyui
 * @date 2019/11/26 18:29
 */

public class MapTest {
    public static void main(String[] args) {
        Map<String,String> map = new ConcurrentHashMap<>();
        map.put("username","huskyui");
        map.put("password","dabendan");
        map.forEach((__,name)->{
            System.out.println(__);
            System.out.println(name);
        });
    }
}

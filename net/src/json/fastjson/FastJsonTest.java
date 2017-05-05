package json.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by ldy on 2017/5/5.
 */
public class FastJsonTest {
	public static void main(String[] args) {
		String jsonString = encode();
		System.out.println(jsonString);
		Group group = decode(jsonString);
		System.out.println(group);
	}

	private static String encode() {
		Group group = new Group();
		group.setId(0L);
		group.setName("admin");

		User guestUser = new User();
		guestUser.setId(2L);
		guestUser.setName("guest");

		User rootUser = new User();
		rootUser.setId(3L);
		rootUser.setName("root");

		group.addUser(guestUser);
		group.addUser(rootUser);

		String jsonString = JSON.toJSONString(group);

		return jsonString;
	}

	/**
	 * 反序列化
	 *
	 * @param jsonString
	 * @return
	 */
	private static Group decode(String jsonString) {
		Group group = JSON.parseObject(jsonString, Group.class);
		return group;
	}
}

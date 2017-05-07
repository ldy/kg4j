package json.gson;

import com.google.gson.Gson;

/**
 * Created by ldy on 2017/5/7.
 */
public class ObjectExample {
	static class BagOfPrimitives {
		private int value1 = 1;
		private String value2 = "abc";
		private transient int value3 = 3;

		BagOfPrimitives() {
			// no-args constructor
		}

		@Override
		public String toString() {
			return "BagOfPrimitives{" + "value1=" + value1 + ", value2='" + value2 + '\'' + ", value3=" + value3 + '}';
		}
	}

	public static void main(String[] args) {
		// Serialization
		BagOfPrimitives obj = new BagOfPrimitives();
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		System.out.println(json);

		// ==> json is {"value1":1,"value2":"abc"}

		// Deserialization
		BagOfPrimitives obj2 = gson.fromJson(json, BagOfPrimitives.class);
		System.out.println(obj2);
		// ==> obj2 is just like obj
	}
}

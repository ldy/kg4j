package json.gson;

import com.google.gson.Gson;

/**
 * Created by ldy on 2017/5/7.
 */
public class PrimitivesExample {
	public static void main(String[] args) {
		// Serialization
		String jsonStr = new String();
		Gson gson = new Gson();
		jsonStr = gson.toJson(1); // ==> 1
		System.out.println(jsonStr);
		jsonStr = gson.toJson("abcd"); // ==> "abcd"
		System.out.println(jsonStr);
		jsonStr = gson.toJson(new Long(10)); // ==> 10
		System.out.println(jsonStr);
		int[] values = { 1 };
		jsonStr = gson.toJson(values); // ==> [1]
		System.out.println(jsonStr);

		// Deserialization
		int one = gson.fromJson("1", int.class);
		// Integer one = gson.fromJson("1", Integer.class);
		// Long one = gson.fromJson("1", Long.class);
		// Boolean false = gson.fromJson("false", Boolean.class);
		String str = gson.fromJson("\"abc\"", String.class);
		String[] anotherStr = gson.fromJson("[\"abc\"]", String[].class);
	}
}

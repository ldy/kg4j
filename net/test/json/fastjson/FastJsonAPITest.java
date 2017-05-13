package json.fastjson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.After;
import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;
import static org.junit.Assert.*;

/**
 * Created by ldy on 2017/5/5.
 */
public class FastJsonAPITest {

	String jsonString = "";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(jsonString);
	}

	@Test
	public void encode() throws Exception {
		jsonString = FastJsonAPI.encode();
	}

	@Test
	public void decode() throws Exception {
		jsonString = "{\"id\":0,\"name\":\"admin\",\"users\":[{\"id\":2,\"name\":\"guest\"},{\"id\":3,\"name\":\"root\"}]}";
		Group group = FastJsonAPI.decode(jsonString);
		System.out.println(group);
	}
}
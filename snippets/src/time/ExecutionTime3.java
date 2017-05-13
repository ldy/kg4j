package time;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Created by ldy on 2017/5/12.
 */
public class ExecutionTime3 {
	public static void main(String[] args) throws InterruptedException {
		long lStartTime = Instant.now().toEpochMilli();
		calculation();
		long lEndTime = Instant.now().toEpochMilli();
		long output = lEndTime - lStartTime;
		System.out.println("Elapsed time in milliseconds: " + output);
	}

	private static void calculation() throws InterruptedException {
		// Sleep 2 seconds
		TimeUnit.SECONDS.sleep(2);
	}
}

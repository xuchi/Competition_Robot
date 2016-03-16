package sunstorm;

public class Utils {
	public static long centimetersToMSec(double centimeters) {
		return (long) ((centimeters * 10000) / Config.TRAVEL_SPEED);
	}
	
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch(Exception e) {}
	}

	public static double msecToCentimeters(long msec) {
		return Config.TRAVEL_SPEED * (double) msec / 10000;
	}
}

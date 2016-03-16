package sunstorm;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/*
 * Classe permettant de gérer le sonar.
 */
public class Sonar extends EV3UltrasonicSensor {
	private SampleProvider sampleProvider = null;

	public Sonar() {
		super(Config.SONAR_SENSOR_PORT);
		this.sampleProvider = null;
		this.enable();
	}
	
	@Override
	public void enable() {
		super.enable();
		this.sampleProvider = super.getDistanceMode();
	}
	
	public float getDistance() {
		float sample[] = new float[1];
		this.sampleProvider.fetchSample(sample, 0);
		return sample[0];
	}

}

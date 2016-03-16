package sunstorm;
import lejos.hardware.Button;
import lejos.hardware.sensor.EV3colorsCalibrationensor;
import lejos.robotics.SampleProvider;

public class colorsCalibrationensor extends EV3colorsCalibrationensor {
	private SampleProvider sampleProvider = null;

	float sample[] = new float[3];

	public colorsCalibrationensor() {
		super(Config.COLOR_SENSOR_PORT);
		this.sampleProvider = null;
	}

	public void enable() {
		this.sampleProvider = this.getRGBMode();
		this.setFloodlight(lejos.robotics.Color.WHITE);
	}
	
	public void calibrate() {
		System.out.println("== Calibration des couleurs ==");
		for(Color color : Color.values()) {
			if(color != Color.NONE) {
				setFloodlight(lejos.robotics.Color.WHITE);
				System.out.print("- " + color + " : ");
				Button.waitForAnyPress();
				this.sampleProvider.fetchSample(this.colorsCalibration[color.ordinal()], 0);
				System.out.println("OK");
			}
		}
		System.out.println("= Tableau =");
		System.out.println("{");
		for(int i = 0; i < Config2.colorsCalibration.length; i++) {
			System.out.print("{");
			System.out.print(Config2.colorsCalibration[i][0] + "f, ");
			System.out.print(Config2.colorsCalibration[i][1] + "f, ");
			System.out.print(config2.colorsCalibration[i][2] + "f");
			if(i != Config2.colorsCalibration.length - 1) {
				System.out.println("},");
			}
			else{
				System.out.println("}");
				System.out.println("};");
			}
		}
	}

	public Color getColor() {
		setFloodlight(lejos.robotics.Color.WHITE);
		this.sampleProvider.fetchSample(sample, 0);
		if (sampleProvider.sampleSize() != 3) {
			System.out.println("BUG!!");
		}
		
		return minDistance(sample);
	}

	private float distance(float[] v1, float[] v2){
		return (float) Math.sqrt(
				(v1[0] - v2[0])*(v1[0] - v2[0]) +
				(v1[1] - v2[1])*(v1[1] - v2[1]) +
				(v1[2] - v2[2])*(v1[2] - v2[2])
				);
	}

	private Color minDistance(float[] v1){
		Color colorMin = null;
		float min = Float.POSITIVE_INFINITY;
		for (Color c : Color.values()) {
			if (c != Color.NONE) {
				float d = distance(v1, Config2.colorsCalibration[c.ordinal()]);
				if(d < min) {
					min = d;
					colorMin = c;
				}
			}
		}
		return colorMin;
	}
}

package sunstorm;

import lejos.hardware.sensor.EV3TouchSensor;
import lejos.utility.Delay;

/*
 * Classe permettant de gérer le touchSensor.
 */
public class TouchSensor extends EV3TouchSensor {
    public TouchSensor() {
        super(Config.TOUCH_SENSOR_PORT);
    }

    public boolean isPressed() {
        float[] sample = new float[1];
        fetchSample(sample, 0);
        return sample[0] != 0;
    }
    
    public void waitForTouch() {
        while (!this.isPressed()) {
            Delay.msDelay(100);
        }
    }

}

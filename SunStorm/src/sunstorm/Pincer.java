package sunstorm;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class Pincer {
	private EV3MediumRegulatedMotor pincerMotor;
	public boolean isOpened;

	Pincer() {
		pincerMotor = new EV3MediumRegulatedMotor(Config.PINCER_MOTOR_PORT);
		pincerMotor.setSpeed(Config.PINCER_SPEED);
		isOpened = !Config.CONFIGURE_PINCER;
	}

	public void close() {
		if (isOpened) {
			pincerMotor.rotate(Config.CLOSE_PINCER_ROTATION);
			isOpened = false;
		}
    }

    public void open() {
    	if (!isOpened) {
    		pincerMotor.rotate(Config.OPEN_PINCER_ROTATION);
        	isOpened = true;
    	}
    }

    public void move(int quantity) {
		pincerMotor.rotate(quantity);
	}

    public void setOpened(boolean b) {
		isOpened = b;
    }
    
    public void configure() {
    	System.out.println("== Configuration des pinces ==");
    	System.out.println("- G : ouvrir");
    	System.out.println("- D : fermer");
    	System.out.println("- autre : quitter");
    	
    	int button = Button.waitForAnyPress();
    	while(button == Button.ID_LEFT || button == Button.ID_RIGHT) {
    		this.move((button == Button.ID_LEFT)
						? Config.OPEN_PINCER_ROTATION / 6
						: Config.CLOSE_PINCER_ROTATION / 6);
    		button = Button.waitForAnyPress();
    	}
    	this.setOpened(true);
    }
}

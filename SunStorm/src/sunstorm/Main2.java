package sunstorm;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class Main2 {
	static private Sonar sonar                      = new Sonar();
	public static TouchSensor touch                 = new TouchSensor();
	private static ColorSensor colorSensor          = new ColorSensor();
	public static Pincer pincer                     = new Pincer();
	public static EV3LargeRegulatedMotor leftMotor  = new EV3LargeRegulatedMotor(Config2.LEFT_MOTOR_PORT);
	public static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(Config2.RIGHT_MOTOR_PORT);
	public static DifferentialPilot pilot           = new DifferentialPilot(Config2.WHEEL_SIZE, Config2.TRACK_SIZE, leftMotor, rightMotor);
	public static boolean left                      = false;
	private static boolean wasPressed               = false;

	public static void takeVeryFirstPalet() {
		pilot.setTravelSpeed(Config2.TRAVEL_SPEED * Config2.INTIAL_ACCELERATION);
		goForward(Config2.INITIAL_PALET_DIST / Config2.INTIAL_ACCELERATION);
		pincer.close();
		pilot.rotate(left ? Config2.INITIAL_SPEEDY_ANGLE : -Config2.INITIAL_SPEEDY_ANGLE);
		goToFirstLine();
	}

	public static void main(String[] args) {
		initMotorsSensors();
		pilot.forward();
		Utils.sleep(Config2.DEBUG_TIME);
		pilot.stop();

		System.out.println("Entrer pour G ou D pour commencer");

		// In this loop, we decide from which side we begin the match
		while (true) {
			switch (Button.waitForAnyPress()) {
				case Button.ID_LEFT:
					left = true;
					break;
				case Button.ID_RIGHT:
					left = false;
					break;
				default:
					continue;
			}
			break;
		}

		takeVeryFirstPalet();

		System.exit(0);
	}

	public static void initMotorsSensors() {
		sonar.enable();
		pilot.setRotateSpeed(Config2.ROTATE_SPEED);
		pilot.setTravelSpeed(Config2.TRAVEL_SPEED);
		colorSensor.enable();
		if(Config2.CALIBRATE_COLORS) {
			colorSensor.calibrate();
		}
		if(Config2.CONFIGURE_PINCER) {
			pincer.configure();
		}
	}

	public static void goToFirstLine() {
		System.out.println("ETAT goToFirstLine");
		pilot.setTravelSpeed(Config2.TRAVEL_SPEED * 1.5);
		goForward(Config2.INITIAL_SPEEDY_DIST);
		pilot.setTravelSpeed(Config2.TRAVEL_SPEED);
		pilot.rotate(left ? -Config2.INITIAL_SPEEDY_ANGLE : Config2.INITIAL_SPEEDY_ANGLE);
		pilot.forward();
		goToFirstLineSeekWhite();
	}

	public static void goToFirstLineSeekWhite() {
		long tstart = System.currentTimeMillis();
		System.out.println("ETAT goToFirstLineSeekWhite");
		Color color = colorSensor.getColor();
		while (color != Color.WHITE) {
			if (color == Color.GRAY && System.currentTimeMillis() - tstart >= Config2.GARDE_FOU) {
				pilot.stop();
				goBackward(Config2.DROP_PALET_SONAR_BACKWARD);
				pilot.forward();
				tstart = System.currentTimeMillis();
			}

			Utils.sleep(Config2.DEFAULT_SLEEP);
			color = colorSensor.getColor();
		}
		goToFirstLineWhite();
	}

	public static void goToFirstLineWhite() {
		pilot.stop();
		goForward(7);
		pilot.stop();
		pincer.open();
		goBackward(Config2.BACKWARD_FIRST_LINE + 7);
		pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
		crossCarefully(true);
		pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
		if (followLine(null, null, Color.WHITE, true)) {
			pincer.close();
			pilot.rotate(Config2.STRAIGHT_ANGLE);
			dropPalet(false, false);
			pilot.rotate(Config2.STRAIGHT_ANGLE);
			goToThirdPalet(followLine(null, null, Color.WHITE, true));
		} else {
			goToBottomNextLine();
		}
	}

	public static void crossCarefully(boolean little) {
		System.out.println("ETAT crossCarefully");
		pilot.setTravelSpeed(Config2.TRAVEL_CAREFULLY_SPEED);
		pilot.forward();
		long tstart = System.currentTimeMillis();

		int width = little ? Config2.WIDTH_LITTLE_CAREFULLY : Config2.WIDTH_CAREFULLY;

		while (true) {
			if (System.currentTimeMillis() - tstart >= Utils.centimetersToMSec(width)) {
				System.out.println("Garde fou");
				pilot.stop();
				goBackward(50);
				pilot.forward();
				tstart = System.currentTimeMillis() + 1000;
			}

			if (colorSensor.getColor() != Color.GRAY) {
				pilot.stop();
				break;
			}
			Utils.sleep(Config2.DEFAULT_SLEEP);
		}
		pilot.setTravelSpeed(Config2.TRAVEL_SPEED);
		goForward(Config2.FORWARD_LINE);

	}
	public static void crossCarefully() {
		crossCarefully(false);
	}

	public static boolean followLine(Color lineColor, Color intermediateColor, Color finalColor) {
		return followLine(lineColor, intermediateColor, finalColor, false);
	}
	public static boolean followLine(Color lineColor, Color intermediateColor, Color finalColor, boolean useSonar) {
		try {
		Color currentColor = colorSensor.getColor();
		pilot.setRotateSpeed(Config2.ROTATE_SPEED);
		pilot.setTravelSpeed(Config2.TRAVEL_SPEED);

		pilot.forward();

		boolean ignoreTouch = touch.isPressed();
		boolean stop = false;
		while (!stopFollowLine(stop, ignoreTouch)) {
			currentColor = colorSensor.getColor();

			if (currentColor == Color.WHITE) {
				return wasPressed;
			}

			if (outOfLine(colorSensor.getColor(), lineColor, intermediateColor)) {
				if (useSonar && (sonar.getDistance() < Config2.DROP_PALET_SONAR_DIST)) {
					System.out.println(sonar.getDistance());
					pilot.stop();
					goBackward(Config2.DROP_PALET_SONAR_BACKWARD);
					pilot.forward();
					continue;
				}

				double searchAngle = Config2.INIT_SEARCH_ANGLE;

				thisWhile: while (!stopFollowLine(stop, ignoreTouch) && (lineColor != null && colorSensor.getColor() != lineColor) || (lineColor == null && currentColor == Color.GRAY)) {

					if (useSonar && (sonar.getDistance() < Config2.DROP_PALET_SONAR_DIST)) {
						System.out.println(sonar.getDistance());
						pilot.stop();
						goBackward(Config2.DROP_PALET_SONAR_BACKWARD);
						break;
					}

					for (int i = 0; i < searchAngle; i++) {
						pilot.rotate(Config2.FOLLOW_LINE_ROTATION);
						currentColor = colorSensor.getColor();

						stopFollowLine(stop, ignoreTouch);

						if (currentColor == lineColor || (lineColor == null && currentColor != Color.GRAY)) {
							break thisWhile;
						}
					}

					pilot.rotate(-searchAngle * Config2.FOLLOW_LINE_ROTATION);

					for (int i = 0; i < searchAngle; i++) {
						pilot.rotate(-Config2.FOLLOW_LINE_ROTATION);
						currentColor = colorSensor.getColor();

						stopFollowLine(stop, ignoreTouch);

						if (currentColor == lineColor || (lineColor == null && currentColor != Color.GRAY)) {
							break thisWhile;
						}
					}

					pilot.rotate(searchAngle * Config2.FOLLOW_LINE_ROTATION);

					System.out.println(searchAngle * Config2.FOLLOW_LINE_ROTATION);

					if (useSonar && searchAngle * Config2.FOLLOW_LINE_ROTATION == 12) {
						goForward(2);
					}

					if (useSonar && searchAngle * Config2.FOLLOW_LINE_ROTATION == 24) {
						goForward(3);
					}

					if (useSonar && searchAngle * Config2.FOLLOW_LINE_ROTATION >= 48) {
						return wasPressed;
					}

					searchAngle *= Config2.MULTIPLY_SEARCH_ANGLE;
				}

				if (!stopFollowLine(stop, ignoreTouch)) {

					if (currentColor == Color.WHITE) {
						return wasPressed;
					}

					pilot.forward();
				}
			} else if (currentColor == Color.WHITE || currentColor == finalColor || (finalColor == null && (currentColor !=lineColor && currentColor != intermediateColor))) {
				stop = true;
			} else {
				Utils.sleep(Config2.DEFAULT_SLEEP);
			}
		}
		} catch(Exception e) { }
		pilot.stop();
		pilot.setRotateSpeed(Config2.ROTATE_SPEED);
		pilot.setTravelSpeed(Config2.TRAVEL_SPEED);
		return wasPressed;
	}

	public static boolean outOfLine(Color current, Color line, Color intermediate) {
    	return current == Color.GRAY;
    }

	public static boolean stopFollowLine(boolean stop, boolean ignoreTouch) throws Exception {
		wasPressed = touch.isPressed();
		if (stop || (!ignoreTouch && wasPressed) || colorSensor.getColor() == Color.WHITE) {
			throw new Exception("stop");
		}
		return false;
	}

	public static void dropPalet(boolean rotateBefore, boolean rotateAfter) {
		if (rotateBefore) {
			pilot.rotate(left ? Config2.RIGHT_ANGLE : -Config2.RIGHT_ANGLE);
		}
		followLine(null, Color.BLACK, Color.WHITE, true);
		goForward(Config2.TRAVEL_DISTANCE_GOAL);
		pincer.open();
		goBackward(Config2.BACKWARD_DISTANCE_GOAL);
		if (rotateAfter) {
			pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
		}
	}

	public static void dropPalet(boolean rotateBefore) {
		dropPalet(rotateBefore, true);
	}

	public static void dropPalet() {
		dropPalet(true);
	}

	public static void goForward(double centimeters) {
		pilot.forward();
		Utils.sleep(Utils.centimetersToMSec(centimeters));
		pilot.stop();
	}

	public static void goBackward(double centimeters) {
		pilot.backward();
		Utils.sleep(Utils.centimetersToMSec(centimeters));
		pilot.stop();
	}

	private static void goToThirdPalet(boolean wasPressed) {
		System.out.println("ETAT goToThirdPalet");
		// (green, yellow) palet
		if (wasPressed) {
			pincer.close();
			pilot.rotate(Config2.STRAIGHT_ANGLE);
			dropPalet(false, false);
			goBackward(Config2.BACKWARD_DISTANCE_GOAL);
			goToTopNextLine();
		} else {
			// we are at the bottom of the terrain
			goToBottomNextLine();
		}
	}

	private static void goToBottomNextLine() {
		System.out.println("ETAT goToBottomNextLine");
		goBackward(Config2.BACKWARD_LINE + 5);
		pilot.rotate(left ? Config2.RIGHT_ANGLE : -Config2.RIGHT_ANGLE);
		crossCarefully();
		pilot.rotate(left ? Config2.RIGHT_ANGLE : -Config2.RIGHT_ANGLE);
		if (followLine(null, null, Color.WHITE, true)) {
			pincer.close();
			pilot.rotate(left ? Config2.RIGHT_ANGLE : -Config2.RIGHT_ANGLE);
			crossCarefully();
			pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
			dropPalet(false);
			crossCarefully();
			pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
			goToSecondPalet();

		} else {
			goBackward(Config2.BACKWARD_LINE);
			pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
			crossCarefully();
			pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
			if (followLine(null, null, Color.WHITE, true)) {
				pincer.close();
				pilot.rotate(Config2.STRAIGHT_ANGLE);
				dropPalet(false, false);
				pilot.rotate(Config2.STRAIGHT_ANGLE);
				goToSecondPalet();
			} else {
				goToBottomNextLine();
			}
		}
	}

	private static void goToTopNextLine() {
		System.out.println("ETAT goToTopNextLine");
		pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
		crossCarefully();
		pilot.rotate(left ? -Config2.RIGHT_ANGLE : Config2.RIGHT_ANGLE);
		firstPalet(followLine(null, null, Color.WHITE, true));
	}

	private static void goToSecondPalet() {
		System.out.println("ETAT goToSecondPalet");
		boolean wasPressed = false;
		// (black, yellow) palet
		if (followLine(null, Color.BLACK, Color.WHITE, true)) {
			pincer.close();
			pilot.rotate(Config2.STRAIGHT_ANGLE);
			dropPalet(false, false);
			pilot.rotate(Config2.STRAIGHT_ANGLE);
			wasPressed = followLine(null, Color.BLACK, Color.WHITE, true);
		}
		goToThirdPalet(wasPressed);
	}

	private static void firstPalet(boolean wasPressed) {
		System.out.println("ETAT firstPalet");
		 // (blue, yellow) palet
		if (wasPressed) {
			pincer.close();
			pilot.rotate(Config2.STRAIGHT_ANGLE);
			dropPalet(false, false);
			pilot.rotate(Config2.STRAIGHT_ANGLE);
		} else {
			goForward(Config2.UNKNOWN2);
			if (touch.isPressed()) {
				dropPalet(true, false);
				pilot.rotate(Config2.STRAIGHT_ANGLE);
			}
		}

		goToSecondPalet();
	}
}

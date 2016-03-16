package sunstorm;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;


public class Config {
	//===== PORT =====
	final static Port   LEFT_MOTOR_PORT   = MotorPort.B;
	final static Port   RIGHT_MOTOR_PORT  = MotorPort.C;
	final static Port   PINCER_MOTOR_PORT = MotorPort.A;
	final static Port   TOUCH_SENSOR_PORT = SensorPort.S2;
	final static Port   SONAR_SENSOR_PORT = SensorPort.S4;
	final static Port   COLOR_SENSOR_PORT = SensorPort.S3;

	//===== SLEEP =====
	final static long   DEFAULT_SLEEP = 100; 
	
	//===== SPEED =====
//	final static double ROTATE_SPEED  = 150;
//	final static double TRAVEL_SPEED  = 200;
//	final static double TRAVEL_CAREFULLY_SPEED = TRAVEL_SPEED / 1.5;
//	final static float PINCER_SPEED = 1000;
	final static double INITIAL_SPEEDY_DIST = 130;
	final static double ROTATE_SPEED  = 150;
	final static double TRAVEL_SPEED  = 200;
	final static double TRAVEL_CAREFULLY_SPEED = TRAVEL_SPEED / 1.5;
	final static float PINCER_SPEED = 1500;
	
	//===== SIZE =====
	final static double WHEEL_SIZE    = 55;
	final static double TRACK_SIZE    = 118.46;
	
	//===== PARAM =====
	static final boolean CONFIGURE_PINCER = true;
	static final boolean CALIBRATE_COLORS = false;
	
	//===== ROTATION =====
	final static int OPEN_PINCER_ROTATION   = 360 * 5;
	final static int CLOSE_PINCER_ROTATION  = -OPEN_PINCER_ROTATION;
	
	//===== ANGLE =====
	static final double LINE_FOLLOWING_SMALL_ANGLE = 30;
	static final double LINE_FOLLOWING_BIG_ANGLE   = 60;
	static final double RIGHT_ANGLE = 90;
	static final double STRAIGHT_ANGLE = RIGHT_ANGLE * 2;
	
	//===== FOLLOW LINE =====
	static final double INIT_SEARCH_ANGLE = 1;
	static final double MULTIPLY_SEARCH_ANGLE = 2;
	static final double FOLLOW_LINE_ROTATION = 6;
	
	//===== OTHER =====
	static final int WIDTH_CAREFULLY = 70;
	static final int WIDTH_LITTLE_CAREFULLY = 45;
	static final double DROP_PALET_SONAR_DIST = 0.09;
	static final int DROP_PALET_SONAR_BACKWARD = 20;
	static final int FORWARD_LINE = 7; //TODO ????
	static final int BACKWARD_LINE = FORWARD_LINE; //TODO ?????
	static final int BACKWARD_FIRST_LINE = 30; //TODO ?????
	static final int UNKNOWN = 20; //TODO ????
	static final int UNKNOWN2 = 9; //TODO ?????
	static final int TRAVEL_DISTANCE_GOAL = 15;
	static final int BACKWARD_DISTANCE_GOAL = 17;
	static final int GARDE_FOU = 5000;
	
	static final int DEBUG_TIME = 2000;
	
	
}

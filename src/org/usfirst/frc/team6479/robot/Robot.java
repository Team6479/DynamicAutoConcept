package org.usfirst.frc.team6479.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot {

	public static final String AUTO_FILE = "/home/lvuser/autonomous";
	
	
	XboxController xbox;
	Spark leftDrive;
	Spark rightDrive;
	Spark leftDriveSecond;
	Spark rightDriveSecond;
	MultiSpeedController left;
	MultiSpeedController right;
	CustomDrive driveTrain;
	
	@Override
	public void robotInit() {
		//init drivetrain
		leftDrive = new Spark(0);
		rightDrive = new Spark(2);
		leftDriveSecond = new Spark(1);
		rightDriveSecond = new Spark(3);
		left = new MultiSpeedController(true, leftDrive, leftDriveSecond);
		right = new MultiSpeedController(false, rightDrive, rightDriveSecond);
		
		// left drive is inverted since both motors are built identical
		leftDrive.setInverted(true);
		driveTrain = new CustomDrive(left, right);
		
		xbox = new XboxController(0);
		
	}

	//was the auto logger succesful in opening
	private boolean autoSuccess;
	
	@Override
	public void autonomousInit() {
		//create the autoLogger
		autoLogger = new AutoLogger(left, right);
		//open it
		autoSuccess = autoLogger.open();
	}

	@Override
	public void autonomousPeriodic() {
		if(autoSuccess) {
			boolean continueAuto = autoLogger.run();
			//run until end of auto routine is reached, then set all motors to zero and wait until time runs out
			if(!continueAuto) {
				autoLogger.stop();
			}
		}
	}

	//wether or not the robot should learm a new auto
	private boolean learnAuto;
	//logger for auto
	private AutoLogger autoLogger;
	
	@Override
	public void teleopInit() {
		//wether or not the robot should learn a new auto
		learnAuto = SmartDashboard.getBoolean("DB/Button 0", false);
		//if it should learn a new auto, make the logger, then open the logger
		if(learnAuto) {
			autoLogger = new AutoLogger(left, right);
			autoLogger.open();
		}
	}
	
	@Override
	public void teleopPeriodic() {
		racing();
		//log
		autoLogger.log();
	}
	
	@Override
	public void disabledInit() {
		//when robot is disablled, if robot was learning a new auto the logger needs to be notified and save the log
		//if the auto just ran, the logger also needs to be notified
		if(learnAuto || autoSuccess) {
			autoLogger.close();
		}
	}
	
	public void racing()
	{
		double left = xbox.getRawAxis(2);
		double right = xbox.getRawAxis(3);
		// each trigger has an axis range of 0 to 1
		// to make left trigger reverse, subtract axis value from right trigger
		double throttle = right - left;
		double turn = xbox.getRawAxis(0) * -1;
		// invert
		/*if(throttle < 0){
			turn=turn*-1;
		}*/
		driveTrain.arcadeDrive(turn, throttle);
	}

	@Override
	public void testPeriodic() {
	}
}


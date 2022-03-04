/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/
// ====================================================================
// FILE NAME: Teleop.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 13, 2015
// CREATED BY: Nathanial Lydick
// MODIFIED ON: June 20, 2019
// MODIFIED BY: Ryan McGee
// ABSTRACT:
// This file is where almost all code for Kilroy will be
// written. All of these functions are functions that should
// override methods in the base class (IterativeRobot). The
// functions are as follows:
// -----------------------------------------------------
// Init() - Initialization code for teleop mode
// should go here. Will be called each time the robot enters
// teleop mode.
// -----------------------------------------------------
// Periodic() - Periodic code for teleop mode should
// go here. Will be called periodically at a regular rate while
// the robot is in teleop mode.
// -----------------------------------------------------
//
// ====================================================================
package frc.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import frc.Hardware.Hardware;

import frc.HardwareInterfaces.BallHandler;
import frc.HardwareInterfaces.BallHandler.FIRE;
import frc.HardwareInterfaces.BallHandler.PROCESS;
import frc.Utils.BallCounter;
import frc.Utils.Launcher.LAUNCH_STATE_TELEOP;
import frc.Utils.Launcher.LAUNCH_STATUS_AUTO;
import frc.Utils.Launcher.LAUNCH_STATUS_TELEOP;
import frc.Utils.Launcher.LAUNCH_TYPE;
import frc.Utils.drive.Drive.debugType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class contains all of the user code for the Autonomous part of the
 * match, namely, the Init and Periodic code
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public class Teleop
    {

    /**
     * User Initialization code for teleop mode should go here. Will be called once
     * when the robot enters teleop mode.
     *
     * @author Nathanial Lydick
     * @written Jan 13, 2015
     */
    public static void init()
    {

        // Initializes Transmission To Gear 1
        Hardware.tankTransmission.setGear(1);

        // INITALIZE CLIMB SERVO
        Hardware.climbServo.set(Hardware.CLIMB_SERVO_POS_OUT);

        // RESET TIMER
        Hardware.climbTimer.stop();
        Hardware.climbTimer.reset();

        // Sets the ball count initalized on the robot
        Hardware.ballCounter.BallCount = 0;
        SmartDashboard.putString("DB/String 0", "     Ball Count");
        if (Hardware.ballCountInitSwitch.isOn())
            {
            Hardware.ballCounter.uncheckedAdd(1);
            }
        SmartDashboard.putString("DB/String 5", "     " + Hardware.ballCounter.BallCount + " ball(s)");

        // Initialize launcher
        Hardware.launchMotorGroup.set(0.0);
        Hardware.drive.stop();

    } // end Init

    /**
     * User Periodic code for teleop mode should go here. Will be called
     * periodically at a regular rate while the robot is in teleop mode.
     *
     * @author Nathanial Lydick
     * @written Jan 13, 2015
     */
    public static void periodic()
    {

        // Joystick Button/Trigger Variables
        boolean rightDriverCameraSwitchButtonPressed = Hardware.rightDriverCameraSwitchButton.get();
        boolean rightOperatorCameraSwitchButtonPressed = Hardware.rightOperatorCameraSwitchButton.get();
        boolean climbUpButtonPressed = Hardware.climbUpButton.get();
        boolean climbDownButtonPressed = Hardware.climbDownButton.get();
        boolean openClimbServoButtonPressed = Hardware.openClimbServo.get();
        boolean closeClimbServoButtonPressed = Hardware.closeClimbServo.get();

        // Joystick Ball Add/Sub Variables
        boolean addBallButtonOn = Hardware.addBallButton.isOn();
        boolean addBallButtonOnNow = Hardware.addBallButton.isOnCheckNow();
        boolean subBallButtonOn = Hardware.subtractBallButton.isOn();
        boolean subBallButtonOnNow = Hardware.subtractBallButton.isOnCheckNow();
        int minNumBallsCarriable = 0;

        // Drive Variables
        double leftDriverJoystickY = Hardware.leftDriver.getY() * Hardware.invertControllerAxis;
        double rightDriverJoystickY = Hardware.rightDriver.getY() * Hardware.invertControllerAxis;

        int currentGear = Hardware.drive.getCurrentGear();

        // Setting Gears
        Hardware.tankTransmission.setGearPercentage(Hardware.PREV_YEAR_GEAR_1, Hardware.PREV_YEAR_GEAR_1_PERCENTAGE);
        Hardware.tankTransmission.setGearPercentage(Hardware.PREV_YEAR_GEAR_2, Hardware.PREV_YEAR_GEAR_2_PERCENTAGE);
        if (currentGear < Hardware.PREV_YEAR_GEAR_1)
            {
            Hardware.tankTransmission.setGear(Hardware.PREV_YEAR_GEAR_1);
            }

        // Switch Camera
        if (rightOperatorCameraSwitchButtonPressed || rightDriverCameraSwitchButtonPressed)
            {
            Hardware.KilroyUSBCameras.switchCameras();
            }

        // Ball Count
        if (subBallButtonOnNow == true)
            {
            Hardware.ballCounter.subtractCheckCount(1);
            }
        if (addBallButtonOnNow == true)
            {
            Hardware.ballCounter.addCheckCount(1);
            }
        // System.out.println("BALL COUNT: " + BallCounter.BallCount);
        // System.out.println("Subtract: " + subBallButtonOnNow + " Add: " +
        // addBallButtonOnNow);
        // System.out.println(Hardware.climbServo.getAngle());

        // CLIMB SERVO OVERRIDE BUTTON
        if (openClimbServoButtonPressed == true)
            Hardware.climbServo.set(Hardware.CLIMB_SERVO_POS_OUT);
        if (closeClimbServoButtonPressed == true && openClimbServoButtonPressed == false)
            Hardware.climbServo.set(Hardware.CLIMB_SERVO_POS_IN);

        // CLIMB UP/DOWN FUNCTIONALITY
        if (climbUpButtonPressed == true && climbDownButtonPressed == false)
            {
            if (Hardware.climbEncoder.getDistance() >= Hardware.CLIMB_ENCODER_MAX_HEIGHT)
                Hardware.climbGroup.set(0); // SET SPEED TO ZERO/STOP
            else
                {
                // CHECK IF THE SERVO IS DISENGAGED
                if (Hardware.climbServo.get() == Hardware.CLIMB_SERVO_POS_IN)
                    resetClimbTimerAndSetOut();
                else
                    {
                    // CHECK IF TIME HAS MOVED AT ALL (MEANS TIMER IS ACTIVATED) & IF IT HAS PASSED
                    // THE REQUIRED WAIT TIME
                    if (Hardware.climbTimer.hasElapsed(Hardware.climbTimerWait) && Hardware.climbTimer.get() != 0.0)
                        resetClimbTimerAndSetSpeeds();
                    else if (Hardware.climbTimer.get() == 0.0)
                        setClimbSpeeds();
                    }
                // Hardware.climbGroup.set(.3);
                }
            }
        else if (climbDownButtonPressed == true)
            {
            Hardware.climbGroup.set(-Hardware.BOTH_CLIMB_ENCODER_SPEED);
            // CHECK IF THE CLIMB ENCODERS ARE BELOW AT LEAST 2 INCHES OF THE MAX HEIGHT,
            // AND THEN MOVE THE CLIMB SERVO IN
            if (Hardware.climbEncoder.getDistance() <= Hardware.CLIMB_ENCODER_MAX_HEIGHT - 2)
                Hardware.climbServo.set(Hardware.CLIMB_SERVO_POS_IN);
            }
        else
            {
            // SET SPEED TO ZERO
            Hardware.climbGroup.set(0);
            }

        processFireOuttakeIntake();

        // Operator Dashboard Variables
        SmartDashboard.putString("DB/String 5", " " + Hardware.ballCounter.BallCount + " ball(s)");
        // System.out.println("BALL COUNT: " + BallCounter.BallCount);

        // =============== AUTOMATED SUBSYSTEMS ===============
        // ================= OPERATOR CONTROLS ================
        // ================== DRIVER CONTROLS =================
        // Shifts Gears
        Hardware.tankTransmission.shiftGears(Hardware.rightDriver.getTrigger(), Hardware.leftDriver.getTrigger());

        Hardware.drive.drive(Hardware.leftDriver, Hardware.rightDriver);

        printStatements();
        // individualTest();
    } // end Periodic()

    public static void individualTest()
    {
        // people test functions
    }

    public static void printStatements()
    {
        // ========== INPUTS ==========

        // ---------- DIGITAL ----------

        // Encoder Distances

        // Encoder Raw Values

        // System.out.println("Launch motor encoder raw = " +
        // Hardware.launchMotorEncoder.getRaw());

        // Switch Values

        // System.out.println("Disable Autonomous Switch is:" +
        // Hardware.autoDisableSwitch.isOn());
        // System.out.println("Six Position Switch is:" + Hardware.autoSixPosSwitch);
        // //FIX
        // System.out.println("Ball Counter Switch is:" +
        // Hardware.ballCountInitSwitch.isOn());
        // System.out.println("Spin Switch is:" + Hardware.spinSwitch.isOn());

        // ---------- ANALOG -----------
        // Inputs/Outputs
        // System.out.println("Delay Potentiometer degrees is " +
        // Hardware.delayPot.get());
        // System.out.println("Delay Potentiometer maximum degree range is " +
        // Hardware.delayPot.getFromRange());

        // System.out.println("Inches from nearest object: " +
        // Hardware.ultraSonic.getDistanceFromNearestBumper());

        // ----------- CAN -------------

        // Wheel Motor Values

        // System.out.println("Voltage of left front motor is: " +
        // Hardware.leftTopMotor.get());
        // System.out.println("Voltage of right front motor is: " +
        // Hardware.rightTopMotor.get());
        // System.out.println("Voltage of left back motor is: " +
        // Hardware.leftBottomMotor.get());
        // System.out.println("Voltage of right back motor is: " +
        // Hardware.rightBottomMotor.get());

        // Climb Motor Values

        // System.out.println("Voltage of left climb motor is: " +
        // Hardware.leftClimbMotor.get());
        // System.out.println("Voltage of right climb motor is: " +
        // Hardware.rightClimbMotor.get());
        // System.out.println("Voltage of climb encoder is: " +
        // Hardware.climbEncoder.get());

        // Launch Motor Values

        // System.out.println("Launch Motor Forward is: " +
        // Hardware.launchMotorForward.get());
        // System.out.println("Launch Motor Backward is: " +
        // Hardware.launchMotorBackward.get());

        // Other Motor Values

        // System.out.println("Voltage of intake motor is: " +
        // Hardware.intakeMotor.get());
        // System.out.println("Voltage of color wheel motor is: " +
        // Hardware.colorWheelMotor.get());

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

        // FIX ALL OF THESE

        // Joystick Values

        // Operator Controls

        // ========== OUTPUTS ==========

        // ---------- DIGITAL ----------

        // System.out.println("Ball PickUp 1 is " + Hardware.ballPickup1.isOn());
        // System.out.println("Ball Pickup 2 is " + Hardware.ballPickup2.isOn());
        // System.out.println("Ball Pickup 3 is " + Hardware.ballPickup3.isOn());
        // System.out.println("Ball Pickup 4 is " + Hardware.ballPickup4.isOn());

        // Digital Inputs
        // System.out.println("Auto Disable Switch is " +
        // Hardware.autoDisableSwitch.isOn());

        // System.out.println("Auto Six Position Switch position is " +
        // Hardware.autoSixPosSwitch.getPosition());

        // System.out.println("Ball Counter Switch is " +
        // Hardware.ballCountInitSwitch.isOn());

        // System.out.println("Spin switch is " + Hardware.spinSwitch.isOn());

        // System.out.println("Single Throw Switch for DT is " +
        // Hardware.unknown1Switch.isOn());

        // System.out.println("Single Throw Switch 2 for DT is " +
        // Hardware.unknown2Switch.isOn());

        // System.out.println("Double Throw Switch is " +
        // Hardware.unknownSwitch.isOn());

        // ---------- ANALOG -----------

        // System.out.println("SPI Gyro is:" + Hardware.gyro.get());

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

    }

    // PRIVATE FUNCTIONS

    /**
     * Used to fire intake and outtake without conflicts Called when you need to
     * fire with used buttons
     * 
     * 
     */
    private static void processFireOuttakeIntake()
    {
        // Used for not firing when we have 0 balls
        int minNumBallsCarriable = 0;

        // Sees if button to outtake is pressed
        if (Hardware.outtakeButton.get() == true)
            {
            // Outtakes
            Hardware.ballHandler.processBallHandler(BallHandler.PROCESS.OUTTAKE);
            }
        // Sees if fire button is pressed
        else if (Hardware.launchButton.get() == true && Hardware.fireOverride.get() == true
                || Hardware.launchButton.get() == true && Hardware.fireOverride.get() == false
                        && Hardware.ballCounter.BallCount > minNumBallsCarriable)
            {
            // Fires
            Hardware.ballHandler.processBallHandler(PROCESS.FIRE);
            }
        // Sees if button to intake is pressed
        else if (Hardware.leftOperator.getTrigger() == true)
            {
            // Intakes
            Hardware.ballHandler.processBallHandler(BallHandler.PROCESS.INTAKE);
            }
        // If fire button not pressed or balls = 0, stops firing; if intake and outtake
        // aren't pressed stops
        else
            {
            // Stops all things
            Hardware.ballHandler.processBallHandler(BallHandler.PROCESS.INTAKE_AND_OUTTAKE_STOP);
            Hardware.ballHandler.processBallHandler(PROCESS.FIRE_STOP);
            Hardware.launcher.launchTeleopGeneral(LAUNCH_STATE_TELEOP.RESTING, LAUNCH_TYPE.LOW);
            }
    }

    /**
     * Stops & resets the climb timer, sets the climb servo to be in the "out"
     * position, and starts the timer (again?)
     */
    private static void resetClimbTimerAndSetOut()
    {
        Hardware.climbTimer.stop();
        Hardware.climbTimer.reset();
        Hardware.climbServo.set(Hardware.CLIMB_SERVO_POS_OUT);
        Hardware.climbTimer.start();
    }

    /**
     * Stops & Resets the climb timer, and sets the climb encoders to their
     * respected speeds
     */
    private static void resetClimbTimerAndSetSpeeds()
    {
        Hardware.climbTimer.stop();
        Hardware.climbTimer.reset();
        Hardware.leftClimbMotor.set(Hardware.LEFT_CLIMB_ENCODER_SPEED);
        Hardware.rightClimbMotor.set(Hardware.RIGHT_CLIMB_ENCODER_SPEED);
    }

    /**
     * Sets the climb encoders to their respected speeds
     */
    private static void setClimbSpeeds()
    {
        Hardware.leftClimbMotor.set(Hardware.LEFT_CLIMB_ENCODER_SPEED);
        Hardware.rightClimbMotor.set(Hardware.RIGHT_CLIMB_ENCODER_SPEED);
    }

    } // end class

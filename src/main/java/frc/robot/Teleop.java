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
import frc.Utils.BallCounter;
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
        Hardware.climbServo.set(Hardware.PREV_YEAR_CLIMB_SERVO_POS_OUT);

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
        Hardware.launcher.disallowLaunching();
        // Hardware.launcher.launchGeneral(LAUNCH_TYPE.OFF);
        Hardware.launcher.setDoneFiring(false);
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

        // Outtake and Intake
        if (Hardware.outtakeButton.get() == true)
            {
            ballHandler.processBallHandler(BallHandler.PROCESS.OUTTAKE);
            }
        else
            {
            ballHandler.processBallHandler(BallHandler.PROCESS.STOP);
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
        if (openClimbServoButtonPressed)
            {
            Hardware.climbServo.set(Hardware.PREV_YEAR_CLIMB_SERVO_POS_OUT);
            }
        if (closeClimbServoButtonPressed && !openClimbServoButtonPressed)
            {
            Hardware.climbServo.set(Hardware.PREV_YEAR_CLIMB_SERVO_POS_IN);
            }

        // CLIMB UP/DOWN FUNCTIONALITY
        if (climbUpButtonPressed && !climbDownButtonPressed)
            {
            if (Hardware.climbEncoder.getDistance() >= Hardware.PREV_YEAR_CLIMB_ENCODER_MAX_HEIGHT)
                {
                Hardware.climbGroup.set(0);
                }
            else
                {
                if (Hardware.climbServo.get() == Hardware.PREV_YEAR_CLIMB_SERVO_POS_IN)
                    {
                    Hardware.climbTimer.stop();
                    Hardware.climbTimer.reset();
                    Hardware.climbServo.set(Hardware.PREV_YEAR_CLIMB_SERVO_POS_OUT);
                    Hardware.climbTimer.start();
                    }
                else
                    {
                    if (Hardware.climbTimer.hasElapsed(Hardware.climbTimerWait) && Hardware.climbTimer.get() != 0.0)
                        {
                        Hardware.climbTimer.stop();
                        Hardware.climbTimer.reset();
                        Hardware.leftClimbMotor.set(.27);
                        Hardware.rightClimbMotor.set(.3);
                        }
                    else
                        if (Hardware.climbTimer.get() == 0.0)
                            {
                            Hardware.leftClimbMotor.set(.27);
                            Hardware.rightClimbMotor.set(.3);
                            }
                    }
                // Hardware.climbGroup.set(.3);
                }
            }
        else
            if (climbDownButtonPressed)
                {
                Hardware.climbGroup.set(-.3);
                if (Hardware.climbEncoder.getDistance() <= Hardware.PREV_YEAR_CLIMB_ENCODER_MAX_HEIGHT - 2)
                    {
                    Hardware.climbServo.set(Hardware.PREV_YEAR_CLIMB_SERVO_POS_IN);
                    }
                }
            else
                {
                Hardware.climbGroup.set(0);
                }
        // Operator Dashboard Variables
        SmartDashboard.putString("DB/String 5", " " + Hardware.ballCounter.BallCount + " ball(s)");
        // System.out.println("BALL COUNT: " + BallCounter.BallCount);

        // =============== AUTOMATED SUBSYSTEMS ===============
        // ================= OPERATOR CONTROLS ================

        if (Hardware.launchButton.get() == true)
            {
            Hardware.launcher.setDoneFiring(false);
            Hardware.launcher.setResting(true);
            }
        if (Hardware.launchButton.get() == false)
            {
            Hardware.launcher.disallowLaunching();
            Hardware.launcher.stopFiring();
            }
        Hardware.launcher.launchGeneral(LAUNCH_TYPE.LOW);
        // ================== DRIVER CONTROLS =================
        // Shifts Gears
        Hardware.tankTransmission.shiftGears(Hardware.driverGearUpPressed, Hardware.driverGearDownPressed);

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

        // ---------- ANALOG -----------
        // Inputs/Outputs
        // System.out.println("Delay Potentiometer degrees is " +
        // Hardware.delayPot.get());
        // System.out.println("Delay Potentiometer maximum degree range is " +
        // Hardware.delayPot.getFromRange());

        // System.out.println("Inches from nearest object: " +
        // Hardware.ultraSonic.getDistanceFromNearestBumper());

        // ----------- CAN -------------
        // System.out.println("Voltage of left front motor is: " +
        // Hardware.leftTopMotor.get());

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

        // ========== OUTPUTS ==========

        // ---------- DIGITAL ----------

        // Sensors

        // System.out.println("Floor Light is " + Hardware.floorLight.isOn());

        // System.out.println("Ball PickUp 1 is " + Hardware.ballPickup1.isOn());

        // System.out.println("Ball Pickup 2 is " + Hardware.ballPickup2.isOn());

        // System.out.println("Ball Pickup 3 is " + Hardware.ballPickup3.isOn());

        // System.out.println("Ball Pickup 4 is " + Hardware.ballPickup4.isOn());

        // Digital Inputs
        System.out.println("Auto Disable Switch is " + Hardware.autoDisableSwitch.isOn());

        System.out.println("Auto Six Position Switch position is " + Hardware.autoSixPosSwitch.getPosition());

        System.out.println("Ball Counter Switch is " + Hardware.ballCountInitSwitch.isOn());

        System.out.println("Spin switch is " + Hardware.spinSwitch.isOn());

        System.out.println("Single Throw Switch for DT is " + Hardware.unknown1Switch.isOn());

        System.out.println("Single Throw Switch 2 for DT is " + Hardware.unknown2Switch.isOn());

        System.out.println("Double Throw Switch is " + Hardware.unknownSwitch.isOn());

        // ---------- ANALOG -----------

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

    }

    static BallHandler ballHandler = new BallHandler();
    } // end class

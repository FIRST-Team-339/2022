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
import frc.Hardware.Hardware;
import frc.Utils.Launcher.LAUNCH_TYPE;
import frc.Utils.ballcounter.BallCounter;
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

        // Sets the ball count initalized on the robot
        BallCounter.BallCount = 0;
        SmartDashboard.putString("DB/String 0", "     Ball Count");
        if (Hardware.ballCountInitSwitch.isOn())
            {
            BallCounter.add(1);
            }
        SmartDashboard.putString("DB/String 5", "     " + BallCounter.BallCount + " ball(s)");

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
        // boolean rightOperatorTriggerPressed = Hardware.rightOperator.getTrigger();
        boolean driverGearUpPressed = Hardware.rightDriver.getTrigger();
        boolean driverGearDownPressed = Hardware.leftDriver.getTrigger();
        boolean rightDriverCameraSwitchButtonPressed = Hardware.rightDriverCameraSwitchButton.get();
        boolean rightOperatorCameraSwitchButtonPressed = Hardware.rightOperatorCameraSwitchButton.get();

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

        // Switch Camera
        if (rightOperatorCameraSwitchButtonPressed || rightDriverCameraSwitchButtonPressed)
            {
            Hardware.KilroyUSBCameras.switchCameras();
            }

        // Ball Count
        if (subBallButtonOnNow == true)
            {
            BallCounter.subtractCheckCount(1);
            }
        if (addBallButtonOnNow == true)
            {
            BallCounter.addCheckCount(1);
            }
        // System.out.println("BALL COUNT: " + BallCounter.BallCount);
        // System.out.println("Subtract: " + subBallButtonOnNow + " Add: " +
        // addBallButtonOnNow);
        // System.out.println(Hardware.climbServo.getAngle());
        if (SmartDashboard.getBoolean("DB/Button 2", false))
            {
            Hardware.climbServo.set(.3);
            }
        else
            {
            Hardware.climbServo.set(.6);
            }

        // Operator Dashboard Variables
        SmartDashboard.putString("DB/String 5", " " + BallCounter.BallCount + " ball(s)");
        // System.out.println("BALL COUNT: " + BallCounter.BallCount);

        // =============== AUTOMATED SUBSYSTEMS ===============
        // ================= OPERATOR CONTROLS ================

        // if (Hardware.launchButton.get() == true)
        // {
        // Hardware.launcher.launchGeneral(LAUNCH_TYPE.LOW);
        // }
        // if (Hardware.launchButton.get() == false)
        // {
        // Hardware.launcher.stopFiring();
        // Hardware.launcher.launchGeneral(LAUNCH_TYPE.OFF);
        // }
        // ================== DRIVER CONTROLS =================
        // Shifts Gears
        Hardware.tankTransmission.shiftGears(driverGearUpPressed, driverGearDownPressed);

        Hardware.drive.drive(Hardware.leftDriver, Hardware.rightDriver);

        // printStatements();
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

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

        // ========== OUTPUTS ==========

        // ---------- DIGITAL ----------

        // System.out.println("Ball init switch = " +
        // Hardware.ballCountInitSwitch.isOn());

        // ---------- ANALOG -----------

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

    }

    } // end class

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

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.BallHandler;
import frc.HardwareInterfaces.Potentiometer;
import frc.HardwareInterfaces.BallHandler.PROCESS;
import frc.Utils.Launcher.LAUNCH_STATE_TELEOP;
import frc.Utils.Launcher.LAUNCH_TYPE;

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

        // Reset drive encoders
        Hardware.leftDriveTopEncoder.reset();
        Hardware.leftDriveBottomEncoder.reset();
        Hardware.rightDriveTopEncoder.reset();
        Hardware.rightDriveBottomEncoder.reset();

        // RESET TIMER
        Hardware.climbTimer.stop();
        Hardware.climbTimer.reset();

        // Sets the ball count initalized on the robot
        // Hardware.ballCounter.BallCount = 0;
        SmartDashboard.putString("DB/String 0", " Ball Count");
        // if (Hardware.ballCountInitSwitch.isOn())
        // {
        // Hardware.ballCounter.uncheckedAdd(1);
        // }
        SmartDashboard.putString("DB/String 5", " " + Hardware.ballCounter.BallCount + " ball(s)");

        // Initialize launcher
        Hardware.launchMotorGroup.set(0.0);
        Hardware.drive.stop();

        if (Hardware.demoSwitch.isOn() == true)
            {
            Hardware.inDemoMode = true;
            // demoMaxDriveSpeed = Hardware.delayPot.get(0.0, MAX_DRIVE_SPEED);
            demoMaxDriveSpeed = 0.30;
            }
        else
            {
            Hardware.inDemoMode = false;
            }

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
        // System.out.println("the switch = " +
        // Hardware.autoSixPosSwitch.getPosition());

        // Joystick Button/Trigger Variables
        boolean rightDriverCameraSwitchButtonPressed = Hardware.rightDriverCameraSwitchButton.get();
        boolean rightOperatorCameraSwitchButtonPressed = Hardware.rightOperatorCameraSwitchButton.get();
        boolean climbUpButtonPressed = Hardware.climbUpButton.get();
        boolean climbDownButtonPressed = Hardware.climbDownButton.get();
        boolean openClimbServoButtonPressed = Hardware.openClimbServo.get();
        boolean closeClimbServoButtonPressed = Hardware.closeClimbServo.get();

        // Joystick Ball Add/Sub Variables
        boolean addBallButtonOnNow = Hardware.addBallButton.isOnCheckNow();
        boolean subBallButtonOnNow = Hardware.subtractBallButton.isOnCheckNow();

        // Drive Variables

        int currentGear = Hardware.drive.getCurrentGear();

        // Setting Gears
        if (Hardware.inDemoMode == true)
            {
            Hardware.tankTransmission.setGearPercentage(Hardware.PREV_YEAR_GEAR_1, demoMaxDriveSpeed);
            Hardware.tankTransmission.setGearPercentage(Hardware.PREV_YEAR_GEAR_2, demoMaxDriveSpeed);
            }
        else
            {
            Hardware.tankTransmission.setGearPercentage(Hardware.PREV_YEAR_GEAR_1,
                    Hardware.PREV_YEAR_GEAR_1_PERCENTAGE);
            Hardware.tankTransmission.setGearPercentage(Hardware.PREV_YEAR_GEAR_2,
                    Hardware.PREV_YEAR_GEAR_2_PERCENTAGE);
            }
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

        if (Hardware.inDemoMode == false)
            {
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
            }

        processFireOuttakeIntake();

        // Operator Dashboard Variables
        SmartDashboard.putString("DB/String 5", " " + Hardware.ballCounter.BallCount + " ball(s)");
        // System.out.println("BALL COUNT: " + BallCounter.BallCount);

        // =============== AUTOMATED SUBSYSTEMS ===============
        // ================= OPERATOR CONTROLS ================
        // ================== DRIVER CONTROLS =================
        // Shifts Gears
        if (Hardware.inDemoMode == false)
            {
            Hardware.tankTransmission.shiftGears(Hardware.rightDriver.getTrigger(), Hardware.leftDriver.getTrigger());
            }

        Hardware.drive.drive(Hardware.leftDriver, Hardware.rightDriver);
        // Prints the Value of the delayPot
        // System.out.println("value of " + Hardware.delayPot.get(0, 270));
        // System.out.println("value of " + Hardware.rightDriver);
        // System.out.println("value of " + Hardware.leftDriver);

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

        // Encoder RPM
        // DONE
        // System.out.println("Launcher RPM = " + Hardware.launchMotorEncoder.getRPM());

        // Encoder Distances
        // DONE
        // System.out.println("LF Distance is " +
        // Hardware.leftDriveTopEncoder.getDistance());
        // System.out.println("LB Distance is " +
        // Hardware.leftDriveBottomEncoder.getDistance());
        // System.out.println("RF Distance is " +
        // Hardware.rightDriveTopEncoder.getDistance());
        // System.out.println("RB Distance is " +
        // Hardware.rightDriveBottomEncoder.getDistance());

        // Encoder Raw Values
        // DONE
        // System.out.println("LF RPM is " + Hardware.leftDriveTopEncoder.getRaw());
        // System.out.println("LB RPM is " + Hardware.leftDriveBottomEncoder.getRaw());
        // System.out.println("RF RPM is " + Hardware.rightDriveTopEncoder.getRaw());
        // System.out.println("RB RPM is " + Hardware.rightDriveBottomEncoder.getRaw());

        // Switch Values
        // DONE
        // System.out.println("Auto Disable Switch is " +
        // Hardware.autoDisableSwitch.isOn());

        // System.out.println("Six Pos is " + Hardware.autoSixPosSwitch.getPosition());

        // True = 1; False = 0;
        // System.out.println("Ball Counter Switch is " +
        // Hardware.ballCountInitSwitch.isOn());

        // System.out.println("Spin switch is " + Hardware.spinSwitch.isOn());

        // System.out.println("Single Throw DT is " +
        // Hardware.unknown1Switch.isOn());

        // System.out.println("Single Throw 2 DT is " +
        // Hardware.unknown2Switch.isOn());

        // System.out.println("Double Throw is " +
        // Hardware.unknownSwitch.isOn());

        // System.out.println("Demo ST is " + Hardware.demoSwitch.isOn());

        // ---------- ANALOG -----------
        // DONE
        // Inputs/Outputs
        // System.out.println("Delay Pot is " + Hardware.delayPot.get(0.0,
        // MAX_DRIVE_SPEED));
        // System.out.println("Delay Pot max is " +
        // Hardware.delayPot.getFromRange());
        // System.out.println("IPiston is " + Hardware.intakePiston.get());

        // ----------- PWM -------------
        // DONE
        // System.out.println("CS Angle is " + Hardware.climbServo.get());

        // ----------- CAN -------------

        // Wheel Motor Values
        // DONE
        // System.out.println("LF Motor Voltage: " +
        // Hardware.leftTopMotor.get());
        // System.out.println("RF Motor Voltage: " + Hardware.rightTopMotor.get());
        // System.out.println("LB Motor Voltage: " + Hardware.leftBottomMotor.get());
        // System.out.println("RB Motor Voltage: " + Hardware.rightBottomMotor.get());

        // Conveyor Motor Values
        // DONE
        // System.out.println("CMF Voltage is " + Hardware.conveyorMotorForward.get());
        // System.out.println("CMB Voltage is " + Hardware.conveyorMotorBackward.get());

        // Climb Motor Values
        // DONE
        // System.out.println("Voltage of left climb motor is: " +
        // Hardware.leftClimbMotor.get());
        // System.out.println("Voltage of right climb motor is: " +
        // Hardware.rightClimbMotor.get());
        // System.out.println("Voltage of climb encoder is: " +
        // Hardware.climbEncoder.get());

        // Launch Motor Values
        // DONE
        // System.out.println("LMF Voltage is: " + Hardware.launchMotorForward.get());
        // System.out.println("LMB Voltage is: " + Hardware.launchMotorBackward.get());
        // System.out.println("LM RPM is " + Hardware.launchMotorEncoder.getRPM());

        // Other Motor Values
        // DONE
        // System.out.println("Voltage of intake motor is: " +
        // Hardware.intakeMotor.get());

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

        // FIX ALL OF THESE

        // Joystick Values

        // Operator Controls

        // System.out.println("Right operator throttle: " +
        // Hardware.rightOperator.getThrottle());

        // ========== OUTPUTS ==========

        // ---------- DIGITAL ----------

        // DONE
        // System.out.println("Ball Pickup 1 is " + Hardware.ballPickup1.isOn());
        // System.out.println("Ball Pickup 2 is " + Hardware.ballPickup2.isOn());
        // System.out.println("Ball Pickup 3 is " + Hardware.ballPickup3.isOn());
        // System.out.println("Ball Pickup 4 is " + Hardware.ballPickup4.isOn());

        // Digital Inputs

        // ---------- ANALOG -----------
        // DONE
        // System.out.println("SPI Gyro Angle is: " + Hardware.gyro.getAngle());

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

    }

    // PRIVATE FUNCTIONS

    /**
     * Used to fire intake and outtake without conflicts; called when you need to
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
            Hardware.ballHandler.processBallHandler(BallHandler.PROCESS.OUTTAKE, LAUNCH_TYPE.LOW);
            }
        // Sees if fire button is pressed
        else if (Hardware.launchButton.get() == true && Hardware.fireOverride.get() == true
                || Hardware.launchButton.get() == true && Hardware.fireOverride.get() == false
                        && Hardware.ballCounter.BallCount > minNumBallsCarriable)
            {
            // Fires
            if (Hardware.fireHigh.get() == true)
                {
                if (launchHighReset == false)
                    {
                    Hardware.ballHandler.processBallHandler(PROCESS.FIRE_STOP, LAUNCH_TYPE.HIGH);
                    Hardware.launcher.launchTeleopGeneral(LAUNCH_STATE_TELEOP.RESTING, LAUNCH_TYPE.HIGH);
                    Hardware.launcher.resetSpeedChecking();
                    launchHighReset = true;
                    }
                Hardware.ballHandler.processBallHandler(PROCESS.FIRE, LAUNCH_TYPE.HIGH);
                launchLowReset = false;
                }
            if (Hardware.fireHigh.get() == false)
                {
                if (launchLowReset == false)
                    {
                    Hardware.ballHandler.processBallHandler(PROCESS.FIRE_STOP, LAUNCH_TYPE.LOW);
                    Hardware.launcher.launchTeleopGeneral(LAUNCH_STATE_TELEOP.RESTING, LAUNCH_TYPE.LOW);
                    Hardware.launcher.resetSpeedChecking();
                    launchLowReset = true;
                    }
                Hardware.ballHandler.processBallHandler(PROCESS.FIRE, LAUNCH_TYPE.LOW);
                launchHighReset = false;
                }
            }
        // Sees if button to intake is pressed
        else if (Hardware.leftOperator.getTrigger() == true)
            {
            // Intakes
            Hardware.ballHandler.processBallHandler(BallHandler.PROCESS.INTAKE, LAUNCH_TYPE.LOW);
            }
        // If fire button not pressed or balls = 0, stops firing; if intake and outtake
        // aren't pressed stops intake and outtake
        else
            {
            // Stops all things
            Hardware.ballHandler.processBallHandler(BallHandler.PROCESS.INTAKE_AND_OUTTAKE_STOP, LAUNCH_TYPE.LOW);
            Hardware.ballHandler.processBallHandler(PROCESS.FIRE_STOP, LAUNCH_TYPE.LOW);
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

    // Variables

    private static boolean launchHighReset = false;
    private static boolean launchLowReset = false;
    private static double demoMaxDriveSpeed;
    private static final double MAX_DRIVE_SPEED = .75;

    } // end class

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/
// ====================================================================
// FILE NAME: Autonomous.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 13, 2015
// CREATED BY: Nathanial Lydick
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is where almost all code for Kilroy will be
// written. Some of these functions are functions that should
// override methods in the base class (IterativeRobot). The
// functions are as follows:
// -----------------------------------------------------
// Init() - Initialization code for autonomous mode
// should go here. Will be called each time the robot enters
// autonomous mode.
// -----------------------------------------------------
// Periodic() - Periodic code for autonomous mode should
// go here. Will be called periodically at a regular rate while
// the robot is in autonomous mode.
// -----------------------------------------------------
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package frc.robot;

import javax.swing.DropMode;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.Transmission.TransmissionBase.MotorPosition;
import frc.Utils.drive.Drive.BrakeType;
import frc.Utils.drive.Drive.debugType;

/**
 * An Autonomous class. This class <b>beautifully</b> uses state machines in
 * order to periodically execute instructions during the Autonomous period.
 *
 * This class contains all of the user code for the Autonomous part of the
 * match, namely, the Init and Periodic code
 *
 *
 * @author Michael Andrzej Klaczynski
 * @written at the eleventh stroke of midnight, the 28th of January, Year of our
 *          LORD 2016. Rewritten ever thereafter.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public class Autonomous
    {

    /**
     * User Initialization code for autonomous mode should go here. Will run once
     * when the autonomous first starts, and will be followed immediately by
     * periodic().
     */
    // TODO init robot components correctly
    public static void init()
    {
        // System.out.println("Six position switch position = " +
        // Hardware.autoSixPosSwitch.getPosition());
        // System.out.println("Auto disable switch: " +
        // Hardware.autoDisableSwitch.isOn());
        if (Hardware.autoDisableSwitch.isOn() == false)
            {
            autoPath = AUTO_PATH.DISABLE;
            }
        else
            {
            switch (Hardware.autoSixPosSwitch.getPosition())
                {
                case (0):
                    autoPath = AUTO_PATH.DRIVE_ONLY_BACKWARD;
                    break;
                case (1):
                    autoPath = AUTO_PATH.DRIVE_AND_DROP;
                    break;
                case (2):
                    autoPath = AUTO_PATH.DROP_AND_DRIVE;
                    break;
                case (3):
                    autoPath = AUTO_PATH.DROP_FROM_START_AND_DRIVE;
                    break;
                case (4):
                    autoPath = AUTO_PATH.DRIVE_ONLY_FORWARD;
                    break;
                case (5):
                    autoPath = AUTO_PATH.DRIVE_AND_DROP_AND_DRIVE_AGAIN;
                    break;
                default:
                    autoPath = AUTO_PATH.DISABLE;
                    break;
                }
            }

        Hardware.autoTimer.stop();
        Hardware.autoTimer.reset();
        Hardware.autoShootPlaceholderTimer.stop();
        Hardware.autoShootPlaceholderTimer.reset();
        Hardware.driveDelayTimer.stop();
        Hardware.driveDelayTimer.reset();
        delaySeconds = Hardware.delayPot.get(0, MAX_DELAY_SECONDS);
        // Hardware.drive.setDebugOnStatus(debugType.DEBUG_BRAKING);
        Hardware.drive.resetEncoders();
        onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.INIT;
        onlyDriveForwardState = ONLY_DRIVE_FORWARD_STATE.INIT;
        dropAndDriveState = DROP_AND_DRIVE_STATE.INIT;
        driveAndDropState = DRIVE_AND_DROP_STATE.INIT;
        dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.INIT;
        driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.INIT;
        spinState = SPIN_STATE.SPIN;
    } // end Init

    /**
     * User Periodic code for autonomous mode should go here. Will be called
     * periodically at a regular rate while the robot is in autonomous mode.
     *
     * @author Nathanial Lydick
     * @written Jan 13, 2015
     *
     *          FYI: drive.stop cuts power to the motors, causing the robot to
     *          coast. drive.brake results in a more complete stop. Meghan Brown; 10
     *          February 2019
     *
     */

    public static void periodic()
    {
        // System.out.println("AUTO_PATH = " + autoPath);
        switch (autoPath)
            {
            case DRIVE_ONLY_BACKWARD:
                if (driveOnlyBackwards() == true)
                    {
                    autoPath = AUTO_PATH.DISABLE;
                    }
                break;
            case DRIVE_AND_DROP:
                if (driveAndDrop() == true)
                    {
                    autoPath = AUTO_PATH.DISABLE;
                    }
                break;
            case DROP_AND_DRIVE:
                if (dropAndDrive() == true)
                    {
                    autoPath = AUTO_PATH.DISABLE;
                    }
                break;
            case DROP_FROM_START_AND_DRIVE:
                if (dropFromStartAndDrive() == true)
                    {
                    autoPath = AUTO_PATH.DISABLE;
                    }
            case DRIVE_ONLY_FORWARD:
                if (driveOnlyForward() == true)
                    {
                    autoPath = AUTO_PATH.DISABLE;
                    }
                break;
            case DRIVE_AND_DROP_AND_DRIVE_AGAIN:
                if (driveDropAndDriveAgain() == true)
                    {
                    autoPath = AUTO_PATH.DISABLE;
                    }
                break;
            case DISABLE:
                Hardware.drive.stop();
                break;
            default:
                break;
            }
    }

    // =====================================================================
    // Methods
    // =====================================================================

    public static boolean dropAndDrive()
    {
        System.out.println("DROP_AND_DRIVE_STATE = " + dropAndDriveState);
        switch (dropAndDriveState)
            {
            case INIT:
                Hardware.autoTimer.start();
                dropAndDriveState = DROP_AND_DRIVE_STATE.DELAY;
                return false;
            case DELAY:
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    dropAndDriveState = DROP_AND_DRIVE_STATE.PREPARE_TO_DROP;
                    }
                return false;
            case PREPARE_TO_DROP:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_WALL_INCHES_PREV_YEAR,
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_NEGATIVE, BrakeType.AFTER_DRIVE);
                    dropAndDriveState = DROP_AND_DRIVE_STATE.DROP;
                    }
                return false;
            case STOP_DRIVING_BEFORE_DROP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    dropAndDriveState = DROP_AND_DRIVE_STATE.DROP;
                    }
                return false;
            case DROP:
                Hardware.autoShootPlaceholderTimer.start();
                if (Hardware.autoShootPlaceholderTimer.get() >= TIME_OF_MOTOR_SPINNING_SECONDS_PREV_YEAR)
                    {
                    Hardware.drive.resetEncoders();
                    dropAndDriveState = DROP_AND_DRIVE_STATE.DRIVE;
                    }
                Hardware.colorWheelMotor.set(TEST_MOTOR_SPEED_PREV_YEAR);
                return false;
            case DRIVE:
                Hardware.colorWheelMotor.set(0.0);
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    dropAndDriveState = DROP_AND_DRIVE_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    if (Hardware.spinSwitch.isOn() == true)
                        {
                        dropAndDriveState = DROP_AND_DRIVE_STATE.SPIN;
                        return false;
                        }
                    dropAndDriveState = DROP_AND_DRIVE_STATE.END;
                    }
                return false;
            case SPIN:
                if (spin() == true)
                    {
                    dropAndDriveState = DROP_AND_DRIVE_STATE.END;
                    }
                return false;
            case END:
                Hardware.drive.stop();
                return true;
            default:
                return false;
            }
    }

    // TODO test
    public static boolean dropFromStartAndDrive()
    {
        System.out.println("DROP_FROM_START_AND_DRIVE_STATE = " + dropFromStartAndDriveState);
        switch (dropFromStartAndDriveState)
            {
            case INIT:
                Hardware.autoTimer.start();
                dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.DELAY;
                return false;
            case DELAY:
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.DROP;
                    }
                return false;
            case DROP:
                Hardware.autoShootPlaceholderTimer.start();
                if (Hardware.autoShootPlaceholderTimer.get() >= TIME_OF_MOTOR_SPINNING_SECONDS_PREV_YEAR)
                    {
                    Hardware.drive.resetEncoders();
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.DRIVE;
                    }
                Hardware.colorWheelMotor.set(TEST_MOTOR_SPEED_PREV_YEAR);
                return false;
            case DRIVE:
                Hardware.colorWheelMotor.set(0.0);
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    if (Hardware.spinSwitch.isOn() == true)
                        {
                        dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.SPIN;
                        return false;
                        }
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.END;
                    }
                return false;
            case SPIN:
                if (spin() == true)
                    {
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.END;
                    }
                return false;
            case END:
                Hardware.drive.stop();
                return true;
            default:
                return false;
            }
    }

    // TODO test
    public static boolean driveAndDrop()
    {
        System.out.println("DRIVE_AND_DROP_STATE = " + driveAndDropState);
        switch (driveAndDropState)
            {
            case INIT:
                Hardware.autoTimer.start();
                driveAndDropState = DRIVE_AND_DROP_STATE.DELAY;
                return false;
            case DELAY:
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    driveAndDropState = DRIVE_AND_DROP_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    driveAndDropState = DRIVE_AND_DROP_STATE.STOP_DRIVING_AFTER_DRIVE;
                    }
                return false;
            case STOP_DRIVING_AFTER_DRIVE:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    Hardware.drive.resetEncoders();
                    driveAndDropState = DRIVE_AND_DROP_STATE.WAIT;
                    }
                return false;
            case WAIT:
                Hardware.driveDelayTimer.start();
                if (Hardware.driveDelayTimer.get() >= DRIVE_DELAY_SECONDS)
                    {
                    driveAndDropState = DRIVE_AND_DROP_STATE.PREPARE_TO_DROP;
                    }
                return false;
            case PREPARE_TO_DROP:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_WALL_FROM_OUTSIDE_OF_TARMAC_PREV_YEAR,
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_NEGATIVE, BrakeType.AFTER_DRIVE);
                    driveAndDropState = DRIVE_AND_DROP_STATE.STOP_DRIVING_BEFORE_DROP;
                    }
                return false;
            case STOP_DRIVING_BEFORE_DROP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    Hardware.autoShootPlaceholderTimer.reset();
                    driveAndDropState = DRIVE_AND_DROP_STATE.DROP;
                    }
                return false;
            case DROP:
                Hardware.autoShootPlaceholderTimer.start();
                if (Hardware.autoShootPlaceholderTimer.get() >= TIME_OF_MOTOR_SPINNING_SECONDS_PREV_YEAR)
                    {
                    Hardware.colorWheelMotor.set(0.0);
                    driveAndDropState = DRIVE_AND_DROP_STATE.END;
                    }
                Hardware.colorWheelMotor.set(TEST_MOTOR_SPEED_PREV_YEAR);
                return false;
            case END:
                Hardware.drive.stop();
                Hardware.colorWheelMotor.set(0.0);
                return true;
            default:
                return false;
            }
    }

    public static boolean driveOnlyBackwards()
    {
        // System.out.println("ONLY_DRIVE_STATE = " + onlyDriveState);
        switch (onlyDriveBackwardsState)
            {
            case INIT:
                Hardware.autoTimer.start();
                onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.DELAY;
                return false;
            case DELAY:
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    if (Hardware.spinSwitch.isOn() == true)
                        {
                        onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.SPIN;
                        return false;
                        }
                    onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.END;
                    }
                return false;
            case SPIN:
                if (spin() == true)
                    {
                    onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.SPIN;
                    }
                return false;
            case END:
                Hardware.drive.stop();
                return true;
            default:
                return false;
            }
    }

    public static boolean driveOnlyForward()
    {
        // System.out.println("ONLY_DRIVE_STATE = " + onlyDriveState);
        switch (onlyDriveForwardState)
            {
            case INIT:
                Hardware.autoTimer.start();
                onlyDriveForwardState = ONLY_DRIVE_FORWARD_STATE.DELAY;
                return false;
            case DELAY:
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    onlyDriveForwardState = ONLY_DRIVE_FORWARD_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_NEGATIVE, BrakeType.AFTER_DRIVE);
                    onlyDriveForwardState = ONLY_DRIVE_FORWARD_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    onlyDriveForwardState = ONLY_DRIVE_FORWARD_STATE.END;
                    }
                return false;
            case END:
                Hardware.drive.stop();
                return true;
            default:
                return false;
            }
    }

    public static boolean driveDropAndDriveAgain()
    {
        System.out.println("DRIVE_DROP_AND_DRIVE_AGAIN_STATE = " + driveDropAndDriveAgainState);
        switch (driveDropAndDriveAgainState)
            {
            case INIT:
                Hardware.autoTimer.start();
                driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.DELAY;
                return false;
            case DELAY:
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.DRIVE_ONE;
                    }
                return false;
            case DRIVE_ONE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.STOP_DRIVING_AFTER_DRIVE_ONE;
                    }
                return false;
            case STOP_DRIVING_AFTER_DRIVE_ONE:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    Hardware.drive.resetEncoders();
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.WAIT;
                    }
                return false;
            case WAIT:
                Hardware.driveDelayTimer.start();
                if (Hardware.driveDelayTimer.get() >= DRIVE_DELAY_SECONDS)
                    {
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.PREPARE_TO_DROP;
                    }
                return false;
            case PREPARE_TO_DROP:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_WALL_FROM_OUTSIDE_OF_TARMAC_PREV_YEAR,
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_NEGATIVE, BrakeType.AFTER_DRIVE);
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.STOP_DRIVING_BEFORE_DROP;
                    }
                return false;
            case STOP_DRIVING_BEFORE_DROP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    Hardware.autoShootPlaceholderTimer.reset();
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.DROP;
                    }
                return false;
            case DROP:
                Hardware.autoShootPlaceholderTimer.start();
                if (Hardware.autoShootPlaceholderTimer.get() >= TIME_OF_MOTOR_SPINNING_SECONDS_PREV_YEAR)
                    {
                    Hardware.colorWheelMotor.set(0.0);
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.LEAVE;
                    }
                Hardware.colorWheelMotor.set(TEST_MOTOR_SPEED_PREV_YEAR);
                return false;
            case LEAVE:
                Hardware.colorWheelMotor.set(0.0);
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    if (Hardware.spinSwitch.isOn() == true)
                        {
                        driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.SPIN;
                        }
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.END;
                    }
                return false;
            case SPIN:
                if (spin() == true)
                    {
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.END;
                    }
                return false;
            case END:
                Hardware.colorWheelMotor.set(0.0);
                Hardware.drive.stop();
                return true;
            default:
                return false;
            }
    }

    public static boolean spin()
    {
        switch (spinState)
            {
            case SPIN:

                if (Hardware.drive.turnDegrees(TURN_AROUND_DEGREES, TURN_SPEED_PREV_YEAR, TURN_ACCELERATION_PREV_YEAR,
                        USING_GYRO_FOR_TURN_PREV_YEAR) == true)
                    {
                    spinState = SPIN_STATE.STOP_SPIN;
                    }
                return false;
            case STOP_SPIN:
                if (Hardware.drive.brake(BrakeType.AFTER_TURN) == true)
                    {
                    spinState = SPIN_STATE.END;
                    }
                return false;
            case END:
                return true;
            default:
                return false;
            }

    }

    /*
     * ===================================================================== Class
     * Data =====================================================================
     */

    public static enum AUTO_PATH
        {
        DRIVE_ONLY_BACKWARD, DRIVE_AND_DROP, DROP_AND_DRIVE, DROP_FROM_START_AND_DRIVE, DRIVE_ONLY_FORWARD, DRIVE_AND_DROP_AND_DRIVE_AGAIN, DISABLE;
        }

    public static enum ONLY_DRIVE_BACKWARDS_STATE
        {
        INIT, DELAY, DRIVE, STOP, SPIN, STOP_SPIN, END;
        }

    public static enum ONLY_DRIVE_FORWARD_STATE
        {
        INIT, DELAY, DRIVE, STOP, END;
        }

    public static enum DRIVE_AND_DROP_STATE
        {
        INIT, DELAY, DRIVE, STOP_DRIVING_AFTER_DRIVE, WAIT, PREPARE_TO_DROP, STOP_DRIVING_BEFORE_DROP, DROP, END;
        }

    public static enum DROP_AND_DRIVE_STATE
        {
        INIT, DELAY, PREPARE_TO_DROP, STOP_DRIVING_BEFORE_DROP, DROP, DRIVE, STOP, SPIN, STOP_SPIN, END;
        }

    public static enum DROP_FROM_START_AND_DRIVE_STATE
        {
        INIT, DELAY, DROP, DRIVE, STOP, SPIN, STOP_SPIN, END;
        }

    public static enum DRIVE_DROP_AND_DRIVE_AGAIN_STATE
        {
        INIT, DELAY, DRIVE_ONE, STOP_DRIVING_AFTER_DRIVE_ONE, WAIT, PREPARE_TO_DROP, STOP_DRIVING_BEFORE_DROP, DROP, LEAVE, STOP, SPIN, STOP_SPIN, END;
        }

    public static enum SPIN_STATE
        {
        SPIN, STOP_SPIN, END;
        }

    public static AUTO_PATH autoPath;

    public static ONLY_DRIVE_BACKWARDS_STATE onlyDriveBackwardsState;

    public static ONLY_DRIVE_FORWARD_STATE onlyDriveForwardState;

    public static DRIVE_AND_DROP_STATE driveAndDropState;

    public static DROP_AND_DRIVE_STATE dropAndDriveState;

    public static DROP_FROM_START_AND_DRIVE_STATE dropFromStartAndDriveState;

    public static DRIVE_DROP_AND_DRIVE_AGAIN_STATE driveDropAndDriveAgainState;

    public static SPIN_STATE spinState;

    public static double delaySeconds;
    /*
     * ============================================================== Constants
     * ==============================================================
     */

    private static final double DISTANCE_TO_WALL_INCHES_PREV_YEAR = 10.0; // TODO test

    private static final double DISTANCE_TO_WALL_FROM_OUTSIDE_OF_TARMAC_PREV_YEAR = 90.0; // TODO test

    private static final double DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR = 90.0; // TODO test

    private static final double DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_CURRENT_YEAR = 90;

    private static final double DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR = 100.0; // TODO test

    private static final double DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_CURRENT_YEAR = 110.0;

    private static final double DISTANCE_AFTER_RED_LIGHT_PREV_YEAR_INCHES = 40.0; // TODO test

    private static final double DRIVE_SPEED_POSITIVE_PREV_YEAR = .4; // TODO test

    private static final double DRIVE_SPEED_NEGATIVE_PREV_YEAR = -.4; // TODO test

    private static final double BRAKE_POWER_POSITIVE = .9;

    private static final double BRAKE_POWER_NEGATIVE = -.9;

    private static final double ACCELERATION_PREV_YEAR = .5; // TODO test

    private static final boolean USING_GYRO_FOR_TURN_PREV_YEAR = true;

    private static final boolean USING_GYRO_FOR_DRIVE_PREV_YEAR = false;

    private static final double MAX_DELAY_SECONDS = 5.0;

    private static final double TIME_OF_MOTOR_SPINNING_SECONDS_PREV_YEAR = 3.0;

    private static final double TEST_MOTOR_SPEED_PREV_YEAR = .4;

    private static final double DRIVE_DELAY_SECONDS = .5;

    private static final double TURN_SPEED_PREV_YEAR = .45;

    private static final double TURN_ACCELERATION_PREV_YEAR = .5;

    private static final int TURN_AROUND_DEGREES = 180;
    }

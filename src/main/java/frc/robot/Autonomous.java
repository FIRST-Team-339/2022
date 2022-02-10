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
                    autoPath = AUTO_PATH.DRIVE_ONLY;
                    break;
                case (1):
                    // TODO test
                    autoPath = AUTO_PATH.DRIVE_AND_DROP;
                    break;
                case (2):
                    autoPath = AUTO_PATH.DROP_AND_DRIVE;
                    break;
                case (3):
                    // TODO test
                    autoPath = AUTO_PATH.DROP_FROM_START_AND_DRIVE;
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
        onlyDriveState = ONLY_DRIVE_STATE.INIT;
        dropAndDriveState = DROP_AND_DRIVE_STATE.INIT;
        driveAndDropState = DRIVE_AND_DROP_STATE.INIT;
        dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.INIT;
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
            case DRIVE_ONLY:
                if (driveOnly() == true)
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
            case DISABLE:
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
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
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
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    dropAndDriveState = DROP_AND_DRIVE_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    dropAndDriveState = DROP_AND_DRIVE_STATE.END;
                    }
                return false;
            case END:
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
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.END;
                    }
                return false;
            case END:
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
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
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
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
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
                Hardware.colorWheelMotor.set(0.0);
                return true;
            default:
                return false;
            }
    }

    public static boolean driveOnly()
    {
        // System.out.println("ONLY_DRIVE_STATE = " + onlyDriveState);
        switch (onlyDriveState)
            {
            case INIT:
                Hardware.autoTimer.start();
                onlyDriveState = ONLY_DRIVE_STATE.DELAY;
                return false;
            case DELAY:
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    onlyDriveState = ONLY_DRIVE_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    onlyDriveState = ONLY_DRIVE_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    onlyDriveState = ONLY_DRIVE_STATE.END;
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
        DRIVE_ONLY, DRIVE_AND_DROP, DROP_AND_DRIVE, DROP_FROM_START_AND_DRIVE, DISABLE;
        }

    public static enum ONLY_DRIVE_STATE
        {
        INIT, DELAY, DRIVE, STOP, END;
        }

    public static enum DRIVE_AND_DROP_STATE
        {
        INIT, DELAY, DRIVE, STOP_DRIVING_AFTER_DRIVE, WAIT, PREPARE_TO_DROP, STOP_DRIVING_BEFORE_DROP, DROP, END;
        }

    public static enum DROP_AND_DRIVE_STATE
        {
        INIT, DELAY, PREPARE_TO_DROP, STOP_DRIVING_BEFORE_DROP, DROP, DRIVE, STOP, END;
        }

    public static enum DROP_FROM_START_AND_DRIVE_STATE
        {
        INIT, DELAY, DROP, DRIVE, STOP, END;
        }

    public static AUTO_PATH autoPath;

    public static ONLY_DRIVE_STATE onlyDriveState;

    public static DRIVE_AND_DROP_STATE driveAndDropState;

    public static DROP_AND_DRIVE_STATE dropAndDriveState;

    public static DROP_FROM_START_AND_DRIVE_STATE dropFromStartAndDriveState;

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

    private static final boolean USING_GYRO_PREV_YEAR = false;

    private static final double MAX_DELAY_SECONDS = 5.0;

    private static final double TIME_OF_MOTOR_SPINNING_SECONDS_PREV_YEAR = 3.0;

    private static final double TEST_MOTOR_SPEED_PREV_YEAR = .4;

    private static final double DRIVE_DELAY_SECONDS = .5;
    }

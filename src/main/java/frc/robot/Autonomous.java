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
import frc.Utils.drive.Drive.BrakeType;

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
public static void init ()
{
    // System.out.println("Six position switch position = " + Hardware.autoSixPosSwitch.getPosition());
    switch(Hardware.autoSixPosSwitch.getPosition())
    {
        case(0):
            autoPath = AUTO_PATH.DRIVE_ONLY;
            break;
        case(1):
            autoPath = AUTO_PATH.DRIVE_AND_DROP;
            break;
        case(2):
            autoPath = AUTO_PATH.DROP_AND_DRIVE;
            break;
        default:
            autoPath = AUTO_PATH.DISABLE;
            break;
    }

    Hardware.autoTimer.stop();
    Hardware.autoTimer.reset(); 
    Hardware.autoShootPlaceholderTimer.stop();
    Hardware.autoShootPlaceholderTimer.reset();
    delaySeconds = Hardware.delayPot.get(0, MAX_DELAY_SECONDS);
    onlyDriveState = ONLY_DRIVE_STATE.INIT;
    dropAndDriveState = DROP_AND_DRIVE_STATE.INIT;
} // end Init

/**
 * User Periodic code for autonomous mode should go here. Will be called
 * periodically at a regular rate while the robot is in autonomous mode.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 *
 *          FYI: drive.stop cuts power to the motors, causing the robot to
 *          coast. drive.brake results in a more complete stop.
 *          Meghan Brown; 10 February 2019
 *
 */

public static void periodic ()
{
    // System.out.println("AUTO_PATH = " + autoPath);
    switch(autoPath)
    {
        case DRIVE_ONLY:
            driveOnly();
            break;
        case DRIVE_AND_DROP:
            break;
        case DROP_AND_DRIVE:
            dropAndDrive();
            break;
        case DISABLE:
            break;
        default:
            break;
    }
}

// =====================================================================
// Methods
// =====================================================================

public static void dropAndDrive()
{
    System.out.println("DROP_AND_DRIVE_STATE = " + dropAndDriveState);
    switch(dropAndDriveState)
    {
        case INIT:
            Hardware.autoTimer.start();
            dropAndDriveState = DROP_AND_DRIVE_STATE.DELAY;
            break;
        case DELAY:
            if(Hardware.autoTimer.get() >= delaySeconds)
            {
                dropAndDriveState = DROP_AND_DRIVE_STATE.PREPARE_TO_DROP;
            }
            break;
        case PREPARE_TO_DROP:
            if (Hardware.drive.driveStraightInches(DISTANCE_TO_WALL_INCHES_PREV_YEAR, DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
            {
                dropAndDriveState = DROP_AND_DRIVE_STATE.DROP;
            }
            break;
        case DROP:
            Hardware.autoShootPlaceholderTimer.start();
            if(Hardware.autoShootPlaceholderTimer.get() <= TIME_OF_MOTOR_SPINNING_SECONDS_PREV_YEAR)
            {
                Hardware.colorWheelMotor.set(TEST_MOTOR_SPEED_PREV_YEAR);
            }
            dropAndDriveState = DROP_AND_DRIVE_STATE.DRIVE;
            break;
        case DRIVE:
            if(Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR, DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
            {
                dropAndDriveState = DROP_AND_DRIVE_STATE.STOP;
            }
            break;
        case STOP:
            if(Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
            {
                dropAndDriveState = DROP_AND_DRIVE_STATE.END;
            }
            break;
        case END:
            break;
        default:
            break;
    }
}

public static void driveOnly()
{
    System.out.println("ONLY_DRIVE_STATE = " + onlyDriveState);
    switch (onlyDriveState)
    {
        case INIT:
            Hardware.autoTimer.start();
            onlyDriveState = ONLY_DRIVE_STATE.DELAY;
            break;
        case DELAY:
            // TODO test
            if(Hardware.autoTimer.get() >= delaySeconds)
            {
                onlyDriveState = ONLY_DRIVE_STATE.DRIVE;
            }
            break;
        case DRIVE:
            if(Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR, DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_PREV_YEAR, USING_GYRO_PREV_YEAR) == true)
            {
                onlyDriveState = ONLY_DRIVE_STATE.STOP;
            }
            break;
        case STOP:
            Hardware.drive.resetEncoders();
            if(Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
            {
                onlyDriveState = ONLY_DRIVE_STATE.END;
            }
            break;
        case END:
            break;
        default:
            break;
    }
}

/* =====================================================================
 * Class Data
 * =====================================================================
 */

 public static enum AUTO_PATH
 {
     DRIVE_ONLY, DRIVE_AND_DROP, DROP_AND_DRIVE, DISABLE;
 }

 public static enum ONLY_DRIVE_STATE
 {
     INIT, DELAY, DRIVE, STOP, END;
 }

 public static enum DRIVE_AND_DROP_STATE
 {
     INIT, DELAY, DRIVE, LOAD, DROP, STOP, END;
 }

 public static enum DROP_AND_DRIVE_STATE
 {
     INIT, DELAY, PREPARE_TO_DROP, DROP, DRIVE, STOP, END;
 }

 public static AUTO_PATH autoPath;

 public static ONLY_DRIVE_STATE onlyDriveState;

 public static DRIVE_AND_DROP_STATE driveAndDropState;

 public static DROP_AND_DRIVE_STATE dropAndDriveState;

 public static double delaySeconds;
/*
 * ==============================================================
 * Constants
 * ==============================================================
 */

 public static final double DISTANCE_TO_WALL_INCHES_PREV_YEAR = 30.0; // TODO test

 public static final double DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR = 90.0; // TODO test

 public static final double DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR = 129.0; // TODO test

 public static final double DRIVE_SPEED_POSITIVE_PREV_YEAR = .4; // TODO test

 public static final double DRIVE_SPEED_NEGATIVE_PREV_YEAR = -.4; // TODO test

 public static final double ACCELERATION_PREV_YEAR = .5; // TODO test

 public static final boolean USING_GYRO_PREV_YEAR = false;

 public static final double MAX_DELAY_SECONDS = 5.0;

 public static final double TIME_OF_MOTOR_SPINNING_SECONDS_PREV_YEAR = 3.0;

 public static final double TEST_MOTOR_SPEED_PREV_YEAR = .4;
}

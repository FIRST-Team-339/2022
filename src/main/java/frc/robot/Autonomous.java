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
    delaySeconds = Hardware.delayPot.get();
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
    switch(autoPath)
    {
        case DRIVE_ONLY:
            driveOnly();
            break;
        case DRIVE_AND_DROP:
            break;
        case DROP_AND_DRIVE:
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

public static void driveToWall()
{
    
}

public static void driveOnly()
{
    switch (onlyDriveState)
    {
        case INIT:
            Hardware.autoTimer.start();
            break;
        case DELAY:
            if(Hardware.autoTimer.get() <= delaySeconds)
            {

            }
            break;
        case DRIVE:
            break;
        case STOP:
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

 public static enum DRIVE_ONLY_STATE
 {
     INIT, DELAY, DRIVE, STOP;
 }

 public static enum DRIVE_AND_SCORE_STATE
 {
     INIT, DELAY, DRIVE, LOAD, SCORE, STOP;
 }

 public static enum SCORE_AND_DRIVE_STATE
 {
     INIT, DELAY, LOAD, SCORE, DRIVE, STOP;
 }

 public static AUTO_PATH autoPath;

 public static DRIVE_ONLY_STATE onlyDriveState;

 public static DRIVE_AND_SCORE_STATE driveAndScoreState;

 public static SCORE_AND_DRIVE_STATE scoreAndDriveState;

 public static double delaySeconds;
/*
 * ==============================================================
 * Constants
 * ==============================================================
 */

 public static final double DISTANCE_FROM_HUB_INCHES = 36; // TODO TBD

 public static final double DRIVE_SPEED = .7; // TODO TBD
}

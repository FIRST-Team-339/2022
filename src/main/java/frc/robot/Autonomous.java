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

import edu.wpi.first.wpilibj.Timer;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.Transmission.TransmissionBase.MotorPosition;
import frc.Utils.Launcher.LAUNCH_STATUS_AUTO;
import frc.Utils.Launcher.LAUNCH_TYPE;
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

        // INITALIZE CLIMB SERVO
        Hardware.climbServo.set(Hardware.CLIMB_SERVO_POS_OUT);

        // INITALIZE TIMER
        spinWaitTimer.stop();
        spinWaitTimer.reset();

        // Checks if the auto disable switch is pressed. It will disable auto if the
        // switch returns a value of false
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
                    autoPath = AUTO_PATH.DRIVE_ONLY_FORWARD;
                    break;
                case (2):
                    autoPath = AUTO_PATH.DROP_FROM_START_AND_DRIVE;
                    break;
                case (3):
                    autoPath = AUTO_PATH.DROP_AND_DRIVE;
                    break;
                case (4):
                    autoPath = AUTO_PATH.DRIVE_AND_DROP;
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
        Hardware.launchDelayTimer.stop();
        Hardware.launchDelayTimer.reset();
        Hardware.driveDelayTimer.stop();
        Hardware.driveDelayTimer.reset();
        delaySeconds = Hardware.delayPot.get(0, MAX_DELAY_SECONDS);
        // Hardware.drive.setDebugOnStatus(debugType.DEBUG_BRAKING);
        Hardware.drive.resetEncoders();
        Hardware.launcher.setDoneStates(false, false, false, false);
        onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.INIT;
        onlyDriveForwardState = ONLY_DRIVE_FORWARD_STATE.INIT;
        dropAndDriveState = DROP_AND_DRIVE_STATE.INIT;
        driveAndDropState = DRIVE_AND_DROP_STATE.INIT;
        dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.INIT;
        driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.INIT;
        spinState = SPIN_STATE.SPIN;
        launchAutoState = LAUNCH_AUTO_STATE.START_DROP;
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
        // System.out.println("LEFT: " + Hardware.leftDriveEncoder.getRaw());
        System.out.println("LEFT V: " + Hardware.leftDriveGroup.get());
        // System.out.println("RIGHT: " + Hardware.rightDriveEncoder.getRaw());
        System.out.println("RIGHT V: " + Hardware.rightDriveGroup.get());

        // System.out.println("GYRO V: " + Hardware.gyro);
        // System.out.println("BOTH AVG: " +
        // Hardware.drive.getEncoderDistanceAverage(MotorPosition.ALL));
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
                break;
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

    /**
     * Auto path to drive into position to drop the ball into the low goal and then
     * drive out of the starting area after dropping the ball
     * 
     * @return true when the method finishes
     * 
     * @Author Dion Marchant
     * @Written February 16th, 2022
     */
    private static boolean dropAndDrive()
    {
        // System.out.println("DROP_AND_DRIVE_STATE = " + dropAndDriveState);
        Hardware.launcher.launchAutoGeneral(LAUNCH_TYPE.LOW);
        switch (dropAndDriveState)
            {
            case INIT:
                Hardware.autoTimer.start();
                dropAndDriveState = DROP_AND_DRIVE_STATE.DELAY;
                return false;
            case DELAY:
                if (Hardware.ballCountInitSwitch.isOn() == true)
                    {
                    Hardware.launcher.setDoneStates(true, true, true, false);
                    }
                // Waits until the delay timer reaches a given time inputted from the
                // potentiometer to move on to the next state
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    dropAndDriveState = DROP_AND_DRIVE_STATE.PREPARE_TO_DROP;
                    }
                return false;
            case PREPARE_TO_DROP:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_WALL_INCHES_PREV_YEAR,
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Sets the brake power to the opposite of the drive direction
                    Hardware.drive.setBrakePower(BRAKE_POWER_NEGATIVE, BrakeType.AFTER_DRIVE);
                    dropAndDriveState = DROP_AND_DRIVE_STATE.STOP_DRIVING_BEFORE_DROP;
                    }
                return false;
            case STOP_DRIVING_BEFORE_DROP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    // If the ball count switch returns true, move on to the drop state, otherwise,
                    // skip and go to a waiting state
                    if (Hardware.ballCountInitSwitch.isOn() == true)
                        {
                        dropAndDriveState = DROP_AND_DRIVE_STATE.DROP;
                        return false;
                        }
                    dropAndDriveState = DROP_AND_DRIVE_STATE.WAIT;
                    }
                return false;
            case DROP:
                if (launchAuto(LAUNCH_TYPE.LOW) == true)
                    {
                    dropAndDriveState = DROP_AND_DRIVE_STATE.DRIVE;
                    }
                return false;
            case WAIT:
                // Exists to let the motors rest before driving after braking
                Hardware.driveDelayTimer.start();
                if (Hardware.driveDelayTimer.get() >= DRIVE_DELAY_SECONDS_PREV_YEAR)
                    {
                    dropAndDriveState = DROP_AND_DRIVE_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Set the brake power to opposite the drive direction
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    dropAndDriveState = DROP_AND_DRIVE_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    // If the spin switch returns true, move on to the spin state to turn the robot
                    // around, otherwise, end the path
                    if (Hardware.spinSwitch.isOn() == true)
                        {
                        dropAndDriveState = DROP_AND_DRIVE_STATE.SPIN;
                        return false;
                        }
                    dropAndDriveState = DROP_AND_DRIVE_STATE.WAIT_SPIN;
                    spinWaitTimer.start();
                    }
                return false;
            case WAIT_SPIN:
                if (spinWaitTimer.hasElapsed(SPIN_DELAY_SECONDS))
                    {
                    spinWaitTimer.stop();
                    spinWaitTimer.reset();
                    dropAndDriveState = DROP_AND_DRIVE_STATE.SPIN;
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

    /**
     * Auto path that drops the fall from the starting location and then drives out
     * of the area
     * 
     * @return true when the path completes
     * 
     * @Author Dion Marchant
     * @Written February 16th, 2022
     */
    private static boolean dropFromStartAndDrive()
    {
        // System.out.println("DROP_FROM_START_AND_DRIVE_STATE = " +
        // dropFromStartAndDriveState);
        Hardware.launcher.launchAutoGeneral(LAUNCH_TYPE.AUTO);
        switch (dropFromStartAndDriveState)
            {
            case INIT:
                Hardware.autoTimer.start();
                dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.DELAY;
                return false;
            case DELAY:
                // Wait until the delay timer reaches the delay seconds from the potentiometer
                // input to move on to the next state
                if (Hardware.ballCountInitSwitch.isOn() == true)
                    {
                    Hardware.launcher.setDoneStates(true, true, true, false);
                    }
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    // Check if the ball count switch is true to know if the drop state should be
                    // run or skipped
                    if (Hardware.ballCountInitSwitch.isOn() == true)
                        {
                        dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.DROP;
                        return false;
                        }
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.DRIVE;
                    }
                return false;
            case DROP:
                if (launchAuto(LAUNCH_TYPE.AUTO) == true)
                    {
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Sets the brake power to opposite the drive direction
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    // Checks if the spin switch is true to go to the spin state, or skip if it is
                    // false
                    if (Hardware.spinSwitch.isOn() == true)
                        {
                        dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.SPIN;
                        return false;
                        }
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.WAIT_SPIN;
                    }
                return false;
            case WAIT_SPIN:
                if (spinWaitTimer.hasElapsed(SPIN_DELAY_SECONDS))
                    {
                    spinWaitTimer.stop();
                    spinWaitTimer.reset();
                    dropFromStartAndDriveState = DROP_FROM_START_AND_DRIVE_STATE.SPIN;
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

    /**
     * Auto path that drives out of the starting area and then drives into the area
     * to score in the low goal
     * 
     * @return true when the path completes
     * 
     * @Author Dion Marchant
     * @Written February 16th, 2022
     */
    private static boolean driveAndDrop()
    {
        Hardware.launcher.launchAutoGeneral(LAUNCH_TYPE.LOW);
        // System.out.println("DRIVE_AND_DROP_STATE = " + driveAndDropState);
        switch (driveAndDropState)
            {
            case INIT:
                Hardware.autoTimer.start();
                driveAndDropState = DRIVE_AND_DROP_STATE.DELAY;
                return false;
            case DELAY:
                // Waits until the delay timer reaches the time inputted from the potentiometer
                // to move on to the next state
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    driveAndDropState = DRIVE_AND_DROP_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Sets the brake power to be in the opposite direction of the drive direction
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
                // Delays the robot to allow the motors to rest before driving after braking
                Hardware.driveDelayTimer.start();
                if (Hardware.driveDelayTimer.get() >= DRIVE_DELAY_SECONDS_PREV_YEAR)
                    {
                    driveAndDropState = DRIVE_AND_DROP_STATE.PREPARE_TO_DROP;
                    }
                return false;
            case PREPARE_TO_DROP:
                if (Hardware.ballCountInitSwitch.isOn() == true)
                    {
                    Hardware.launcher.setDoneStates(true, true, true, false);
                    }
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_WALL_FROM_OUTSIDE_OF_TARMAC_INCHES_PREV_YEAR,
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Sets the brake power to be opposite the drive direction
                    Hardware.drive.setBrakePower(BRAKE_POWER_NEGATIVE, BrakeType.AFTER_DRIVE);
                    driveAndDropState = DRIVE_AND_DROP_STATE.STOP_DRIVING_BEFORE_DROP;
                    }
                return false;
            case STOP_DRIVING_BEFORE_DROP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    Hardware.launchDelayTimer.reset();
                    // Checks if the ball count switch on to determine if the drop state should be
                    // skipped
                    if (Hardware.ballCountInitSwitch.isOn() == true)
                        {
                        driveAndDropState = DRIVE_AND_DROP_STATE.DROP;
                        return false;
                        }
                    driveAndDropState = DRIVE_AND_DROP_STATE.END;
                    }
                return false;
            case DROP:
                if (launchAuto(LAUNCH_TYPE.LOW) == true)
                    {
                    driveAndDropState = DRIVE_AND_DROP_STATE.END;
                    }
                return false;
            case END:
                Hardware.launcher.launchAutoGeneral(LAUNCH_TYPE.OFF);
                Hardware.drive.stop();
                return true;
            default:
                return false;
            }
    }

    /**
     * Auto path to back out of the starting area
     * 
     * @return true when the path finishes
     * 
     * @Author Dion Marchant
     * @Written February 16th, 2022
     */
    private static boolean driveOnlyBackwards()
    {
        // System.out.println("ONLY_DRIVE_STATE = " + onlyDriveState);
        switch (onlyDriveBackwardsState)
            {
            case INIT:
                Hardware.autoTimer.start();
                onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.DELAY;
                return false;
            case DELAY:
                // Waits until the auto delay timer reaches the time from the potentiometer
                // input to move on to the next state
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Sets the brake power to be opposite the drive direction
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    // Checks if the spin switch is true to send to the spin state, otherwise, go to
                    // the end state
                    if (Hardware.spinSwitch.isOn() == true)
                        {
                        onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.SPIN;
                        return false;
                        }
                    onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.END;
                    }
                return false;
            case WAIT_SPIN:
                if (spinWaitTimer.hasElapsed(SPIN_DELAY_SECONDS))
                    {
                    spinWaitTimer.stop();
                    spinWaitTimer.reset();
                    onlyDriveBackwardsState = ONLY_DRIVE_BACKWARDS_STATE.SPIN;
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

    /**
     * Auto path to drive forward out of the starting area
     * 
     * @return true when the path completes
     * 
     * @Author Dion Marchant
     * @Written February 16th, 2022
     */
    private static boolean driveOnlyForward()
    {
        // System.out.println("ONLY_DRIVE_STATE = " + onlyDriveState);
        switch (onlyDriveForwardState)
            {
            case INIT:
                Hardware.autoTimer.start();
                onlyDriveForwardState = ONLY_DRIVE_FORWARD_STATE.DELAY;
                return false;
            case DELAY:
                // Wait until the auto delay timer reaches the time from the potentiometer input
                // to move on to the next state
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    onlyDriveForwardState = ONLY_DRIVE_FORWARD_STATE.DRIVE;
                    }
                return false;
            case DRIVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Sets the brake power to be opposite the drive direction
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

    /**
     * Auto path to drive out of the starting area, drive back in, and then leave
     * the starting area after scoring in the low goal
     * 
     * @return true when the path in completed
     * 
     * @Author Dion Marchant
     * @Written February 16th, 2022
     */
    private static boolean driveDropAndDriveAgain()
    {
        // System.out.println("DRIVE_DROP_AND_DRIVE_AGAIN_STATE = " +
        // driveDropAndDriveAgainState);
        Hardware.launcher.launchAutoGeneral(LAUNCH_TYPE.LOW);
        switch (driveDropAndDriveAgainState)
            {
            case INIT:
                Hardware.autoTimer.start();
                driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.DELAY;
                return false;
            case DELAY:
                // Waits until the auto delay timer reaches the time from the potentiometer
                // input to move on to the next state
                if (Hardware.autoTimer.get() >= delaySeconds)
                    {
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.DRIVE_ONE;
                    }
                return false;
            case DRIVE_ONE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Sets the brake power to be opposite the drive direction
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.STOP_DRIVING_AFTER_DRIVE_ONE;
                    }
                return false;
            case STOP_DRIVING_AFTER_DRIVE_ONE:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    Hardware.drive.resetEncoders();
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.WAIT_AFTER_DRIVE_ONE;
                    }
                return false;
            case WAIT_AFTER_DRIVE_ONE:
                if (Hardware.ballCountInitSwitch.isOn() == true)
                    {
                    Hardware.launcher.setDoneStates(true, true, true, false);
                    }
                // Hardware.launcher.launchGeneral(LAUNCH_TYPE.LOW);
                // Brief delay to let the motors rest after braking
                Hardware.driveDelayTimer.start();
                if (Hardware.driveDelayTimer.get() >= DRIVE_DELAY_SECONDS_PREV_YEAR)
                    {
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.PREPARE_TO_DROP;
                    }
                return false;
            case PREPARE_TO_DROP:
                // Hardware.launcher.launchGeneral(LAUNCH_TYPE.LOW);
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_WALL_FROM_OUTSIDE_OF_TARMAC_INCHES_PREV_YEAR,
                        DRIVE_SPEED_POSITIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    Hardware.drive.resetEncoders();
                    // Sets the brake power to be opposite the drive direction
                    Hardware.drive.setBrakePower(BRAKE_POWER_NEGATIVE, BrakeType.AFTER_DRIVE);
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.STOP_DRIVING_BEFORE_DROP;
                    }
                return false;
            case STOP_DRIVING_BEFORE_DROP:
                // Hardware.launcher.launchGeneral(LAUNCH_TYPE.LOW);
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    // If the ball count switch is true, move on to the drop state, otherwise, move
                    // on to a waiting state before leaving the area
                    if (Hardware.ballCountInitSwitch.isOn() == true)
                        {
                        driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.DROP;
                        return false;
                        }
                    Hardware.driveDelayTimer.stop();
                    Hardware.driveDelayTimer.reset();
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.WAIT_AFTER_DRIVE_TWO;
                    }
                return false;
            case DROP:
                if (launchAuto(LAUNCH_TYPE.LOW) == true)
                    {
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.LEAVE;
                    }
                return false;
            case WAIT_AFTER_DRIVE_TWO:
                // Short delay after driving if the launch does not run to let the motors rest
                // after braking
                Hardware.driveDelayTimer.start();
                if (Hardware.driveDelayTimer.get() >= DRIVE_DELAY_SECONDS_PREV_YEAR)
                    {
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.LEAVE;
                    }
                return false;
            case LEAVE:
                if (Hardware.drive.driveStraightInches(DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR,
                        DRIVE_SPEED_NEGATIVE_PREV_YEAR, ACCELERATION_SECONDS_PREV_YEAR,
                        USING_GYRO_FOR_DRIVE_PREV_YEAR) == true)
                    {
                    // Sets the brake power to be opposite the drive direction
                    Hardware.drive.setBrakePower(BRAKE_POWER_POSITIVE, BrakeType.AFTER_DRIVE);
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.STOP;
                    }
                return false;
            case STOP:
                if (Hardware.drive.brake(BrakeType.AFTER_DRIVE) == true)
                    {
                    // Checks if the spin switch is true to send the robot to the spin state,
                    // otherwise, end the path
                    if (Hardware.spinSwitch.isOn() == true)
                        {
                        driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.SPIN;
                        return false;
                        }
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.END;
                    }
                return false;
            case WAIT_SPIN:
                if (spinWaitTimer.hasElapsed(SPIN_DELAY_SECONDS))
                    {
                    spinWaitTimer.stop();
                    spinWaitTimer.reset();
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.SPIN;
                    }
                return false;
            case SPIN:
                if (spin() == true)
                    {
                    driveDropAndDriveAgainState = DRIVE_DROP_AND_DRIVE_AGAIN_STATE.END;
                    }
                return false;
            case END:
                Hardware.drive.stop();
                return true;
            default:
                return false;
            }
    }

    /**
     * Method to cause the robot to turn around in auto
     * 
     * @return true when the robot has turned 180 degrees
     * 
     * @Author Dion Marchant
     * @Written February 16th, 2022
     */
    private static boolean spin()
    {
        switch (spinState)
            {
            // Executes the turn method
            case SPIN:
                Hardware.drive.setPivotDegreesStationaryPercentage(PIVOT_SPEED_CURRENT_YEAR);
                if (Hardware.drive.pivotTurnDegrees(TURN_AROUND_DEGREES, TURN_SPEED_CURRENT_YEAR,
                        TURN_ACCELERATION_SECONDS_PREV_YEAR, USING_GYRO_FOR_TURN_PREV_YEAR) == true)
                    {
                    spinState = SPIN_STATE.STOP_SPIN;
                    }
                // if (Hardware.drive.arc(TURN_SPEED_CURRENT_YEAR, 10, TURN_AROUND_DEGREES,
                // TURN_ACCELERATION_SECONDS_PREV_YEAR) == true)
                // {
                // spinState = SPIN_STATE.STOP_SPIN;
                // }
                return false;
            // Executes the braking
            case STOP_SPIN:
                if (Hardware.drive.brake(BrakeType.AFTER_TURN) == true)
                    {
                    spinState = SPIN_STATE.END;
                    }
                return false;
            // Finished
            case END:
                return true;
            default:
                return false;
            }

    }

    /**
     * Method to launch a ball in auto
     * 
     * @param type
     *            - the type of launch
     * @return true when finished
     */
    private static boolean launchAuto(LAUNCH_TYPE type)
    {
        // System.out.println("Launch auto state: " + launchAutoState);
        Hardware.launcher.launchAutoGeneral(type);
        switch (launchAutoState)
            {
            // Checks if the top rl is on and moves to the corresponding state
            case START_DROP:
                if (Hardware.ballPickup4.isOn() == true)
                    {
                    launchAutoState = LAUNCH_AUTO_STATE.RL_TRIGGERED;
                    return false;
                    }
                if (Hardware.ballPickup4.isOn() == false)
                    {
                    launchAutoState = LAUNCH_AUTO_STATE.RL_OFF1;
                    return false;
                    }
                return false;
            // Moves the conveyor until the rl is triggered
            case RL_OFF1:
                // TODO Move the conveyor in this state
                if (Hardware.ballPickup4.isOn() == true)
                    {
                    launchAutoState = LAUNCH_AUTO_STATE.RL_TRIGGERED;
                    return false;
                    }
                return false;
            // Waits until the launcher returns a ready to fire status
            case RL_TRIGGERED:
                // If the rl is off and the launcher is ready to fire, move the conveyor to fire
                // the ball
                // TODO stop the conveyor
                if (Hardware.launcher.getStatusAuto() == LAUNCH_STATUS_AUTO.READY_TO_FIRE)
                    {
                    launchAutoState = LAUNCH_AUTO_STATE.FIRING;
                    }
                return false;
            // Moves the conveyor to fire
            case FIRING:
                // Wait until the top rl does not see the ball to move on to a short delay to
                // ensure that the ball has been fired
                // TODO move the conveyor here
                if (Hardware.ballPickup4.isOn() == false)
                    {
                    launchAutoState = LAUNCH_AUTO_STATE.FIRING_DELAY;
                    }
                return false;
            // Brief wait to ensure that the ball has been fired
            case FIRING_DELAY:
                // Delay to turn off the launcher after we assume enough time has passed to fire
                // the stored ball
                Hardware.launchDelayTimer.start();
                if (Hardware.launchDelayTimer.get() >= LAUNCH_DELAY_SECONDS_PREV_YEAR)
                    {
                    // TODO Stop moving the conveyor here
                    Hardware.launcher.stopFiring();
                    Hardware.launchDelayTimer.stop();
                    Hardware.launchDelayTimer.reset();
                    Hardware.launcher.disallowLaunching();
                    return true;
                    }
                return false;
            default:
                return false;
            }
    }

    /*
     * ===================================================================== Class
     * Data =====================================================================
     */

    private static enum AUTO_PATH
        {
        DRIVE_ONLY_BACKWARD, DRIVE_AND_DROP, DROP_AND_DRIVE, DROP_FROM_START_AND_DRIVE, DRIVE_ONLY_FORWARD, DRIVE_AND_DROP_AND_DRIVE_AGAIN, DISABLE;
        }

    private static enum ONLY_DRIVE_BACKWARDS_STATE
        {
        INIT, DELAY, DRIVE, STOP, WAIT_SPIN, SPIN, STOP_SPIN, END;
        }

    private static enum ONLY_DRIVE_FORWARD_STATE
        {
        INIT, DELAY, DRIVE, STOP, END;
        }

    private static enum DRIVE_AND_DROP_STATE
        {
        INIT, DELAY, DRIVE, STOP_DRIVING_AFTER_DRIVE, WAIT, PREPARE_TO_DROP, STOP_DRIVING_BEFORE_DROP, DROP, END;
        }

    private static enum DROP_AND_DRIVE_STATE
        {
        INIT, DELAY, PREPARE_TO_DROP, STOP_DRIVING_BEFORE_DROP, DROP, WAIT, DRIVE, STOP, WAIT_SPIN, SPIN, STOP_SPIN, END;
        }

    private static enum DROP_FROM_START_AND_DRIVE_STATE
        {
        INIT, DELAY, DROP, DRIVE, STOP, WAIT_SPIN, SPIN, STOP_SPIN, END;
        }

    private static enum DRIVE_DROP_AND_DRIVE_AGAIN_STATE
        {
        INIT, DELAY, DRIVE_ONE, STOP_DRIVING_AFTER_DRIVE_ONE, WAIT_AFTER_DRIVE_ONE, PREPARE_TO_DROP, STOP_DRIVING_BEFORE_DROP, WAIT_AFTER_DRIVE_TWO, DROP, LEAVE, STOP, WAIT_SPIN, SPIN, STOP_SPIN, END;
        }

    private static enum SPIN_STATE
        {
        SPIN, STOP_SPIN, END;
        }

    private static enum LAUNCH_AUTO_STATE
        {
        START_DROP, RL_OFF1, RL_TRIGGERED, FIRING, FIRING_DELAY;
        }

    private static AUTO_PATH autoPath;

    private static ONLY_DRIVE_BACKWARDS_STATE onlyDriveBackwardsState;

    private static ONLY_DRIVE_FORWARD_STATE onlyDriveForwardState;

    private static DRIVE_AND_DROP_STATE driveAndDropState;

    private static DROP_AND_DRIVE_STATE dropAndDriveState;

    private static DROP_FROM_START_AND_DRIVE_STATE dropFromStartAndDriveState;

    private static DRIVE_DROP_AND_DRIVE_AGAIN_STATE driveDropAndDriveAgainState;

    private static LAUNCH_AUTO_STATE launchAutoState;

    private static SPIN_STATE spinState;

    private static double delaySeconds;

    public static Timer spinWaitTimer = new Timer();
    /*
     * ========================================= Constants
     * =========================================
     */

    private static final double DISTANCE_TO_WALL_INCHES_PREV_YEAR = 10.0; // TODO test

    private static final double DISTANCE_TO_WALL_INCHES_CURRECT_YEAR = 10.0; // TODO

    private static final double DISTANCE_TO_WALL_FROM_OUTSIDE_OF_TARMAC_INCHES_PREV_YEAR = 90.0; // TODO test

    private static final double DISTANCE_TO_WALL_FROM_OUTSIDE_OF_TARMAC_INCHES_CURRENT_YEAR = 90.0; // TODO

    private static final double DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_PREV_YEAR = 90.0; // TODO test

    private static final double DISTANCE_TO_LEAVE_TARMAC_FROM_START_INCHES_CURRENT_YEAR = 90;

    private static final double DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_PREV_YEAR = 100.0; // TODO test

    private static final double DISTANCE_TO_LEAVE_TARMAC_FROM_WALL_INCHES_CURRENT_YEAR = 110.0;

    private static final double DISTANCE_AFTER_RED_LIGHT_PREV_YEAR_INCHES = 40.0; // TODO test

    private static final double DISTANCE_AFTER_RED_LIGHT_CURRENT_YEAR_INCHES = 40.0; // TODO

    private static final double DRIVE_SPEED_POSITIVE_PREV_YEAR = .4; // TODO test

    private static final double DRIVE_SPEED_POSITIVE_CURRENT_YEAR = .4; // TODO

    private static final double DRIVE_SPEED_NEGATIVE_PREV_YEAR = -.4; // TODO test

    private static final double DRIVE_SPEED_NEGATIVE_CURRENT_YEAR = -.4; // TODO

    private static final double BRAKE_POWER_POSITIVE = .9;

    private static final double BRAKE_POWER_NEGATIVE = -.9;

    private static final double ACCELERATION_SECONDS_PREV_YEAR = .5;

    private static final double ACCELERATION_SECONDS_CURRENT_YEAR = .5; // TODO

    private static final boolean USING_GYRO_FOR_TURN_PREV_YEAR = true;

    private static final boolean USING_GYRO_FOR_TURN_CURRENT_YEAR = true; // TODO

    private static final boolean USING_GYRO_FOR_DRIVE_PREV_YEAR = false;

    private static final boolean USING_GYRO_FOR_DRIVE_CURRENT_YEAR = false; // TODO

    private static final double MAX_DELAY_SECONDS = 5.0;

    private static final double LAUNCH_DELAY_SECONDS_PREV_YEAR = 1.0;

    private static final double LAUNCH_DELAY_SECONDS_CURRENT_YEAR = 1.0; // TODO

    private static final double DRIVE_DELAY_SECONDS_PREV_YEAR = .5;

    private static final double DRIVE_DELAY_SECONDS_CURRENT_YEAR = .5; // TODO

    private static final double TURN_SPEED_PREV_YEAR = .45;

    private static final double TURN_SPEED_CURRENT_YEAR = .65;

    private static final double PIVOT_SPEED_CURRENT_YEAR = .35;

    private static final double TURN_ACCELERATION_SECONDS_PREV_YEAR = .5;

    private static final double TURN_ACCELERATION_SECONDS_CURRENT_YEAR = .5; // TODO

    private static final int TURN_AROUND_DEGREES = 180;

    private static final double SPIN_DELAY_SECONDS = 0.65;
    }

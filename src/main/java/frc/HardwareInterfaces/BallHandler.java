// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// ---------------------------------------

// Stores the state machine for intake. eject, and part of firing

package frc.HardwareInterfaces;

import frc.Hardware.Hardware;
import frc.Utils.Launcher.LAUNCH_STATE_TELEOP;
import frc.Utils.Launcher.LAUNCH_STATUS_TELEOP;
import frc.Utils.Launcher.LAUNCH_TYPE;

/**
 * 
 * Code for Intake outtake and part of firing goes here
 * 
 * @author
 * @written Feb 24, 2022
 */
public class BallHandler
    {

    // Add variables when made
    public BallHandler() // Constructer
        {
            fireState = FIRE.FIRE_START_LAUNCHER;
            outtakeState = OUTTAKE.OUTTAKE_INIT;
            intakeState = INTAKE.INTAKE_INIT;
            intakeMotorIntakeSpeed = 0.5;
            intakeMotorOuttakeSpeed = -0.5;
            conveyerWheelIntakeSpeed = -0.5;
            conveyerWheelFireSpeed = -0.5;
            conveyerwheelOutakeSpeed = 0.5;
            motorRestingSpeed = 0.0;
            ballSubInt = 1;
            ballAddInt = 1;

        }

    /**
     * Used to switch between states that involve intake and outtake
     * 
     * 
     * @param processNow
     *            Can be RESTING, OUTTAKE, or STOP called in Teleop
     * @return
     */
    public PROCESS processBallHandler(PROCESS processNow, LAUNCH_TYPE launchType)
    {
        // System.out.println(processNow);
        switch (processNow)
            {
            case RESTING:
                break;
            case OUTTAKE:
                // Calls process outtake function
                processOuttakeFunc();
                break;
            case INTAKE:
                processIntakeFunc();
                break;
            case FIRE:
                // Calls process fire function
                processFireFunc(launchType);
                break;
            case INTAKE_AND_OUTTAKE_STOP:
                Hardware.intakeMotor.set(motorRestingSpeed);
                Hardware.conveyorGroup.set(motorRestingSpeed);
                Hardware.intakePiston.setReverse(true);
                intakeState = INTAKE.INTAKE_INIT;
                outtakeState = OUTTAKE.OUTTAKE_INIT;
                break;
            // case INTAKE_STOP:
            // intakeState = INTAKE.INTAKE_END;
            // processIntakeFunc();
            // intakeState = INTAKE.INTAKE_INIT;
            // break;
            case FIRE_STOP:
                fireState = FIRE.FIRE_END;
                processFireFunc(launchType);
                fireState = FIRE.FIRE_START_LAUNCHER;
                break;
            case RESET_FIRE:
                fireState = FIRE.FIRE_START_LAUNCHER;
                break;
            default:
                break;
            }
        return processNow;
    }

    // Outtake code stored here, called by process ball handler funtion
    private OUTTAKE processOuttakeFunc()
    {
        // System.out.println(outtakeState);
        switch (outtakeState)
            {
            case OUTTAKE_INIT:
                Hardware.intakePiston.setForward(true);
                if (Hardware.ballPickup1.isOn() == false)
                    {
                    // Switches to case where there isn't a ball in front of RL sensor
                    outtakeState = OUTTAKE.OUTTAKE_WORKING_LIGHT_OFF;
                    }
                else
                    {
                    // Switches to case where there is a ball in front of RL sensor
                    outtakeState = OUTTAKE.OUTTAKE_WORKING_LIGHT_ON;
                    }
                break;
            // Is used when the ball is not in front of the RL sensor
            case OUTTAKE_WORKING_LIGHT_OFF:
                Hardware.intakeMotor.set(intakeMotorOuttakeSpeed);
                Hardware.conveyorGroup.set(conveyerwheelOutakeSpeed);
                if (Hardware.ballPickup1.isOn() == true)
                    {
                    // Switches when a ball is in front of RL sensor
                    outtakeState = OUTTAKE.OUTTAKE_WORKING_LIGHT_ON;
                    }
                else
                    {
                    outtakeState = OUTTAKE.OUTTAKE_WORKING_LIGHT_OFF;
                    }
                break;
            // Is used when the ball is in front of the RL sensor
            case OUTTAKE_WORKING_LIGHT_ON:
                Hardware.intakeMotor.set(intakeMotorOuttakeSpeed);
                Hardware.conveyorGroup.set(conveyerwheelOutakeSpeed);
                if (Hardware.ballPickup1.isOn() == false)
                    {
                    // Subtracts one ball when it is ejected
                    Hardware.ballCounter.subtractCheckCount(1);
                    // Switches when ball is not in front of RL sensor anymore
                    outtakeState = OUTTAKE.OUTTAKE_WORKING_LIGHT_OFF;
                    }
                else
                    {

                    outtakeState = OUTTAKE.OUTTAKE_WORKING_LIGHT_ON;
                    }
                break;
            // Case called at the end of outtake, stops everything, and
            // moves intake piston down
            case OUTTAKE_END:
                Hardware.intakeMotor.set(motorRestingSpeed);
                Hardware.conveyorGroup.set(motorRestingSpeed);
                Hardware.intakePiston.setReverse(true);
                break;
            default:
                break;
            }
        return outtakeState;
    }

    /**
     * The intake system
     * 
     * @return the intake state
     */
    private INTAKE processIntakeFunc()
    {
        // System.out.println("Intake state: " + intakeState);
        // Checks if the maximum number of balls in already in the robot. Stops the
        // system if so.
        if (Hardware.ballCounter.BallCount >= Hardware.ballCounter.getMaximumBallCount())
            {
            intakeState = INTAKE.INTAKE_END;
            }
        else
            {
            Hardware.intakeMotor.set(intakeMotorIntakeSpeed);
            }

//        System.out.println("INTAKE STATE = " + intakeState);
//        System.out.println("INTAKE MOTOR SPEED IS CURRENTLY " + Hardware.intakeMotor.get());
        switch (intakeState)
            {
            case INTAKE_INIT:
                Hardware.intakePiston.setForward(true);
                // Moves the conveyor down until the ball hits rl2 or a ball hits rl1
                if (Hardware.ballPickup2.isOn() == false && Hardware.ballPickup1.isOn() == false)
                    {
                    Hardware.conveyorGroup.set(conveyerwheelOutakeSpeed);
                    }
                // Turns off the conveyor after a ball hits either rl1 or 2
                else
                    {
                    Hardware.conveyorGroup.set(motorRestingSpeed);
                    intakeState = INTAKE.INTAKE_CONVEYOR_UP_RL1_OFF;
                    }
                break;
            case INTAKE_CONVEYOR_UP_RL1_OFF:
                // Waits until rl1 is triggered to move the conveyor up
                if (Hardware.ballPickup1.isOn() == true)
                    {
                    Hardware.conveyorGroup.set(conveyerWheelIntakeSpeed);
                    intakeState = INTAKE.INTAKE_CONVEYOR_UP_RL1_ON;
                    }
                break;
            case INTAKE_CONVEYOR_UP_RL1_ON:
                // Waits until both rl1 and 2 are not triggered to check for the second ball
                // hitting rl2
                if (Hardware.ballPickup1.isOn() == false && Hardware.ballPickup2.isOn() == false)
                    {
                    // Hardware.ballCounter.addCheckCount(1);
                    intakeState = INTAKE.INTAKE_CONVEYOR_UP_CHECK_FOR_RL2;
                    }
                Hardware.conveyorGroup.set(conveyerWheelIntakeSpeed);
                break;
            case INTAKE_CONVEYOR_UP_CHECK_FOR_RL2:
                // Checks for when a ball hits rl2 and increments the ball counter and stops the
                // conveyor
                if (Hardware.ballPickup2.isOn() == true)
                    {
                    Hardware.ballCounter.addCheckCount(ballAddInt);
                    Hardware.conveyorGroup.set(motorRestingSpeed);
                    intakeState = INTAKE.INTAKE_INIT;
                    }
                break;
            case INTAKE_END:
                // Turns off and resets the intake system
                Hardware.intakeMotor.set(motorRestingSpeed);
                Hardware.conveyorGroup.set(motorRestingSpeed);
                Hardware.intakePiston.setReverse(true);
                break;
            default:
                break;
            }
        return intakeState;
    }

    private FIRE processFireFunc(LAUNCH_TYPE type)
    {
        // System.out.println(fireState);
        switch (fireState)
            {
            case FIRE_START_LAUNCHER:
                // Starts the launcher and waits until it is ready to move to the next state
                Hardware.intakePiston.setReverse(true);
                Hardware.intakeMotor.set(motorRestingSpeed);
                Hardware.conveyorGroup.set(motorRestingSpeed);
                Hardware.launcher.launchTeleopGeneral(LAUNCH_STATE_TELEOP.SPINNING_UP, type);
                if (Hardware.launcher.getStatusTeleop() == LAUNCH_STATUS_TELEOP.DONE_SPINNING_UP)
                    {
                    fireState = FIRE.FIRE_WAIT_FOR_LAUNCHER;
                    }
                break;
            case FIRE_WAIT_FOR_LAUNCHER:
                // Waits until the launcher is done checking speed to start the conveyor moving
                // and rl4 checking if balls have passed
                Hardware.launcher.launchTeleopGeneral(LAUNCH_STATE_TELEOP.AT_SPEED, type);
                if (Hardware.launcher.getStatusTeleop() != LAUNCH_STATUS_TELEOP.DONE_CHECKING_SPEED)
                    {
                    break;
                    }
                else
                    {
                    fireState = FIRE.FIRE_WORKING_LIGHT_OFF;
                    }
                break;
            case FIRE_WORKING_LIGHT_OFF:
                // Checks for when rl4 is on to go to the next state where it checks for rl4
                // being off in order to know if a ball has passed rl4
                Hardware.conveyorGroup.set(conveyerWheelFireSpeed);
                Hardware.launcher.launchTeleopGeneral(LAUNCH_STATE_TELEOP.AT_SPEED, type);
                if (Hardware.ballPickup4.isOn() == true)
                    {
                    fireState = FIRE.FIRE_WORKING_LIGHT_ON;
                    }
                else
                    {
                    fireState = FIRE.FIRE_WORKING_LIGHT_OFF;
                    }
                break;
            case FIRE_WORKING_LIGHT_ON:
                // Decrements the ball counter when rl4 is no longer triggered, meaning a ball
                // has passed and returns to the state waiting for rl4 to be triggered
                Hardware.launcher.launchTeleopGeneral(LAUNCH_STATE_TELEOP.AT_SPEED, type);
                Hardware.conveyorGroup.set(conveyerWheelFireSpeed);
                if (Hardware.ballPickup4.isOn() == false)
                    {
                    fireState = FIRE.FIRE_WORKING_LIGHT_OFF;
                    Hardware.ballCounter.subtractCheckCount(ballSubInt);
                    }
                else
                    {
                    fireState = FIRE.FIRE_WORKING_LIGHT_ON;
                    }
                break;
            case FIRE_END:
                // Turns off and resets the launcher
                Hardware.launcher.launchTeleopGeneral(LAUNCH_STATE_TELEOP.RESTING, LAUNCH_TYPE.OFF);
                Hardware.conveyorGroup.set(motorRestingSpeed);
                break;
            default:
                break;
            }
        return fireState;
    }

    // Add Variales to init when made
    private static FIRE fireState = FIRE.FIRE_START_LAUNCHER;
    private static OUTTAKE outtakeState = OUTTAKE.OUTTAKE_INIT;
    private static INTAKE intakeState = INTAKE.INTAKE_INIT;
    private static double intakeMotorIntakeSpeed;
    private static double intakeMotorOuttakeSpeed;
    private static double conveyerWheelIntakeSpeed;
    private static double conveyerWheelFireSpeed;
    private static double conveyerwheelOutakeSpeed;
    private static double motorRestingSpeed;
    private static int ballSubInt;
    private static int ballAddInt;

    public static enum BH_LAUNCHER
        {
        RESTING, SPINNING_UP, VERIFYING_VOLTAGE, FIRING;
        }

    public static enum PROCESS
        {
        RESTING, OUTTAKE, INTAKE, FIRE, INTAKE_AND_OUTTAKE_STOP, INTAKE_STOP, FIRE_STOP, RESET_FIRE;
        }

    public static enum INTAKE_MOTOR
        {
        INTAKE_MOTOR_UP, INTAKE_MOTOR_DOWN;
        }

    public static enum WHEEL_MOTOR
        {
        WHEEL_MOTOR_RESTING, WHEEL_MOTOR_SPINNING_BACKWARDS, WHEEL_MOTOR_SPINNING_FORWARD;
        }

    public static enum FIRE
        {
        FIRE_START_LAUNCHER, FIRE_WAIT_FOR_LAUNCHER, FIRE_WORKING_LIGHT_OFF, FIRE_WORKING_LIGHT_ON, FIRE_END;
        }

    public static enum OUTTAKE
        {
        OUTTAKE_INIT, OUTTAKE_WORKING_LIGHT_OFF, OUTTAKE_WORKING_LIGHT_ON, OUTTAKE_END;
        }

    public static enum INTAKE
        {
        INTAKE_INIT, INTAKE_CONVEYOR_UP_CHECK_FOR_RL2, INTAKE_CONVEYOR_UP_RL1_ON, INTAKE_CONVEYOR_UP_RL1_OFF, INTAKE_END;
        }

    }
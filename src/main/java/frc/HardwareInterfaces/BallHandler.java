// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// ---------------------------------------

// Stores the state machine for intake. eject, and part of firing

package frc.HardwareInterfaces;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.Hardware;
import frc.Utils.BallCounter;
import frc.Utils.Launcher.LAUNCH_STATE_TELEOP;
import frc.Utils.Launcher.LAUNCH_STATUS_TELEOP;

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
            fireState = FIRE.FIRE_INIT;
            outtakeState = OUTTAKE.OUTTAKE_INIT;
            intakeState = INTAKE.INTAKE_INIT;
            bhLauncherState = BH_LAUNCHER.RESTING;
            intakeMotorIntakeSpeed = 0.5;
            intakeMotorOuttakeSpeed = -0.5;
            conveyerWheelIntakeSpeed = -0.5;
            conveyerWheelFireSpeed = -0.5;
            conveyerwheelOutakeSpeed = 0.5;
            motorRestingSpeed = 0.0;
            ballSubInt = 1;

        }

    /**
     * Used to switch between states that involve intake and outtake
     * 
     * 
     * @param processNow
     *            Can be RESTING, OUTTAKE, or STOP called in Teleop
     * @return
     */
    public PROCESS processBallHandler(PROCESS processNow)
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
                break;
            case FIRE:
                // Calls process fire function
                processFireFunc();
                break;
            case OUTTAKE_STOP:
                // Switches states to end so everyting stops
                outtakeState = OUTTAKE.OUTTAKE_END;
                // Finishes Calling end by setting everything to end
                processOuttakeFunc();
                // Switches states to INIT for next time buttons are pressed.
                outtakeState = OUTTAKE.OUTTAKE_INIT;
                break;
            case FIRE_STOP:
                fireState = FIRE.FIRE_END;
                processFireFunc();
                fireState = FIRE.FIRE_INIT;
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

    private INTAKE processIntakeFunc()
    {
        // System.out.println(intakeState);
        switch (intakeState)
            {
            case INTAKE_INIT:
                Hardware.intakePiston.setForward(true);
                break;
            case INTAKE_WORKING:
                break;
            case INTAKE_END:
                Hardware.intakeMotor.set(motorRestingSpeed);
                Hardware.conveyorGroup.set(motorRestingSpeed);
                Hardware.intakePiston.setReverse(true);
                break;
            default:
                break;
            }
        return intakeState;
    }

    private FIRE processFireFunc()
    {
        System.out.println(fireState);
        switch (fireState)
            {
            case FIRE_INIT:
                Hardware.intakePiston.setReverse(true);
                Hardware.intakeMotor.set(motorRestingSpeed);
                Hardware.conveyorGroup.set(motorRestingSpeed);
                if (Hardware.launcher.getStatusTeleop() != LAUNCH_STATUS_TELEOP.FIRING)
                    {
                    fireState = FIRE.FIRE_INIT;
                    }
                else
                    {
                    fireState = FIRE.FIRE_WORKING_LIGHT_OFF;
                    }
                break;
            case FIRE_WORKING_LIGHT_OFF:
                Hardware.conveyorGroup.set(conveyerWheelFireSpeed);
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
                Hardware.conveyorGroup.set(motorRestingSpeed);
                break;
            default:
                break;
            }
        return fireState;
    }

    public static BH_LAUNCHER processLauncher()
    {
        switch (bhLauncherState)
            {
            case RESTING:
                break;
            case SPINNING_UP:
                break;
            case VERIFYING_VOLTAGE:
                break;
            case FIRING:
                break;
            }
        return bhLauncherState;
    }

    // Add Variales to init when made
    private static FIRE fireState = FIRE.FIRE_INIT;
    private static OUTTAKE outtakeState = OUTTAKE.OUTTAKE_INIT;
    private static INTAKE intakeState = INTAKE.INTAKE_INIT;
    private static BH_LAUNCHER bhLauncherState = BH_LAUNCHER.RESTING;
    private static double intakeMotorIntakeSpeed;
    private static double intakeMotorOuttakeSpeed;
    private static double conveyerWheelIntakeSpeed;
    private static double conveyerWheelFireSpeed;
    private static double conveyerwheelOutakeSpeed;
    private static double motorRestingSpeed;
    private static int ballSubInt;

    public static enum BH_LAUNCHER
        {
        RESTING, SPINNING_UP, VERIFYING_VOLTAGE, FIRING;
        }

    public static enum PROCESS
        {
        RESTING, OUTTAKE, INTAKE, FIRE, OUTTAKE_STOP, INTAKE_STOP, FIRE_STOP;
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
        FIRE_INIT, FIRE_WORKING_LIGHT_OFF, FIRE_WORKING_LIGHT_ON, FIRE_END;
        }

    public static enum OUTTAKE
        {
        OUTTAKE_INIT, OUTTAKE_WORKING_LIGHT_OFF, OUTTAKE_WORKING_LIGHT_ON, OUTTAKE_END;
        }

    public static enum INTAKE
        {
        INTAKE_INIT, INTAKE_WORKING, INTAKE_END;
        }

    }
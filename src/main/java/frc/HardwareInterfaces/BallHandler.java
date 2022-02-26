// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// ---------------------------------------

// Stores the state machine for intake. eject, and part of firing

package frc.HardwareInterfaces;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.Hardware;
import frc.Utils.BallCounter;

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
    public BallHandler() // Init
        {
            outtakeState = OUTTAKE.OUTTAKE_INIT;
            intakeState = INTAKE.INTAKE_INIT;
            intakeMotorIntakeSpeed = 0.5;
            intakeMotorOuttakeSpeed = -0.5;
            colorWheelIntakeSpeed = -0.5;
            colorWheelFireSpeed = -0.5;
            colorwheelOutakeSpeed = 0.5;
            motorRestingSpeed = 0.0;

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
            case STOP:
                // Switches states to end so everyting stops
                outtakeState = OUTTAKE.OUTTAKE_END;
                intakeState = INTAKE.INTAKE_END;
                // Finishes Calling end by setting everythign to end
                processOuttakeFunc();
                // Switches states to INIT for next time buttons are pressed.
                outtakeState = OUTTAKE.OUTTAKE_INIT;
                intakeState = INTAKE.INTAKE_INIT;
                break;
            default:
                break;
            }
        return PROCESS.STOP;
    }

    private INTAKE processIntakeFunc()
    {
        // System.out.println(intakeState);
        switch (intakeState)
            {
            case INTAKE_INIT:
                Hardware.intakePiston.setForward(true);
                intakeState = INTAKE.INTAKE_WORKING;
                break;
            case INTAKE_WORKING:
                break;
            case INTAKE_END:
                Hardware.intakeMotor.set(motorRestingSpeed);
                Hardware.colorWheelMotor.set(motorRestingSpeed);
                Hardware.intakePiston.setReverse(true);
                break;
            }
        return intakeState;
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
                Hardware.colorWheelMotor.set(colorwheelOutakeSpeed);
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
                Hardware.colorWheelMotor.set(colorwheelOutakeSpeed);
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
            // Case called at the end of outtake, stops everything, and moves intake piston
            // down
            case OUTTAKE_END:
                Hardware.intakeMotor.set(motorRestingSpeed);
                Hardware.colorWheelMotor.set(motorRestingSpeed);
                Hardware.intakePiston.setReverse(true);
                break;
            }
        return outtakeState;
    }

    // Add Variales to init when made
    private static OUTTAKE outtakeState = OUTTAKE.OUTTAKE_INIT;
    private static INTAKE intakeState = INTAKE.INTAKE_INIT;
    private static double intakeMotorIntakeSpeed;
    private static double intakeMotorOuttakeSpeed;
    private static double colorWheelIntakeSpeed;
    private static double colorWheelFireSpeed;
    private static double colorwheelOutakeSpeed;
    private static double motorRestingSpeed;

    public static enum PROCESS
        {
        RESTING, OUTTAKE, INTAKE, STOP;
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
        TEST;
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
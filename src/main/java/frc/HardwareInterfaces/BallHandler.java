// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// ---------------------------------------

// Stores the state machine for intake and ejection

package frc.HardwareInterfaces;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.Hardware;

/** Add your docs here. */
public class BallHandler
    {

    // Add variables when made
    public BallHandler()
        {
            outtakeState = OUTTAKE.INIT;
        }

    public PROCESS processBallHandler(PROCESS processNow)
    {
        switch (processNow)
            {
            case RESTING:
                break;
            case OUTTAKE:
                processOuttakeFunc();
                break;
            case STOP:
                outtakeState = OUTTAKE.END;
                processOuttakeFunc();
                outtakeState = OUTTAKE.INIT;
                break;
            default:
                break;
            }
        return PROCESS.STOP;
    }

    private OUTTAKE processOuttakeFunc()
    {
        switch (outtakeState)
            {
            case INIT:
                Hardware.intakePiston.setForward(true);
                outtakeState = OUTTAKE.WORKING;
                break;
            case WORKING:
                Hardware.intakeMotor.set(0.5);
                Hardware.colorWheelMotor.set(0.5);
                break;
            case END:
                Hardware.intakeMotor.set(0.0);
                Hardware.colorWheelMotor.set(0.0);
                Hardware.intakePiston.setReverse(true);
                break;
            }
        return outtakeState;
    }

    private static OUTTAKE outtakeState = OUTTAKE.INIT;

    public static enum PROCESS
        {
        RESTING, OUTTAKE, STOP;
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
        INIT, WORKING, END;
        }
    }
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

    public BallHandler(boolean intakeTrigger, JoystickButton outtakeButton)
        {
            if (outtakeButton.get() == true)
                {
                PROCESS currentProcessState = PROCESS.OUTTAKE;
                }
        }

    public static enum PROCESS
        {
        RESTING, OUTTAKE
            {

            },
        STOP;
        }

    public static enum INTAKE_MOTOR
        {
        INTAKE_MOTOR_UP
            {

            },
        INTAKE_MOTOR_DOWN
            {

            };
        }

    public static enum WHEEL_MOTOR
        {
        WHEEL_MOTOR_RESTING, WHEEL_MOTOR_SPINNING_BACKWARDS, WHEEL_MOTOR_SPINNING_FORWARD;
        }

    public static enum FIRE
        {
        TEST;
        }
    }
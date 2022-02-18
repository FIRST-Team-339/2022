// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// ---------------------------------------

// Stores the state machine for the launch

package frc.HardwareInterfaces;

import frc.Hardware.Hardware;

/** Add your docs here. */
public class BallHandler
    {

    // Where code gets initalized
    public static void ballHandlerInit()
    {

    }

    // Where code gets called periodically
    public static void ballHandlerPeriodic()
    {
        // ---------------------------------------
        // Keep Code Commented Until Ready To Test
        // ---------------------------------------
        // State currentState = State.EJECTING;
        // switch (currentState)
        // {
        // case EJECTING:
        // Hardware.intakeMotor.set(possibleIntakeMotorEjectingSpeed);
        // break;
        // case INTAKE:
        // if (Hardware.intakeMotor.get() > stillIntakeMotor)
        // {

        // }
        // break;
        // default:
        // System.out.println("!!!SOMETINGS BROKEN!!!");
        // break;

    }

    }

// public static enum State
// {
// EJECTING, INTAKE;
// }

// public static double possibleIntakeMotorEjectingSpeed = -1.0;
// public static double stillIntakeMotor = 0.0;

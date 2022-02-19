// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// ---------------------------------------

// Stores the state machine for intake and ejection

package frc.HardwareInterfaces;

import frc.Hardware.Hardware;

/** Add your docs here. */
public class BallHandler
    {
    // ---------------------------------------
    // Keep Code Commented Until Ready To Test
    // ---------------------------------------
    // public boolean intakeTeleop()
    // {
    // switch (this.currentIntakeState)
    // {
    // case INTAKE_RESTING:
    // if (Hardware.operatorIntakeButtonPressed == true)
    // {
    // currentIntakeState = INTAKE.PISTON_DOWN_INTAKE;
    // }
    // return false;
    // case PISTON_DOWN_INTAKE:
    // if (Hardware.operatorIntakeButtonPressed == false)
    // {
    // currentIntakeState = INTAKE.INTAKE_RESTING;
    // }
    // else
    // {
    // Hardware.intakePiston.setForward(true);
    // }
    // return false;
    // case INTAKE_SPINNING:
    // if (Hardware.operatorIntakeButtonPressed == false)
    // {
    // Hardware.intakePiston.setReverse(true);
    // currentIntakeState = INTAKE.INTAKE_RESTING;
    // }
    // else
    // {
    // Hardware.intakeMotor.set(0.3);
    // currentIntakeState = INTAKE.INTAKE_SPINNING;
    // }
    // return false;
    // case PISTON_UP_INTAKE:
    // Hardware.intakePiston.setReverse(true);
    // currentIntakeState = INTAKE.INTAKE_RESTING;
    // return false;
    // default:
    // System.out.println("!!!!SOMETHING'S BROKEN!!!!");
    // return false;
    // }
    // }

    public static enum EJECT
        {
        EJECTING_RESTING, PISTON_DOWN_EJECTING, INTAKE_SPINNING_UP_BACKWARDS, WHEEL_EJECTING_BALLS, STOP_EJECTING, PISTON_UP_EJECTING, EJECTING_END;
        }

    public static enum INTAKE
        {
        INTAKE_RESTING, PISTON_DOWN_INTAKE, INTAKE_SPINNING_UP, INTAKE_SPINNING, INTAKE_STOPPING, PISTON_UP_INTAKE, INTAKE_END;
        }

    INTAKE currentIntakeState = INTAKE.INTAKE_RESTING;
    public static double possibleIntakeMotorEjectingSpeed = -1.0;
    public static double stillIntakeMotor = 0.0;
    }
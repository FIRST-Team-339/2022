package frc.Utils;

import frc.Hardware.Hardware;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.HardwareInterfaces.KilroyEncoder;

public class Launcher
    {
    public Launcher(MotorControllerGroup launchMotors, KilroyEncoder launchEncoder)
        {
            this.launchMotors = launchMotors;
            this.launchEncoder = launchEncoder;
        }

    public boolean launchAuto()
    {
        switch (this.launchState)
            {
            case SPINNING_UP:
                return false;
            case FIRING:
                return false;
            case RESTING:
                this.launchMotors.set(0.0);
                return true;
            default:
                return false;
            }
    }

    public boolean launchTeleop()
    {
        switch (this.launchState)
            {
            case SPINNING_UP:
                this.launchMotors.set(LAUNCH_MOTOR_SPEED);
                if (this.launchEncoder.getRate() >= TARGET_MOTOR_RPM_PREV_YEAR)
                    {
                    launchState = LAUNCH_STATE.AT_SPEED;
                    }
                return false;
            case AT_SPEED:
                if (this.maintainSpeed() == true)
                    {
                    launchState = LAUNCH_STATE.FIRING;
                    }
                return false;
            case FIRING:
                return false;
            case RESTING:
                this.launchMotors.set(0.0);
                return true;
            default:
                return false;
            }
    }

    private boolean maintainSpeed()
    {
        switch (this.maintainingIterations)
            {
            case (1):
                if (this.launchEncoder.getRate() <= (TARGET_MOTOR_RPM_PREV_YEAR - LAUNCH_DEADBAND))
                    {
                    launchMotorSpeed += CORRECTION_VALUE;
                    return false;
                    }
                if (this.launchEncoder.getRate() >= (TARGET_MOTOR_RPM_PREV_YEAR + LAUNCH_DEADBAND))
                    {
                    launchMotorSpeed -= CORRECTION_VALUE;
                    return false;
                    }
                if (Math.abs(this.launchEncoder.getRate() - TARGET_MOTOR_RPM_PREV_YEAR) <= LAUNCH_DEADBAND)
                    {
                    this.launchMotors.set(this.launchMotorSpeed);
                    this.maintainingIterations++;
                    return false;
                    }
                this.launchMotors.set(this.launchMotorSpeed);
                return false;
            case (2):
                return false;
            case (3):
                return false;
            case (4):
                return false;
            case (5):
                return true;
            default:
                return false;
            }
    }

    public LAUNCH_STATE getState()
    {
        return this.launchState;
    }

    public LAUNCH_STATUS getStatus()
    {
        return this.launchStatus;
    }

    public boolean setState(LAUNCH_STATE state)
    {
        this.launchState = state;
        return true;
    }

    private enum LAUNCH_STATE
        {
        SPINNING_UP, AT_SPEED, FIRING, RESTING;
        }

    public enum LAUNCH_STATUS
        {
        SPINNING_UP, AT_SPEED, FIRING, RESTING;
        }

    // Variables

    private LAUNCH_STATE launchState;

    private LAUNCH_STATUS launchStatus;

    private int maintainingIterations = 1;

    private double launchMotorSpeed;

    private MotorControllerGroup launchMotors;

    private MotorController launchMotorTop;

    private MotorController launchMotorBottom;

    private KilroyEncoder launchEncoder;

    // Constants

    private double TARGET_MOTOR_RPM_PREV_YEAR = 100;

    private double LAUNCH_MOTOR_SPEED = .4;

    private double LAUNCH_DEADBAND = 10;

    private double CORRECTION_VALUE = .05;
    }

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
            this.launchEncoder.setDistancePerPulse(DISTANCE_PER_PULSE_PREV);
            this.launchEncoder.reset();
        }

    public boolean launchGeneral(LAUNCH_TYPE type)
    {
        switch (type)
            {
            case LOW:
                if (launch(LAUNCH_MOTOR_SPEED_LOW_PREV, TARGET_MOTOR_RPM_LOW_PREV) == true)
                    {
                    return true;
                    }
                return false;
            case HIGH:
                if (launch(LAUNCH_MOTOR_SPEED_HIGH_PREV, TARGET_MOTOR_RPM_HIGH_PREV) == true)
                    {
                    return true;
                    }
                return false;
            case AUTO:
                if (launch(LAUNCH_MOTOR_SPEED_AUTO_PREV, TARGET_MOTOR_RPM_AUTO_PREV) == true)
                    {
                    return true;
                    }
                return false;
            case OFF:
                this.launchState = LAUNCH_STATE.SPINNING_UP;
                return false;
            default:
                return false;
            }
    }

    private boolean launch(double motorSpeed, double target)
    {
        System.out.println("Launch state: " + this.launchState);
        switch (this.launchState)
            {
            case SPINNING_UP:
                this.launchMotors.set(motorSpeed);
                // System.out.println("Motor rpm: " + launchEncoder.getRPM());
                if (this.launchEncoder.getRPM() >= target)
                    {
                    this.launchState = LAUNCH_STATE.AT_SPEED;
                    }
                return false;
            case AT_SPEED:
                if (maintainSpeed(motorSpeed, target) == true)
                    {
                    this.launchState = LAUNCH_STATE.FIRING;
                    }
                return false;
            case FIRING:
                // System.out.println("Motor rpm: " + launchEncoder.getRPM());
                System.out.println("Motor speed: " + this.newMotorSpeed);
                return false;
            case RESTING:
                this.launchMotors.set(0.0);
                return true;
            default:
                return false;
            }
    }

    private boolean maintainSpeed(double initalSpeed, double targetRPM)
    {
        System.out.println("Maintaining speed iterations: " + this.maintainingIterations);
        System.out.println("Motor RPM: " + this.launchEncoder.getRPM());
        if (this.newMotorSpeed == 0.0)
            {
            System.out.println("Motor speed: " + initalSpeed);
            }
        if (this.newMotorSpeed != 0.0)
            {
            System.out.println("Motor speed: " + this.newMotorSpeed);
            }
        if (this.maintainingIterations >= MAX_ITERATIONS_PREV)
            {
            this.firstCorrectionInteration = true;
            // this.launchMotors.set(this.newMotorSpeed);
            return true;
            }
        if (Math.abs(this.launchEncoder.getRPM() - targetRPM) <= LAUNCH_DEADBAND_PREV)
            {
            this.maintainingIterations++;
            }
        else
            {
            this.launchMotors.set(correctSpeed(initalSpeed, targetRPM));
            this.maintainingIterations = 0;
            }
        return false;
    }

    private double correctSpeed(double initalSpeed, double target)
    {
        // System.out.println("Correcting speed");
        if (this.firstCorrectionInteration == true)
            {
            this.newMotorSpeed = initalSpeed;
            this.firstCorrectionInteration = false;
            }
        if (Hardware.launchMotorEncoder.getRPM() > target + LAUNCH_DEADBAND_PREV)
            {
            this.newMotorSpeed = this.newMotorSpeed - CORRECTION_VALUE_PREV;
            return this.newMotorSpeed;
            }
        if (Hardware.launchMotorEncoder.getRPM() < target - LAUNCH_DEADBAND_PREV)
            {
            this.newMotorSpeed = this.newMotorSpeed + CORRECTION_VALUE_PREV;
            return this.newMotorSpeed;
            }
        return this.newMotorSpeed;
    }

    private LAUNCH_STATE getState()
    {
        return this.launchState;
    }

    public LAUNCH_STATUS getStatus()
    {
        return this.launchStatus;
    }

    public boolean stopFiring()
    {
        this.launchState = LAUNCH_STATE.RESTING;
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

    public enum LAUNCH_TYPE
        {
        LOW, HIGH, AUTO, TEST, OFF;
        }

    // Variables

    private LAUNCH_STATE launchState = LAUNCH_STATE.SPINNING_UP;

    private LAUNCH_STATUS launchStatus;

    private int maintainingIterations = 0;

    private double launchMotorSpeed;

    private MotorControllerGroup launchMotors;

    private MotorController launchMotorTop;

    private MotorController launchMotorBottom;

    private KilroyEncoder launchEncoder;

    private double initalMotorSpeed;

    private double newMotorSpeed;

    private boolean firstCorrectionInteration = true;

    // Constants

    private double TARGET_MOTOR_RPM_LOW_PREV = 1000.0; // TODO find

    private double TARGET_MOTOR_RPM_HIGH_PREV = 3000.0; // TODO find

    private double TARGET_MOTOR_RPM_AUTO_PREV = 2000.0; // TODO find

    private double LAUNCH_MOTOR_SPEED_LOW_PREV = .2; // TODO find

    private double LAUNCH_MOTOR_SPEED_HIGH_PREV = .4; // TODO find

    private double LAUNCH_MOTOR_SPEED_AUTO_PREV = .35; // TODO find

    private double LAUNCH_DEADBAND_PREV = 50.0; // TODO find

    private double CORRECTION_VALUE_PREV = .02; // TODO find

    private int MAX_ITERATIONS_PREV = 10;

    private double DISTANCE_PER_PULSE_PREV = 1.0;
    }

package frc.Utils;

import frc.Hardware.Hardware;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.HardwareInterfaces.KilroyEncoder;

public class Launcher
    {
    /**
     * Constructor
     * 
     * @param launchMotors
     *            - motor group used for the launch system
     * @param launchEncoder
     *            - encoder used with the launch system
     */
    public Launcher(MotorControllerGroup launchMotors, KilroyEncoder launchEncoder)
        {
            this.launchMotors = launchMotors;
            this.launchEncoder = launchEncoder;
            this.launchEncoder.setDistancePerPulse(DISTANCE_PER_PULSE_PREV);
            this.launchEncoder.reset();
        }

    /**
     * General launch method to select which type of launch
     * 
     * @param type
     *            - the type of launch
     * @return true when done launching
     */
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
                this.launchMotors.set(0.0);
                return false;
            default:
                return false;
            }
    }

    public boolean launchGeneral()
    {
        switch (launchType)
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
                this.launchMotors.set(0.0);
                return false;
            default:
                return false;
            }
    }

    // TODO may need to run through the whole launcher method each time a ball is
    // launched
    /**
     * Launch the ball based on a given speed and target rpm
     * 
     * @param motorSpeed
     *            - the inital speed to send to the launch motors
     * @param target
     *            - the target rpm of the motors
     * @return true when done firing
     */
    private boolean launch(double motorSpeed, double target)
    {
        System.out.println("Launch state: " + this.launchState);
        switch (this.launchState)
            {
            case RESTING:
                launchMotors.set(0.0);
                if (this.doneResting == true)
                    {
                    this.launchState = LAUNCH_STATE.SPINNING_UP;
                    }
                return false;
            case SPINNING_UP:
                this.launchMotors.set(motorSpeed);
                System.out.println("Motor rpm: " + launchEncoder.getRPM());
                // Checks when the motor RPM exceedes the target
                if (this.launchEncoder.getRPM() >= target && this.doneSpinning == true)
                    {
                    this.launchStatus = LAUNCH_STATUS.AT_SPEED;
                    this.launchState = LAUNCH_STATE.AT_SPEED;
                    }
                return false;
            case AT_SPEED:
                // Checks if the motor voltage is correct
                if (maintainSpeed(motorSpeed, target) == true && this.doneCheckingSpeed == true)
                    {
                    this.launchStatus = LAUNCH_STATUS.FIRING;
                    this.launchState = LAUNCH_STATE.FIRING;
                    }
                return false;
            case FIRING:
                System.out.println("Motor rpm: " + launchEncoder.getRPM());
                System.out.println("Motor speed: " + this.newMotorSpeed);
                this.maintainingIterations = 0;
                if (this.doneFiring == true)
                    {
                    this.launchState = LAUNCH_STATE.RESTING;
                    this.launchStatus = LAUNCH_STATUS.RESTING;
                    return true;
                    }
                return false;
            default:
                return false;
            }
    }

    /**
     * Checks if the launch motors remain within an rpm deadband
     * 
     * @param initalSpeed
     *            - the inital speed of the motors
     * @param targetRPM
     *            - the target rpm of the motors
     * @return true when the motors have been running within the deadband for a
     *         certain amount of time
     */
    private boolean maintainSpeed(double initalSpeed, double targetRPM)
    {
        // System.out.println("Maintaining speed iterations: " +
        // this.maintainingIterations);
        // System.out.println("Motor RPM: " + this.launchEncoder.getRPM());
        if (this.newMotorSpeed == 0.0)
            {
            // System.out.println("Motor speed: " + initalSpeed);
            }
        if (this.newMotorSpeed != 0.0)
            {
            // System.out.println("Motor speed: " + this.newMotorSpeed);
            }
        // Checks if the motors have been within the deadband for a satisfactory amount
        // of time
        if (this.maintainingIterations >= MAX_ITERATIONS_PREV)
            {
            this.firstCorrectionInteration = true;
            // this.launchMotors.set(this.newMotorSpeed);
            return true;
            }
        // If the delta of the actual and target rpm is less than the
        // deadband, increase the counter that determines when the method can be exited
        if (Math.abs(this.launchEncoder.getRPM() - targetRPM) <= LAUNCH_DEADBAND_PREV)
            {
            this.maintainingIterations++;
            }
        // If the delta of the actual and target rpm is not less than the
        // deadband, change the motor voltage
        else
            {
            this.launchMotors.set(correctSpeed(initalSpeed, targetRPM));
            this.maintainingIterations = 0;
            }
        return false;
    }

    /**
     * Corrects the voltage being sent to the motors to be within the rpm deadband
     * 
     * @param initalSpeed
     *            - the intial speed of the motors that needs to be corrected
     * @param target
     *            - the target rpm of the motors
     * @return the corrected motor speed
     */
    private double correctSpeed(double initalSpeed, double target)
    {
        // System.out.println("Correcting speed");
        // If this is the first time calling the function, set a local variable to the
        // inital speed passed in
        if (this.firstCorrectionInteration == true)
            {
            this.newMotorSpeed = initalSpeed;
            this.firstCorrectionInteration = false;
            }
        // If the actual rpm is outside of the acceptable range in the positive
        // direction, reduce the voltage by a
        // correction value
        if (Hardware.launchMotorEncoder.getRPM() > target + LAUNCH_DEADBAND_PREV)
            {
            this.newMotorSpeed = this.newMotorSpeed - CORRECTION_VALUE_PREV;
            return this.newMotorSpeed;
            }
        // If the actual rpm is outside of the acceptable range in the negative
        // direction, increase the voltage by a
        // correction value
        if (Hardware.launchMotorEncoder.getRPM() < target - LAUNCH_DEADBAND_PREV)
            {
            this.newMotorSpeed = this.newMotorSpeed + CORRECTION_VALUE_PREV;
            return this.newMotorSpeed;
            }
        return this.newMotorSpeed;
    }

    /**
     * @return the launch state
     */
    private LAUNCH_STATE getState()
    {
        return this.launchState;
    }

    /**
     * @return the launch status
     */
    public LAUNCH_STATUS getStatus()
    {
        return this.launchStatus;
    }

    public boolean setDoneStates(boolean resting, boolean spinning, boolean atSpeed, boolean firing)
    {
        this.doneSpinning = spinning;
        this.doneCheckingSpeed = atSpeed;
        this.doneFiring = firing;
        this.doneResting = resting;
        return true;
    }

    public boolean stopFiring()
    {
        this.doneFiring = true;
        return true;
    }

    public boolean setDoneFiring(boolean firing)
    {
        this.doneFiring = firing;
        return true;
    }

    public boolean setResting(boolean resting)
    {
        this.doneResting = resting;
        return true;
    }

    public boolean disallowLaunching()
    {
        this.doneResting = false;
        return true;
    }

    public boolean setLaunchType(LAUNCH_TYPE type)
    {
        this.launchType = type;
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

    private LAUNCH_STATE launchState = LAUNCH_STATE.RESTING;

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

    private boolean doneSpinning = true;

    private boolean doneCheckingSpeed = true;

    private boolean doneFiring = false;

    private boolean doneResting = false;

    private LAUNCH_TYPE launchType;

    // Constants

    private double TARGET_MOTOR_RPM_LOW_PREV = 1000.0; // TODO find

    private double TARGET_MOTOR_RPM_HIGH_PREV = 3000.0; // TODO find

    private double TARGET_MOTOR_RPM_AUTO_PREV = 2000.0; // TODO find

    private double LAUNCH_MOTOR_SPEED_LOW_PREV = .21; // TODO find

    private double LAUNCH_MOTOR_SPEED_HIGH_PREV = .6; // TODO find

    private double LAUNCH_MOTOR_SPEED_AUTO_PREV = .35; // TODO find

    private double LAUNCH_DEADBAND_PREV = 50.0; // TODO find

    private double CORRECTION_VALUE_PREV = .0005; // TODO find

    private int MAX_ITERATIONS_PREV = 10;

    private double DISTANCE_PER_PULSE_PREV = 1.0;
    }

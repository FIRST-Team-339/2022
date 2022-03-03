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
     * Launch for auto dependent on type
     * 
     * @param type
     *            - the type of launch
     * @return true when done launching
     */
    public boolean launchAutoGeneral(LAUNCH_TYPE type)
    {
        switch (type)
            {
            // Case for shooting the low goal from in front of the wall
            case LOW:
                if (launchAuto(LAUNCH_MOTOR_SPEED_LOW_PREV, TARGET_MOTOR_RPM_LOW_PREV) == true)
                    {
                    return true;
                    }
                return false;
            // Case for shooting to the high goal
            case HIGH:
                if (launchAuto(LAUNCH_MOTOR_SPEED_HIGH_PREV, TARGET_MOTOR_RPM_HIGH_PREV) == true)
                    {
                    return true;
                    }
                return false;
            // Case for shooting in the low goal from the starting position
            case AUTO:
                if (launchAuto(LAUNCH_MOTOR_SPEED_AUTO_PREV, TARGET_MOTOR_RPM_AUTO_PREV) == true)
                    {
                    return true;
                    }
                return false;
            // Turns off the launcher
            case OFF:
                this.launchStatusAuto = LAUNCH_STATUS_AUTO.RESTING;
                this.launchStateAuto = LAUNCH_STATE_AUTO.RESTING;
                this.launchMotors.set(0.0);
                return false;
            default:
                return false;
            }
    }

    /**
     * Overload method uses the launcher for use in autonomous with an externally
     * set type through setLaunchType().
     * 
     * @return true when done launching
     */
    public boolean launchAutoGeneral()
    {
        switch (launchType)
            {
            // Case for shooting in the low goal from the wall
            case LOW:
                if (launchAuto(LAUNCH_MOTOR_SPEED_LOW_PREV, TARGET_MOTOR_RPM_LOW_PREV) == true)
                    {
                    return true;
                    }
                return false;
            // Case for shooting in the high goal
            case HIGH:
                if (launchAuto(LAUNCH_MOTOR_SPEED_HIGH_PREV, TARGET_MOTOR_RPM_HIGH_PREV) == true)
                    {
                    return true;
                    }
                return false;
            // Case for shooting from the starting position
            case AUTO:
                if (launchAuto(LAUNCH_MOTOR_SPEED_AUTO_PREV, TARGET_MOTOR_RPM_AUTO_PREV) == true)
                    {
                    return true;
                    }
                return false;
            // Case for turning off the launcher
            case OFF:
                this.launchStatusAuto = LAUNCH_STATUS_AUTO.RESTING;
                this.launchStateAuto = LAUNCH_STATE_AUTO.RESTING;
                this.launchMotors.set(0.0);
                return false;
            default:
                return false;
            }
    }

    // TODO may need to run through the whole launcher method each time a ball is
    // launched
    /**
     * Launch the ball based on a given speed and target rpm for use in autonomous
     * 
     * @param motorSpeed
     *            - the inital speed to send to the launch motors
     * @param target
     *            - the target rpm of the motors
     * @return true when done firing
     */
    private boolean launchAuto(double motorSpeed, double target)
    {
        // System.out.println("Launch state: " + this.launchState);
        switch (this.launchStateAuto)
            {
            // State where the launcher is off
            case RESTING:
                launchMotors.set(0.0);
                if (this.doneResting == true)
                    {
                    this.launchStatusAuto = LAUNCH_STATUS_AUTO.SPINNING_UP;
                    this.launchStateAuto = LAUNCH_STATE_AUTO.SPINNING_UP;
                    }
                return false;
            // State when the launch motors are running at the inital speed until they reach
            // the target speed
            case SPINNING_UP:
                this.launchMotors.set(motorSpeed);
                // System.out.println("Motor rpm: " + launchEncoder.getRPM());
                // Checks when the motor RPM exceedes the target
                if (this.launchEncoder.getRPM() >= target && this.doneSpinning == true)
                    {
                    this.launchStatusAuto = LAUNCH_STATUS_AUTO.AT_SPEED;
                    this.launchStateAuto = LAUNCH_STATE_AUTO.AT_SPEED;
                    }
                return false;
            // Checks that the rpm of the launch wheels is consistently within the deadband
            case AT_SPEED:
                // Checks if the motor voltage is correct
                if (maintainSpeed(motorSpeed, target) == true && this.doneCheckingSpeed == true)
                    {
                    this.launchStatusAuto = LAUNCH_STATUS_AUTO.READY_TO_FIRE;
                    this.launchStateAuto = LAUNCH_STATE_AUTO.READY_TO_FIRE;
                    }
                return false;
            // State where the launcher is at speed and ready to fire
            case READY_TO_FIRE:
                // System.out.println("Motor rpm: " + launchEncoder.getRPM());
                // System.out.println("Motor speed: " + this.newMotorSpeed);
                this.maintainingIterations = 0;
                if (this.doneFiring == true)
                    {
                    this.launchStateAuto = LAUNCH_STATE_AUTO.RESTING;
                    this.launchStatusAuto = LAUNCH_STATUS_AUTO.RESTING;
                    return true;
                    }
                return false;
            default:
                return false;
            }
    }

    /**
     * Method to use the launcher in teleop. Does not automatically change states
     * 
     * @param state
     *            - which state the launcher is in
     * @param type
     *            - The type of launch to use
     * @return the status of the launcher
     */
    public LAUNCH_STATUS_TELEOP launchTeleopGeneral(LAUNCH_STATE_TELEOP state, LAUNCH_TYPE type)
    {
        switch (type)
            {
            // Case for launching from in front of the wall in teleop
            case LOW:
                this.launchTeleop(state, LAUNCH_MOTOR_SPEED_LOW_PREV, TARGET_MOTOR_RPM_LOW_PREV);
                return this.launchStatusTeleop;
            // Case for scoring in the high goal in teleop
            case HIGH:
                this.launchTeleop(state, LAUNCH_MOTOR_SPEED_HIGH_PREV, TARGET_MOTOR_RPM_HIGH_PREV);
                return this.launchStatusTeleop;
            default:
                return this.launchStatusTeleop;
            }
    }

    /**
     * Launches the ball for use in teleop
     * 
     * @param state
     *            - the state the launcher should be in
     * @param initalSpeed
     *            - the inital speed to use for the motors
     * @param targetRPM
     *            - the target rpm that the motors should reach
     */
    private void launchTeleop(LAUNCH_STATE_TELEOP state, double initalSpeed, double targetRPM)
    {
        // System.out.println("Launch teleop state: " + state);
        switch (state)
            {
            // The launch motors are off
            case RESTING:
                launchMotors.set(0.0);
                this.launchStatusAuto = LAUNCH_STATUS_AUTO.RESTING;
                break;
            // The motors are supplied the inital voltage and waits until the rpm of the
            // wheels reaches the target
            case SPINNING_UP:
                this.launchMotors.set(initalSpeed);
                if (this.launchEncoder.getRPM() >= targetRPM)
                    {
                    this.launchStatusTeleop = LAUNCH_STATUS_TELEOP.DONE_SPINNING_UP;
                    }
                else
                    {
                    this.launchStatusTeleop = LAUNCH_STATUS_TELEOP.SPINNING_UP;
                    }
                // System.out.println("Motor rpm: " + launchEncoder.getRPM());
                break;
            // Ensures that the launch wheels spin consistently within the deadband
            case AT_SPEED:
                // Checks if the motor voltage is correct
                // TODO this may create a problem if you attempt to verify voltage twice without
                // changing the status
                if (maintainSpeed(initalSpeed, targetRPM) == true
                        || this.launchStatusTeleop == LAUNCH_STATUS_TELEOP.DONE_CHECKING_SPEED)
                    {
                    this.maintainingIterations = 0;
                    this.launchStatusTeleop = LAUNCH_STATUS_TELEOP.DONE_CHECKING_SPEED;
                    }
                else
                    {
                    this.launchStatusTeleop = LAUNCH_STATUS_TELEOP.CHECKING_SPEED;
                    }
                break;
            // // Returns that the launcher is firing after ensuring that the rpm is correct
            // case READY_TO_FIRE:
            // // System.out.println("Motor rpm: " + launchEncoder.getRPM());
            // // System.out.println("Motor speed: " + this.newMotorSpeed);
            // this.maintainingIterations = 0;
            // this.launchStatusTeleop = LAUNCH_STATUS_TELEOP.FIRING;
            // break;
            default:
                break;
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
        if (this.maintainingIterations >= TARGET_ITERATIONS_PREV)
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
            return false;
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
        if (Hardware.launchMotorEncoder.getRPM() >= (target + LAUNCH_DEADBAND_PREV))
            {
            this.newMotorSpeed = this.newMotorSpeed - CORRECTION_VALUE_PREV;
            return this.newMotorSpeed;
            }
        // If the actual rpm is outside of the acceptable range in the negative
        // direction, increase the voltage by a
        // correction value
        if (Hardware.launchMotorEncoder.getRPM() <= (target - LAUNCH_DEADBAND_PREV))
            {
            this.newMotorSpeed = this.newMotorSpeed + CORRECTION_VALUE_PREV;
            return this.newMotorSpeed;
            }
        return this.newMotorSpeed;
    }

    /**
     * @return the launch state
     */
    private LAUNCH_STATE_AUTO getStateAuto()
    {
        return this.launchStateAuto;
    }

    /**
     * @return the launch status
     */
    public LAUNCH_STATUS_AUTO getStatusAuto()
    {
        return this.launchStatusAuto;
    }

    /**
     * @return the status of the launcher in teleop
     */
    public LAUNCH_STATUS_TELEOP getStatusTeleop()
    {
        return this.launchStatusTeleop;
    }

    /**
     * Sets the status of the launcher in teleop
     * 
     * @param status
     *            - the desired status for the launcher to be in
     * @return true when completed
     */
    public boolean setTeleopLaunchStatus(LAUNCH_STATUS_TELEOP status)
    {
        this.launchStatusTeleop = status;
        return true;
    }

    /**
     * Sets the flags to allow the launcher to continue on to the next state. True
     * will allow the launcher to move from the current state to the next state and
     * false will maintain the current state
     * 
     * @param resting
     *            - set the doneResting flag to true or false
     * @param spinning
     *            - set the doneSpinning flag to true or false
     * @param atSpeed
     *            - set the checkingSpeed flag to true or false
     * @param firing
     *            - set the doneFiring flag to true or false
     * @return true when the process is finished
     */
    public boolean setDoneStates(boolean resting, boolean spinning, boolean atSpeed, boolean firing)
    {
        this.doneSpinning = spinning;
        this.doneCheckingSpeed = atSpeed;
        this.doneFiring = firing;
        this.doneResting = resting;
        return true;
    }

    /**
     * Sets the doneFiring flag to true to stop the launcher from firing
     * 
     * @return true when the process is finished
     */
    public boolean stopFiring()
    {
        this.doneFiring = true;
        return true;
    }

    /**
     * Sets the doneFiring flag to true or false. False will cause the launcher to
     * remain in the firing state until the flag becomes true
     * 
     * @param firing
     *            - set the doneFiring flag to true or false
     * @return true when the process is finished
     */
    public boolean setDoneFiring(boolean firing)
    {
        this.doneFiring = firing;
        return true;
    }

    /**
     * Sets the doneResting state to true or false. False will stop the launcher
     * from firing until the flag is set to true.
     * 
     * @param resting
     *            - sets the doneResting flag to true or false
     * @return true when the process is finished
     */
    public boolean setResting(boolean resting)
    {
        this.doneResting = resting;
        return true;
    }

    /**
     * Stops the launcher from firing by setting the doneResting flag to false
     * 
     * @return true when the process is finished
     */
    public boolean disallowLaunching()
    {
        this.doneResting = false;
        return true;
    }

    /**
     * Sets which type of launch to run
     * 
     * @param type
     *            - desired launch type
     * @return true when the process is finished
     */
    public boolean setLaunchType(LAUNCH_TYPE type)
    {
        this.launchType = type;
        return true;
    }

    private enum LAUNCH_STATE_AUTO
        {
        SPINNING_UP, AT_SPEED, READY_TO_FIRE, RESTING;
        }

    public enum LAUNCH_STATE_TELEOP
        {
        RESTING, SPINNING_UP, AT_SPEED, READY_TO_FIRE;
        }

    public enum LAUNCH_STATUS_AUTO
        {
        SPINNING_UP, AT_SPEED, READY_TO_FIRE, RESTING;
        }

    public enum LAUNCH_STATUS_TELEOP
        {
        RESTING, SPINNING_UP, DONE_SPINNING_UP, CHECKING_SPEED, DONE_CHECKING_SPEED, FIRING;
        }

    public enum LAUNCH_TYPE
        {
        LOW, HIGH, AUTO, TEST, OFF;
        }

    // Variables

    private LAUNCH_STATE_AUTO launchStateAuto = LAUNCH_STATE_AUTO.RESTING;

    private LAUNCH_STATE_TELEOP launchStateTeleop = LAUNCH_STATE_TELEOP.RESTING;

    private LAUNCH_STATUS_AUTO launchStatusAuto;

    private LAUNCH_STATUS_TELEOP launchStatusTeleop;

    private int maintainingIterations = 0;

    private double launchMotorSpeed;

    private MotorControllerGroup launchMotors;

    private MotorController launchMotorTop;

    private MotorController launchMotorBottom;

    private KilroyEncoder launchEncoder;

    private double initalMotorSpeed;

    private double targetMotorRPM;

    private double newMotorSpeed;

    private boolean firstCorrectionInteration = true;

    private boolean doneSpinning = true;

    private boolean doneCheckingSpeed = true;

    private boolean doneFiring = false;

    private boolean doneResting = false;

    private LAUNCH_TYPE launchType;

    // Constants

    private final double TARGET_MOTOR_RPM_LOW_PREV = 1000.0; // TODO find

    private final double TARGET_MOTOR_RPM_LOW_CURRENT = 1000.0; // TODO

    private final double TARGET_MOTOR_RPM_HIGH_PREV = 3000.0; // TODO find

    private final double TARGET_MOTOR_RPM_HIGH_CURRENT = 3000.0; // TODO

    private final double TARGET_MOTOR_RPM_AUTO_PREV = 2000.0; // TODO find

    private final double TARGET_MOTOR_RPM_AUTO_CURRENT = 2000.0; // TODO

    private final double LAUNCH_MOTOR_SPEED_LOW_PREV = .21; // TODO find

    private final double LAUNCH_MOTOR_SPEED_LOW_CURRENT = .21; // TODO

    private final double LAUNCH_MOTOR_SPEED_HIGH_PREV = .6; // TODO find

    private final double LAUNCH_MOTOR_SPEED_HIGH_CURRENT = .6; // TODO

    private final double LAUNCH_MOTOR_SPEED_AUTO_PREV = .4; // TODO find

    private final double LAUNCH_MOTOR_SPEED_AUTO_CURRENT = .4; // TODO

    private final double LAUNCH_DEADBAND_PREV = 50.0; // TODO find

    private final double LAUNCH_DEADBAND_CURRENT = 50.0; // TODO

    private final double CORRECTION_VALUE_PREV = .0005; // TODO find

    private final double CORRECTION_VALUE_CURRENT = .0005; // TODO

    private final int TARGET_ITERATIONS_PREV = 10;

    private final double DISTANCE_PER_PULSE_PREV = 1.0;
    }

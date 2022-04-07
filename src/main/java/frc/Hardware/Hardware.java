// ===================================================================
// FILE NAME: Hardware.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 2, 2011
// CREATED BY: Bob Brown
// MODIFIED ON: June 24, 2019
// MODIFIED BY: Ryan McGee
// ABSTRACT:
// This file contains all of the global definitions for the
// hardware objects in the system
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package frc.Hardware;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import frc.HardwareInterfaces.BallHandler;
import frc.HardwareInterfaces.DoubleSolenoid;
import frc.HardwareInterfaces.DoubleThrowSwitch;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.KilroyServo;
import frc.HardwareInterfaces.KilroyUSBCamera;
import frc.HardwareInterfaces.LightSensor;
import frc.HardwareInterfaces.MomentarySwitch;
import frc.HardwareInterfaces.Potentiometer;
import frc.HardwareInterfaces.SingleThrowSwitch;
import frc.HardwareInterfaces.SixPositionSwitch;
import frc.HardwareInterfaces.UltraSonic;
import frc.HardwareInterfaces.Transmission.TankTransmission;
import frc.Utils.drive.Drive;
import frc.Utils.Launcher;
import frc.Utils.BallCounter;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * ------------------------------------------------------- puts all of the
 * hardware declarations into one place. In addition, it makes them available to
 * both autonomous and teleop.
 *
 * @class HardwareDeclarations
 * @author Bob Brown
 *
 * @written Jan 2, 2011 -------------------------------------------------------
 */

public class Hardware
    {

    public static enum yearIdentifier
        {
        CurrentYear("2022"), PrevYear("2020"), TestBoard("Test");

        private final String name;

        private yearIdentifier(String s)
            {
                this.name = s;
            }

        public boolean equalsName(String otherName)
        {
            // (otherName == null) check is not needed because name.equals(null)
            // returns
            // false
            return name.equals(otherName);
        }

        public String toString()
        {
            return this.name;
        }
        };

    public static yearIdentifier robotIdentity = yearIdentifier.CurrentYear;

    /**********************************************
     * generalInit() function initializes all Hardware items that REQUIRE
     * initialization.
     *
     * @author R. Brown
     * @date 12/5/2021
     ***********************************************/
    public static void generalInit()
    {

        // **********************************************************
        // ANALOG I/O
        // **********************************************************

        // Right operator throttle configuration
        // Sets the throttle to get from the second channel on the operator joysticks
        // because the throttle is on the second channel because of the lack of Z
        // movement on the operator joysticks
        Hardware.rightOperator.setThrottleChannel(2);

        // **********************************************************
        // PNEUMATIC DEVICES
        // **********************************************************

    } // end of generalInit()

    /**********************************************
     * initialize() function initializes all Hardware items that REQUIRE
     * initialization. It calls a function for either this year or the previous year
     *
     * @author R. Brown
     * @date 12/5/2021
     ***********************************************/
    public static void initialize()
    {
        generalInit();
        if (robotIdentity.equals(Hardware.yearIdentifier.CurrentYear))
            {
            initializeCurrentYear();
            }
        else
            {
            initializePrevYear();
            }
    } // end of initialize()

    /**********************************************
     * initializeCurrentYear() function initializes all Hardware items that are
     * REQUIRED for this year
     *
     * @author R. Brown
     * @date 12/5/2021
     *********************************************/
    public static void initializeCurrentYear() // 2022
    {

        // ==============CAN INIT=============
        // Motor Controllers
        // ====================================
        leftTopMotor = new WPI_TalonFX(9);
        leftTopMotor.setInverted(false);
        rightBottomMotor = new WPI_TalonFX(16);
        rightBottomMotor.setInverted(true);

        launchMotorFront = new WPI_TalonFX(7);
        launchMotorBack = new WPI_TalonFX(5);

        launchMotorGroup = new MotorControllerGroup(launchMotorFront, launchMotorBack);

        conveyorMotorFront = new WPI_TalonSRX(26);
        conveyorMotorFront.setInverted(false);
        conveyorMotorBack = new WPI_TalonSRX(22);
        conveyorMotorBack.setInverted(true);

        conveyorGroup = new MotorControllerGroup(conveyorMotorFront, conveyorMotorBack);

        leftBottomMotor = new WPI_TalonFX(8);
        leftBottomMotor.setInverted(false);
        rightTopMotor = new WPI_TalonFX(19);
        rightTopMotor.setInverted(true);

        leftClimbMotor = new WPI_TalonSRX(25);
        rightClimbMotor = new WPI_TalonSRX(24);

        intakeMotor = new WPI_TalonSRX(23);
        intakeMotor.setInverted(true);

        // ------------------------------------
        // configure climb encoders
        // ------------------------------------
        climbEncoder = new KilroyEncoder((WPI_TalonSRX) leftClimbMotor);
        climbEncoder.setDistancePerPulse(CURRENT_YEAR_CLIMB_DISTANCE_PER_TICK);
        climbEncoder.setReverseDirection(true);
        climbEncoder.reset();

        // -----------------------------------
        // initialize the drive speed controllers and servo
        // -----------------------------------
        leftDriveGroup = new MotorControllerGroup(leftBottomMotor, leftTopMotor);
        rightDriveGroup = new MotorControllerGroup(rightBottomMotor, rightTopMotor);
        climbServo = new KilroyServo(CURRENT_YEAR_CLIMB_SERVO_PWM_PORT, CLIMB_SERVO_MAX_DEGREES);
        // climbServo.set(value);

        // -----------------------------------
        // initalize the climb controller groups
        // -----------------------------------
        climbGroup = new MotorControllerGroup(leftClimbMotor, rightClimbMotor);

        // ==============================
        // CLIMB CONSTS
        // ==============================
        CLIMB_SERVO_POS_OUT = 1.0;
        CLIMB_SERVO_POS_IN = 0.0;
        CLIMB_ENCODER_MAX_HEIGHT = 20.0;
        CLIMB_SERVO_MAX_DEGREES = 360.0;

        LEFT_CLIMB_ENCODER_SPEED = 0.2725;
        RIGHT_CLIMB_ENCODER_SPEED = 0.3;
        BOTH_CLIMB_ENCODER_SPEED = 0.3;

        // -----------------------------------
        // configure the drive system encoders
        // -----------------------------------
        leftDriveTopEncoder = new KilroyEncoder((WPI_TalonFX) leftTopMotor);
        leftDriveTopEncoder.setDistancePerPulse(CURRENT_YEAR_DISTANCE_PER_TICK);
        leftDriveTopEncoder.setReverseDirection(true);
        leftDriveBottomEncoder = new KilroyEncoder((WPI_TalonFX) leftBottomMotor);
        leftDriveBottomEncoder.setDistancePerPulse(CURRENT_YEAR_DISTANCE_PER_TICK);
        leftDriveBottomEncoder.setReverseDirection(true);

        rightDriveTopEncoder = new KilroyEncoder((WPI_TalonFX) rightTopMotor);
        rightDriveTopEncoder.setDistancePerPulse(CURRENT_YEAR_DISTANCE_PER_TICK);
        rightDriveTopEncoder.setReverseDirection(true);
        rightDriveBottomEncoder = new KilroyEncoder((WPI_TalonFX) rightBottomMotor);
        rightDriveBottomEncoder.setDistancePerPulse(CURRENT_YEAR_DISTANCE_PER_TICK);
        rightDriveBottomEncoder.setReverseDirection(true);
        rightDriveBottomEncoder.getRPM();

        // -----------------------------------
        // Configure launch encoders
        // -----------------------------------
        launchMotorEncoder = new KilroyEncoder((WPI_TalonFX) launchMotorFront);
        launchMotorEncoder.setDistancePerPulse(LAUNCHER_DISTANCE_PER_PULSE_CURR);

        // ------------------------------------
        // configure climb encoders
        // ------------------------------------
        climbEncoder = new KilroyEncoder((WPI_TalonSRX) leftClimbMotor);
        climbEncoder.setDistancePerPulse(CURRENT_YEAR_CLIMB_DISTANCE_PER_TICK);
        climbEncoder.setReverseDirection(true);
        climbEncoder.reset();

        // ------------------------------------
        // Drive System
        // ------------------------------------
        tankTransmission = new TankTransmission(leftDriveGroup, rightDriveGroup);
        drive = new Drive(tankTransmission, leftDriveBottomEncoder, rightDriveBottomEncoder, gyro);

        gyro.calibrate();

        // ------------------------------------
        // Pnuematics
        // ------------------------------------

        intakePiston = new DoubleSolenoid(4, 5);
        intakePiston.setReverse(true);

        ballPickup1 = new LightSensor(21, false);
        ballPickup2 = new LightSensor(22, false);
        ballPickup3 = new LightSensor(24, false);
        ballPickup4 = new LightSensor(25, false);
        // --------------------------------------
        // Launch system
        // --------------------------------------
        launcher = new Launcher(launchMotorGroup, launchMotorEncoder, yearIdentifier.CurrentYear);

    } // end of initializeCurrentYear()

    /**********************************************
     * initializePrevYear() function initializes all Hardware items that are
     * REQUIRED for the previous year
     *
     * @author R. Brown
     * @date 1/25/2020
     *********************************************/
    public static void initializePrevYear() // 2020
    {
        // ==============CAN INIT=============
        // Motor Controllers
        // ====================================
        leftTopMotor = new WPI_TalonFX(6);
        leftTopMotor.setInverted(false);
        rightBottomMotor = new WPI_TalonFX(12);
        rightBottomMotor.setInverted(true);

        launchMotorFront = new WPI_TalonFX(17);
        launchMotorBack = new WPI_TalonFX(18);

        launchMotorGroup = new MotorControllerGroup(launchMotorFront, launchMotorBack);

        conveyorMotorFront = new WPI_TalonSRX(21);
        conveyorMotorFront.setInverted(false);
        conveyorMotorBack = new WPI_TalonSRX(22);
        conveyorMotorBack.setInverted(true);

        conveyorGroup = new MotorControllerGroup(conveyorMotorFront, conveyorMotorBack);

        leftBottomMotor = new WPI_TalonFX(15);
        leftBottomMotor.setInverted(false);
        rightTopMotor = new WPI_TalonFX(14);
        rightTopMotor.setInverted(true);

        leftClimbMotor = new WPI_TalonSRX(10);
        rightClimbMotor = new WPI_TalonSRX(24);

        intakeMotor = new WPI_TalonSRX(23);
        intakeMotor.setInverted(true);

        // -----------------------------------
        // initialize the drive speed controllers and servo
        // -----------------------------------
        leftDriveGroup = new MotorControllerGroup(leftBottomMotor, leftTopMotor);
        rightDriveGroup = new MotorControllerGroup(rightBottomMotor, rightTopMotor);
        climbServo = new KilroyServo(PREV_YEAR_CLIMB_SERVO_PWM_PORT, CLIMB_SERVO_MAX_DEGREES);
        // climbServo.set(value);

        // -----------------------------------
        // initalize the climb controller groups
        // -----------------------------------
        climbGroup = new MotorControllerGroup(leftClimbMotor, rightClimbMotor);

        // ===========================
        // CLIMB CONSTS
        // ===========================
        CLIMB_SERVO_POS_OUT = 1.0;
        CLIMB_SERVO_POS_IN = 0.0;
        CLIMB_ENCODER_MAX_HEIGHT = 20.0;
        CLIMB_SERVO_MAX_DEGREES = 360.0;

        LEFT_CLIMB_ENCODER_SPEED = 0.2725;
        RIGHT_CLIMB_ENCODER_SPEED = 0.3;
        BOTH_CLIMB_ENCODER_SPEED = 0.3;

        // -----------------------------------
        // configure the drive system encoders
        // -----------------------------------
        leftDriveTopEncoder = new KilroyEncoder((WPI_TalonFX) leftTopMotor);
        leftDriveTopEncoder.setDistancePerPulse(PREV_YEAR_DISTANCE_PER_TICK);
        leftDriveTopEncoder.setReverseDirection(true);
        leftDriveBottomEncoder = new KilroyEncoder((WPI_TalonFX) leftBottomMotor);
        leftDriveBottomEncoder.setDistancePerPulse(PREV_YEAR_DISTANCE_PER_TICK);
        leftDriveBottomEncoder.setReverseDirection(true);

        rightDriveTopEncoder = new KilroyEncoder((WPI_TalonFX) rightTopMotor);
        rightDriveTopEncoder.setDistancePerPulse(PREV_YEAR_DISTANCE_PER_TICK);
        rightDriveTopEncoder.setReverseDirection(true);
        rightDriveBottomEncoder = new KilroyEncoder((WPI_TalonFX) rightBottomMotor);
        rightDriveBottomEncoder.setDistancePerPulse(PREV_YEAR_DISTANCE_PER_TICK);
        rightDriveBottomEncoder.setReverseDirection(true);

        // -----------------------------------
        // Configure launch encoders
        // -----------------------------------
        launchMotorEncoder = new KilroyEncoder((WPI_TalonFX) launchMotorFront);
        launchMotorEncoder.setDistancePerPulse(LAUNCHER_DISTANCE_PER_PULSE_PREV);

        ballPickup1 = new LightSensor(21);
        ballPickup2 = new LightSensor(22, true);
        ballPickup3 = new LightSensor(23);
        ballPickup4 = new LightSensor(24);
        // ------------------------------------
        // configure climb encoders
        // ------------------------------------
        climbEncoder = new KilroyEncoder((WPI_TalonSRX) leftClimbMotor);
        climbEncoder.setDistancePerPulse(PREV_YEAR_CLIMB_DISTANCE_PER_TICK);
        climbEncoder.setReverseDirection(true);
        climbEncoder.reset();
        // ------------------------------------
        // Drive System
        // ------------------------------------
        tankTransmission = new TankTransmission(leftDriveGroup, rightDriveGroup);
        drive = new Drive(tankTransmission, leftDriveBottomEncoder, rightDriveBottomEncoder, gyro);

        gyro.calibrate();

        // ------------------------------------
        // Pnuematics
        // ------------------------------------
        intakePiston = new DoubleSolenoid(5, 4);
        intakePiston.setReverse(true);

        // --------------------------------------
        // Launch system
        // --------------------------------------
        launcher = new Launcher(launchMotorGroup, launchMotorEncoder, yearIdentifier.PrevYear);

    } // end of initializePrevYear()

    // **********************************************************
    // CAN DEVICES
    // **********************************************************
    public static MotorController leftBottomMotor = null;
    public static MotorController rightTopMotor = null;
    public static MotorController leftTopMotor = null;
    public static MotorController rightBottomMotor = null;

    public static MotorController launchMotorFront = null;
    public static MotorController launchMotorBack = null;

    public static MotorControllerGroup launchMotorGroup = null;

    public static Launcher launcher = null;

    public static KilroyEncoder launchMotorEncoder = null;

    public static MotorControllerGroup leftDriveGroup = null;
    public static MotorControllerGroup rightDriveGroup = null;

    public static KilroyEncoder leftDriveTopEncoder = null;
    public static KilroyEncoder leftDriveBottomEncoder = null;
    public static KilroyEncoder rightDriveTopEncoder = null;
    public static KilroyEncoder rightDriveBottomEncoder = null;

    public static MotorController conveyorMotorFront = null;
    public static MotorController conveyorMotorBack = null;

    public static MotorControllerGroup conveyorGroup = null;

    public static MotorController rightClimbMotor = null;
    public static MotorController leftClimbMotor = null;

    public static MotorController intakeMotor = null;

    public static MotorControllerGroup climbGroup = null;

    public static KilroyServo climbServo = null;

    public static KilroyEncoder climbEncoder = null;

    public static double PREV_YEAR_CLIMB_DISTANCE_PER_TICK = .004507692;
    public static double CURRENT_YEAR_CLIMB_DISTANCE_PER_TICK = .004507692;

    public static double CLIMB_ENCODER_MAX_HEIGHT = 0.0;

    public static int PREV_YEAR_CLIMB_SERVO_PWM_PORT = 2;
    public static int CURRENT_YEAR_CLIMB_SERVO_PWM_PORT = 2;

    public static double LAUNCHER_DISTANCE_PER_PULSE_PREV = 1.0 / 2048.0;
    public static double LAUNCHER_DISTANCE_PER_PULSE_CURR = 1.0;

    public static double CLIMB_SERVO_MAX_DEGREES = 0.0;
    public static double CLIMB_SERVO_POS_OUT = 0.0;
    public static double CLIMB_SERVO_POS_IN = 0.0;
    public static double LEFT_CLIMB_ENCODER_SPEED = 0.0;
    public static double RIGHT_CLIMB_ENCODER_SPEED = 0.0;
    public static double BOTH_CLIMB_ENCODER_SPEED = 0.0;
    // public static double PREV_YEAR_SERVO_INIT_POS = 0;

    // **********************************************************
    // DIGITAL I/O
    // **********************************************************
    public static SixPositionSwitch autoSixPosSwitch = new SixPositionSwitch(13, 14, 15, 16, 17, 18);

    public static SingleThrowSwitch autoDisableSwitch = new SingleThrowSwitch(10);
    public static SingleThrowSwitch ballCountInitSwitch = new SingleThrowSwitch(4);

    public static SingleThrowSwitch demoSwitch = new SingleThrowSwitch(9);

    public static SingleThrowSwitch spinSwitch = new SingleThrowSwitch(23);
    public static SingleThrowSwitch unknown1Switch = new SingleThrowSwitch(11);
    public static SingleThrowSwitch unknown2Switch = new SingleThrowSwitch(12);
    public static DoubleThrowSwitch unknownSwitch = new DoubleThrowSwitch(unknown1Switch, unknown2Switch);

    public static LightSensor ballPickup1 = null;
    public static LightSensor ballPickup2 = null;
    public static LightSensor ballPickup3 = null;
    public static LightSensor ballPickup4 = null;

    // **********************************************************
    // ANALOG I/O
    // **********************************************************
    public static Potentiometer delayPot = new Potentiometer(2);

    public static UltraSonic ultraSonic = null;

    // **********************************************************
    // PNEUMATIC DEVICES
    // **********************************************************

    public static Compressor compressor = new Compressor(PneumaticsModuleType.CTREPCM);

    public static DoubleSolenoid intakePiston = null;

    // **********************************************************
    // roboRIO CONNECTIONS CLASSES
    // **********************************************************

    public static PowerDistribution pdp = new PowerDistribution();

    // **********************************************************
    // DRIVER STATION CLASSES
    // **********************************************************

    public static DriverStation driverStation = DriverStation.getInstance();

    public static Joystick leftDriver = new Joystick(0);
    public static Joystick rightDriver = new Joystick(1);
    public static Joystick leftOperator = new Joystick(2);
    public static Joystick rightOperator = new Joystick(3);

    public static int invertControllerAxis = -1;

    public static JoystickButton rightOperatorCameraSwitchButton = new JoystickButton(rightOperator, 10);
    public static JoystickButton rightDriverCameraSwitchButton = new JoystickButton(rightDriver, 3);
    public static JoystickButton closeClimbServo = new JoystickButton(leftOperator, 4);
    public static JoystickButton openClimbServo = new JoystickButton(leftOperator, 5);
    public static JoystickButton climbUpButton = new JoystickButton(rightOperator, 3);
    public static JoystickButton climbDownButton = new JoystickButton(rightOperator, 2);
    public static JoystickButton outtakeButton = new JoystickButton(leftOperator, 2);
    public static JoystickButton fireLow = new JoystickButton(rightOperator, 11);
    public static JoystickButton fireHigh = new JoystickButton(rightOperator, 4);
    public static JoystickButton fireOverride = new JoystickButton(leftOperator, 11);
    public static JoystickButton testButton = new JoystickButton(leftDriver, 6);
    public static MomentarySwitch subtractBallButton = new MomentarySwitch(rightOperator, 8, false);
    public static MomentarySwitch addBallButton = new MomentarySwitch(rightOperator, 9, false);
    public static double rightOperatorThrottleValue = rightOperator.getThrottle();

    public static JoystickButton launchButton = new JoystickButton(rightOperator, 1);

    public static JoystickButton launchDisableButton = new JoystickButton(rightOperator, 5);
    // **********************************************************
    // Kilroy's Ancillary classes
    // **********************************************************

    // ------------------------------------
    // Utility classes
    // ------------------------------------
    public static Timer autoTimer = new Timer();

    public static Timer launchDelayTimer = new Timer();

    public static Timer driveDelayTimer = new Timer();

    public static Timer climbTimer = new Timer();
    public static double climbTimerWait = 0.65;

    public static BallCounter ballCounter = new BallCounter(0, 2, addBallButton, subtractBallButton);

    // ------------------------------------
    // Drive system
    // ------------------------------------
    public final static double PREV_YEAR_DISTANCE_PER_TICK = .000746;
    public final static double CURRENT_YEAR_DISTANCE_PER_TICK = .000847;

    public static Drive drive = null;

    public static TankTransmission tankTransmission = null;

    public static ADXRS450_Gyro gyro = new ADXRS450_Gyro();

    // Gear Variables
    public static int PREV_YEAR_GEAR_1 = 1;
    public static double PREV_YEAR_GEAR_1_PERCENTAGE = 0.5;
    public static int PREV_YEAR_GEAR_2 = 2;
    public static double PREV_YEAR_GEAR_2_PERCENTAGE = 0.7;

    // ------------------------------------------
    // Vision stuff
    // ----------------------------
    // public static int KilroyUSBCamerasRotation = 180;
    public static boolean usingTwoCameras = true;
    public static KilroyUSBCamera KilroyUSBCameras = new KilroyUSBCamera(usingTwoCameras);

    // -------------------
    // Subassemblies
    // -------------------
    public static BallHandler ballHandler = new BallHandler();

    }// end class

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
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import frc.HardwareInterfaces.DoubleSolenoid;
import frc.HardwareInterfaces.DoubleThrowSwitch;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.LightSensor;
import frc.HardwareInterfaces.MomentarySwitch;
import frc.HardwareInterfaces.Potentiometer;
import frc.HardwareInterfaces.SingleThrowSwitch;
import frc.HardwareInterfaces.SixPositionSwitch;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import frc.Utils.drive.DrivePID;
import edu.wpi.first.wpilibj.Timer;

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

    public static yearIdentifier robotIdentity = yearIdentifier.PrevYear;

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
        leftFrontMotor = new WPI_TalonFX(13);
        leftFrontMotor.setInverted(false);
        rightFrontMotor = new WPI_TalonFX(12);
        rightFrontMotor.setInverted(true);

        leftRearMotor = new WPI_TalonFX(15);
        leftRearMotor.setInverted(false);
        rightRearMotor = new WPI_TalonFX(14);
        rightRearMotor.setInverted(true);

        // -----------------------------------
        // initialize the drive speed controllers
        // -----------------------------------
        leftDriveGroup = new MotorControllerGroup(leftRearMotor, leftFrontMotor);
        rightDriveGroup = new MotorControllerGroup(rightRearMotor, rightFrontMotor);

        // -----------------------------------
        // configure the drive system encoders
        // -----------------------------------
        leftDriveEncoder = new KilroyEncoder((WPI_TalonFX) leftFrontMotor);
        leftDriveEncoder.setDistancePerPulse(PREV_YEAR_DISTANCE_PER_TICK);
        leftDriveEncoder.setReverseDirection(true);

        rightDriveEncoder = new KilroyEncoder((WPI_TalonFX) rightFrontMotor);
        rightDriveEncoder.setDistancePerPulse(PREV_YEAR_DISTANCE_PER_TICK);
        rightDriveEncoder.setReverseDirection(true);

    } // end of initializePrevYear()

    // **********************************************************
    // CAN DEVICES
    // **********************************************************
    public static MotorController leftRearMotor = null;
    public static MotorController rightRearMotor = null;
    public static MotorController leftFrontMotor = null;
    public static MotorController rightFrontMotor = null;

    public static MotorControllerGroup leftDriveGroup = null;
    public static MotorControllerGroup rightDriveGroup = null;

    public static KilroyEncoder leftDriveEncoder = null;
    public static KilroyEncoder rightDriveEncoder = null;

    // **********************************************************
    // DIGITAL I/O
    // **********************************************************
    public static SixPositionSwitch autoSixPosSwitch = new SixPositionSwitch(13, 14, 15, 16, 17, 18);

    public static SingleThrowSwitch autoDisableSwitch = new SingleThrowSwitch(10);

    public static LightSensor infraredSensor = null;

    // **********************************************************
    // ANALOG I/O
    // **********************************************************
    public static Potentiometer delayPot = new Potentiometer(2);

    // **********************************************************
    // PNEUMATIC DEVICES
    // **********************************************************

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

    // **********************************************************
    // Kilroy's Ancillary classes
    // **********************************************************

    // ------------------------------------
    // Utility classes
    // ------------------------------------

    public static Timer autoTimer = new Timer();

    // ------------------------------------
    // Drive system
    // ------------------------------------
    public final static double PREV_YEAR_DISTANCE_PER_TICK = .000746;
    // ------------------------------------------
    // Vision stuff
    // ----------------------------

    // -------------------
    // Subassemblies
    // -------------------

    } // end class

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/
// ====================================================================
// FILE NAME: Teleop.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 13, 2015
// CREATED BY: Nathanial Lydick
// MODIFIED ON: June 20, 2019
// MODIFIED BY: Ryan McGee
// ABSTRACT:
// This file is where almost all code for Kilroy will be
// written. All of these functions are functions that should
// override methods in the base class (IterativeRobot). The
// functions are as follows:
// -----------------------------------------------------
// Init() - Initialization code for teleop mode
// should go here. Will be called each time the robot enters
// teleop mode.
// -----------------------------------------------------
// Periodic() - Periodic code for teleop mode should
// go here. Will be called periodically at a regular rate while
// the robot is in teleop mode.
// -----------------------------------------------------
//
// ====================================================================
package frc.robot;

import frc.Hardware.Hardware;

/**
 * This class contains all of the user code for the Autonomous part of the
 * match, namely, the Init and Periodic code
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public class Teleop {

    /**
     * User Initialization code for teleop mode should go here. Will be called once
     * when the robot enters teleop mode.
     *
     * @author Nathanial Lydick
     * @written Jan 13, 2015
     */
    public static void init() {

    } // end Init

    /**
     * User Periodic code for teleop mode should go here. Will be called
     * periodically at a regular rate while the robot is in teleop mode.
     *
     * @author Nathanial Lydick
     * @written Jan 13, 2015
     */
    public static void periodic() {

        //Joystick Button/Trigger Variables
        boolean rightOperatorTriggerPressed = Hardware.rightOperator.getTrigger();
        boolean driverGearUpPressed = Hardware.rightDriver.getTrigger();
        boolean driverGearDownPressed = Hardware.rightDriver.getTrigger();
        boolean rightDriverCameraSwitchButtonPressed = Hardware.rightDriverCameraSwitchButton.get();
        boolean rightOperatorCameraSwitchButtonPressed = Hardware.rightOperatorCameraSwitchButton.get();

        //Drive Variables
        double leftDriverJoystickY = Hardware.leftDriver.getY() * -1;
        double rightDriverJoystickY = Hardware.rightDriver.getY() * -1;

        //Switch Camera
        if (rightOperatorCameraSwitchButtonPressed || rightDriverCameraSwitchButtonPressed) {
            Hardware.KilroyUSBCameras.switchCameras();
        }
        

        // =============== AUTOMATED SUBSYSTEMS ===============
    System.out.println("ballPickup1 = " + Hardware.ballPickup1.isOn());
    System.out.println("ballPickup2 = " + Hardware.ballPickup2.isOn());
    System.out.println("ballPickup3 = " + Hardware.ballPickup3.isOn());
    System.out.println("ballPickup4 = " + Hardware.ballPickup4.isOn());
    System.out.println("floorLight = " + Hardware.floorLight.isOn());
        // ================= OPERATOR CONTROLS ================


        // ================== DRIVER CONTROLS =================
        Hardware.drive.drive(Hardware.leftDriver, Hardware.rightDriver);

        Hardware.tankTransmission.shiftGears(driverGearUpPressed, driverGearDownPressed);

        System.out.println("ltJoy" + leftDriverJoystickY + "ltMotorGroup" + Hardware.leftDriveGroup.get());
        System.out.println("rtJoy" + rightDriverJoystickY + "rtMotorGroup" +  Hardware.rightDriveGroup.get());
        System.out.println(Hardware.drive.getCurrentGear());


        individualTest();
    } // end Periodic()

    public static void individualTest() {
        // people test functions
    }

    public static void printStatements() {
        // ========== INPUTS ==========

        // ---------- DIGITAL ----------

        // Encoder Distances

        // Encoder Raw Values

        // Switch Values

        // ---------- ANALOG -----------

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------


        // ========== OUTPUTS ==========

        // ---------- DIGITAL ----------

        // ---------- ANALOG -----------

        // ----------- CAN -------------

        // -------- SUBSYSTEMS ---------

        // ---------- OTHER ------------

    }

} // end class

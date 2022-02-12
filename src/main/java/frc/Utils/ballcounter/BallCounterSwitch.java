// ====================================================================
// FILE NAME: BallCounterSwitch.java (Team 339 - Kilroy)
//
// CREATED ON: Feb 12, 2022
// CREATED BY: Jacob Fisher
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is used when we want to use a momentary switch
// as denoting ON or Off. It keeps track of whether or not
// the switch was called being held and what state the
// switch should be considered (ON or OFF)
//
// Extends the momentary switch and uses the ball counter to make more
// accurate decisions.
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package frc.Utils.ballcounter;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.*;
import edu.wpi.first.wpilibj.Joystick;

public class BallCounterSwitch extends MomentarySwitch
    {
    /**
     * constructor
     *
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public BallCounterSwitch()
        {
            this.isOn = false;
            this.previouslyOn = false;
            this.joystickToCheck = null;
        } // end BallCounterSwitch

    // -----------------------------------------------------
    /**
     * constructor
     *
     * @param startingState
     *            what state we should start as
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public BallCounterSwitch(final boolean startingState)
        {
            this.isOn = startingState;
            this.previouslyOn = false;
            this.joystickToCheck = null;
        } // end BallCounterSwitch

    // -----------------------------------------------------
    /**
     * constructor
     *
     * @param joystick
     *            - joystick which will be used to check to see if the button has
     *            been pushed
     * @param buttonNumber
     *            - which button is to be checked on this joystick
     * @param startingState
     *            what state we should start as
     * @author Bob Brown
     * @written Feb 06, 2011
     *          --------------------------------------------------------
     */
    public BallCounterSwitch(final Joystick joystick, final int buttonNumber, final boolean startingState)
        {
            this.isOn = startingState;
            this.previouslyOn = false;
            this.joystickToCheck = joystick;
            this.buttonNumber = buttonNumber;
        } // end BallCounterSwitch
    // -----------------------------------------------------

    // TODO JAVA DOC IT
    public int add(int addBy)
    {
        BallCounter.BallCount += addBy;
        return BallCounter.BallCount;
    }

    // TODO JAVA DOC IT
    public int subtract(int subtractBy)
    {
        BallCounter.BallCount -= subtractBy;
        return BallCounter.BallCount;
    }

    // TODO JAVA DOC IT
    public int addCheckCount(int addBy)
    {
        if (BallCounter.BallCount + addBy >= 2)
            {
            BallCounter.BallCount -= addBy;
            // Associated Subtract Button
            Hardware.subtractBallButton.setValue(false);
            }
        if (Hardware.addBallButton.isOn() == true)
            // Associated Add Button
            Hardware.addBallButton.setValue(false);
        return BallCounter.BallCount;
    }

    // TODO JAVA DOC IT
    public int subtractCheckCount(int subtractBy)
    {
        if (BallCounter.BallCount - subtractBy >= 0)
            {
            BallCounter.BallCount -= subtractBy;
            // Associated Subtract Button
            Hardware.subtractBallButton.setValue(false);
            }
        if (Hardware.addBallButton.isOn() == true)
            // Associated Add Button
            Hardware.addBallButton.setValue(false);
        return BallCounter.BallCount;
    }
    }

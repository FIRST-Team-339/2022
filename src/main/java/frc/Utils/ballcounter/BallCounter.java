// ===================================================================
// FILE NAME: BallCounter.java (Team 339 - Kilroy)
//
// CREATED ON: Feb 12, 2022
// CREATED BY: Jacob Fisher
// MODIFIED ON: Feb 12, 2022
// MODIFIED BY: Jacob Fisher
// ABSTRACT:
// This file contains all of the global definitions for the
// ball counter related items in the system
// (probably just the one BallCounter integer)
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package frc.Utils.ballcounter;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.MomentarySwitch;

public class BallCounter
    {
    // BALL COUNT
    public static int BallCount = 0;

    // MOMENTARY SWITCHES
    public static MomentarySwitch subtractBallButton = Hardware.subtractBallButton;
    public static MomentarySwitch addBallButton = Hardware.addBallButton;

    // TODO JAVA DOC IT
    public static int add(int addBy)
    {
        BallCount += addBy;
        return BallCount;
    }

    // TODO JAVA DOC IT
    public static int subtract(int subtractBy)
    {
        BallCount -= subtractBy;
        return BallCount;
    }

    // TODO JAVA DOC IT
    public static int addCheckCount(int addBy)
    {
        if (BallCount + addBy <= 2)
            {
            BallCount += addBy;
            // Associated Switch goes off
            addBallButton.setValue(false);
            }
        if (subtractBallButton.isOn() == true)
            // Alt Switch goes off
            subtractBallButton.setValue(false);
        return BallCounter.BallCount;
    }

    // TODO JAVA DOC IT
    public static int subtractCheckCount(int subtractBy)
    {
        if (BallCounter.BallCount - subtractBy >= 0)
            {
            BallCounter.BallCount -= subtractBy;
            // Associated Switch goes off
            subtractBallButton.setValue(false);
            }
        if (addBallButton.isOn() == true)
            // Alt Switch goes off
            addBallButton.setValue(false);
        return BallCounter.BallCount;
    }
    }

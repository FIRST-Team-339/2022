// ===================================================================
// FILE NAME: BallCounter.java (Team 339 - Kilroy)
//
// CREATED ON: Feb 12, 2022
// CREATED BY: Jacob Fisher
// MODIFIED ON: Feb 22, 2022
// MODIFIED BY: Jacob Fisher
// ABSTRACT:
// This file contains all of the global definitions for the
// ball counter related items in the system
// (probably just the one BallCounter integer)
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package frc.Utils;

import frc.HardwareInterfaces.MomentarySwitch;

public class BallCounter
    {
        
    // BALL COUNTS
    public int BallCount = 0;
    private int MinimumBallCount = 0;
    private int MaximumBallCount = 2;

    // MOMENTARY SWITCHES
    private MomentarySwitch addBallButton = null;
    private MomentarySwitch subtractBallButton = null;

    // -----------------------------------------------------

    /**
     * constructor
     *
     * NO PARAMS - UNRECOMMENDED 
     * 
     * @author Jacob Fisher
     * @written Feb 22, 2022
     */
    public BallCounter()
        {

        }
    
    /**
     * constructor
     *
     * @param setMinimumBallCount the minimum the ball count can be
     * @param setMaximumBallCount the maximum the ball count can be
     * 
     * @author Jacob Fisher
     * @written Feb 22, 2022
     */
    public BallCounter(final int setMinimumBallCount, final int setMaximumBallCount)
        {
            MinimumBallCount = setMinimumBallCount;
            MaximumBallCount = setMaximumBallCount;
        }
    
    /**
     * constructor
     *
     * @param setAddBallButton the add ball button from Hardware
     * @param setSubtractBallButton the subtract ball button from Hardware
     * 
     * @author Jacob Fisher
     * @written Feb 22, 2022
     */
    public BallCounter(final MomentarySwitch setAddBallButton, final MomentarySwitch setSubtractBallButton)
        {
            addBallButton = setAddBallButton;
            subtractBallButton = setSubtractBallButton;
        }

    /**
     * constructor
     *
     * @param setMinimumBallCount the minimum the ball count can be
     * @param setMaximumBallCount the maximum the ball count can be
     * @param setAddBallButton the add ball button from Hardware
     * @param setSubtractBallButton the subtract ball button from Hardware
     * 
     * @author Jacob Fisher
     * @written Feb 22, 2022
     */
    public BallCounter(final int setMinimumBallCount, final int setMaximumBallCount, final MomentarySwitch setAddBallButton, final MomentarySwitch setSubtractBallButton)
        {
            MinimumBallCount = setMinimumBallCount;
            MaximumBallCount = setMaximumBallCount;
            addBallButton = setAddBallButton;
            subtractBallButton = setSubtractBallButton;
        }

    // TODO JAVA DOC IT
    public int setMinimumBallCount(int newMinimum) 
    {
        MinimumBallCount = newMinimum;
        return MinimumBallCount;
    }

    // TODO JAVA DOC IT
    public int setMaximumBallCount(int newMaximum) 
    {
        MaximumBallCount = newMaximum;
        return MaximumBallCount;
    }

    // TODO JAVA DOC IT
    public MomentarySwitch setAddBallButton(final MomentarySwitch newAddBallButton)
    {
        addBallButton = newAddBallButton;
        return addBallButton;
    }

    // TODO JAVA DOC IT
    public MomentarySwitch setSubtractBallButton(final MomentarySwitch newSubtractBallButton)
    {
        subtractBallButton = newSubtractBallButton;
        return subtractBallButton;
    }

    // TODO JAVA DOC IT
    public int add(int addBy)
    {
        BallCount = BallCount + addBy;
        return BallCount;
    }

    // TODO JAVA DOC IT
    public int subtract(int subtractBy)
    {
        BallCount = BallCount - subtractBy;
        return BallCount;
    }

    // TODO JAVA DOC IT
    public int addCheckCount(int addBy)
    {
        if (BallCount + addBy <= MaximumBallCount)
            {
            BallCount = BallCount + addBy;
            // Associated Switch goes off
            addBallButton.setValue(false);
            }
        if (subtractBallButton.isOn() == true)
            // Alt Switch goes off
            subtractBallButton.setValue(false);
        return BallCount;
    }

    // TODO JAVA DOC IT
    public int subtractCheckCount(int subtractBy)
    {
        if (BallCount - subtractBy >= MinimumBallCount)
            {
            BallCount = BallCount - subtractBy;
            // Associated Switch goes off
            subtractBallButton.setValue(false);
            }
        if (addBallButton.isOn() == true)
            // Alt Switch goes off
            addBallButton.setValue(false);
        return BallCount;
    }
    }

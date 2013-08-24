/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcme.marozzo.animations.animations.commands;

import com.mcme.marozzo.animations.animations.MCMEClipboardStore;

/**
 *
 * @author Luca
 */
public class MCMEFrameBuilder {

    private MCMEClipboardStore clipboard;
    private int duration;

    public MCMEClipboardStore getSchematic() {
        return clipboard;
    }

    public void setSchematic(MCMEClipboardStore schematic) {
        this.clipboard = schematic;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}

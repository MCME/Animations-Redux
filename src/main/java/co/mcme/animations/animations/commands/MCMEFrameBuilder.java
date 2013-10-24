/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package co.mcme.animations.animations.commands;

import co.mcme.animations.animations.MCMEClipboardStore;

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

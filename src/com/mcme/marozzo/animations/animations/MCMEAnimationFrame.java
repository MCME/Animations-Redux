/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcme.marozzo.animations.animations;

/**
 *
 * @author Luca
 */
public class MCMEAnimationFrame {

    private String frameName;
    private long Duration;

    public long getDuration() {
        return Duration;
    }

    public String getFrameName() {
        return frameName;
    }

    public MCMEAnimationFrame(String frameName, long Duration) {
        this.frameName = frameName;
        this.Duration = Duration;
    }

    
}

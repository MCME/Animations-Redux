/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations.actions;

import com.mcme.marozzo.animations.AnimationAction;
import com.mcme.marozzo.animations.MCMEAnimations;
import com.mcme.marozzo.animations.animations.MCMEAnimation;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class ChainAnimationAction implements AnimationAction {

    private MCMEAnimation parent;
    private int frame;
    private String targetName;

    public ChainAnimationAction(MCMEAnimation parent, int frame, String targetName) {
        this.parent = parent;
        this.frame = frame;
        this.targetName = targetName;
    }

    public boolean check() {
        if (parent.getCurrentFrame() == frame) {
            return true;
        }
        return false;
    }

    public void performAction() {
        for (MCMEAnimation a : MCMEAnimations.animations) {
            if (a.getName().equals(targetName)) {
                a.start();
            }
        }
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public String getAnimationName() {
        return parent.getName();
    }

    public String getTarget() {
        return targetName;
    }

    public void setTarget(String target) {
        targetName = target;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
        data.put("target", targetName);
        result.put("chain_animation", data);
        return result;
    }

    @Override
    public String toString() {
        return "Chain Animation action";
    }

    public String toHtml() {
        return String.format(template, toString(), "Start "+targetName, "on frame #"+String.valueOf(frame));
    }
}

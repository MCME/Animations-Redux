/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations.triggers;

import com.mcme.marozzo.animations.AnimationTrigger;
import com.mcme.marozzo.animations.animations.MCMEAnimation;
import org.bukkit.Location;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class AlwaysActiveTrigger implements AnimationTrigger {

    MCMEAnimation parent;

    public AlwaysActiveTrigger(MCMEAnimation parent) {
        this.parent = parent;
    }

    public boolean check(Location location) {
        //No need to check.
        return true;
    }

    public void trigger() {
        parent.start();
    }

    public boolean check(Location location, String message) {
        //No need to check
        return true;
    }

    public int getFrame() {
        return -1;
    }

    public void setFrame(int frame) {
        //Do nothing
    }

    public Object getData() {
        return null;
    }

    public void setData(Object data) {
        //Do nothing
    }

    @Override
    public String toString() {
        return "Always active trigger";
    }

    public void setParent(MCMEAnimation parent) {
        this.parent = parent;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject dummy = new JSONObject();
        dummy.put("dummy", "dummy");
        result.put("always_active", dummy);
        return result;
    }

    public String toHtml() {
        return String.format(template, toString(), "This animation will start on the first player joining the server", "");
    }

    public MCMEAnimation getParent() {
        return parent;
    }
}

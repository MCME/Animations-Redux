/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcme.marozzo.animations;

import com.mcme.marozzo.animations.animations.MCMEAnimation;
import org.bukkit.Location;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public interface AnimationTrigger {

    public boolean check(Location location);
    public boolean check(Location location, String message);
    public void trigger();
    public int getFrame();
    public void setFrame(int frame);
    public Object getData();
    public void setData(Object data);
    public void setParent(MCMEAnimation parent);
    public JSONObject toJSON();
}

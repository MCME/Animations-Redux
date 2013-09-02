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

    public static String template = "<tr><td>%1$s</td><td>%2$s</td><td style=\"text-align:right\">%3$s</td></tr>";
    public boolean check(Location location);
    public boolean check(Location location, String message);
    public void trigger();
    public int getFrame();
    public void setFrame(int frame);
    public void setData(Object data);
    public void setParent(MCMEAnimation parent);
    public MCMEAnimation getParent();
    public JSONObject toJSON();
    public String toHtml();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations.triggers;

import com.mcme.marozzo.animations.AnimationTrigger;
import com.mcme.marozzo.animations.MCMEAnimations;
import com.mcme.marozzo.animations.animations.MCMEAnimation;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import org.bukkit.Location;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class ShapeInteractTrigger implements AnimationTrigger {

    MCMEAnimation parent;
    int frame;

    public ShapeInteractTrigger(MCMEAnimation parent, int frame) {
        this.parent = parent;
        this.frame =frame;
    }

    public int getFrame(){
        return frame;
    }

    public boolean check(Location location) {

//        SamplePlugin.WEPlugin.getServer().getLogger().info("Checking shape triggering...");
//        SamplePlugin.WEPlugin.getServer().getLogger().info("-=Shape=-");
//        SamplePlugin.WEPlugin.getServer().getLogger().info("Pos1: " + parent.getBounds().getPos1().toString());
//        SamplePlugin.WEPlugin.getServer().getLogger().info("Pos2: " + parent.getBounds().getPos2().toString());
//        SamplePlugin.WEPlugin.getServer().getLogger().info("-=Trigger=-");
//        SamplePlugin.WEPlugin.getServer().getLogger().info("Location: " + location.toString());

//        MCMEAnimations.MCMEAnimationsInstance.getLogger().info("min: "+parent.getBounds().getMinimumPoint());
//        MCMEAnimations.MCMEAnimationsInstance.getLogger().info("max: "+parent.getBounds().getMaximumPoint());
//        MCMEAnimations.MCMEAnimationsInstance.getLogger().info("loc: "+BukkitUtil.toVector(location));
        if((parent.getCurrentFrame() == frame) &&
            parent.getBounds().contains(BukkitUtil.toVector(location))
           ){
            return true;
        }
        return false;
    }

    public void trigger() {
        parent.start();
    }

    public boolean check(Location location, String message) {
        return false;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public void setData(Object data) {
        //Do nothing
    }

    @Override
    public String toString() {
        return "Shape interaction trigger";
    }

    public void setParent(MCMEAnimation parent) {
        this.parent = parent;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
        result.put("shape_interaction", data);
        return result;
    }

    public String toHtml() {
        return String.format(template, toString(), "activate when player interact with shape", "on frame #"+String.valueOf(frame));
    }

    public MCMEAnimation getParent() {
        return parent;
    }



}

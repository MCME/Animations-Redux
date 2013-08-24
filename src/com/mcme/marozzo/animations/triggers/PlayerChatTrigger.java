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
public class PlayerChatTrigger implements AnimationTrigger {

    MCMEAnimation parent;
    int frame;
    double radius;
    String message;

    public PlayerChatTrigger(MCMEAnimation parent, int frame, double radius, String message) {
        this.parent = parent;
        this.frame = frame;
        this.radius = radius;
        this.message = message;
    }

    public int getFrame() {
        return frame;
    }

    public boolean check(Location location) {
        return false;
    }

    public void trigger() {
        parent.start();
    }

    public boolean check(Location location, String message) {

        double distance = MCMEAnimations.distance3D(BukkitUtil.toVector(location), parent.getCenter());
//        SamplePlugin.WEPlugin.getServer().getLogger().info("Checking distance: "+String.valueOf(distance));
//        SamplePlugin.WEPlugin.getServer().getLogger().info("frame: "+String.valueOf(parent.getCurrentFrame()));
//        SamplePlugin.WEPlugin.getServer().getLogger().info("message: "+String.valueOf(this.message));
        if ((parent.getCurrentFrame() == frame)
                && message.equals(this.message)
                && distance <= this.radius) {
            return true;
        }
        return false;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public Object getData() {
        return new Object[]{radius, message};
    }

    public void setData(Object data) {
        if (data instanceof Object[]) {
            if (((Object[]) data).length == 2) {
                if ((((Object[]) data)[0] instanceof Double)
                        && (((Object[]) data)[0] instanceof String)) {
                    radius = (Double) (((Object[]) data)[0]);
                    message = (String) (((Object[]) data)[1]);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Player chat trigger";
    }

    public void setParent(MCMEAnimation parent) {
        this.parent = parent;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
        data.put("message", message);
        data.put("distance", radius);
        result.put("player_chat", data);
        return result;
    }
}

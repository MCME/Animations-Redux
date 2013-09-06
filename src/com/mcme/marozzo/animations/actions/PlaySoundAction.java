/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations.actions;

import com.mcme.marozzo.animations.AnimationAction;
import com.mcme.marozzo.animations.MCMEAnimations;
import com.mcme.marozzo.animations.animations.MCMEAnimation;
import com.mcme.marozzo.animations.animations.WELoader;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class PlaySoundAction implements AnimationAction {

    private MCMEAnimation parent;
    private Vector epicenter;
    private int frame;
    private Sound theSound;
    private double radius;

    public PlaySoundAction(MCMEAnimation parent, int frame, String sound, double radius) {
        this.parent = parent;
        this.frame = frame;
        this.radius = radius;
        for (Sound s : Sound.values()) {
            if (s.toString().equals(sound.toUpperCase())) {
                theSound = s;
                break;
            }
        }
    }

    public boolean check() {
        if (parent.getCurrentFrame() == frame) {
            return true;
        }
        return false;
    }

    public void performAction() {
        epicenter = parent.getBounds().getCenter();
        for (Player p : MCMEAnimations.MCMEAnimationsInstance.getServer().getOnlinePlayers()) {
            if (p.getWorld().equals(parent.getWorld())) {
                double dist = MCMEAnimations.distance3D(epicenter, BukkitUtil.toVector(p.getLocation()));
                if (dist < radius) {
                    float volume = (float) ((1.0 - (dist / radius)) * 1.5);
                    p.playSound(p.getLocation(), theSound, volume, 1);
                }
            }
        }
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setSound(String soundName) {
        for (Sound s : Sound.values()) {
            if (s.toString().equals(soundName.toUpperCase())) {
                theSound = s;
                break;
            }
        }
    }

    public String getAnimationName() {
        return parent.getName();
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
        data.put("sound", theSound.toString());
        data.put("radius", radius);
        result.put("sound", data);
        return result;
    }

    @Override
    public String toString() {
        return "Play Sound action";
    }

    public String toHtml() {
        return String.format(template, toString(), "Play " + theSound.toString(), " within a " + radius + " blocks radius on frame #" + String.valueOf(frame));
    }
}

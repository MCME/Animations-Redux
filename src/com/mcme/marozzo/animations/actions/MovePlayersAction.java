/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations.actions;

import com.mcme.marozzo.animations.AnimationAction;
import com.mcme.marozzo.animations.MCMEAnimations;
import com.mcme.marozzo.animations.animations.MCMEAnimation;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class MovePlayersAction implements AnimationAction {

    private MCMEAnimation parent;
//    private Vector direction;
    private int frame;

//    public MovePlayersAction(MCMEAnimation parent, int frame, Vector direction) {
    public MovePlayersAction(MCMEAnimation parent, int frame) {
        this.parent = parent;
        this.frame = frame;
//        this.direction = direction;
    }

    public boolean check() {
        if (parent.getCurrentFrame() == frame) {
            return true;
        }
        return false;
    }

    public void performAction() {
        for (Player p : MCMEAnimations.MCMEAnimationsInstance.getServer().getOnlinePlayers()) {
            if (p.getWorld().equals(parent.getWorld())) {
                if (parent.getBounds().contains(BukkitUtil.toVector(p.getLocation()))) {
                    Location loc = p.getLocation();                    
                    loc.setX(loc.getX() - parent.getVirtualDirection().getX());
                    loc.setY(loc.getY() - parent.getVirtualDirection().getY());
                    loc.setZ(loc.getZ() - parent.getVirtualDirection().getZ());

//                    MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("*********************************");
//                    MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Frame : "+parent.getCurrentFrame());
//                    MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Player pos : "+p.getLocation().getX()+","+p.getLocation().getY()+","+p.getLocation().getZ());
//                    MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Direction  : "+parent.getVirtualDirection().getX()+","+parent.getVirtualDirection().getY()+","+parent.getVirtualDirection().getZ());
//                    MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Teleport to: "+loc.getX()+","+loc.getY()+","+loc.getZ());
                    p.teleport(loc);
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

    public String getAnimationName() {
        return parent.getName();
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
//        JSONArray dir = new JSONArray();
//        dir.add(direction.getX());
//        dir.add(direction.getY());
//        dir.add(direction.getZ());
//        data.put("direction", dir);
        result.put("move_players", data);
        return result;
    }

    @Override
    public String toString() {
        return "Move Players action";
    }

}

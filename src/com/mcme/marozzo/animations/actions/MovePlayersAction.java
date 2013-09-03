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
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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

                Location loc = p.getLocation();

                double x = loc.getX();
                double y = loc.getY();
                double z = loc.getZ();

                Vector min = parent.getBounds().getMinimumPoint();
                Vector max = parent.getBounds().getMaximumPoint();

                if (x >= min.getX() && x <= max.getX()
                        && y >= min.getY() && y <= max.getY()
                        && z >= min.getZ() && z <= max.getZ()) {

                    Location newLoc = new Location(loc.getWorld(),
                            loc.getX() - parent.getVirtualDirection().getX(),
                            loc.getY() - parent.getVirtualDirection().getY(),
                            loc.getZ() - parent.getVirtualDirection().getZ(),
                            loc.getYaw(), loc.getPitch());

                    p.teleport(newLoc);
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

    public String toHtml() {
        return String.format(template, toString(), "Move players in the direction of the animation", "on frame #" + String.valueOf(frame));
    }
}

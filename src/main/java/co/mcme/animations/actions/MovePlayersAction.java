/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.mcme.animations.actions;

import co.mcme.animations.AnimationAction;
import co.mcme.animations.MCMEAnimations;
import co.mcme.animations.animations.MCMEAnimation;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.logging.Level;
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

                if (isInBounds(loc, parent.getBounds())) {
                    Location newLoc = new Location(loc.getWorld(),
                            loc.getX() - parent.getVirtualDirection().getX(),
                            loc.getY() - parent.getVirtualDirection().getY(),
                            loc.getZ() - parent.getVirtualDirection().getZ(),
                            p.getEyeLocation().getYaw(), p.getEyeLocation().getPitch());

                    p.teleport(newLoc);
                }
            }
        }
    }

    private boolean isInBounds(Location loc, CuboidRegion bounds) {
        boolean result;
        double x = Math.floor(loc.getX());
        double y = Math.floor(loc.getY());
        double z = Math.floor(loc.getZ());

        Vector min = bounds.getMinimumPoint();
        Vector max = bounds.getMaximumPoint();
        result = (x >= Math.floor(min.getX()) && x <= Math.floor(max.getX())
                && y >= Math.floor(min.getY()) && y <= Math.floor(max.getY())
                && z >= Math.floor(min.getZ()) && z <= Math.floor(max.getZ()));

//        MCMEAnimations.MCMEAnimationsInstance.getLogger().info("-------TESTING--------");
//        MCMEAnimations.MCMEAnimationsInstance.getLogger().log(Level.INFO, "Loc - X: {0} Y:{1} Z: {2}", new Object[]{x, y, z});
//        MCMEAnimations.MCMEAnimationsInstance.getLogger().log(Level.INFO, "Min - X: {0} Y:{1} Z: {2}", new Object[]{min.getX(), min.getY(), min.getZ()});
//        MCMEAnimations.MCMEAnimationsInstance.getLogger().log(Level.INFO, "Max - X: {0} Y:{1} Z: {2}", new Object[]{max.getX(), max.getY(), max.getZ()});
        return result;
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

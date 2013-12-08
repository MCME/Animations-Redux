/*  This file is part of MCMEAnimations.
 * 
 *  MCMEAnimations is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MCMEAnimations is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MCMEAnimations.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.mcme.animations.triggers;

import co.mcme.animations.AnimationTrigger;
import co.mcme.animations.animations.MCMEAnimation;
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
        this.frame = frame;
    }

    @Override
    public int getFrame() {
        return frame;
    }

    @Override
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
        if ((parent.getCurrentFrame() == frame)
                && parent.getBounds().contains(BukkitUtil.toVector(location))) {
            return true;
        }
        return false;
    }

    @Override
    public void trigger() {
        parent.start();
    }

    @Override
    public boolean check(Location location, String message) {
        return false;
    }

    @Override
    public void setFrame(int frame) {
        this.frame = frame;
    }

    @Override
    public void setData(Object data) {
        //Do nothing
    }

    @Override
    public String toString() {
        return "Shape interaction trigger";
    }

    @Override
    public void setParent(MCMEAnimation parent) {
        this.parent = parent;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
        result.put("shape_interaction", data);
        return result;
    }

    @Override
    public String toHtml() {
        return String.format(template, toString(), "activate when player interact with shape", "on frame #" + String.valueOf(frame));
    }

    @Override
    public MCMEAnimation getParent() {
        return parent;
    }
}

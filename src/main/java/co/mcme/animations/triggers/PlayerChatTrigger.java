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
import co.mcme.animations.MCMEAnimations;
import co.mcme.animations.animations.MCMEAnimation;
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

    @Override
    public int getFrame() {
        return frame;
    }

    @Override
    public boolean check(Location location) {
        return false;
    }

    @Override
    public void trigger() {
        parent.start();
    }

    @Override
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

    @Override
    public void setFrame(int frame) {
        this.frame = frame;
    }

    @Override
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

    @Override
    public void setParent(MCMEAnimation parent) {
        this.parent = parent;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
        data.put("message", message);
        data.put("distance", radius);
        result.put("player_chat", data);
        return result;
    }

    @Override
    public String toHtml() {
        return String.format(template, toString(), "activated by \"" + message + "\" chat command", "on frame #" + String.valueOf(frame));
    }

    @Override
    public MCMEAnimation getParent() {
        return parent;
    }
}

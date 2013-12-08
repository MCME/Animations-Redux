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
package co.mcme.animations.actions;

import co.mcme.animations.AnimationAction;
import co.mcme.animations.MCMEAnimations;
import co.mcme.animations.animations.MCMEAnimation;
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

    @Override
    public boolean check() {
        if (parent.getCurrentFrame() == frame) {
            return true;
        }
        return false;
    }

    @Override
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

    @Override
    public int getFrame() {
        return frame;
    }

    @Override
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

    @Override
    public String getAnimationName() {
        return parent.getName();
    }

    @Override
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

    @Override
    public String toHtml() {
        return String.format(template, toString(), "Play " + theSound.toString(), " within a " + radius + " blocks radius on frame #" + String.valueOf(frame));
    }
}

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
import org.bukkit.Location;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class AlwaysActiveTrigger implements AnimationTrigger {

    MCMEAnimation parent;

    public AlwaysActiveTrigger(MCMEAnimation parent) {
        this.parent = parent;
    }

    @Override
    public boolean check(Location location) {
        //No need to check.
        return true;
    }

    @Override
    public void trigger() {
        parent.start();
    }

    @Override
    public boolean check(Location location, String message) {
        //No need to check
        return true;
    }

    @Override
    public int getFrame() {
        return -1;
    }

    @Override
    public void setFrame(int frame) {
        //Do nothing
    }

    @Override
    public void setData(Object data) {
        //Do nothing
    }

    @Override
    public String toString() {
        return "Always active trigger";
    }

    @Override
    public void setParent(MCMEAnimation parent) {
        this.parent = parent;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject dummy = new JSONObject();
        dummy.put("dummy", "dummy");
        result.put("always_active", dummy);
        return result;
    }

    @Override
    public String toHtml() {
        return String.format(template, toString(), "This animation will start on the first player joining the server", "");
    }

    @Override
    public MCMEAnimation getParent() {
        return parent;
    }
}

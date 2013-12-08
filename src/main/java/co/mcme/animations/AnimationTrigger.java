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
package co.mcme.animations;

import co.mcme.animations.animations.MCMEAnimation;
import org.bukkit.Location;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public interface AnimationTrigger {

    public static String template = "<tr><td>%1$s</td><td>%2$s</td><td style=\"text-align:right\">%3$s</td></tr>";

    public boolean check(Location location);

    public boolean check(Location location, String message);

    public void trigger();

    public int getFrame();

    public void setFrame(int frame);

    public void setData(Object data);

    public void setParent(MCMEAnimation parent);

    public MCMEAnimation getParent();

    public JSONObject toJSON();

    public String toHtml();
}

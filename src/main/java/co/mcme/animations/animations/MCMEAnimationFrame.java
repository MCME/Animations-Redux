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
package co.mcme.animations.animations;

/**
 *
 * @author Luca
 */
public class MCMEAnimationFrame {

    private final String frameName;
    private final long Duration;

    public long getDuration() {
        return Duration;
    }

    public String getFrameName() {
        return frameName;
    }

    public MCMEAnimationFrame(String frameName, long Duration) {
        this.frameName = frameName;
        this.Duration = Duration;
    }

    
}

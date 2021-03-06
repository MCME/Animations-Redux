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

import co.mcme.animations.MCMEAnimations;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;

/**
 *
 * @author Luca
 */
public class TwoWayAnimation extends MCMEAnimation {

    private final animationType type = animationType.REVERSIBLE_ANIMATION;
    volatile int currentFrame;
    private int direction = 0;

    public TwoWayAnimation(File configFile) {
        super(configFile);
        currentFrame = 0;
        init();
    }

    @Override
    public void start() {
//        SamplePlugin.WEPlugin.getServer().getLogger().info("Starting two-way animation: "+animationName);

        long MaxDuration = 0;
        int shadowFrame = 0;
        if (!MCMEAnimations.CurrentJobs.contains(animationName)) {
            loadFrames();
            MCMEAnimations.CurrentJobs.add(animationName);
            if (direction == 0) {
//                for (final MCMEAnimationFrame f : frames) {
                for (int i = 0; i < frames.size(); i++) {
                    MCMEAnimationFrame f = frames.get(i);
                    MaxDuration = Math.max(MaxDuration, f.getDuration());
                    final int temp = shadowFrame;
                    final int clipboardIndex = i;
                    MCMEAnimations.WEPlugin.getServer().getScheduler().runTaskLater(MCMEAnimations.WEPlugin, new Runnable() {
                        @Override
                        public void run() {
                            CuboidClipboard clip = clipboards.get(clipboardIndex);
                            WELoader.placeFrame(origin, clip, localWorldName);
                            Vector v1 = new Vector(origin.getX() + clip.getOffset().getX(), origin.getY() + clip.getOffset().getY(), origin.getZ() + clip.getOffset().getZ());
                            Vector v2 = new Vector(Math.floor(origin.getX() + clip.getWidth() + clip.getOffset().getX() - 1),
                                    Math.floor(origin.getY() + clip.getHeight() + clip.getOffset().getY() - 1),
                                    Math.floor(origin.getZ() + clip.getLength() + clip.getOffset().getZ() - 1));

                            Vector previousMin = shape.getMinimumPoint();
//                            MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Min point1: "+previousMin.toString());
                            shape = new CuboidRegion(v1, v2);
//                            MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Min point2: "+shape.getMinimumPoint().toString());

                            virtual_direction = new Vector(
                                    previousMin.getX() - shape.getMinimumPoint().getX(),
                                    previousMin.getY() - shape.getMinimumPoint().getY(),
                                    previousMin.getZ() - shape.getMinimumPoint().getZ());

//                            MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Virtual vector: "+virtual_direction.toString());
                            currentFrame = temp;

                            executeActions();
//                            Selection sel = new CuboidSelection(MCMEAnimations.WEPlayer.getWorld(), v1, v2);
//                            MCMEAnimations.WEPlugin.setSelection(MCMEAnimations.WEPlayer, sel);
                        }
                    }, f.getDuration());
                    shadowFrame++;
                }
            } else {
                for (final MCMEAnimationFrame f : frames) {
                    MaxDuration = Math.max(MaxDuration, f.getDuration());
                }
//                shadowFrame = frames.size()-1;
                for (int i = 0; i < frames.size(); i++) {
                    MCMEAnimationFrame f = frames.get(i);
                    final int temp = shadowFrame;
                    final int clipboardIndex = i;
                    MCMEAnimations.WEPlugin.getServer().getScheduler().runTaskLater(MCMEAnimations.WEPlugin, new Runnable() {

                        @Override
                        public void run() {
                            CuboidClipboard clip = clipboards.get(clipboardIndex);
                            WELoader.placeFrame(origin, clip, localWorldName);
                            Vector v1 = new Vector(origin.getX() + clip.getOffset().getX(), origin.getY() + clip.getOffset().getY(), origin.getZ() + clip.getOffset().getZ());
                            Vector v2 = new Vector(Math.floor(origin.getX() + clip.getWidth() + clip.getOffset().getX() - 1),
                                    Math.floor(origin.getY() + clip.getHeight() + clip.getOffset().getY() - 1),
                                    Math.floor(origin.getZ() + clip.getLength() + clip.getOffset().getZ() - 1));

                            Vector previousMin = shape.getMinimumPoint();
//                            MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Min point1: "+previousMin.toString());
                            shape = new CuboidRegion(v1, v2);
//                            MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Min point2: "+shape.getMinimumPoint().toString());
                            virtual_direction = new Vector(
                                    previousMin.getX() - shape.getMinimumPoint().getX(),
                                    previousMin.getY() - shape.getMinimumPoint().getY(),
                                    previousMin.getZ() - shape.getMinimumPoint().getZ());
//                            MCMEAnimations.MCMEAnimationsInstance.getServer().getLogger().info("Virtual vector: "+virtual_direction.toString());

                            currentFrame = temp;
                            executeActions();
//                            Selection sel = new CuboidSelection(MCMEAnimations.WEPlayer.getWorld(), v1, v2);
//                            MCMEAnimations.WEPlugin.setSelection(MCMEAnimations.WEPlayer, sel);

                        }
                    }, MaxDuration - f.getDuration());
                    shadowFrame++;
                }
            }
            MCMEAnimations.WEPlugin.getServer().getScheduler().runTaskLater(MCMEAnimations.WEPlugin, new Runnable() {

                @Override
                public void run() {
                    afterJobDelegate();
                }
            }, MaxDuration + 1);
            direction = direction == 0 ? 1 : 0;
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public animationType getType() {
        return type;
    }

    @Override
    public void afterJobDelegate() {
        unloadFrames();
        MCMEAnimations.CurrentJobs.remove(animationName);
    }

    @Override
    public int getCurrentFrame() {
        return currentFrame;
    }

    @Override
    public CuboidRegion getBounds() {
        return shape;
    }

    @Override
    public void init() {
        loadConfiguration();
        final MCMEAnimationFrame f = frames.get(0);
        shape = WELoader.loadBounds(schematicBaseName + File.separator + f.getFrameName(), this);
        virtual_direction = new Vector(0, 0, 0);
    }

    @Override
    public Vector getCenter() {
        return shape.getCenter();
    }

    @Override
    public void loadFrames() {
        clipboards.clear();
        for (final MCMEAnimationFrame f : frames) {
            CuboidClipboard clip = WELoader.loadFrame(schematicBaseName + File.separator + f.getFrameName());
            clipboards.add(clip);
        }
    }

    @Override
    public void unloadFrames() {
        for (CuboidClipboard c : clipboards) {
            c = null;
        }
        clipboards.clear();
    }
}

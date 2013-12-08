package co.mcme.animations.animations;

import co.mcme.animations.MCMEAnimations;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;

/**
 *
 * @author Luca
 */
public class LoopAnimation extends MCMEAnimation {

    private final animationType type = animationType.LOOP_ANIMATION;
    volatile int currentFrame;
    volatile boolean shouldStop;
    volatile boolean running;

    public LoopAnimation(File configFile) {
        super(configFile);
        currentFrame = 0;
        init();
    }

    @Override
    public void start() {
        if (!MCMEAnimations.CurrentJobs.contains(animationName)) {
            loadFrames();

            MCMEAnimations.WEPlugin.getServer().getLogger().info("Starting loop animation");
            MCMEAnimations.CurrentJobs.add(animationName);
            running = true;
            shouldStop = false;

            doJob();

        }
    }

    public void doJob() {
        final MCMEAnimationFrame f = frames.get(currentFrame);
        MCMEAnimations.WEPlugin.getServer().getScheduler().runTaskLater(MCMEAnimations.WEPlugin, new Runnable() {

            @Override
            public void run() {
//                SamplePlugin.WEPlugin.getServer().getLogger().info("Rendering " + animationName + " frame: " + currentFrame);
                CuboidClipboard clip = clipboards.get(currentFrame);
                WELoader.placeFrame(origin, clip, localWorldName);

                Vector previousMin = shape.getMinimumPoint();

                shape = new CuboidRegion(BukkitUtil.toVector(origin),
                        new Vector(Math.floor(origin.getX() + clip.getWidth()),
                                Math.floor(origin.getY() + clip.getHeight()),
                                Math.floor(origin.getZ() + clip.getLength())));

                virtual_direction = new Vector(
                        previousMin.getX() - shape.getMinimumPoint().getX(),
                        previousMin.getY() - shape.getMinimumPoint().getY(),
                        previousMin.getZ() - shape.getMinimumPoint().getZ());
                executeActions();
                if (!shouldStop) {
                    currentFrame = currentFrame == frames.size() - 1 ? 0 : currentFrame + 1;
                    doJob();
                } else {
                    running = false;
                    stop();
                }
            }
        }, f.getDuration());

    }

    @Override
    public void stop() {
        afterJobDelegate();
    }

    @Override
    public animationType getType() {
        return type;
    }

    @Override
    public void afterJobDelegate() {
        //called on stop
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

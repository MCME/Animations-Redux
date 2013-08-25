/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations.animations;

import com.mcme.marozzo.animations.AnimationAction;
import com.mcme.marozzo.animations.MCMEAnimations;
import com.mcme.marozzo.animations.actions.ChainAnimationAction;
import com.mcme.marozzo.animations.actions.ExplosionAction;
import com.mcme.marozzo.animations.actions.MovePlayersAction;
import com.mcme.marozzo.animations.triggers.AlwaysActiveTrigger;
import com.mcme.marozzo.animations.triggers.BlockInteractTrigger;
import com.mcme.marozzo.animations.triggers.PlayerChatTrigger;
import com.mcme.marozzo.animations.triggers.ShapeInteractTrigger;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Luca
 */
public abstract class MCMEAnimation {

    public enum animationType {

        ONE_TIME_ANIMATION,
        LOOP_ANIMATION,
        REVERSIBLE_ANIMATION;

        @Override
        public String toString() {
            switch (this) {
                case LOOP_ANIMATION:
                    return "loop";
                case ONE_TIME_ANIMATION:
                    return "one-time";
                case REVERSIBLE_ANIMATION:
                    return "two-way";
            }
            return super.toString();
        }
    }
    protected String schematicBaseName;
    protected File configFile;
    protected String animationName;
    protected String animationDescription;
    protected int localWorldIndex;
    protected JSONObject animationConfiguration;
    protected ArrayList<MCMEAnimationFrame> frames = new ArrayList<MCMEAnimationFrame>();
    protected ArrayList<CuboidClipboard> clipboards = new ArrayList<CuboidClipboard>();
    protected Location origin;
    protected CuboidRegion shape;
    protected volatile com.sk89q.worldedit.Vector virtual_direction;

    public MCMEAnimation(File configFile) {
        this.configFile = configFile;
    }

    ;

    public abstract void init();

    public abstract void start();

    public abstract void stop();

    public abstract void loadFrames();

    public abstract void unloadFrames();

    public abstract com.sk89q.worldedit.Vector getCenter();

    public abstract int getCurrentFrame();

    public abstract CuboidRegion getBounds();

    abstract void afterJobDelegate();

    abstract public animationType getType();

    public World getWorld() {
        return MCMEAnimations.MCMEAnimationsInstance.getServer().getWorld(MCMEAnimations.WEPlugin.getServerInterface().getWorlds().get(localWorldIndex).getName());
    }

    public String getName() {
        return animationName;
    }

    public com.sk89q.worldedit.Vector getVirtualDirection() {
        return virtual_direction;
    }

    public JSONObject getConfiguration() {
        return animationConfiguration;
    }

    public File getAnimationRoot() {
        return configFile.getParentFile();
    }

    public static MCMEAnimation createInstance(File configFile) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(configFile));
        JSONObject conf = (JSONObject) obj;
        String animationType = (String) conf.get("type");

        if (animationType.equals("one-time")) {
            OneTimeAnimation result = new OneTimeAnimation(configFile);
            return result;
        } else if (animationType.equals("two-way")) {
            TwoWayAnimation result = new TwoWayAnimation(configFile);
            return result;
        } else if (animationType.equals("loop")) {
            LoopAnimation result = new LoopAnimation(configFile);
            return result;
        } else {
            return null;
        }
    }

    ;

    public void loadConfiguration() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(configFile));

            animationConfiguration = (JSONObject) obj;

            animationName = (String) animationConfiguration.get("name");
            schematicBaseName = animationName;

            localWorldIndex = ((Long) (animationConfiguration.get("world-index"))).intValue();

            JSONArray JSONFrames = (JSONArray) animationConfiguration.get("frames");
            JSONArray JSONDurations = (JSONArray) animationConfiguration.get("durations");

            for (int i = 0; i < JSONFrames.size(); i++) {
                MCMEAnimationFrame frame = new MCMEAnimationFrame((String) JSONFrames.get(i), (Long) JSONDurations.get(i));
                frames.add(frame);
            }

            JSONArray JSONOrigin = (JSONArray) animationConfiguration.get("origin");
            origin = new Location(MCMEAnimations.WEPlugin.getServer().getWorlds().get(0), (Long) JSONOrigin.get(0), (Long) JSONOrigin.get(1), (Long) JSONOrigin.get(2));

            try {
                animationDescription = (String) animationConfiguration.get("description");
            } catch (Exception ex) {
                //Failed to load description.
                //Put in a try-catch block for backward compatibility
            }

            loadTriggers();

            loadActions();

        } catch (IOException ex) {
            Logger.getLogger(OneTimeAnimation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(OneTimeAnimation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeActions() {
        MCMEAnimations.MCMEAnimationsInstance.getServer().getScheduler().runTaskAsynchronously(MCMEAnimations.MCMEAnimationsInstance, new Runnable() {

            public void run() {
                for (AnimationAction a : MCMEAnimations.actions) {
                    if (a.getAnimationName().equals(animationName)) {
                        if (a.check()) {
                            a.performAction();
                        }
                    }
                }
            }
        });
    }

    private void loadActions() {
        JSONArray actions = (JSONArray) animationConfiguration.get("actions");
        if (null != actions) {
            for (int i = 0; i < actions.size(); i++) {
                JSONObject action = (JSONObject) actions.get(i);

                JSONObject actionType = (JSONObject) action.get("explosion");
                if (null != actionType) {
                    int frame = ((Long) actionType.get("frame")).intValue();
                    ExplosionAction exp = new ExplosionAction(this, frame);
                    MCMEAnimations.actions.add(exp);
                }

                actionType = (JSONObject) action.get("move_players");
                if (null != actionType) {
                    int frame = ((Long) actionType.get("frame")).intValue();
//                    JSONArray direction = (JSONArray) actionType.get("direction");
//                    if (null != direction) {
//                        double x = (Double) direction.get(0);
//                        double y = (Double) direction.get(1);
//                        double z = (Double) direction.get(2);
//
//                        com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(x, y, z);
//                        MovePlayersAction mpa = new MovePlayersAction(this, frame, v);
                    MovePlayersAction mpa = new MovePlayersAction(this, frame);
                    MCMEAnimations.actions.add(mpa);
//                    }
                }

                actionType = (JSONObject) action.get("chain_animation");
                if (null != actionType) {
                    int frame = ((Long) actionType.get("frame")).intValue();
                    String target = (String) actionType.get("target");
                    ChainAnimationAction caa = new ChainAnimationAction(this, frame, target);
                    MCMEAnimations.actions.add(caa);
                }
            }
        }
    }

    private void loadTriggers() {
        JSONArray interactions = (JSONArray) animationConfiguration.get("interactions");

        for (int i = 0; i < interactions.size(); i++) {
            JSONObject interaction = (JSONObject) interactions.get(i);
            JSONObject interactionType = (JSONObject) interaction.get("block_interaction");

            if (null != interactionType) {
                //BLOCK INTERACTION
                int frame = ((Long) interactionType.get("frame")).intValue();
                JSONArray blocks = (JSONArray) interactionType.get("blocks");
                ArrayList<Vector> blocksArray = new ArrayList<Vector>();
                for (int j = 0; j < blocks.size(); j++) {
                    JSONObject coordsObj = (JSONObject) blocks.get(j);
                    JSONArray coords = (JSONArray) coordsObj.get("block");
                    Vector v = new Vector(
                            ((Long) coords.get(0)).intValue(),
                            ((Long) coords.get(1)).intValue(),
                            ((Long) coords.get(2)).intValue());
                    blocksArray.add(v);
                }
                if (blocksArray.size() > 0) {
                    BlockInteractTrigger bit = new BlockInteractTrigger(this, blocksArray, frame);
                    MCMEAnimations.triggers.add(bit);
                }
                continue;
            }

            interactionType = (JSONObject) interaction.get("shape_interaction");

            if (null != interactionType) {
                //SHAPE INTERACTION
                int frame = ((Long) interactionType.get("frame")).intValue();
                ShapeInteractTrigger sit = new ShapeInteractTrigger(this, frame);
                MCMEAnimations.triggers.add(sit);
                continue;
            }

            interactionType = (JSONObject) interaction.get("player_chat");

            if (null != interactionType) {
                //PLAYER CHAT
                int frame = ((Long) interactionType.get("frame")).intValue();
                double distance = ((Double) interactionType.get("distance"));
                String message = (String) interactionType.get("message");
                PlayerChatTrigger pct = new PlayerChatTrigger(this, frame, distance, message);
                MCMEAnimations.triggers.add(pct);
                continue;
            }

            interactionType = (JSONObject) interaction.get("always_active");

            if (null != interactionType) {
                //ALWAYS ACTIVE
                AlwaysActiveTrigger aat = new AlwaysActiveTrigger(this);
                MCMEAnimations.triggers.add(aat);
                continue;
            }

        }
    }

    ;
}

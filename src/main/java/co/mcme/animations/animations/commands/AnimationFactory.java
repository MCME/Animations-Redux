package co.mcme.animations.animations.commands;

import co.mcme.animations.AnimationAction;
import co.mcme.animations.AnimationTrigger;
import co.mcme.animations.MCMEAnimations;
import co.mcme.animations.actions.ChainAnimationAction;
import co.mcme.animations.actions.ExplosionAction;
import co.mcme.animations.actions.MovePlayersAction;
import co.mcme.animations.actions.PlaySoundAction;
import co.mcme.animations.animations.MCMEAnimation;
import co.mcme.animations.animations.MCMEAnimation.animationType;
import co.mcme.animations.animations.MCMEAnimationFrame;
import co.mcme.animations.animations.MCMEClipboardStore;
import co.mcme.animations.triggers.AlwaysActiveTrigger;
import co.mcme.animations.triggers.BlockInteractTrigger;
import co.mcme.animations.triggers.PlayerChatTrigger;
import co.mcme.animations.triggers.ShapeInteractTrigger;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class AnimationFactory {

    public enum FactoryState {

        STATUS_IDLE,
        STATUS_BUILDING_FRAME,
        STATUS_BUILDING_TRIGGER,
        STATUS_STORING_CLIPS,
        STATUS_ANIMATION_SETUP,
        STATUS_BUILDING_ACTIONS
    }

    public enum AutomationState {

        STATUS_NO_AUTOMATION,
        STATUS_AUTO_FRAMES,
        STATUS_AUTO_TRIGGERS,
        STATUS_AUTO_ACTIONS
    }
    private static String animationName;
    private static String animationDescription;
//    private static int worldIndex;
    private static Vector origin;
    private static animationType type;
    private static Player owner;
    private static final ArrayList<MCMEClipboardStore> clips = new ArrayList();
    private static final ArrayList<MCMEFrameBuilder> frames = new ArrayList();
    private static final ArrayList<AnimationTrigger> triggers = new ArrayList();
    private static final ArrayList<AnimationAction> actions = new ArrayList();
    private static FactoryState status = FactoryState.STATUS_IDLE;
    private static AutomationState automationStatus = AutomationState.STATUS_NO_AUTOMATION;

    public static FactoryState getStatus() {
        return status;
    }

    public static void setStatus(Player p, FactoryState factoryStatus) {
        status = factoryStatus;
        owner = p;
    }

    public static AutomationState getAutomationStatus() {
        return automationStatus;
    }

    public static void setAutomationState(AutomationState status) {
        automationStatus = status;
    }

    public static Player getOwner() {
        return owner;
    }

    public static void clear() {
        clips.clear();
        frames.clear();
        triggers.clear();
        actions.clear();

        animationName = "";
        origin = null;
        owner = null;

        setStatus(null, FactoryState.STATUS_IDLE);
    }

    public static boolean editAnimation(Player p, String animationName) {
        clear();
        MCMEAnimation animation = null;
        for (MCMEAnimation a : MCMEAnimations.animations) {
            if (a.getName().equals(animationName)) {
                animation = a;
            }
        }
        if (null == animation) {
            p.sendMessage(ChatColor.RED + "Cannot find animation\"" + animationName + "\"!");
            return false;
        }

        AnimationFactory.animationName = animationName;
        AnimationFactory.animationDescription = (String) animation.getConfiguration().get("description");
        AnimationFactory.origin = BukkitUtil.toVector(animation.getOrigin());
        AnimationFactory.owner = p;
        AnimationFactory.type = animation.getType();
        ArrayList<MCMEClipboardStore> clipboards = new ArrayList();
        MCMEClipboardStore c = new MCMEClipboardStore();
        for (MCMEAnimationFrame f : animation.getFrames()) {
            f.getFrameName();

        }
        return true;
    }

    public static boolean automateActions(Player p, String actionType) {
        if (frames.isEmpty()) {
            p.sendMessage(ChatColor.RED + "Animation has no frames. Create some frames before adding actions");
            return false;
        }
        if (actionType.equals("move")) {
            for (int i = 0; i < frames.size(); i++) {
                newAction(p, "move-players");
                setActionFrame(p, i, i);
            }

        } else {
            p.sendMessage(ChatColor.RED + "Invalid action type. Available types: move");
            return false;
        }

        p.sendMessage(ChatColor.BLUE + "actions created.");
        return true;
    }

    public static boolean automateTriggers(Player p, String[] strings) {
        if (frames.isEmpty()) {
            p.sendMessage(ChatColor.RED + "Animation has no frames. Create some frames before adding triggers");
            return false;
        }
        if (strings[2].equals("shape")) {
            newTrigger(p, "shape");
            setTriggerFrame(p, 0, 0);
            newTrigger(p, "shape");
            setTriggerFrame(p, 1, frames.size() - 1);

        } else if (strings[2].equals("chat")) {
            try {
                newTrigger(p, "player-chat");
                setPlayerChatTrigger(p, 0, strings[3], Double.parseDouble(strings[4]));
                setTriggerFrame(p, 0, 0);
                newTrigger(p, "player-chat");
                setTriggerFrame(p, 1, frames.size() - 1);
                setPlayerChatTrigger(p, 1, strings[3], Double.parseDouble(strings[4]));
            } catch (NumberFormatException ex) {
                p.sendMessage(ChatColor.RED + "You need to specify a chat command and a duration!");
                return false;
            }
        } else {
            p.sendMessage(ChatColor.RED + "Invalid trigger type. Available types: shape, chat");
            return false;
        }

        p.sendMessage(ChatColor.BLUE + strings[2] + " triggers created.");
        return true;
    }

    public static boolean automateFrames(Player p, String type, int delay) {

        if (clips.isEmpty()) {
            p.sendMessage(ChatColor.RED + "Clipboard storage is empty. Store some clipboards before creating the frames");
            return false;
        }
        animationType animation_type = null;
        if (type.equals("one-time")) {
            animation_type = animationType.ONE_TIME_ANIMATION;
        } else if (type.equals("two-way")) {
            animation_type = animationType.REVERSIBLE_ANIMATION;
        } else if (type.equals("loop")) {
            animation_type = animationType.LOOP_ANIMATION;
        } else {
            p.sendMessage(ChatColor.RED + "Invalid animation type. Available types: one-time, two-way, loop");
            return false;
        }
        switch (animation_type) {
            case ONE_TIME_ANIMATION:
                setAnimationType(p, "one-time");
                int frameIndex = 0;
                for (int i = 0; i < clips.size(); i++) {
                    MCMEClipboardStore c = clips.get(i);
                    newFrame(p);
                    setFrameClipboard(p, frameIndex, c.getSchematicName());
                    setFrameDuration(p, frameIndex, delay * frameIndex);
                    frameIndex++;
                }
                newFrame(p);
                setFrameClipboard(p, frameIndex, clips.get(clips.size() - 2).getSchematicName());
                setFrameDuration(p, frameIndex, delay * 9 + delay * frameIndex);
                frameIndex++;
                for (int i = clips.size() - 2; i >= 0; i--) {
                    MCMEClipboardStore c = clips.get(i);
                    newFrame(p);
                    setFrameClipboard(p, frameIndex, c.getSchematicName());
                    setFrameDuration(p, frameIndex, delay * frameIndex + delay * 10);
                }
                break;
            case REVERSIBLE_ANIMATION:
                setAnimationType(p, "two-way");
                frameIndex = 0;
                for (int i = 0; i < clips.size(); i++) {
                    MCMEClipboardStore c = clips.get(i);
                    newFrame(p);
                    setFrameClipboard(p, frameIndex, c.getSchematicName());
                    setFrameDuration(p, frameIndex, delay * frameIndex);
                    frameIndex++;
                }
                break;
            case LOOP_ANIMATION:
                setAnimationType(p, "loop");
                frameIndex = 0;
                for (int i = 0; i < clips.size(); i++) {
                    MCMEClipboardStore c = clips.get(i);
                    newFrame(p);
                    setFrameClipboard(p, frameIndex, c.getSchematicName());
                    setFrameDuration(p, frameIndex, delay);
                    frameIndex++;
                }
                break;
        }
        p.sendMessage(ChatColor.BLUE + type + " frames created.");
        return true;
    }

    public static boolean deleteClip(Player p, String frameName) {
        for (MCMEClipboardStore cs : clips) {
            if (cs.getSchematicName().equals(frameName)) {
                if (cs.getUses() == 0) {
                    clips.remove(cs);
                    return true;
                } else {
                    p.sendMessage(ChatColor.RED + "Clipboard is used in one or more frames. Delete the frames or reset the creation process");
                    return false;
                }
            }
        }
        p.sendMessage(ChatColor.RED + "Could not find clipboard " + frameName + "!");
        return false;
    }

    public static boolean storeClip(Player p, String frameName) {
        LocalSession s = MCMEAnimations.WEPlugin.getSession(p);
        try {
//            Selection sel = MCMEAnimations.WEPlugin.getSelection(p);
//            if(null == sel){
//                p.sendMessage(ChatColor.RED + "Select a placement block using WorldEdit!");
//                return false;
//            }
//            if(sel.getArea()!=1){
//                p.sendMessage(ChatColor.RED + "Select a single placement block using WorldEdit!");
//                return false;
//            }
            CuboidClipboard clip = s.getClipboard();
            boolean alreadyThere = false;
            for (MCMEClipboardStore cs : clips) {
                if (cs.getSchematicName().equals(frameName)) {
                    cs.setClip(clip);
                    p.sendMessage(ChatColor.BLUE + "Clipboard named \"" + frameName + "\" replaced.");
                    alreadyThere = true;
                    break;
                }
            }
            if (!alreadyThere) {
                MCMEClipboardStore store = new MCMEClipboardStore();
                store.setClip(clip);
                store.setSchematicName(frameName);
                clips.add(store);
                p.sendMessage(ChatColor.BLUE + "Clipboard named \"" + frameName + "\" stored.");
            }
            s.setClipboard(null);
            return true;
        } catch (EmptyClipboardException ex) {
            p.sendMessage(ChatColor.RED + "WorldEdit clipboard is empty! Remember to /copy before storing the frame");
            return false;
            //Logger.getLogger(AnimationFacroty.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void listTriggers(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Currently stored triggers:");
        for (int i = 0; i < triggers.size(); i++) {
            AnimationTrigger t = triggers.get(i);
            if (t instanceof AlwaysActiveTrigger) {
                p.sendMessage("#" + String.valueOf(i) + " - " + t.toString());
            } else {
                p.sendMessage("#" + String.valueOf(i) + " - " + t.toString() + " on frame #" + t.getFrame());
            }
        }
    }

    public static void listActions(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Currently stored actions:");
        for (int i = 0; i < actions.size(); i++) {
            AnimationAction a = actions.get(i);
            p.sendMessage("#" + String.valueOf(i) + " - " + a.toString() + " on frame #" + a.getFrame());
        }
    }

    public static void newAction(Player p, String actionType) {
        AnimationAction a = null;
        if (actionType.equals("explosion")) {
            a = new ExplosionAction(null, -1);
        }
        if (actionType.equals("move-players")) {
            a = new MovePlayersAction(null, -1);
        }
        if (actionType.equals("chain-animation")) {
            a = new ChainAnimationAction(null, -1, "");
        }
        if (actionType.equals("play-sound")) {
            a = new PlaySoundAction(null, -1, "", -1);
        }
        if (null == a) {
            p.sendMessage(ChatColor.RED + "Could not create action! Available actions: <explosion|move-players>");
            return;
        }
        actions.add(a);
        p.sendMessage(ChatColor.BLUE + a.toString() + " created at index #" + String.valueOf(actions.size() - 1));
    }

    public static void deleteAction(Player p, int index) {
        try {
            actions.remove(index);
            p.sendMessage(ChatColor.BLUE + "Action removed.");
        } catch (IndexOutOfBoundsException ex) {
            p.sendMessage(ChatColor.RED + "Could not remove action at index #" + String.valueOf(index) + "!");
        }
    }

    public static void setActionFrame(Player p, int actionIndex, int frame) {
        try {
            AnimationAction a = actions.get(actionIndex);
            a.setFrame(frame);
            p.sendMessage(ChatColor.BLUE + "Action frame set.");
        } catch (Exception ex) {
            p.sendMessage(ChatColor.RED + "Could not set activation frame for action at index #" + String.valueOf(actionIndex) + "!");
        }
    }

    public static void listAvailableSounds(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "---------------------------");
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Available Sound constants:");
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "---------------------------");
        String sounds = "";
        for (Sound s : Sound.values()) {
            sounds += ", " + s.toString();
        }
        sounds.replaceFirst(", ", "");
        p.sendMessage(ChatColor.AQUA + sounds);
    }

    public static void setActionSound(Player p, int actionIndex, String soundName, double radius) {
        try {
            AnimationAction a = actions.get(actionIndex);
            if (a instanceof PlaySoundAction) {
                try {
                    Sound s = Sound.valueOf(soundName.toUpperCase());
                    if (s != null) {
                        ((PlaySoundAction) a).setSound(soundName);
                        ((PlaySoundAction) a).setRadius(radius);
                    }
                } catch (Exception ex) {
                    p.sendMessage(ChatColor.RED + "Sound " + soundName + " not found! For a list of available sounds, use /manage soudlist.");
                    return;
                }
                p.sendMessage(ChatColor.BLUE + "Action frame set.");
            } else {
                p.sendMessage(ChatColor.RED + "This action is not a Play Sound action!");
            }
        } catch (Exception ex) {
            p.sendMessage(ChatColor.RED + "Could not set sound for action at index #" + String.valueOf(actionIndex) + "!");
        }
    }

    public static void setActionTarget(Player p, int actionIndex, String target) {
        try {
            AnimationAction a = actions.get(actionIndex);
            if (a instanceof ChainAnimationAction) {
                ((ChainAnimationAction) a).setTarget(target);
                p.sendMessage(ChatColor.BLUE + "Action target set.");
            } else {
                p.sendMessage(ChatColor.RED + "Action #" + String.valueOf(actionIndex) + " is not a Chain_Animation action.");
            }
        } catch (Exception ex) {
            p.sendMessage(ChatColor.RED + "Could not set target animation for action at index #" + String.valueOf(actionIndex) + "!");
        }
    }

    public static void newTrigger(Player p, String triggerType) {
        AnimationTrigger t = null;
        if (triggerType.equals("always-active")) {
            t = new AlwaysActiveTrigger(null);
        }
        if (triggerType.equals("shape")) {
            t = new ShapeInteractTrigger(null, -1);
        }
        if (triggerType.equals("chat")) {
            t = new PlayerChatTrigger(null, -1, -1, "");
        }
        if (triggerType.equals("block")) {
            t = new BlockInteractTrigger(null, null, -1);
        }
        if (null == t) {
            p.sendMessage(ChatColor.RED + "Could not create trigger! Available triggers: <always-active|shape|block|chat>");
            return;
        }
        triggers.add(t);
        p.sendMessage(ChatColor.BLUE + t.toString() + " created at index #" + String.valueOf(triggers.size() - 1));
    }

    public static void deleteTrigger(Player p, int index) {
        try {
            triggers.remove(index);
            p.sendMessage(ChatColor.BLUE + "Trigger removed.");
        } catch (IndexOutOfBoundsException ex) {
            p.sendMessage(ChatColor.RED + "Could not remove trigger at index #" + String.valueOf(index) + "!");
        }
    }

    public static void setTriggerFrame(Player p, int triggerIndex, int frame) {
        try {
            AnimationTrigger t = triggers.get(triggerIndex);
            t.setFrame(frame);
            p.sendMessage(ChatColor.BLUE + "Trigger frame set.");
        } catch (Exception ex) {
            p.sendMessage(ChatColor.RED + "Could not remove set activation frame for trigger at index #" + String.valueOf(triggerIndex) + "!");
        }
    }

    public static void setPlayerChatTrigger(Player p, int triggerIndex, String message, double distance) {
        try {
            AnimationTrigger t = triggers.get(triggerIndex);
            if (!(t instanceof PlayerChatTrigger)) {
                p.sendMessage(ChatColor.RED + "Trigger at index #" + String.valueOf(triggerIndex) + " is not a Player Chat trigger!");
                return;
            }
            if (message.trim().isEmpty()) {
                p.sendMessage(ChatColor.RED + "Activation message is empty!");
                return;
            }
            ((PlayerChatTrigger) t).setData(new Object[]{distance, message});
            p.sendMessage(ChatColor.BLUE + "Player chat event stored.");

        } catch (Exception ex) {
            p.sendMessage(ChatColor.RED + "Could not set data for trigger at index #" + String.valueOf(triggerIndex) + "!");
        }
    }

    public static void setBlocksTrigger(Player p, int triggerIndex) {
        try {
            AnimationTrigger t = triggers.get(triggerIndex);
            if (!(t instanceof BlockInteractTrigger)) {
                p.sendMessage(ChatColor.RED + "Trigger at index #" + String.valueOf(triggerIndex) + " is not a Block Interaction trigger!");
                return;
            }

            Selection s = MCMEAnimations.WEPlugin.getSelection(p);
            if (s.getArea() > 16) {
                p.sendMessage(ChatColor.RED + "More than 16 blocks are selected! Create more triggers with less blocks each.");
                return;
            }
            if (s.getArea() == 0) {
                p.sendMessage(ChatColor.RED + "No blocks selected. Perform a WE selection before adding blocks to the trigger.");
                return;
            }

            ArrayList<Vector> locs = new ArrayList();

            int x1 = (int) s.getMinimumPoint().getX();
            int y1 = (int) s.getMinimumPoint().getY();
            int z1 = (int) s.getMinimumPoint().getZ();

            for (int x = x1; x < x1 + s.getWidth(); x++) {
                for (int y = y1; y < y1 + s.getHeight(); y++) {
                    for (int z = z1; z < z1 + s.getLength(); z++) {
                        Vector v = new Vector(x, y, z);
                        locs.add(v);
                    }
                }
            }

            t.setData(locs);
            p.sendMessage(ChatColor.BLUE + "Activation blocks stored.");
        } catch (Exception ex) {
            p.sendMessage(ChatColor.RED + "Could not set data for trigger at index #" + String.valueOf(triggerIndex) + "!");
        }
    }

    public static void listClips(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Currently stored clips:");
        for (MCMEClipboardStore cs : clips) {
            if (cs.getUses() != 0) {
                p.sendMessage(cs.getSchematicName() + ChatColor.ITALIC + " - used " + String.valueOf(cs.getUses()) + " time/s");
            } else {
                p.sendMessage(cs.getSchematicName() + ChatColor.ITALIC + " - unused");
            }
        }
    }

    public static void newFrame(Player p) {
        MCMEFrameBuilder fb = new MCMEFrameBuilder();
        frames.add(fb);
        p.sendMessage(ChatColor.BLUE + "New frame created.");
    }

    public static void insertFrame(Player p, int index) {
        try {
            MCMEFrameBuilder fb = new MCMEFrameBuilder();
            frames.add(index, fb);
            p.sendMessage(ChatColor.BLUE + "New frame added.");
        } catch (Exception ex) {
            p.sendMessage(ChatColor.RED + "Could not add frame at position " + String.valueOf(index) + "!");
        }
    }

    public static void setFrameDuration(Player p, int index, int duration) {
        try {
            MCMEFrameBuilder f = frames.get(index);
            f.setDuration(duration);
            p.sendMessage(ChatColor.BLUE + "Frame #" + String.valueOf(index) + " has been given a delay of " + String.valueOf(duration) + " system ticks.");
        } catch (IndexOutOfBoundsException ex) {
            p.sendMessage(ChatColor.RED + "Could not find frame " + String.valueOf(index) + "!");
        }
    }

    public static void setFrameClipboard(Player p, int index, String clipName) {
        MCMEClipboardStore theClip = null;
        for (MCMEClipboardStore cs : clips) {
            if (cs.getSchematicName().equals(clipName)) {
                theClip = cs;
                p.sendMessage(ChatColor.BLUE + "Clipboard " + clipName + " assigned to frame #" + String.valueOf(index) + ".");
                break;
            }
        }
        if (null == theClip) {
            p.sendMessage(ChatColor.RED + "Could not find clipboard " + clipName + "!");
            return;
        }
        try {
            MCMEFrameBuilder f = frames.get(index);
            if (f.getSchematic() != null) {
                f.getSchematic().setUses(f.getSchematic().getUses() - 1);
            }
            f.setSchematic(theClip);
            theClip.setUses(theClip.getUses() + 1);
        } catch (IndexOutOfBoundsException ex) {
            p.sendMessage(ChatColor.RED + "Could not find frame " + String.valueOf(index) + "!");
        }
    }

    public static void deleteFrame(Player p, int index) {
        try {
            MCMEFrameBuilder f = frames.remove(index);
            if (f.getSchematic() != null) {
                f.getSchematic().setUses(f.getSchematic().getUses() - 1);
            }
            p.sendMessage(ChatColor.BLUE + "Frame deleted.");
        } catch (IndexOutOfBoundsException e) {
            p.sendMessage(ChatColor.RED + "Could not find frame " + String.valueOf(index) + "!");
        }
    }

    public static void listFrames(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Currently stored frames:");
        for (int i = 0; i < frames.size(); i++) {
            MCMEFrameBuilder fb = frames.get(i);
            p.sendMessage("Frame " + String.valueOf(i) + " - Duration: " + String.valueOf(fb.getDuration()) + " Clipboard: " + fb.getSchematic());
        }
    }

    public static void setAnimationOrigin(Player p) {
        Selection s = MCMEAnimations.WEPlugin.getSelection(p);
        if (!s.getMinimumPoint().equals(s.getMaximumPoint())) {
            p.sendMessage(ChatColor.RED + "More than one block selected! Select a single block in order to set the Animation origin");
            return;
        }
        origin = BukkitUtil.toVector(s.getMinimumPoint());
        p.sendMessage(ChatColor.BLUE + "Animation origin point set to X:" + origin.getX() + " Y:" + origin.getY() + " Z:" + origin.getZ());
    }

    public static void setAnimationType(Player p, String animType) {
        if (animType.equalsIgnoreCase("one-time")) {
            type = animationType.ONE_TIME_ANIMATION;
        } else if (animType.equalsIgnoreCase("two-way")) {
            type = animationType.REVERSIBLE_ANIMATION;
        } else if (animType.equalsIgnoreCase("loop")) {
            type = animationType.LOOP_ANIMATION;
        } else {
            p.sendMessage(ChatColor.RED + "Unknown animation type. Available types are: one-time, two-way, loop");
            return;
        }
        p.sendMessage(ChatColor.BLUE + "Animation type set to " + animType);
    }

    public static void setAnimationName(Player p, String name) {
        if (name.trim().contains(" ")) {
            p.sendMessage(ChatColor.RED + "Animation name should not contain any space!");
            return;
        }
        animationName = name;
        p.sendMessage(ChatColor.BLUE + "Animation name set to " + name);
    }

    public static void setAnimationDescription(Player p, String[] allArguments) {
        if (allArguments.length > 1) {
            String des = "";
            for (int i = 1; i < allArguments.length; i++) {
                des += " " + allArguments[i];
            }
            des = des.trim();
            animationDescription = des;
            p.sendMessage(ChatColor.BLUE + "Animation description stored.");
        } else {
            p.sendMessage(ChatColor.RED + "Animation description is empty!");
        }
    }

    public static boolean saveAnimationData(Player p) {
        //Perform Animation integrity checks
        if (animationName.trim().isEmpty()) {
            p.sendMessage(ChatColor.RED + "Animation name has not been set. Use /anim name while in Animation setup mode to set up the animation name");
            return false;
        }

        if ((null == animationDescription) || (animationDescription.trim().isEmpty())) {
            p.sendMessage(ChatColor.RED + "The animation needs a description. Use /anim description while in Animation setup mode to set up the animation description");
            return false;
        }

        if (null == origin) {
            p.sendMessage(ChatColor.RED + "Origin has not been set. Use /anim origin while in Animation setup mode to set up the animation origin");
            return false;
        }

        if (null == type) {
            p.sendMessage(ChatColor.RED + "Animation type has not been set. Use /anim type while in Animation setup mode to set up the animation type");
            return false;
        }

        if (frames.isEmpty()) {
            p.sendMessage(ChatColor.RED + "The Animation doesn't contain any Frame!");
            return false;
        }

        if (clips.isEmpty()) {
            p.sendMessage(ChatColor.RED + "The Animation doesn't contain any Clipboard!");
            return false;
        }
        if (triggers.isEmpty()) {
            p.sendMessage(ChatColor.RED + "The Animation doesn't contain any Trigger!");
            return false;
        }
        //Save all the schematics
        File animationFolder = new File(MCMEAnimations.MCMEAnimationsInstance.getDataFolder() + File.separator + "schematics" + File.separator + "animations" + File.separator + animationName);
        if (animationFolder.exists()) {
            try {
                delete(animationFolder);
            } catch (IOException ex) {
                Logger.getLogger(AnimationFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        animationFolder.mkdirs();

        for (MCMEClipboardStore cs : clips) {
            if (cs.getUses() > 0) {
                saveClipboardToFile(cs.getClip(), cs.getSchematicName(), animationFolder);
            }
        }

        //Save the configuration file
        JSONObject configuration = new JSONObject();
        configuration.put("name", animationName);
        configuration.put("world-index", p.getWorld().getName());
        JSONArray frameList = new JSONArray();
        JSONArray durationList = new JSONArray();
        for (int i = 0; i < frames.size(); i++) {
            frameList.add(frames.get(i).getSchematic().getSchematicName());
            durationList.add(frames.get(i).getDuration());
        }
        configuration.put("frames", frameList);
        configuration.put("durations", durationList);

        JSONArray originPoints = new JSONArray();
        originPoints.add((int) Math.floor(origin.getX()));
        originPoints.add((int) Math.floor(origin.getY()));
        originPoints.add((int) Math.floor(origin.getZ()));
        configuration.put("origin", originPoints);

        configuration.put("type", type.toString());

        JSONArray interactions = new JSONArray();
        for (AnimationTrigger at : triggers) {
            interactions.add(at.toJSON());
        }

        configuration.put("interactions", interactions);

        JSONArray animationActions = new JSONArray();
        for (AnimationAction a : actions) {
            animationActions.add(a.toJSON());
        }
        if (animationActions.size() > 0) {
            configuration.put("actions", animationActions);
        }

        configuration.put("creator", owner.getDisplayName());
        configuration.put("description", animationDescription);

        try {
            FileWriter fw = new FileWriter(new File(MCMEAnimations.MCMEAnimationsInstance.getDataFolder() + File.separator + "schematics" + File.separator + "animations" + File.separator + animationName + File.separator + "conf.json"));
            fw.write(configuration.toJSONString());
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            p.sendMessage("Error saving Animation configuration file!");
            return false;
        }

        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Animation " + animationName + " saved and ready to run! Use /anim reset to reload the configuration.");
        return true;
    }

    public static void animationInfo(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "----==== CURRENT ANIMATION INFO ===---");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Owner:" + ChatColor.RESET + "" + ChatColor.AQUA + owner.getDisplayName());
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Name:" + ChatColor.RESET + "" + ChatColor.AQUA + animationName);
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Description:" + ChatColor.RESET + "" + ChatColor.AQUA + animationDescription);
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Origin:" + ChatColor.RESET + "" + ChatColor.AQUA + origin);
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Type:" + ChatColor.RESET + "" + ChatColor.AQUA + type);
        p.sendMessage("--------------------------------------");
        listFrames(p);
        p.sendMessage("--------------------------------------");
        listTriggers(p);
        p.sendMessage("--------------------------------------");
        listActions(p);
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "----===============================---");
    }

    private static void saveClipboardToFile(CuboidClipboard clip, String name, File animationFolder) {
        SchematicFormat sf = SchematicFormat.MCEDIT;
        try {
            File data = new File(animationFolder + File.separator + name + ".schematic");
            sf.save(clip, data);
            //p.sendMessage("File " + data.getAbsolutePath() + " saved!");
        } catch (IOException | DataException ex) {
            Logger.getLogger(AnimationFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //UTILITIES
    private static LocalWorld worldToLocalWorld(World w) {
        String name = w.getName();
        for (LocalWorld lw : MCMEAnimations.WEPlugin.getServerInterface().getWorlds()) {
            if (lw.getName().equals(name)) {
                return lw;
            }
        }
        return null;
    }

    //RECURSIVE FILE DELETION
    public static void delete(File file) throws IOException {

        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length == 0) {
                file.delete();
                System.out.println("Directory is deleted : " + file.getAbsolutePath());
            } else {
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println("Directory is deleted : " + file.getAbsolutePath());
                }
            }
        } else {
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }
}

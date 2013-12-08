package co.mcme.animations.triggers;

import co.mcme.animations.AnimationTrigger;
import co.mcme.animations.animations.MCMEAnimation;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class BlockInteractTrigger implements AnimationTrigger {

    MCMEAnimation parent;
    int frame;
    ArrayList<Vector> triggerLocation = new ArrayList();

    public BlockInteractTrigger(MCMEAnimation parent, ArrayList<Vector> loc, int frame) {
        this.parent = parent;
        this.frame = frame;
        this.triggerLocation = loc;
    }

    @Override
    public int getFrame() {
        return frame;
    }

    @Override
    public boolean check(Location location) {
//        SamplePlugin.WEPlugin.getServer().getLogger().info("--------------------");
//        SamplePlugin.WEPlugin.getServer().getLogger().info("Checking block trigger... ");
//        SamplePlugin.WEPlugin.getServer().getLogger().info("frame:" + frame);
//        SamplePlugin.WEPlugin.getServer().getLogger().info("X:" + triggerLocation.getX());
//        SamplePlugin.WEPlugin.getServer().getLogger().info("Y:" + triggerLocation.getY());
//        SamplePlugin.WEPlugin.getServer().getLogger().info("Z:" + triggerLocation.getZ());

        for (Vector v : triggerLocation) {
            if ((parent.getCurrentFrame() == frame)
                    && (Math.floor(v.getX()) == Math.floor(location.getX()))
                    && (Math.floor(v.getY()) == Math.floor(location.getY()))
                    && (Math.floor(v.getZ()) == Math.floor(location.getZ()))) {
                return true;
            }
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
        if (data instanceof ArrayList) {
            try {
                triggerLocation = (ArrayList<Vector>) data;
            } catch (Exception ex) {
                //Silent
            }
        }
    }

    @Override
    public String toString() {
        return "Block interaction trigger";
    }

    @Override
    public void setParent(MCMEAnimation parent) {
        this.parent = parent;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray blocks = new JSONArray();
        for (Vector v : triggerLocation) {
            JSONObject blockData = new JSONObject();
            JSONArray vect = new JSONArray();
            vect.add((int) v.getX());
            vect.add((int) v.getY());
            vect.add((int) v.getZ());
            blockData.put("block", vect);
            blocks.add(blockData);
        }
        data.put("frame", frame);
        data.put("blocks", blocks);
        result.put("block_interaction", data);
        return result;
    }

    @Override
    public String toHtml() {
        return String.format(template, toString(), "Block interaction on " + String.valueOf(triggerLocation.size()) + " blocks", "on frame #" + String.valueOf(frame));
    }

    @Override
    public MCMEAnimation getParent() {
        return parent;
    }
}

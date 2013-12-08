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

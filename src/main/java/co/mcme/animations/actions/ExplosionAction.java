/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.mcme.animations.actions;

import co.mcme.animations.AnimationAction;
import co.mcme.animations.animations.MCMEAnimation;
import com.sk89q.worldedit.Vector;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class ExplosionAction implements AnimationAction {

    private MCMEAnimation parent;
    private Vector epicenter;
    private int frame;

    public ExplosionAction(MCMEAnimation parent, int frame) {
        this.parent = parent;
        this.frame = frame;
    }

    public boolean check() {
        if (parent.getCurrentFrame() == frame) {
            return true;
        }
        return false;
    }

    public void performAction() {
        epicenter = parent.getBounds().getCenter();
        parent.getWorld().createExplosion(epicenter.getX(), epicenter.getY(), epicenter.getZ(), 0);
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public String getAnimationName() {
        return parent.getName();
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
        result.put("explosion", data);
        return result;
    }

    @Override
    public String toString() {
        return "Explosion action";
    }

    public String toHtml() {
        return String.format(template, toString(), "Create explosion centered on the shape", "on frame #"+String.valueOf(frame));
    }
}
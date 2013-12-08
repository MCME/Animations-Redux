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

    private final MCMEAnimation parent;
    private Vector epicenter;
    private int frame;

    public ExplosionAction(MCMEAnimation parent, int frame) {
        this.parent = parent;
        this.frame = frame;
    }

    @Override
    public boolean check() {
        return parent.getCurrentFrame() == frame;
    }

    @Override
    public void performAction() {
        epicenter = parent.getBounds().getCenter();
        parent.getWorld().createExplosion(epicenter.getX(), epicenter.getY(), epicenter.getZ(), 0);
    }

    @Override
    public int getFrame() {
        return frame;
    }

    @Override
    public void setFrame(int frame) {
        this.frame = frame;
    }

    @Override
    public String getAnimationName() {
        return parent.getName();
    }

    @Override
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

    @Override
    public String toHtml() {
        return String.format(template, toString(), "Create explosion centered on the shape", "on frame #" + String.valueOf(frame));
    }
}

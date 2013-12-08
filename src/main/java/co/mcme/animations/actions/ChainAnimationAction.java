package co.mcme.animations.actions;

import co.mcme.animations.AnimationAction;
import co.mcme.animations.MCMEAnimations;
import co.mcme.animations.animations.MCMEAnimation;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class ChainAnimationAction implements AnimationAction {

    private final MCMEAnimation parent;
    private int frame;
    private String targetName;

    public ChainAnimationAction(MCMEAnimation parent, int frame, String targetName) {
        this.parent = parent;
        this.frame = frame;
        this.targetName = targetName;
    }

    @Override
    public boolean check() {
        return parent.getCurrentFrame() == frame;
    }

    @Override
    public void performAction() {
        for (MCMEAnimation a : MCMEAnimations.animations) {
            if (a.getName().equals(targetName)) {
                a.start();
            }
        }
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

    public String getTarget() {
        return targetName;
    }

    public void setTarget(String target) {
        targetName = target;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("frame", frame);
        data.put("target", targetName);
        result.put("chain_animation", data);
        return result;
    }

    @Override
    public String toString() {
        return "Chain Animation action";
    }

    @Override
    public String toHtml() {
        return String.format(template, toString(), "Start " + targetName, "on frame #" + String.valueOf(frame));
    }
}

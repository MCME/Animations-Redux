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

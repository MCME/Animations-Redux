/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.mcme.animations;

import co.mcme.animations.animations.MCMEAnimation;
import co.mcme.animations.animations.commands.AnimationCommands;
import co.mcme.animations.animations.commands.AnimationManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Luca
 */
public class MCMEAnimations extends JavaPlugin {

    private final TriggerListener triggerListener = new TriggerListener(this);
//    public static Player WEPlayer;
    public static WorldEditPlugin WEPlugin;
    public static MCMEAnimations MCMEAnimationsInstance;
    public static HashSet<String> CurrentJobs = new HashSet<String>();
    public static ArrayList<MCMEAnimation> animations = new ArrayList<MCMEAnimation>();
    public static ArrayList<AnimationTrigger> triggers = new ArrayList<AnimationTrigger>();
    public static ArrayList<AnimationAction> actions = new ArrayList<AnimationAction>();

    public static MCMEAnimations getInstance(){
        return (MCMEAnimations)MCMEAnimations.WEPlugin.getServer().getPluginManager().getPlugin("MCMEAnimations");
    }
    @Override
    public void onDisable() {
        getLogger().info("MCMEAnimations disabled");
    }

    @Override
    public void onEnable() {
        MCMEAnimationsInstance = this;
        final PluginManager pm = getServer().getPluginManager();

        final Plugin WEplugin = getServer().getPluginManager().getPlugin("WorldEdit");
        WEPlugin = (WorldEditPlugin) WEplugin;

        getCommand("anim").setExecutor(new AnimationCommands());
        getCommand("manage").setExecutor(new AnimationManager());

        loadAnimations();

        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().log(Level.INFO, "{0} version {1}is enabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});

        final Plugin plugin = this;
        getServer().getScheduler().runTaskLater(this, new Runnable(){

            public void run() {
                pm.registerEvents(triggerListener, plugin);
                getLogger().info("TriggerListener successfully registered.");
            }
        }, 10);
    }

    public void loadAnimations() {
        triggers.clear();
        animations.clear();
        actions.clear();
        File animationsPath = new File(MCMEAnimationsInstance.getDataFolder() + File.separator + "schematics" + File.separator + "animations");
        if(!animationsPath.exists()){
            animationsPath.mkdirs();
        }
        File[] folders = animationsPath.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        for (File f : folders) {
            File[] confFile = f.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    return pathname.getAbsolutePath().endsWith(".json");
                }
            });
            if (confFile.length > 0) {
                //generate exception.
                //More than one configuration file
            }
            try {
                animations.add(MCMEAnimation.createInstance(confFile[0]));

            } catch (IOException ex) {
                Logger.getLogger(MCMEAnimations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(MCMEAnimations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //UTILITIES

    public static double distance3D(Vector pos1, Vector pos2){
        return Math.sqrt(
                Math.pow(pos1.getX() - pos2.getX(), 2) +
                Math.pow(pos1.getY() - pos2.getY(), 2) +
                Math.pow(pos1.getZ() - pos2.getZ(), 2)
                );
    }
}

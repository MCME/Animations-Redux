/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations.animations.commands;

import com.mcme.marozzo.animations.MCMEAnimations;
import com.mcme.marozzo.animations.animations.MCMEAnimation;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 *
 * @author Luca
 */
public class AnimationManager implements CommandExecutor {

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs instanceof Player) {
            Player p = (Player) cs;

            if (strings.length == 1) {
                if (strings[0].equals("list")) {
                    listAnimations(p);
                    return true;
                } else if (strings[0].equals("export")) {
                    p.sendMessage(ChatColor.YELLOW + "Command not yet implemented.");
                    return true;
                }
            } else if (strings.length == 2) {
                if (strings[0].equals("delete")) {
                    deleteAnimation(p, strings[1]);
                    return true;
                }
            }

            showHelp(p);
            return true;
        }
        return false;
    }

    private void showHelp(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "MCME Animations manager");
        p.sendMessage(ChatColor.AQUA + "Available commands:");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "list" + ChatColor.RESET + " - lists all the loaded animations");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "delete <animation-name>" + ChatColor.RESET + " - delete the given animation");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "export" + ChatColor.RESET + " - Creates a file listing all the animations inside the plugin folder");
    }

    private void listAnimations(Player p) {
        p.sendMessage(ChatColor.BLUE + "-------=====================-------");
        p.sendMessage(ChatColor.BLUE + "       Currently loaded Animations");
        p.sendMessage(ChatColor.BLUE + "-------=====================-------");
        for (MCMEAnimation a : MCMEAnimations.animations) {
            p.sendMessage(a.getName() + " " + a.getConfiguration().get("description") + " created by " + a.getConfiguration().get("creator"));
        }
        p.sendMessage(ChatColor.BLUE + "-------=====================-------");
    }

    private void deleteAnimation(Player p, String animationName) {
        for (MCMEAnimation a : MCMEAnimations.animations) {
            if (a.getName().equals(animationName)) {
                try {
                    MCMEAnimations.animations.remove(a);
                    AnimationFactory.delete(a.getAnimationRoot());
                    MCMEAnimations.MCMEAnimationsInstance.loadAnimations();
                    p.sendMessage(ChatColor.BLUE + a.getName() + " successfully deleted from the server.");
                } catch (IOException ex) {
                    Logger.getLogger(AnimationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            }
        }
        p.sendMessage(ChatColor.RED + "Could not find animation \"" + animationName + "\"");
    }
}

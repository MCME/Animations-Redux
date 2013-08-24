/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations;

import com.mcme.marozzo.animations.triggers.AlwaysActiveTrigger;
import com.mcme.marozzo.animations.triggers.BlockInteractTrigger;
import com.mcme.marozzo.animations.triggers.PlayerChatTrigger;
import com.mcme.marozzo.animations.triggers.ShapeInteractTrigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Luca
 */
public class TriggerListener implements Listener {

    MCMEAnimations parent;

    public TriggerListener(MCMEAnimations parent) {
        this.parent = parent;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) ||
            event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
//        MCMEAnimations.WEPlayer = event.getPlayer();
            for (AnimationTrigger at : MCMEAnimations.triggers) {
                if (at instanceof BlockInteractTrigger) {
                    if (at.check(event.getClickedBlock().getLocation())) {
                        at.trigger();
                    }
                }
                if (at instanceof ShapeInteractTrigger) {
                    if (at.check(event.getClickedBlock().getLocation())) {
                        at.trigger();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        for (AnimationTrigger at : MCMEAnimations.triggers) {
            if (at instanceof PlayerChatTrigger) {
                if (at.check(event.getPlayer().getLocation(), event.getMessage())) {
                    event.setCancelled(true);
                    at.trigger();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (AnimationTrigger at : MCMEAnimations.triggers) {
            if (at instanceof AlwaysActiveTrigger) {
                at.trigger();
            }
        }
    }
}

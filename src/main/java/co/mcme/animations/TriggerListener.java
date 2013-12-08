/*  This file is part of MCMEAnimations.
 * 
 *  MCMEAnimations is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MCMEAnimations is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MCMEAnimations.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.mcme.animations;

import co.mcme.animations.triggers.AlwaysActiveTrigger;
import co.mcme.animations.triggers.BlockInteractTrigger;
import co.mcme.animations.triggers.PlayerChatTrigger;
import co.mcme.animations.triggers.ShapeInteractTrigger;
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
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.commands.RegionCommands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Luca
 */
public class WorldEditHelper {

    private WorldEditPlugin worldEditPlugin;
    private WorldEdit worldEdit;
    private LocalPlayer localPlayer;
    private Player player;

    public WorldEditHelper(Player player) {
        this.worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = worldEditPlugin.getWorldEdit();
        this.localPlayer = worldEditPlugin.wrapPlayer(player);
        this.player = player;
    }

    public Selection getSelection() {
        return worldEditPlugin.getSelection(player);
    }

    public void replace(String args) throws CommandException, WorldEditException {
        CommandContext commandContext = null;

        commandContext = new CommandContext(args);

        LocalSession localSession = worldEdit.getSession(localPlayer);
        LocalWorld localWorld = localSession.getSelectionWorld();
        EditSession editSession = new EditSession(localWorld, -1);

        new RegionCommands(worldEdit).replace(commandContext, localSession, localPlayer, editSession); //Invoke WorldEdit's replace command to avoid using their API

    }

//    public int getReplaceInformation(String args) throws DisallowedItemException, UnknownItemException, IncompleteRegionException {
//        CommandContext commandContext = null;
//        try {
//            commandContext = new CommandContext(args);
//        } catch (CommandException e) {
//            e.printStackTrace();
//        }
//
//        LocalSession localSession = worldEdit.getSession(localPlayer);
//        LocalWorld localWorld = localSession.getSelectionWorld();
//        ExtendedEditSession editSession = new ExtendedEditSession(localWorld, -1);
//
//        Set<BaseBlock> from;
//        Pattern to;
//
//        int affected = 0;
//
//        if (commandContext.argsLength() == 1) {
//            from = null;
//            to = worldEdit.getBlockPattern(localPlayer, commandContext.getString(0));
//
//        } else {
//            from = worldEdit.getBlocks(localPlayer, commandContext.getString(0), true, !commandContext.hasFlag('f'));
//            to = worldEdit.getBlockPattern(localPlayer, commandContext.getString(1));
//        }
//
//        editSession.getBlockChangeCount(); //TODO: I don't think this needs to be here, don't have time to test right now. This was probably from a previous test.
//
//        if (to instanceof SingleBlockPattern) {
//            affected = editSession.getAffectedBlocks(localSession.getSelection(localPlayer.getWorld()), from, ((SingleBlockPattern) to).getBlock());
//        } else {
//            affected = editSession.getAffectedBlocks(localSession.getSelection(localPlayer.getWorld()), from, to);
//        }
//
//        return affected;
//}


}

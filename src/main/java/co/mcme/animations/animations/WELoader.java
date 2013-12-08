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
package co.mcme.animations.animations;

import co.mcme.animations.MCMEAnimations;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;

/**
 *
 * @author Luca
 */
public class WELoader {

    public static CuboidRegion loadBounds(String frameName, MCMEAnimation animation) {
        SchematicFormat sf = MCEditSchematicFormat.MCEDIT;
        try {
            CuboidClipboard clip = sf.load(new File(MCMEAnimations.MCMEAnimationsInstance.getDataFolder() + File.separator + "schematics" + File.separator + "animations" + File.separator + frameName + ".schematic"));

            Vector v1 = new Vector(animation.origin.getX() + clip.getOffset().getX(), animation.origin.getY() + clip.getOffset().getY(), animation.origin.getZ() + clip.getOffset().getZ());
            Vector v2 = new Vector(Math.floor(animation.origin.getX() + clip.getWidth() + clip.getOffset().getX() - 1),
                    Math.floor(animation.origin.getY() + clip.getHeight() + clip.getOffset().getY() - 1),
                    Math.floor(animation.origin.getZ() + clip.getLength() + clip.getOffset().getZ() - 1));

            return new CuboidRegion(v1, v2);

        } catch (IOException | DataException ex) {
            Logger.getLogger(WELoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static LocalWorld getLocalWorldByName(String name){
        for(LocalWorld lw : MCMEAnimations.WEPlugin.getServerInterface().getWorlds()){
            if(lw.getName().equals(name)){
                return lw;
            }
        }
        return null;
    }
    public static boolean placeFrame(Location loc, CuboidClipboard clip, String localWorldName) {
        EditSessionFactory esf = new EditSessionFactory();
        EditSession es = esf.getEditSession(getLocalWorldByName(localWorldName), 65535);

        try {
//            clip.place(es, BukkitUtil.toVector(loc), false);
            clip.paste(es, BukkitUtil.toVector(loc), false);
            es.flushQueue();
            return true;
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(WELoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static CuboidClipboard loadFrame(String frameName) {

        try {
            SchematicFormat sf = MCEditSchematicFormat.MCEDIT;
            CuboidClipboard clip = sf.load(new File(MCMEAnimations.MCMEAnimationsInstance.getDataFolder() + File.separator + "schematics" + File.separator + "animations" + File.separator + frameName + ".schematic"));
            return clip;
        } catch (IOException | DataException ex) {
            Logger.getLogger(WELoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}

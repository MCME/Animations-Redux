/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcme.marozzo.animations.animations;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Luca
 */
public class MCMEClipboardStore {

    private CuboidClipboard clip;
    private String schematicName;
    private int uses = 0;

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public CuboidClipboard getClip() {
        return clip;
    }

    public void setClip(CuboidClipboard clip) {
        this.clip = clip;
    }

    public String getSchematicName() {
        return schematicName;
    }

    public void setSchematicName(String schematicName) {
        this.schematicName = schematicName;
    }

    @Override
    public String toString() {
        return getSchematicName();
    }

}

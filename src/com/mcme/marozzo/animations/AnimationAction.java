/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations;

import org.json.simple.JSONObject;


/**
 *
 * @author Luca
 */
public interface AnimationAction {

    public boolean check();

    public void performAction();

    public int getFrame();

    public void setFrame(int frame);

    public String getAnimationName();

    public JSONObject toJSON();
}

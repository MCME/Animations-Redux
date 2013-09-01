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

    public static String template ="<tr><td>%1$s</td><td>%2$s</td><td style=\"text-align:right\">%3$s</td></tr>";

    public boolean check();

    public void performAction();

    public int getFrame();

    public void setFrame(int frame);

    public String getAnimationName();

    public JSONObject toJSON();

    public String toHtml();
}

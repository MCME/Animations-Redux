/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.marozzo.animations.animations.commands;

import com.mcme.marozzo.animations.MCMEAnimations;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Luca
 */
public class AnimationCommands implements CommandExecutor {

    private Player p;

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs instanceof Player) {
            p = (Player) cs;
        } else {
            return false;
        }

        if (strings.length == 1) {
            if (strings[0].equals("reset")) {
                MCMEAnimations.MCMEAnimationsInstance.loadAnimations();
                try {
                    if (!AnimationFactory.getOwner().equals(p) && AnimationFactory.getOwner().isOnline()) {
                        AnimationFactory.getOwner().sendMessage("WARNING!! Animation plugin has been reset by " + p.getDisplayName());
                    }
                } catch (Exception ex) {
                    //Could not find animation
                }
                AnimationFactory.clear();
                p.sendMessage(ChatColor.AQUA + "Animations successfully reset.");
                return true;
            }
        }


        if (null != AnimationFactory.getOwner()) {
            if (!AnimationFactory.getOwner().getDisplayName().equals(p.getDisplayName())) {
                p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An animation is currently being created by " + AnimationFactory.getOwner().getDisplayName());
                p.sendMessage(ChatColor.RED + "Use /anim reset to reset the plugin. WARNING: ask " + AnimationFactory.getOwner().getDisplayName() + " first!!");
            }
        }

        if (strings.length == 0) {
            switch (AnimationFactory.getStatus()) {
                case STATUS_STORING_CLIPS:
                    showClipHelp(p);
                    break;
                case STATUS_BUILDING_FRAME:
                    showFramesHelp(p);
                    break;
                case STATUS_BUILDING_TRIGGER:
                    showTriggersHelp(p);
                    break;
                case STATUS_ANIMATION_SETUP:
                    showAnimationHelp(p);
                    break;
                case STATUS_BUILDING_ACTIONS:
                    showActionsHelp(p);
                    break;
                default:
                    p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "MCMEAnimations commands usage:");
                    p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "?" + ChatColor.RESET + " - Show help on the current status");
                    p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "reset" + ChatColor.RESET + " - Reset the plugin status and reload the animation configuration files");
                    p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "switch" + ChatColor.RESET + "" + ChatColor.AQUA + " <clipboards|animation|frames|triggers|actions|idle>" + ChatColor.RESET + " - Switch plugin mode");
            }
            return true;
        }

        if (strings[0].equals("?")) {
            switch (AnimationFactory.getStatus()) {
                case STATUS_STORING_CLIPS:
                    p.sendMessage(ChatColor.AQUA + "Storing Clipboards");
                    break;
                case STATUS_BUILDING_FRAME:
                    p.sendMessage(ChatColor.AQUA + "Building Animation Frames");
                    break;
                case STATUS_BUILDING_TRIGGER:
                    p.sendMessage(ChatColor.AQUA + "Building Animation Triggers");
                    break;
                case STATUS_ANIMATION_SETUP:
                    p.sendMessage(ChatColor.AQUA + "Setting up Animation");
                    break;
                case STATUS_BUILDING_ACTIONS:
                    p.sendMessage(ChatColor.AQUA + "Building Animation Actions");
                    break;
                default:
                    p.sendMessage(ChatColor.AQUA + "Plugin in Idle status");
            }
            return true;
        }

        if (strings.length == 2) {
            if (strings[0].equals("switch")) {
                if (strings[1].equals("clipboards")) {
                    AnimationFactory.setStatus(p, AnimationFactory.FactoryState.STATUS_STORING_CLIPS);
                    p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Clipboard storing mode");
                    return true;
                } else if (strings[1].equals("animation")) {
                    AnimationFactory.setStatus(p, AnimationFactory.FactoryState.STATUS_ANIMATION_SETUP);
                    p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Animation setup mode");
                    return true;
                } else if (strings[1].equals("frames")) {
                    AnimationFactory.setStatus(p, AnimationFactory.FactoryState.STATUS_BUILDING_FRAME);
                    p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Frames building mode");
                    return true;
                } else if (strings[1].equals("triggers")) {
                    AnimationFactory.setStatus(p, AnimationFactory.FactoryState.STATUS_BUILDING_TRIGGER);
                    p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Triggers building mode");
                    return true;
                } else if (strings[1].equals("actions")) {
                    AnimationFactory.setStatus(p, AnimationFactory.FactoryState.STATUS_BUILDING_ACTIONS);
                    p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Actions building mode");
                    return true;
                } else if (strings[1].equals("idle")) {
                    AnimationFactory.setStatus(p, AnimationFactory.FactoryState.STATUS_IDLE);
                    p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Idle mode");
                    return true;
                }
            }
        }

        try {
            switch (AnimationFactory.getStatus()) {
                case STATUS_STORING_CLIPS:
                    if (strings.length == 1) {
                        if (strings[0].equals("list")) {
                            AnimationFactory.listClips(p);
                            return true;
                        }
                    } else if (strings.length == 2) {
                        if (strings[0].equals("store")) {
                            AnimationFactory.storeClip(p, strings[1]);
                            return true;
                        } else if (strings[0].equals("delete")) {
                            AnimationFactory.deleteClip(p, strings[1]);
                            return true;
                        }
                    }
                    showClipHelp(p);
                    break;
                case STATUS_BUILDING_FRAME:
                    if (strings.length == 1) {
                        if (strings[0].equals("list")) {
                            AnimationFactory.listFrames(p);
                            return true;
                        } else if (strings[0].equals("listclips")) {
                            AnimationFactory.listClips(p);
                            return true;
                        } else if (strings[0].equals("new")) {
                            AnimationFactory.newFrame(p);
                            return true;
                        }
                    } else if (strings.length == 2) {
                        if (strings[0].equals("delete")) {
                            AnimationFactory.deleteFrame(p, Integer.parseInt(strings[1]));
                            return true;
                        } else if (strings[0].equals("insert")) {
                            AnimationFactory.insertFrame(p, Integer.parseInt(strings[1]));
                            return true;
                        }
                    } else if (strings.length == 3) {
                        if (strings[0].equals("duration")) {
                            AnimationFactory.setFrameDuration(p, Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                            return true;
                        } else if (strings[0].equals("clipboard")) {
                            AnimationFactory.setFrameClipboard(p, Integer.parseInt(strings[1]), strings[2]);
                            return true;
                        }
                    }
                    showFramesHelp(p);
                    break;
                case STATUS_BUILDING_TRIGGER:
                    if (strings.length == 1) {
                        if (strings[0].equals("list")) {
                            AnimationFactory.listTriggers(p);
                            return true;
                        }
                    } else if (strings.length == 2) {
                        if (strings[0].equals("new")) {
                            AnimationFactory.newTrigger(p, strings[1]);
                            return true;
                        } else if (strings[0].equals("delete")) {
                            AnimationFactory.deleteTrigger(p, Integer.parseInt(strings[1]));
                            return true;
                        } else if (strings[0].equals("info")) {
                            return true;
                        }
                    } else if (strings.length == 3) {
                        if (strings[0].equals("set")) {
                            if (strings[2].equals("blocks")) {
                                AnimationFactory.setBlocksTrigger(p, Integer.parseInt(strings[1]));
                                return true;
                            }
                        }
                    } else if (strings.length == 4) {
                        if (strings[0].equals("set")) {
                            if (strings[1].equals("frame")) {
                                AnimationFactory.setTriggerFrame(p, Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                                return true;
                            } else {
                                AnimationFactory.setPlayerChatTrigger(p, Integer.parseInt(strings[1]), strings[2], Double.parseDouble(strings[3]));
                                return true;
                            }
                        }
                    }
                    showTriggersHelp(p);
                    break;
                case STATUS_BUILDING_ACTIONS:
                    if (strings.length == 1) {
                        if (strings[0].equals("list")) {
                            AnimationFactory.listActions(p);
                            return true;
                        }
                    } else if (strings.length == 2) {
                        if (strings[0].equals("new")) {
                            AnimationFactory.newAction(p, strings[1]);
                            return true;
                        } else if (strings[0].equals("delete")) {
                            AnimationFactory.deleteAction(p, Integer.parseInt(strings[1]));
                            return true;
                        }
                    } else if (strings.length == 4) {
                        if (strings[0].equals("set")) {
                            if (strings[1].equals("frame")) {
                                AnimationFactory.setActionFrame(p, Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                                return true;
                            }
                        }
                    }
                    showActionsHelp(p);
                    break;
                case STATUS_ANIMATION_SETUP:
                    if (strings.length == 1) {
                        if (strings[0].equals("origin")) {
                            AnimationFactory.setAnimationOrigin(p);
                            return true;
                        } else if (strings[0].equals("info")) {
                            AnimationFactory.animationInfo(p);
                            return true;
                        } else if (strings[0].equals("finish")) {
                            AnimationFactory.saveAnimationData(p);
                            return true;
                        }
                    } else if ((strings.length == 2) && (!(strings[0].equals("description")))) {
                        if (strings[0].equals("name")) {
                            AnimationFactory.setAnimationName(p, strings[1]);
                            return true;
                        } else if (strings[0].equals("type")) {
                            AnimationFactory.setAnimationType(p, strings[1]);
                            return true;
                        }
                    } else if (strings[0].equals("description")) {
                        //
                    }
                    showAnimationHelp(p);
                    break;
                default:
                    return false;
            }
        } catch (Exception ex) {
            p.sendMessage(ChatColor.RED + "Error parsing command!");
        }

        return false;
    }

    private void showClipHelp(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Storind Clipboards");
        p.sendMessage(ChatColor.AQUA + "Available commands:");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "store <framename>" + ChatColor.RESET + " - stores the current WE clipboard with a name");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "delete <framename>" + ChatColor.RESET + " - delete the given clipboard");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "list" + ChatColor.RESET + " - Lists the currently saved clipboards");
    }

    private void showFramesHelp(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Building Frames");
        p.sendMessage(ChatColor.AQUA + "Available commands:");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "new" + ChatColor.RESET + " - creates a new Frame at the end of the list");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "insert <#index>" + ChatColor.RESET + " - creates a new Frame after the given index");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "delete <#index>" + ChatColor.RESET + " - delete the given Frame");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "list" + ChatColor.RESET + " - Lists the currently saved frames");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "duration <#index> <ticks>" + ChatColor.RESET + " - Set the duration (system ticks) for the given frame");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "clipboard <#index> <clipname>" + ChatColor.RESET + " - Set the clipboard for the given frame");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "listclips" + ChatColor.RESET + " - Lists the currently saved clipboards");
    }

    private void showAnimationHelp(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Setting up Animation");
        p.sendMessage(ChatColor.AQUA + "Available commands:");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "info" + ChatColor.RESET + " - shows info on the current animation");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "origin" + ChatColor.RESET + " - sets the animation origin to the curretly selected block");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "type <one-time|two-way|loop>" + ChatColor.RESET + " - sets the animation type");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "name <animation_name>" + ChatColor.RESET + " - sets the animation name (no spaces allowed)");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "description <animation description>" + ChatColor.RESET + " - sets the animation description");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "finish" + ChatColor.RESET + " - save all data to disk and complete the animation");
    }

    private void showTriggersHelp(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Building Animation Triggers");
        p.sendMessage(ChatColor.AQUA + "Available commands:");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "list" + ChatColor.RESET + " - lists the currently stored triggers");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "new <always-active|block|shape|chat>" + ChatColor.RESET + " - creates a new trigger of given type");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "delete <#triggerID>" + ChatColor.RESET + " - deletes the trigger with the given ID");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "info <#triggerID>" + ChatColor.RESET + " - shows details on the given trigger");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "set frame <#triggerID> <#frame>" + ChatColor.RESET + " - sets the activation Frame of this trigger");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "set <#triggerID> <chat command> <#distance>" + ChatColor.RESET + " - sets the chat command word and the activation distance for the given trigger. CASE SENSITIVE");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "set <#triggerID> blocks" + ChatColor.RESET + " - add the selected WorldEdit blocks to the blocks that will trigger the animation");
    }

    private void showActionsHelp(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Building Animation Actions");
        p.sendMessage(ChatColor.AQUA + "Available commands:");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "list" + ChatColor.RESET + " - lists the currently stored actions");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "new <explosion|move-players>" + ChatColor.RESET + " - creates a new action of given type");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "delete <#actionID>" + ChatColor.RESET + " - deletes the action with the given ID");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "set frame <#actionID> <#frame>" + ChatColor.RESET + " - sets the activation Frame of this action");
    }
}

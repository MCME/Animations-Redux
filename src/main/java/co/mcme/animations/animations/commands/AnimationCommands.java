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
package co.mcme.animations.animations.commands;

import co.mcme.animations.MCMEAnimations;
import java.util.HashMap;
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

    //private Player p;
    private HashMap<String, AnimationFactory> factories = new HashMap();

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs instanceof Player) {
            Player p = (Player) cs;
            AnimationFactory playerFactory;
            if (factories.containsKey(p.getName())) {
                playerFactory = factories.get(p.getName());
            } else {
                playerFactory = new AnimationFactory();
                factories.put(p.getName(), playerFactory);
            }

            if (strings.length == 1) {
                if (strings[0].equals("reset")) {
                    MCMEAnimations.MCMEAnimationsInstance.loadAnimations();
                    playerFactory.clear();
                    p.sendMessage(ChatColor.AQUA + "Your factory was reset successfully reset.");
                    return true;
                }
            }

            if (strings.length == 0) {
                showGeneralHelp(p, playerFactory);
                return true;
            }

            if (strings[0].equals("?")) {
                showPluginStatus(p, playerFactory);
                return true;
            }

            if (strings[0].equals("auto")) {
                return manageAutomation(p, strings, playerFactory);
            }

            if (strings.length == 2) {
                if (strings[0].equals("switch")) {
                    return manageSwitch(p, strings, playerFactory);
                }
            }

            boolean success = false;
            try {
                switch (playerFactory.getStatus()) {
                    case STATUS_STORING_CLIPS:
                        success = manageStoreClips(p, strings, playerFactory);
                        break;
                    case STATUS_BUILDING_FRAME:
                        success = manageBuildingFrames(p, strings, playerFactory);
                        break;
                    case STATUS_BUILDING_TRIGGER:
                        success = manageBuildingTriggers(p, strings, playerFactory);
                        break;
                    case STATUS_BUILDING_ACTIONS:
                        success = manageBuildingActions(p, strings, playerFactory);
                        break;
                    case STATUS_ANIMATION_SETUP:
                        success = manageAnimationSetup(p, strings, playerFactory);
                        break;
                    default:
                        success = false;
                }
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Error parsing command!");
            }

            return success;
        } else {
            cs.sendMessage("You must be a player to run this command.");
            return true;
        }
    }

    /*
     * AUTOMATIONS
     */
    private boolean manageAutomation(Player p, String[] strings, AnimationFactory factory) {

        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "auto frames <one-time|two-way|loop> <#speed in system ticks>" + ChatColor.RESET + " - automatically creates all the frames needed for the stored clipboards, giving a constant frame duration of #speed. If creating a two-way animation, adds a speed * 10 pause before rolling the animation back to top.");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "auto triggers <shape|chat> [chat command] [#activation radius]" + ChatColor.RESET + " - automatically creates the needed triggers of the given type. The chat command and radius are oprional");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "auto actions <move>" + ChatColor.RESET + " - creates the needed actions");

        boolean success = false;
        try {
            if (strings[1].equals("frames")) {
                success = factory.automateFrames(p, strings[2], Integer.parseInt(strings[3]));
            }
            if (strings[1].equals("triggers")) {
                success = factory.automateTriggers(p, strings);
            }
            if (strings[1].equals("actions")) {
                success = factory.automateActions(p, strings[2]);
            }
        } catch (NumberFormatException ex) {
        }

        if (!success) {
            showAutomationHelp(p);
        }
        return success;
    }

    /*
     *
     */
    private void showGeneralHelp(Player p, AnimationFactory factory) {
        switch (factory.getStatus()) {
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
    }

    private void showPluginStatus(Player p, AnimationFactory factory) {
        switch (factory.getStatus()) {
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
    }

    private boolean manageAnimationSetup(Player p, String[] strings, AnimationFactory factory) {
        boolean success = false;
        if (strings.length == 1) {
            if (strings[0].equals("origin")) {
                factory.setAnimationOrigin(p);
                success = true;
            } else if (strings[0].equals("info")) {
                factory.animationInfo(p);
                success = true;
            } else if (strings[0].equals("finish")) {
                factory.saveAnimationData(p);
                success = true;
            }
        } else if ((strings.length == 2) && (!(strings[0].equals("description")))) {
            if (strings[0].equals("name")) {
                factory.setAnimationName(p, strings[1]);
                success = true;
            } else if (strings[0].equals("type")) {
                factory.setAnimationType(p, strings[1]);
                success = true;
            }
        } else if (strings[0].equals("description")) {
            factory.setAnimationDescription(p, strings);
            success = true;
        }
        if (!success) {
            showAnimationHelp(p);
        }
        return success;
    }

    private boolean manageBuildingActions(Player p, String[] strings, AnimationFactory factory) {
        boolean success = false;
        if (strings.length == 1) {
            if (strings[0].equals("list")) {
                factory.listActions(p);
                success = true;
            } else if (strings[0].equals("listsounds")) {
                factory.listAvailableSounds(p);
                success = true;
            }
        } else if (strings.length == 2) {
            if (strings[0].equals("new")) {
                factory.newAction(p, strings[1]);
                success = true;
            } else if (strings[0].equals("delete")) {
                factory.deleteAction(p, Integer.parseInt(strings[1]));
                success = true;
            }
        } else if (strings.length == 4) {
            if (strings[0].equals("set")) {
                if (strings[1].equals("frame")) {
                    factory.setActionFrame(p, Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                    success = true;
                } else if (strings[1].equals("target")) {
                    factory.setActionTarget(p, Integer.parseInt(strings[2]), strings[3]);
                    success = true;
                }
            }
        } else if (strings.length == 5) {
            if (strings[0].equals("set")) {
                if (strings[1].equals("sound")) {
                    factory.setActionSound(p, Integer.parseInt(strings[2]), strings[3], Double.parseDouble(strings[4]));
                    success = true;
                }
            }
        }
        if (!success) {
            showActionsHelp(p);
        }
        return success;
    }

    private boolean manageBuildingTriggers(Player p, String[] strings, AnimationFactory factory) {
        boolean success = false;
        if (strings.length == 1) {
            if (strings[0].equals("list")) {
                factory.listTriggers(p);
                success = true;
            }
        } else if (strings.length == 2) {
            if (strings[0].equals("new")) {
                factory.newTrigger(p, strings[1]);
                success = true;
            } else if (strings[0].equals("delete")) {
                factory.deleteTrigger(p, Integer.parseInt(strings[1]));
                success = true;
            } else if (strings[0].equals("info")) {
                success = true;
            }
        } else if (strings.length == 3) {
            if (strings[0].equals("set")) {
                if (strings[2].equals("blocks")) {
                    factory.setBlocksTrigger(p, Integer.parseInt(strings[1]));
                    success = true;
                }
            }
        } else if (strings.length == 4) {
            if (strings[0].equals("set")) {
                if (strings[1].equals("frame")) {
                    factory.setTriggerFrame(p, Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                    success = true;
                } else {
                    factory.setPlayerChatTrigger(p, Integer.parseInt(strings[1]), strings[2], Double.parseDouble(strings[3]));
                    success = true;
                }
            }
        }
        if (!success) {
            showTriggersHelp(p);
        }
        return success;
    }

    private boolean manageBuildingFrames(Player p, String[] strings, AnimationFactory factory) {
        boolean success = false;
        if (strings.length == 1) {
            if (strings[0].equals("list")) {
                factory.listFrames(p);
                success = true;
            } else if (strings[0].equals("listclips")) {
                factory.listClips(p);
                success = true;
            } else if (strings[0].equals("new")) {
                factory.newFrame(p);
                success = true;
            }
        } else if (strings.length == 2) {
            if (strings[0].equals("delete")) {
                factory.deleteFrame(p, Integer.parseInt(strings[1]));
                success = true;
            } else if (strings[0].equals("insert")) {
                factory.insertFrame(p, Integer.parseInt(strings[1]));
                success = true;
            }
        } else if (strings.length == 3) {
            if (strings[0].equals("duration")) {
                factory.setFrameDuration(p, Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                success = true;
            } else if (strings[0].equals("clipboard")) {
                factory.setFrameClipboard(p, Integer.parseInt(strings[1]), strings[2]);
                success = true;
            }
        }
        if (!success) {
            showFramesHelp(p);
        }
        return success;
    }

    private boolean manageStoreClips(Player p, String[] strings, AnimationFactory factory) {
        boolean success = false;
        if (strings.length == 1) {
            if (strings[0].equals("list")) {
                factory.listClips(p);
                success = true;
            }
        } else if (strings.length == 2) {
            if (strings[0].equals("store")) {
                factory.storeClip(p, strings[1]);
                success = true;
            } else if (strings[0].equals("delete")) {
                factory.deleteClip(p, strings[1]);
                success = true;
            }
        }
        if (!success) {
            showClipHelp(p);
        }
        return success;
    }

    private boolean manageSwitch(Player p, String[] strings, AnimationFactory factory) {
        if (strings[1].equals("clipboards")) {
            factory.setStatus(p, AnimationFactory.FactoryState.STATUS_STORING_CLIPS);
            p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Clipboard storing mode");
            return true;
        } else if (strings[1].equals("animation")) {
            factory.setStatus(p, AnimationFactory.FactoryState.STATUS_ANIMATION_SETUP);
            p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Animation setup mode");
            return true;
        } else if (strings[1].equals("frames")) {
            factory.setStatus(p, AnimationFactory.FactoryState.STATUS_BUILDING_FRAME);
            p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Frames building mode");
            return true;
        } else if (strings[1].equals("triggers")) {
            factory.setStatus(p, AnimationFactory.FactoryState.STATUS_BUILDING_TRIGGER);
            p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Triggers building mode");
            return true;
        } else if (strings[1].equals("actions")) {
            factory.setStatus(p, AnimationFactory.FactoryState.STATUS_BUILDING_ACTIONS);
            p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Actions building mode");
            return true;
        } else if (strings[1].equals("idle")) {
            factory.setStatus(p, AnimationFactory.FactoryState.STATUS_IDLE);
            p.sendMessage(ChatColor.DARK_PURPLE + "Switching to Idle mode");
            return true;
        }
        return false;
    }

    private void showAutomationHelp(Player p) {
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "*************************");
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Automation mode commands");
        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "*************************");
        p.sendMessage(ChatColor.AQUA + "Available commands:");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "auto frames <one-time|two-way|loop> <#speed in system ticks>" + ChatColor.RESET + " - automatically creates all the frames needed for the stored clipboards, giving a constant frame duration of #speed. If creating a two-way animation, adds a speed * 10 pause before rolling the animation back to top.");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "auto triggers <shape|chat> [chat command] [#activation radius]" + ChatColor.RESET + " - automatically creates the needed triggers of the given type. The chat command and radius are oprional");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "auto actions <move>" + ChatColor.RESET + " - creates the needed actions");
        p.sendMessage(ChatColor.AQUA + "Refer to the Google Doc for details.");
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
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "listsounds" + ChatColor.RESET + " - lists all the available sounds");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "new <explosion|move-players|chain-animation|play-sound>" + ChatColor.RESET + " - creates a new action of given type");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "delete <#actionID>" + ChatColor.RESET + " - deletes the action with the given ID");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "set frame <#actionID> <#frame>" + ChatColor.RESET + " - sets the activation Frame of this action");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "set target <#actionID> <targetname>" + ChatColor.RESET + " - sets the target animation of this action");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "set sound <#actionID> <soundName> <#sound radius>" + ChatColor.RESET + " - sets the sound for this action");
    }
}

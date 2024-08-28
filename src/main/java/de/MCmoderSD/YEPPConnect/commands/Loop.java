package de.MCmoderSD.YEPPConnect.commands;

import de.MCmoderSD.YEPPConnect.core.YEPPConnect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Loop implements CommandExecutor, TabExecutor {

    // Associations
    private final YEPPConnect yeppConnect;

    // Attributes
    private final List<String> defaultArgs;
    private final List<String> args;


    // Constructor
    public Loop(YEPPConnect yeppConnect) {

        // Initialize Associations
        this.yeppConnect = yeppConnect;

        // Initialize Arguments
        defaultArgs = Arrays.stream(new String[] {"true", "false"}).collect(Collectors.toCollection(ArrayList::new));
        args = Arrays.stream(new String[] {"enable", "disable", "hold", "pause", "resume"}).collect(Collectors.toCollection(ArrayList::new));
        args.addAll(defaultArgs);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // Check Permissions
        if (!commandSender.isOp()) {
            commandSender.sendMessage("You do not have permission to use this command.");
            return false;
        }

        // Check Syntax
        if (strings.length < 1 ) return false;

        String verb = strings[0].toLowerCase();

        // Check Verb
        if (!args.contains(verb)) return false;

        switch (verb) {
            case "true":
            case "enable":
            case "resume":
                yeppConnect.setLoopActive(true);
                commandSender.sendMessage("YEPPConnect update loop has been enabled.");
                return true;
            case "false":
            case "disable":
            case "hold":
            case "pause":
                yeppConnect.setLoopActive(false);
                commandSender.sendMessage("YEPPConnect update loop has been disabled.");
                return true;
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) return defaultArgs;
        return Collections.emptyList();
    }
}
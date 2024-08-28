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

public class Whitelist implements CommandExecutor, TabExecutor {

    // Associations
    private final YEPPConnect yeppConnect;

    // Attributes
    private final List<String> defaultVerbs;
    private final List<String> verbs;
    private final List<String> defaultOptions;
    private final List<String> options;

    public Whitelist(YEPPConnect yeppConnect) {

        // Initialize Associations
        this.yeppConnect = yeppConnect;

        // Initialize Verbs
        defaultVerbs = Arrays.stream(new String[] {"add", "remove"}).collect(Collectors.toCollection(ArrayList::new));
        verbs = Arrays.stream(new String[] {"delete"}).collect(Collectors.toCollection(ArrayList::new));
        verbs.addAll(defaultVerbs);

        // Initialize Options
        defaultOptions = Arrays.stream(new String[] {"name", "id"}).collect(Collectors.toCollection(ArrayList::new));
        options = Arrays.stream(new String[] {"broadcaster", "channel", "user", "channelid", "userid", "broadcasterid"}).collect(Collectors.toCollection(ArrayList::new));
        options.addAll(defaultOptions);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // Check Permissions
        if (!commandSender.isOp()) {
            commandSender.sendMessage("You do not have permission to use this command.");
            return false;
        }

        // Check Syntax
        if (strings.length < 3 ) return false;

        // Variables
        String verb = strings[0].toLowerCase();
        String option = strings[1].toLowerCase();
        String value = strings[2].toLowerCase();

        // Check Verb
        if (!verbs.contains(verb)) return false;
        if (!options.contains(option)) return false;
        if (value.isEmpty()) return false;

        switch (verb) {
            case "add":
                return edit(commandSender, option, value, true);
            case "remove":
            case "delete":
                return edit(commandSender, option, value, false);
            default:
                return false;
        }
    }

    private boolean edit(CommandSender commandSender, String option, String value, boolean add) {
        boolean valid;
        switch (option) {
            case "name":
            case "broadcaster":
            case "channel":
            case "user":
                valid = add ? yeppConnect.addChannel(value) : yeppConnect.removeChannel(value);
                if (valid) commandSender.sendMessage("Broadcaster " + value + " has been " + (add ? "added" : "removed") + " to the update loop.");
                else commandSender.sendMessage("Broadcaster " + value + " does not exist.");
                return true;
            case "id":
            case "channelid":
            case "userid":
            case "broadcasterid":
                valid = add ? yeppConnect.addChannel(Integer.parseInt(value)) : yeppConnect.removeChannel(Integer.parseInt(value));
                if (valid) commandSender.sendMessage("Channel ID " + value + " has been " + (add ? "added" : "removed") + " to the update loop.");
                else commandSender.sendMessage("Channel ID " + value + " does not exist.");
                return true;
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) return defaultVerbs;
        if (strings.length == 2) return defaultOptions;
       return Collections.emptyList();
    }
}

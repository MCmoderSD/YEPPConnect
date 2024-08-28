package de.MCmoderSD.YEPPConnect.core;

import de.MCmoderSD.YEPPConnect.utilities.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static de.MCmoderSD.YEPPConnect.utilities.other.Calculate.*;

public final class YEPPConnect {

    // Associations
    private final JavaPlugin plugin;
    private final MySQL mySQL;

    // Attributes
    private final Set<Integer> channels;
    private final HashMap<Integer, Set<OfflinePlayer>> channelCache;

    // Variables
    private boolean loopActive;

    // Constructor
    public YEPPConnect(JavaPlugin plugin) {

        // Initialize Associations
        this.plugin = plugin;

        // Check if config.yml exists, if not create it
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) generateConfig(configFile);

        // Load Config
        FileConfiguration config = plugin.getConfig();

        // Initialize MySQL
        String host = "MCmoderSD.de";
        int port = 3306;
        String database = "YEPPBot";
        String username = "YEPPConnect";
        String password = "YEPPConnect";
        mySQL = new MySQL(host, port, database, username, password);

        // Initialize Channels
        channels = new HashSet<>();

        // Load Broadcaster IDs
        config.getStringList("BroadcasterIDs").forEach(id -> {
            int channel_id = Integer.parseInt(id);
            if (mySQL.channelExists(channel_id)) channels.add(channel_id);
            else System.err.println("Channel " + id + " does not exist.");
        });

        // Load Broadcaster Names
        config.getStringList("BroadcasterNames").forEach(name -> {
            int channel_id = mySQL.queryID("users", name.toLowerCase());
            if (channel_id != -1 && mySQL.channelExists(channel_id)) channels.add(channel_id);
            else System.err.println("Channel " + name + " does not exist.");
        });

        // Initialize Cache
        channelCache = new HashMap<>();

        // Initialize Variables
        loopActive = config.getBoolean("LoopActive");

        // Update Config
        updateConfig();

        // Debugging
        System.out.println("Channels: ");
        channels.forEach(id -> System.out.println(" - " + mySQL.queryName("users", id) + " [" + id + "]"));

        // Initialize Update Loop
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::update, 0, 50, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void generateConfig(File configFile) {
        configFile.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {

            // Write to File
            writer.write("# YEPPConnect Configuration\n\n");
            writer.write("LoopActive: true");
            writer.write("\n\nBroadcasterNames:\n");
            writer.write(" - YEPPBot\n");
            writer.write(" - ModersEsel\n");
            writer.write(" - ChatTheRhino\n");
            writer.write("\n\nBroadcasterIDs:\n");
            writer.write(" - 639299314\n");
            writer.write(" - 644984959\n");
            writer.write(" - 786129492\n");

            // Debugging
            System.out.println("config.yml has been created.");
        } catch (IOException e) {
            System.err.println("Error while creating config.yml: " + e.getMessage());
        }
    }

    // Update Loop
    private void update() {
        if (!loopActive && channels.isEmpty()) return;

        // Variables
        boolean changes = false;
        Set<String> oldWhitelist = getWhitelistedPlayers();

        // Iterate over Channels
        for (int id : channels) {
            if (mySQL.checkForChanges(id)) {
                changes = true;
                ArrayList<String> whitelist = mySQL.getWhitelist(id);
                Set<OfflinePlayer> players = Bukkit.getWhitelistedPlayers();
                for (String name : whitelist) players.add(getOfflinePlayer(name));
                channelCache.put(id, players);
            }
        }

        // Update Whitelist
        if (changes) for (int id : channels) {
            Set<OfflinePlayer> players = channelCache.get(id);
            if (players != null) {
                Set<OfflinePlayer> current = Bukkit.getWhitelistedPlayers();
                for (OfflinePlayer player : players) if (!current.contains(player)) player.setWhitelisted(true);
            }

            // Send Notification
            Set<String> whitelistChanges = getWhitelistChanges(oldWhitelist);
            whitelistChanges.forEach(name -> sendMessageToOPs(String.format("Added %s to the whitelist.", name)));
            whitelistChanges.forEach(name -> System.out.printf("Added %s to the whitelist.%n", name));
        }
    }
    
    private void editChannels(int channelId, boolean add) {
        if (add) {
            channels.add(channelId);
            channelCache.put(channelId, new HashSet<>());
        } else {
            channels.remove(channelId);
            channelCache.remove(channelId);
        }

        updateConfig();
    }

    private void updateConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write("# YEPPConnect Configuration\n\n");
            writer.write("LoopActive: " + (loopActive ? "true" : "false") );
            writer.write("\n\nBroadcasterNames:\n");
            for (int channelId : channels) writer.write(" - " + mySQL.queryName("users", channelId) + "\n");
            writer.write("\n\nBroadcasterIDs:\n");
            for (int channelId : channels) writer.write(" - " + channelId + "\n");
        } catch (IOException e) {
            System.err.println("Error while updating config.yml: " + e.getMessage());
        }
    }

    // Setter
    public void setLoopActive(boolean loopActive) {
        this.loopActive = loopActive;
        updateConfig();
    }

    public boolean addChannel(String name) {
        int id = mySQL.queryID("users", name.toLowerCase());
        return id != -1 && addChannel(id);
    }

    public boolean removeChannel(String name) {
        int id = mySQL.queryID("users", name.toLowerCase());
        return id != -1 && removeChannel(id);
    }

    public boolean addChannel(int channelId) {

        // Error Handling
        if (channels.contains(channelId)) {
            System.err.println("Channel " + channelId + " already exists.");
            return true;
        } else if (!mySQL.channelExists(channelId)) {
            System.err.println("Channel " + channelId + " does not exist.");
            return false;
        }

        editChannels(channelId, true);
        return true;
    }

    public boolean removeChannel(int channelId) {
        if (channels.contains(channelId)) {
            editChannels(channelId, false);
            return true;
        } else {
            System.err.println("Channel " + channelId + " does not exist.");
            return false;
        }
    }
}
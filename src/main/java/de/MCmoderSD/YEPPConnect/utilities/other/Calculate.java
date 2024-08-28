package de.MCmoderSD.YEPPConnect.utilities.other;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Calculate {

    public static void sendMessageToOPs(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) if (player.isOp()) player.sendMessage(message);
    }

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(String name) {
        return Bukkit.getOfflinePlayer(name);
    }

    public static Set<String> getWhitelistedPlayers() {
        Set<String> whitelist = new HashSet<>();
        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) whitelist.add(player.getName());
        return whitelist;
    }

    public static Set<String> getWhitelistChanges(Set<String> oldWhitelist) {
        Set<String> changes = new HashSet<>();
        for (String name : getWhitelistedPlayers()) if (!oldWhitelist.contains(name)) changes.add(name);
        return changes;
    }
}

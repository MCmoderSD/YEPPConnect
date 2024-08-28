package de.MCmoderSD.YEPPConnect.main;

import de.MCmoderSD.YEPPConnect.commands.Loop;
import de.MCmoderSD.YEPPConnect.commands.Whitelist;
import de.MCmoderSD.YEPPConnect.core.YEPPConnect;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        YEPPConnect yeppConnect = new YEPPConnect(this);

        // Register Commands
        getCommand("YEPPConnect-loop").setExecutor(new Loop(yeppConnect));
        getCommand("YEPPConnect-whitelist").setExecutor(new Whitelist(yeppConnect));
    }

    @Override
    public void onDisable() {
        getLogger().info("YEPPConnect has been disabled.");
    }

    public static void main(String[] args) {
        new Main();
    }
}

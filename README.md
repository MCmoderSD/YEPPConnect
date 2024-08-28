# YEPPConnect

## Description
This is a simple Minecraft plugin that allows players to connect to whitelist themselves via the [YEPPBot](https://github.com/MCmoderSD/YEPPBot/). <br>
The plugin is written in Java 8 and uses the [Spigot API](https://www.spigotmc.org/) to let your [YEPPBot](https://github.com/MCmoderSD/YEPPBot/) interact with your Minecraft server. <br>
That way you can easily manage your whitelist and let players join your server via your Twitch Chat without having to manually whitelist them. <br>

## Compatibility
The plugin is written in Java 8 so it should work with all Minecraft Spigot versions from 1.13 and up. <br>
It is thought only tested with Minecraft Spigot 1.21.1.
The [YEPPBot](https://github.com/MCmoderSD/YEPPBot/) needs to be at least at version 1.21.4 to work with this plugin. <br>

## Installation

1. Download the latest version of the plugin from the [releases page](https://github.com/MCmoderSD/YEPPConnect/releases/latest).
2. Put the downloaded .jar file into your server's `plugins` folder.
3. Start your server. 
4. The plugin will create a `config.yml` file in the `plugins/YEPPConnect` folder.

## Configuration
You can configure the plugin by editing the `config.yml` file in the `plugins/YEPPConnect` folder. <br>
After you edited the file, you have to reload the plugin by running `/reload` in the server console or by restarting the server.

### Configuration Options
- LoopActive: `true` or `false` - If set to `true`, the plugin will update the whitelist every tick (20 times per second). If set to `false`, the plugin won't update the whitelist automatically.
- BroadcasterNames: The names of the Twitch channels that are allowed to whitelist players. You can remove or add names to this list.
- BroadcasterIDs: The IDs of the Twitch channels that are allowed to whitelist players. You can remove or add IDs to this list.

You can get the ID on this website: [Twitch ID Finder](https://www.streamweasels.com/tools/convert-twitch-username-to-user-id/)
You don't need to add the ID if you have the name in the `BroadcasterNames`, same vise versa.

**But FIRST!!!** you have to whitelist yourself in your channel via the [YEPPBot](https://github.com/MCmoderSD/YEPPBot/) before you can add your Channel to the Update Loop!

## Commands and Permissions

You need to be an operator to use the commands.

- `/yeppconnect-loop <true|false>` - Enable or disable the automatic whitelist update loop. <br>
- `/yeppconnect-whitelist <add|remove> <name|id> <value>` - Add or remove a Broadcaster to the update loop. <br>

Example: `/yeppconnect-whitelist add name ChannelName` <br>
Example: `/yeppconnect-whitelist remove id 123456789` <br>

## Support

If you need help or have any questions, feel free to contact me on [Discord](https://www.mcmodersd.de/dc) or via [Mail](mailto:business@mcmodersd.de), you can also contact me on [Twitch](https://www.twitch.tv/mcmodersd). <br>
I respond within 24 hours, usually a lot faster. If you want to cooperate? need a version that fit your needs? <br>
Just write a [Mail](mailto:business@mcmodersd.de) to [business@mcmodersd.de](mailto:business@mcmodersd.de) <br> <br>
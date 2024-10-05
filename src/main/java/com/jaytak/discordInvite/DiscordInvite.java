package com.jaytak.discordInvite;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public final class DiscordInvite extends JavaPlugin {

    private String discordLink;
    private File historyFile;
    private final String playerPrefix = "[§2Discord Invite§r] ";

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        discordLink = getConfig().getString("DiscordLink", "No discord link has been set.");
        historyFile = new File(getDataFolder(), "history.txt");
        if (!historyFile.exists()) {
            try{
                getDataFolder().mkdirs();
                historyFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Failed to create history file.");
                throw new RuntimeException(e);
            }
        }
        boolean showLinkOnLoad = getConfig().getString("ShowLinkOnStart", "true").equalsIgnoreCase("true");
        getLogger().info("Discord Invite written by JayTAK");
        if (Objects.equals(discordLink, "BLANK-CONFIG")){
            getLogger().info("You have not yet configured the discord link. You can do so using the command 'setdiscord <link>'");
        } else if (showLinkOnLoad) {
            getLogger().info("Current Discord Link: " + discordLink);
        }

        int pluginId = 23503;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("setdiscord")) {
            if (args.length != 1){
                sender.sendMessage(playerPrefix + "Usage: /setdiscord <link>");
                return true;
            }
            String oldLink = discordLink;
            discordLink = args[0];
            if (!sender.hasPermission("discordinvite.admin")){
                sender.sendMessage(playerPrefix + "You do not have permission to use this command.");
                JTLogger(sender.getName() + " Attempted to change the discord link from " + oldLink + " to " + discordLink + " but did not have permission.");
                return true;
            }
            discordLink = args[0];
            getConfig().set("DiscordLink", discordLink);
            saveConfig();
            JTLogger(sender.getName() + " Has changed the discord link from " + oldLink + " to " + discordLink);
            sender.sendMessage(playerPrefix + "Discord link has been set to:\n" + discordLink);
            getLogger().info(sender.getName() + " Has changed the discord link to: " + discordLink);
            return true;
        }

        if (command.getName().equalsIgnoreCase("discord")) {
            if (!sender.hasPermission("discordinvite.invite")){
                sender.sendMessage(playerPrefix + "You do not have permission to use this command.");
                getLogger().info(sender.getName() + " Requested the discord link, but did not have permission.");
                JTLogger(sender.getName() + " Requested the discord link, but did not have permission.");
                return true;
            }
            if (Objects.equals(discordLink, "BLANK-CONFIG")){
                if (sender.hasPermission("discordinvite.admin")){
                    sender.sendMessage(playerPrefix + "This server has not yet set its discord link. As you are an admin, you can set the link using '/setdiscord <link>'");
                }else{
                    sender.sendMessage(playerPrefix + "This server has not yet set its discord link. Contact an admin.");
                }
                JTLogger(sender.getName() + " Requested the discord link, but it was not set.");
                getLogger().info(sender.getName() + " Requested the discord link, but it was not set.");
                return true;
            }
            sender.sendMessage(playerPrefix + "Join our discord!\n" + discordLink);
            getLogger().info(sender.getName() + " Requested and received the discord invite link!");
            JTLogger(sender.getName() + " Requested and received the discord invite link! Link Provided: " + discordLink);
            return true;
        }
        return false;
    }


    private void JTLogger(String log){
        try(FileWriter writer = new FileWriter(historyFile, true)){
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:hh:mm:ss"));
            writer.write("[" + timestamp + "] " + log + "\n");
        }
        catch (Exception e){
            getLogger().severe(playerPrefix + "Failed to log changes. Exception:\n" + e);
        }
    }
}

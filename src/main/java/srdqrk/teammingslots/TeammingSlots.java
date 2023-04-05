package srdqrk.teammingslots;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import srdqrk.teammingslots.teams.TeamCMD;
import srdqrk.teammingslots.teams.TeamListener;
import srdqrk.teammingslots.teams.TeamManager;

public final class TeammingSlots extends JavaPlugin {
    @Getter
    TeamManager teamManager;
    @Getter
    BukkitCommandManager commandManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        loadDefaultConfig();
        this.teamManager = new TeamManager(this);
        this.commandManager = new BukkitCommandManager(this);
        Bukkit.getPluginManager().registerEvents(new TeamListener(this), this);
        commandManager.registerCommand(new TeamCMD(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadDefaultConfig() {
        FileConfiguration config = getConfig();

        config.addDefault("hoyo_1.x", 0);
        config.addDefault("hoyo_1.y", 0);
        config.addDefault("hoyo_1.z", 0);

        config.addDefault("hoyo_2.x", 0);
        config.addDefault("hoyo_2.y", 0);
        config.addDefault("hoyo_2.z", 0);

        config.addDefault("hoyo_3.x", 0);
        config.addDefault("hoyo_3.y", 0);
        config.addDefault("hoyo_3.z", 0);

        config.addDefault("hoyo_1.world", "world");
        config.addDefault("hoyo_2.world", "world");
        config.addDefault("hoyo_3.world", "world");

        config.options().copyDefaults(true);
        saveConfig();
    }
}

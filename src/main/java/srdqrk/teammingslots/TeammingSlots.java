package srdqrk.teammingslots;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import org.bukkit.Bukkit;
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
        this.teamManager = new TeamManager(this);
        this.commandManager = new BukkitCommandManager(this);
        Bukkit.getPluginManager().registerEvents(new TeamListener(this), this);
        commandManager.registerCommand(new TeamCMD(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

package srdqrk.teammingslots;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandCompletionContext;
import lombok.Getter;
import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import srdqrk.teammingslots.teams.MatchCMD;
import srdqrk.teammingslots.teams.TeamCMD;
import srdqrk.teammingslots.teams.TeamListener;
import srdqrk.teammingslots.teams.TeamManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class TeammingSlots extends JavaPlugin {
    @Getter
    TeamManager teamManager;
    @Getter
    BukkitCommandManager commandManager;
    FileConfiguration configFile;
    protected static TeammingSlots instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
      this.instance = this;
        // Load config default values
        this.loadDefaultConfigFile();
        // Instance Managers and events
        this.teamManager = new TeamManager(this);
        this.commandManager = new BukkitCommandManager(this);
        Bukkit.getPluginManager().registerEvents(new TeamListener(this), this);
        this.configFile = this.getConfig();
        // Add default config
        this.configGame();


        /** Commands **/
        commandManager.registerCommand(new TeamCMD(this));
        commandManager.registerCommand(new MatchCMD());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadDefaultConfigFile() {
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


        config.addDefault("noParticipantes", new ArrayList<>());
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void configGame() {
        List<String> participantes = configFile.getStringList("participantes");
        this.commandManager.getCommandCompletions().registerAsyncCompletion("participants",
                p -> participantes.stream().collect(Collectors.toList()));
    }

    private List<String> locations(CommandCompletionContext context) {
        if (!(context.getIssuer() instanceof Player)) {
            return Collections.emptyList();
        }
        Player sender = (Player) context.getIssuer();
        List<String> completions = new ArrayList<>();
        completions.add(String.format("%.2f %.2f %.2f", sender.getLocation().getX(), sender.getLocation().getY(), sender.getLocation().getZ()));
        return completions;
    }

    public static TeammingSlots instance() {
      return TeammingSlots.instance;
    }
}

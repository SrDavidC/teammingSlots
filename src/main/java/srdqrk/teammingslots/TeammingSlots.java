package srdqrk.teammingslots;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandCompletionContext;
import lombok.Getter;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import srdqrk.teammingslots.game.Game;
import srdqrk.teammingslots.game.GameStateEnum;
import srdqrk.teammingslots.matches.MatchCMD;
import srdqrk.teammingslots.matches.MatchListener;
import srdqrk.teammingslots.matches.MatchManager;
import srdqrk.teammingslots.minigames.MinigamesCMD;
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
    @Getter
    MatchManager matchManager;
    @Getter
    Game game;
    protected static TeammingSlots instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
      this.instance = this;

        /** Load default config **/
        this.loadDefaultConfigFile();
        this.game = new Game(this, GameStateEnum.IN_SLOTS);

        /** Managers **/
        this.teamManager = new TeamManager(this);
        this.commandManager = new BukkitCommandManager(this);
        this.matchManager = new MatchManager(this);

        /** Listeners **/
        Bukkit.getPluginManager().registerEvents(new TeamListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MatchListener(this), this);
        this.configFile = this.getConfig();
        // Add default config
        this.configGame();

        /** Commands **/
        commandManager.registerCommand(new TeamCMD(this));
        commandManager.registerCommand(new MatchCMD(this.matchManager));
        commandManager.registerCommand(new MinigamesCMD());

        /**Extra **/
        System.out.println("Teaming Slots loaded");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
      System.out.println("Teaming Slots disabled");
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


        // config.addDefault("noParticipantes", new ArrayList<>());
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void configGame() {
        List<String> participantes = configFile.getStringList("participantes");
        this.commandManager.getCommandCompletions().registerAsyncCompletion("participants",
                p -> participantes.stream().collect(Collectors.toList()));
    }


    public void logStaff(String message) {
        for (Player staffPlayer : Bukkit.getOnlinePlayers()) {
            if (staffPlayer.hasPermission("teammingslots.executer")) {
                staffPlayer.sendMessage(ChatColor.YELLOW + "[INFO] " + message);
            }
        }
    }


    public static TeammingSlots instance() {
      return TeammingSlots.instance;
    }
}

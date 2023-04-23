package srdqrk.teammingslots.teams;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.Dependency;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.game.GameStateEnum;

import java.util.ArrayList;
import java.util.List;

public class TeamListener  implements Listener {

    @Dependency
    TeammingSlots instance;
    FileConfiguration configFile;
    public TeamListener(TeammingSlots instance) {
        this.instance = instance;
        this.configFile = instance.getConfig();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // if the players aren't on slots, do nothing
        if (this.instance.getGame().getGameState() != GameStateEnum.IN_SLOTS) {
            return;
        }

        Player player = e.getEntity();
        CommandSender sender = Bukkit.getConsoleSender();
        // If the player is not in 'participantes', notify
        if ((this.configFile.getList("participantes") != null && this.configFile.getList("participantes").contains(player.getName()))) {
            sender.sendMessage(ChatColor.RED + "El jugador " + player.getName() + " no se encuentra en la lista de participantes.");
            return;
        }
        // Delete from participantes and add to noParticipantes
        List<String> participantes = this.configFile.getStringList("participantes");
        List<String> noParticipantes = this.configFile.getStringList("noParticipantes");
        participantes.remove(player.getName());
        noParticipantes.add(player.getName());
        this.configFile.set("participantes", participantes);
        this.configFile.set("noParticipantes", participantes);
        // save config
        this.instance.saveConfig();
        // Broadcast to all staffs
        for (Player staffPlayer : Bukkit.getOnlinePlayers()) {
            if (staffPlayer.hasPermission("teammingslots.executer")) {
                staffPlayer.sendMessage(ChatColor.YELLOW + "[INFO]" + ChatColor.RED + " El jugador " + player.getName() +
                        " ha sido eliminado");
            }
        }
        sender.sendMessage(ChatColor.GREEN + "Se ha eliminado al jugador " + player.getName() + " de la lista de participantes");
    }
}

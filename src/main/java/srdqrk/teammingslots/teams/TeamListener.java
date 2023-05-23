package srdqrk.teammingslots.teams;

import co.aikar.commands.annotation.Dependency;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import srdqrk.teammingslots.TeammingSlots;

import java.util.List;

public class TeamListener implements Listener {

  @Dependency
  TeammingSlots instance;
  FileConfiguration configFile;

  public TeamListener(TeammingSlots instance) {
    this.instance = instance;
    this.configFile = instance.getConfig();
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    // Check if the player death occurred in the correct world
    if (!e.getEntity().getWorld().getName().equals("world")) {
      return;
    }

    Player player = e.getEntity();
    CommandSender sender = Bukkit.getConsoleSender();

    // Check if the player is in the 'participantes' list
    List<String> participantes = this.configFile.getStringList("participantes");
    if (!(participantes.contains(player.getName()))) {
      sender.sendMessage(ChatColor.RED + "El jugador " + player.getName() + " no se encuentra en la lista de participantes.");
      return;
    }

    // Remove player from 'participantes' and add to 'noParticipantes'
    participantes.remove(player.getName());
    List<String> noParticipantes = this.configFile.getStringList("noParticipantes");
    noParticipantes.add(player.getName());

    // Update the config
    this.configFile.set("participantes", participantes);
    this.configFile.set("noParticipantes", noParticipantes);

    // Save the config
    this.instance.saveConfig();

    // Broadcast to all players with 'teammingslots.executer' permission
    TeammingSlots.instance().logStaff(ChatColor.RED + "El jugador " + player.getName() +
            " ha sido eliminado.");
  }


}

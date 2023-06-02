package srdqrk.teammingslots.matches;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.game.GameStateEnum;
import org.bukkit.Location;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatchListener implements Listener {

  TeammingSlots instance;
  MatchManager matchManager;
  List<Integer> bannedItems;
  public MatchListener(TeammingSlots instance) {
    this.instance = instance;
    this.matchManager = instance.getMatchManager();

    bannedItems = new ArrayList<>();

    bannedItems.add(1);
    bannedItems.add(2);
    bannedItems.add(3);
    bannedItems.add(102);
    bannedItems.add(104);
    bannedItems.add(105);
    bannedItems.add(106);
    bannedItems.add(107);
  }

  @EventHandler
  public void onPlayerDeath(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player player
            && e.getCause().toString().equals(EntityDamageEvent.DamageCause.VOID.toString())
              &&  this.instance.getGame().getGameState() == GameStateEnum.IN_MATCH) {
        MatchPair matchPair = this.matchManager.getPlayerPair(player);
        if (matchPair != null) {
          Location location = matchPair.getPlayerSpawnPoint(player);
          player.teleport(location);
          player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        }
    }
  }
  @EventHandler
  public void onPlayerAttack(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player damager && e.getEntity() instanceof Player damaged) {
      if (this.instance.getGame().getGameState() == GameStateEnum.IN_MATCH) {
        Team damagedTeam = this.instance.getTeamManager().getPlayerTeam(damaged);
        Team damagerTeam = this.instance.getTeamManager().getPlayerTeam(damager);
        if ( !(damagedTeam.getSlot().getNumber() == damagerTeam.getSlot().getNumber())) {
          e.setCancelled(true);
        }
      }
    }
  }


  @EventHandler
  public void onPlayerMoves(PlayerMoveEvent e) {
    if (this.instance.getGame().getGameState() == GameStateEnum.STARTING_MATCH) {
      List<String> participants = TeammingSlots.instance().getConfig().getStringList("participantes");
      String playerName = e.getPlayer().getName();
      if (participants.contains(playerName)) {
        Location from = e.getFrom();
        Location to = e.getTo();

        // Comprueba si solo ha cambiado la posición en Y
        if (from.getBlockY() != to.getBlockY()) {
          return;
        }
        // Comprueba si solo ha cambiado la dirección de la vista

        if (from.getYaw() != to.getYaw() || from.getPitch() != to.getPitch()) {
          return;
        }

        // Permite el movimiento si no se cumple ninguna de las condiciones anteriores
        e.setCancelled(true);
      }
    }
  }
}




package srdqrk.teammingslots.matches;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.game.GameStateEnum;
import org.bukkit.Location;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.Objects;

public class MatchListener implements Listener {

  TeammingSlots instance;
  MatchManager matchManager;
  public MatchListener(TeammingSlots instance) {
    this.instance = instance;
    this.matchManager = instance.getMatchManager();
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

}

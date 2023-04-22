package srdqrk.teammingslots.matches;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.game.GameStateEnum;

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
            && e.getCause() == EntityDamageEvent.DamageCause.VOID
            && this.instance.getGame().getGameState() == GameStateEnum.IN_MATCH) {
        MatchPair matchPair = this.matchManager.getPlayerPair(player);
        if (matchPair != null) {
          player.teleport(matchPair.getSpawnLocation());
          player.setHealth(player.getMaxHealth());
        }
    }
  }

}

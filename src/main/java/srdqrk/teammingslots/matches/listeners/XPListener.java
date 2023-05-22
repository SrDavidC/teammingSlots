package srdqrk.teammingslots.matches.listeners;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import srdqrk.teammingslots.TeammingSlots;

public class XPListener implements Listener {

  TeammingSlots instance;
  private @Getter
  @Setter int decreasePeriod = 7;
  private @Getter
  @Setter float additionalXP;
  private @Getter
  @Setter float decreaseXP;

  private @Getter
  @Setter String worldName;

  public XPListener(float additionalXP, float decreaseXP, String worldName) {
    this.instance = TeammingSlots.instance();
    this.additionalXP = additionalXP;
    this.decreaseXP = decreaseXP;
    this.worldName = worldName;
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (event.getPlayer().getWorld().getName().equals(worldName)) {
      Player player = event.getPlayer();
      Location from = event.getFrom();
      Location to = event.getTo();
      if (from.distance(to) > 0) { // if player moves
        float currentExp = player.getExp();
        float newExp = currentExp + this.additionalXP;
        if (newExp >= 1f) { // if xp up to 1
          // player.setHealth(0);
          player.sendMessage("Moriste, mucha XP");
        } else { // if not up to one, update xp
          player.setExp(newExp);
        }
      }
    }

  }
}



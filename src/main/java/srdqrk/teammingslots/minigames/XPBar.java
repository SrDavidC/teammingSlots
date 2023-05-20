package srdqrk.teammingslots.minigames;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import srdqrk.teammingslots.TeammingSlots;

public class XPBar implements Listener {

  TeammingSlots instance;

  private int decreaseIntervalTicks = 20;
  private @Getter @Setter int decreasePeriod = 7;
  private @Getter @Setter float additionalXP;
  private @Getter @Setter float decreaseXP;

  private @Getter int task;

  public XPBar(float additionalXP, float decreaseXP ) {
    this.instance = TeammingSlots.instance();
    this.additionalXP = additionalXP;
    this.decreaseXP = decreaseXP;
  }

  public void addTask() {
    this.task = instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, () -> {
      for (Player player : instance.getServer().getOnlinePlayers()) {
        // calculate xp
        float currentExp = player.getExp();
        float newExp = currentExp - this.decreaseXP;
        // if xp negative
        if (newExp < 0) {
          // player.setHealth(0);
          player.sendMessage("Moriste, poca XP");
        } else {
          // if not negative xp, update player xp
          player.setExp(newExp);
        }
      }
    }, 20, this.decreasePeriod);
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
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

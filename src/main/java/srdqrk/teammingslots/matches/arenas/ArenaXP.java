package srdqrk.teammingslots.matches.arenas;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.game.CurrentArena;
import srdqrk.teammingslots.game.GameStateEnum;
import srdqrk.teammingslots.matches.MatchPair;

import java.util.List;

public class ArenaXP extends Arena {

  private int decreaseIntervalTicks = 20;
  private @Getter
  @Setter int decreasePeriod = 7;
  private @Getter
  @Setter float additionalXP;
  private @Getter
  @Setter float decreaseXP;
  private @Getter int task;

  public ArenaXP(List<MatchPair> pairs, @NonNull Location coords, @NonNull Vector gap, @NonNull Vector gapTeam, @NonNull CurrentArena id, @NonNull Listener listener) {
    super(pairs, coords, gap, gapTeam, id, listener);
  }

  public ArenaError start() {
    ArenaError error = ArenaError.SUCCESSFUL;
    if (!this.started) {
      this.sendTitleCountdown(5, () -> {
        // TODO: disable something that players can move
        this.addTask();
        // Initializes their Listeners
        Bukkit.getPluginManager().registerEvents(this.listener, TeammingSlots.instance());
        // Change GameStage to IN_MATCH
        TeammingSlots.instance().getGame().setGameState(GameStateEnum.IN_MATCH);
        // Change current arena
        TeammingSlots.instance().getGame().setCurrentArena(this.id);
        // Update started
        this.started = true;

        // Code to execute after the countdown has finished
        for (Player player : Bukkit.getOnlinePlayers()) {
          player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_0, 1F, 1.4F);
          player.playSound(player, Sound.BLOCK_GLASS_BREAK, 2F, 2F);
          player.sendActionBar(ChatColor.YELLOW + "¡LA MATCH HA EMPEZADO! \uD83C\uDFF9");
        }
      });
    } else {
      error = ArenaError.ALREADY_STARTED;
    }
    return error;
  }

  private void addTask() {
    this.task = TeammingSlots.instance().getServer().getScheduler().scheduleSyncRepeatingTask(TeammingSlots.instance(), () -> {
      for (Player player : TeammingSlots.instance().getServer().getOnlinePlayers()) {
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

  public void sendTitleCountdown(int number, Runnable onFinish) {
    new BukkitRunnable() {
      int count = number;

      @Override
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          if (count <= 3) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1f, 0.5f);
            player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + count, "", 10, 30, 10);
          } else {
            player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + count, "", 10, 30, 10);
          }
        }
        count--;

        if (count < 0) {
          this.cancel();
          for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_0, 1F, 1.4F);
            player.playSound(player, Sound.BLOCK_GLASS_BREAK, 2F, 2F);
            player.sendActionBar(ChatColor.YELLOW + "¡LA MATCH HA EMPEZADO! \uD83C\uDFF9"); // TODO: solve the spanglish
          }
          onFinish.run();
        }
      }
    }.runTaskTimer(TeammingSlots.instance(), 0, 20);
  }


  @Override
  public ArenaError finish() {
    ArenaError error = ArenaError.SUCCESSFUL;
    if (this.started) {
      // TP pairs to their slot
      for (MatchPair pair : this.pairs) {
        if (pair.getLeft() != null) {
          pair.getLeft().teleportTeamToOwnLocation();
        }
        if (pair.getRight() != null) {
          pair.getRight().teleportTeamToOwnLocation();
        }
      }
      // Change GameStage to IN_SLOTS
      TeammingSlots.instance().getGame().startSlots();
      // Change CurrentArena to NONE
      TeammingSlots.instance().getGame().setCurrentArena(CurrentArena.NONE);
      // Unregister listener
      HandlerList.unregisterAll(this.listener);
      // Remove actual arena from manager
      this.matchManager.setActualArena(null);
      // Cancel task
      Bukkit.getScheduler().cancelTask(this.task);
    } else {
      error = ArenaError.NOT_STARTED;
    }
    return error;
  }
}

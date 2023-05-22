package srdqrk.teammingslots.matches.arenas;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import srdqrk.teammingslots.matches.MatchManager;
import srdqrk.teammingslots.matches.MatchPair;

import java.util.List;


public class Arena {
  protected @Getter
  @Setter List<MatchPair> pairs;
  final protected @Getter Location coords;
  final protected @Getter Location oppositeCoords;
  final protected @Getter Vector gapTeam;
  final protected @Getter Vector gap;
  protected @Getter
  @Setter boolean started;
  protected @Getter
  @Setter boolean stopped;
  final public @Getter CurrentArena id;
  final public @Getter MiniMessage mm = TeammingSlots.instance().getMm();

  @Setter Listener listener;

   @Getter MatchManager matchManager;

  public Arena(List<MatchPair> pairs, @NonNull Location coords,@NonNull Vector gap,@NonNull Vector gapTeam,
               @NonNull CurrentArena id, @NonNull Listener listener) {
    this.pairs = pairs;
    this.gapTeam = gapTeam;
    this.gap = gap;
    this.coords = coords;
    this.oppositeCoords = this.coords.clone().add(gapTeam);
    this.id = id;
    this.listener = listener;
    this.started = false;
    this.stopped = false;
    this.matchManager = TeammingSlots.instance().getMatchManager();

  }

  @Override
  public String toString() {
    return "Arena{" +
            "pairs=" + pairs +
            ", coords=" + coords +
            ", gap=" + gap +
            ", started=" + started +
            ", stopped=" + stopped +
            '}';
  }

  /**
   * It makes TP to all pairs from this.pairs
   */
  public ArenaError setup() {
    // Tp all pairs
    Location firstPair_loc = this.coords;
    Location secondPair_loc = this.oppositeCoords;

    ArenaError error = ArenaError.SUCCESSFUL;
    for (MatchPair pair: this.pairs) {
      if (pair.getLeft() != null) {
        pair.getLeft().teleportTeam(firstPair_loc.clone());
        pair.setSpawnLocation(pair.getLeft().getSlot().getNumber(), firstPair_loc.clone());
      }
      if (pair.getRight() != null) {
        pair.getRight().teleportTeam(secondPair_loc);
        pair.setSpawnLocation(pair.getRight().getSlot().getNumber(), secondPair_loc.clone());
      }
      firstPair_loc.add(gap);
      secondPair_loc.add(gap);
    }
    return error;
  }

  /**
   * It initializes their Listeners
   * Change GameStage to IN_MATCH and CurrentArena to ARENA_X, being X arena number
   */
  public ArenaError start() {
    ArenaError error = ArenaError.SUCCESSFUL;
    if (!(this.started)) {
      sendTitleCountdown(5);
      // TODO: disable something that players can move
      // Initializes their Listeners
      Bukkit.getPluginManager().registerEvents(this.listener, TeammingSlots.instance());
      // Change GameStage to IN_MATCH
      TeammingSlots.instance().getGame().setGameState(GameStateEnum.IN_MATCH);
      // Change current arena
      TeammingSlots.instance().getGame().setCurrentArena(this.id);
      // Update started
      this.started = true;
    } else {
      error = ArenaError.ALREADY_STARTED;
    }
    return error;
  }

  /**
   * It makes:
   * - tp to all teams from this.pairs to their slots
   * - Change GameStage to IN_SLOTS
   * - Change CurrentArena to NONE
   * - If listeners, REMOVE it
   */
  public ArenaError finish() {
    ArenaError error = ArenaError.SUCCESSFUL;
    if (this.started) {
      // TP pairs to their slot
      for (MatchPair pair: this.pairs) {
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
    } else {
      error = ArenaError.NOT_STARTED;
    }
    return error;
  }

  /**
   * If this.stopped is false, change it true and remove the listeners
   */
  public ArenaError stop() {
    ArenaError error = ArenaError.SUCCESSFUL;
    if (this.started) {
      if (!(this.stopped) ) {
        HandlerList.unregisterAll(this.listener);
        this.stopped = true;
      } else {
        error = ArenaError.ALREADY_STOPPED;
      }
    } else {
      error = ArenaError.NOT_STARTED;
    }

    return error;
  }

  /**
   * If this.stoped is true, create again the listners. Otherwise, do nothing and advice to the sender
   */
  public ArenaError resume() {
    ArenaError error = ArenaError.SUCCESSFUL;
    if (this.stopped) {
      Bukkit.getPluginManager().registerEvents(this.listener, TeammingSlots.instance());
      this.stopped = false;
    } else {
      error = ArenaError.NOT_STOPPED;
    }
    return error;
  }




  public void sendTitleCountdown(int number) {
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
        }
      }
    }.runTaskTimer(TeammingSlots.instance(), 0, 20);
  }



}

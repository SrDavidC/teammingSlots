package srdqrk.teammingslots.matches;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.game.CurrentArena;
import srdqrk.teammingslots.matches.arenas.Arena;
import srdqrk.teammingslots.matches.arenas.ArenaError;
import srdqrk.teammingslots.teams.objects.Team;
import srdqrk.teammingslots.utils.Utils;

import java.util.List;
import java.util.Objects;


@CommandAlias("m|match|matches")
public class MatchCMD extends BaseCommand {

  final private MatchManager matchManager;

  // final private MiniMessage mm = TeammingSlots.instance().getMm();

  public MatchCMD(MatchManager matchManager) {
    this.matchManager = matchManager;
  }





  public void clearEffectsOnPlayer() {
    List<String> participants = TeammingSlots.instance().getConfig().getStringList("participantes");
    for (String pName: participants) {
      Player player = Bukkit.getPlayer(pName);
      if (player != null && player.isOnline()) {
        cleanPlayer(Objects.requireNonNull(Bukkit.getPlayer(pName)));

      }
    }
  }

  @Subcommand("finish")
  @CommandPermission("teammingslots.executer")
  @Description("Retorna todos los jugadores a su posicion de team")
  public void onForceFinish(CommandSender sender) {
    Arena arena = this.matchManager.getActualArena();
    if (arena != null && arena.isStarted()) {
      ArenaError error = arena.finish();
      System.out.println(error);
      // clears levitation
      clearEffectsOnPlayer();
      // Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "i disable " + false);
      Utils.sendTitleToAll();
    } else {
      if (arena == null) {
        sender.sendMessage(ChatColor.RED +  "No existe una arena actual. Cree una con /m create <ARENA>");
      } else if (!(arena.isStarted())) {
        sender.sendMessage(ChatColor.RED +  "La arena actual no está empezada. Empiecela con /start, espere el contador y luego " +
                "terminela con /m finish");
      }
    }

  }

  @Subcommand("start")
  @CommandPermission("teammingslots.executer")
  @Description("Empieza una match, el numero de match [1-6] representa cual match debe empezar")
  public void onStart(CommandSender sender) {
    Arena arena = this.matchManager.getActualArena();
    if (arena != null) {
      if ( !(arena.isStarted()) ) {
        System.out.println(arena.start());
      } else {
        sender.sendMessage(ChatColor.RED +  "La arena actual YA empezó. Para terminarla usa /m finish");
      }
    } else {
      sender.sendMessage(ChatColor.RED +  "No existe una arena actual");
    }
  }
  @Subcommand("stop")
  @CommandPermission("teammingslots.executer")
  @Description("")
  public void onStop(CommandSender sender) {
    Arena arena = this.matchManager.getActualArena();
    if (arena != null) {
      if (arena.isStarted()) {
        System.out.println(arena.stop());
      } else {
        sender.sendMessage(ChatColor.RED +  "La arena actual no ha empezado. Utiliza /m start para empezarla");
      }
    } else {
      sender.sendMessage(ChatColor.RED +  "No existe una arena actual");
    }
  }

  @Subcommand("resume")
  @CommandPermission("teammingslots.executer")
  @Description("")
  public void onResume(CommandSender sender) {
    Arena arena = this.matchManager.getActualArena();
    if (arena != null) {
      if (arena.isStarted()) {
        System.out.println(arena.resume());
      } else {
        sender.sendMessage(ChatColor.RED +  "La arena actual no ha empezado. Utiliza /m start para empezarla");
      }
    } else {
      sender.sendMessage(ChatColor.RED +  "No existe una arena actual");
    }
  }
  @Subcommand("create")
  @CommandPermission("teammingslots.executer")
  @Description("Empieza una match, el numero de match [1-6] representa cual match debe empezar")
  public void onCreate(CommandSender sender, CurrentArena arenaNumber) {
    if (matchManager.teamManager.getTeams().isEmpty()) {
      sender.sendMessage(ChatColor.RED + "No hay equipos creados");
      return;
    }

    if (matchManager.getActualArena() == null || !(matchManager.getActualArena().isStarted())) {
      this.matchManager.createArena(arenaNumber);
      TeammingSlots.instance().logStaff("Se ha creado una instancia de Arena: " + arenaNumber);
      this.matchManager.getActualArena().setup();
      // Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "i disable " + true);
      Utils.sendTitleToAll();
    } else {
      sender.sendMessage(ChatColor.RED +  "Ya existe una arena creada. La arena es " +
              matchManager.getActualArena().id + "\nEliminela ejecutando /match finish");
    }

  }

  @Subcommand("win")
  @CommandPermission("teammingslots.executer")
  @Description("Termina una match individual y tepea a los dos jugadores que estaban en la match a su slot del hoyo")
  public void onWin(CommandSender sender, OnlinePlayer player) {
    MatchPair matchPair = this.matchManager.getPlayerPair(player.getPlayer());
    Team team = TeammingSlots.instance().getTeamManager().getPlayerTeam(player.getPlayer());
    for (Player p: team.getPlayers()) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "i giveRandom " + p.getName());
    }
    if (matchPair != null) {
      Team t1 = matchPair.getLeft();
      Team t2 =  matchPair.getRight();
      // tp the players  to their location and clears levitation
      if (t1 != null) {
        t1.teleportTeamToOwnLocation();
        for (Player p: t1.getPlayers()) {
          cleanPlayer(p);
          Utils.sendTeleportTitle(p);
        }
      }

      if (t2 != null) {
        t2.teleportTeamToOwnLocation();
        for (Player p: t2.getPlayers()) {
          cleanPlayer(p);
          Utils.sendTeleportTitle(p);
        }
      }
      this.matchManager.playerPairs.remove(matchPair);
    } else {
      sender.sendMessage(ChatColor.RED + "El jugador " + player.getPlayer().getName() +  " tiene pareja nula. No existe o no tiene pareja");
    }
  }

  @Subcommand("info")
  @CommandPermission("teammingslots.executer")
  public void onTestFunc(Player sender) {
    String m = "";
    m+= "GameState: " + TeammingSlots.instance().getGame().getGameState();
    m+= "\nCurrentArena: " + TeammingSlots.instance().getGame().getCurrentArena();
    m+= "\nCantidad de Equipos:" + TeammingSlots.instance().getTeamManager().getTeams().size();
    m+= "\nCantidad de Parejas de Equipos: " + TeammingSlots.instance().getMatchManager().getPlayerPairs().size();
    sender.sendMessage(m);
  }
  private void cleanPlayer(@NonNull Player player) {
    for (PotionEffect activeEffect : player.getActivePotionEffects()) {
      PotionEffectType type = activeEffect.getType();

      if (type != PotionEffectType.NIGHT_VISION) {
        player.removePotionEffect(type);
      }
    }
  }



}

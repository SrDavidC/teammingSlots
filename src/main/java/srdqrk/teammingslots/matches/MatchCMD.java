package srdqrk.teammingslots.matches;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.game.CurrentArena;
import srdqrk.teammingslots.matches.arenas.Arena;
import srdqrk.teammingslots.teams.objects.Team;



@CommandAlias("m|match|matches")
public class MatchCMD extends BaseCommand {

  final private MatchManager matchManager;

  final private MiniMessage mm = TeammingSlots.instance().getMm();

  public MatchCMD(MatchManager matchManager) {
    this.matchManager = matchManager;
  }


  @Subcommand("finish")
  @CommandPermission("teammingslots.executer")
  @Description("Retorna todos los jugadores a su posicion de team")
  public void onForceFinish(CommandSender sender) {
    Arena arena = this.matchManager.getActualArena();
    if (arena != null) {
      System.out.println(arena.finish());
    } else {
      sender.sendMessage(ChatColor.RED +  "No existe una arena actual");
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
    /*
    if (matchManager.teamManager.getTeams().size() == 1) {
      sender.sendMessage(ChatColor.RED + "La cantidad de equipos debe ser mayor 1.");
      return;
    }
    */

    if (matchManager.getActualArena() == null || !(matchManager.getActualArena().isStarted())) {
      this.matchManager.createArena(arenaNumber);
      TeammingSlots.instance().logStaff("Se ha creado una instancia de Arena: " + arenaNumber);
      this.matchManager.getActualArena().setup();
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
    if (matchPair != null) {
      Team t1 = matchPair.getLeft();
      Team t2 =  matchPair.getRight();
      // tp the players  to their location
      if (t1 != null)
        t1.teleportTeamToOwnLocation();
      if (t2 != null)
        t2.teleportTeamToOwnLocation();
      this.matchManager.playerPairs.remove(matchPair);
    } else {
      sender.sendMessage(ChatColor.RED + "El jugador " + player.getPlayer().getName() +  " tiene pareja nula. No existe o no tiene pareja");
    }
  }

  @Subcommand("test1")
  @CommandPermission("teammingslots.executer")
  public void onTestFunc(Player sender) {
    MatchPair matchPair = this.matchManager.getPlayerPair(sender);
    sender.teleport(matchPair.getPlayerSpawnPoint(sender));
  }



}

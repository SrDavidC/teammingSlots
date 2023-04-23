package srdqrk.teammingslots.matches;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.game.GameStateEnum;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.*;


@CommandAlias("match|matches")
public class MatchCMD extends BaseCommand {

  final private MatchManager matchManager;

  public MatchCMD(MatchManager matchManager) {
    this.matchManager = matchManager;
  }


  @Subcommand("forcefinish")
  @CommandPermission("teammingslots.executer")
  @Description("Retorna todos los jugadores a su posicion de team")
  public void onForceFinish(CommandSender sender) {
    if (matchManager.teamManager.getTeams().isEmpty()) {
      sender.sendMessage(ChatColor.RED + "No hay equipos creados");
      return;
    }
    for (Team team : matchManager.teamManager.getTeams()) {
      team.teleportTeamToOwnLocation();
    }
    // remove past pairs, set up to the next match
    this.matchManager.playerPairs.clear();
    this.matchManager.instance.getGame().startSlots();
  }

  @Subcommand("start")
  @CommandPermission("teammingslots.executer")
  @Description("Empieza una match, el numero de match [1-6] representa cual match debe empezar")
  @CommandCompletion("@range:1-6")
  public void onStart(CommandSender sender, int arenaNumber) {
    if (matchManager.teamManager.getTeams().isEmpty()) {
      sender.sendMessage(ChatColor.RED + "No hay equipos creados");
      return;
    }

    this.matchManager.getInstance().getGame().startSlots();
    List<Team> teams = this.matchManager.getTeamManager().getTeams();
    Collections.shuffle(teams);

    int arenasGap = 10;
    int x =  this.matchManager.arenas.get(arenaNumber).getBlockX();
    int y = this.matchManager.arenas.get(arenaNumber).getBlockY();
    int z = this.matchManager.arenas.get(arenaNumber).getBlockZ();
    for (int i = 0; i < teams.size(); i += 2) {
      Team t1 = teams.get(i);
      Team t2 = teams.get(i + 1);
      Location loc1 = new Location(this.matchManager.arenas.get(arenaNumber).getWorld(), x, y + i * arenasGap, z);
      t1.teleportTeam(loc1);
      t2.teleportTeam(loc1);
      MatchPair newMatchPair = new MatchPair(t1,t2,loc1);
      this.matchManager.playerPairs.add(newMatchPair);
    }

  }

  @Subcommand("win")
  @CommandPermission("teammingslots.executer")
  @Description("Termina una match individual y tepea a los dos jugadores que estaban en la match a su slot del hoyo")
  public void onWin(CommandSender sender, OnlinePlayer player) {
    MatchPair matchPair = this.matchManager.getPlayerPair(player.getPlayer());
    if (matchPair != null) {
      Team t1 = matchPair.getPair().getLeft();
      Team t2 =  matchPair.getPair().getRight();
      // tp the players  to their location
      t1.teleportTeamToOwnLocation();
      t2.teleportTeamToOwnLocation();
      this.matchManager.playerPairs.remove(matchPair);
    } else {
      sender.sendMessage(ChatColor.RED + "El jugador " + player.getPlayer().getName() +  " tiene pareja nula. No existe o no tiene pareja");
    }
  }








}

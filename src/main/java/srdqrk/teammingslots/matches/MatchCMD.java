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

    List<Player> participants = new ArrayList<>();
    for (Team team : matchManager.teamManager.getTeams()) {
      participants.addAll(team.getPlayers());
    }

    Collections.shuffle(participants);
    int arenasGap = 10;
    int x =  this.matchManager.arenas.get(arenaNumber).getBlockX();
    int y = this.matchManager.arenas.get(arenaNumber).getBlockY();
    int z = this.matchManager.arenas.get(arenaNumber).getBlockZ();
    for (int i = 0; i < participants.size(); i += 2) {
      Player p1 = participants.get(i);
      Player p2 = participants.get(i + 1);
      Location loc1 = new Location(this.matchManager.arenas.get(arenaNumber).getWorld(), x, y + i * arenasGap, z);
      p1.teleport(loc1);
      p2.teleport(loc1);
      MatchPair newMatchPair = new MatchPair(p1,p2,loc1);
      this.matchManager.playerPairs.add(newMatchPair);
    }

  }

  @Subcommand("win")
  @CommandPermission("teammingslots.executer")
  @Description("Termina una match individual y tepea a los dos jugadores que estaban en la match a su slot del hoyo")
  public void onWin(CommandSender sender, OnlinePlayer player) {
    MatchPair matchPair = this.matchManager.getPlayerPair(player.getPlayer());
    if (matchPair != null) {
      Player player1 = matchPair.getPair().getLeft();
      Player player2 =  matchPair.getPair().getRight();
      // tp the players  to their location
      player1.teleport(this.matchManager.teamManager.getPlayerTeam(player1).getTeamLocation());
      player2.teleport(this.matchManager.teamManager.getPlayerTeam(player2).getTeamLocation());
      this.matchManager.playerPairs.remove(matchPair);
    } else {
      sender.sendMessage(ChatColor.RED + "El jugador " + player.getPlayer().getName() +  " tiene pareja nula. No existe o no tiene pareja");
    }
  }








}

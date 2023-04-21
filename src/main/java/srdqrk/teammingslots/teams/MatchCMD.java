package srdqrk.teammingslots.teams;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.*;


@CommandAlias("match|matches")
public class MatchCMD extends BaseCommand {

  final String arenasWorld = "world";
  TeamManager teamManager;
  Map<Integer, Location> arenas;
  List<Pair<Player, Player>> playerPairs;
  public MatchCMD() {
    this.teamManager = TeammingSlots.instance().getTeamManager();
    this.arenas = new HashMap<>();
    this.playerPairs = new ArrayList<>();

    arenas.put(1,new Location(Bukkit.getWorld(arenasWorld),0,100,0));
    arenas.put(2,new Location(Bukkit.getWorld(arenasWorld),0,100,0));
    arenas.put(3,new Location(Bukkit.getWorld(arenasWorld),0,100,0));
    arenas.put(4,new Location(Bukkit.getWorld(arenasWorld),0,100,0));
    arenas.put(5,new Location(Bukkit.getWorld(arenasWorld),0,100,0));
    arenas.put(6,new Location(Bukkit.getWorld(arenasWorld),0,100,0));

  }


  @Subcommand("forcefinish")
  @CommandPermission("teammingslots.executer")
  @Description("Retorna un jugador a su posicion de team")
  public void onForceFinish(CommandSender sender) {
    if (teamManager.getTeams().isEmpty()) {
      sender.sendMessage(ChatColor.RED + "No hay equipos creados");
      return;
    }
    for (Team team : teamManager.getTeams()) {
      team.teleportTeamToOwnLocation();
    }
    // remove past pairs, set up to the next match
    this.playerPairs.clear();
  }

  @Subcommand("start")
  @CommandPermission("teammingslots.executer")
  @Description("Empieza una match, el numero de match [1-6] representa cual match debe empezar")
  @CommandCompletion("@range:1-6")
  public void onStart(CommandSender sender, int arenaNumber) {
    if (teamManager.getTeams().isEmpty()) {
      sender.sendMessage(ChatColor.RED + "No hay equipos creados");
      return;
    }

    List<Player> participants = new ArrayList<>();
    for (Team team : teamManager.getTeams()) {
      participants.addAll(team.getPlayers());
    }

    Collections.shuffle(participants);
    int arenasGap = 10;
    int x =  this.arenas.get(arenaNumber).getBlockX();
    int y = this.arenas.get(arenaNumber).getBlockY();
    int z = this.arenas.get(arenaNumber).getBlockZ();
    for (int i = 0; i < participants.size(); i += 2) {
      Player p1 = participants.get(i);
      Player p2 = participants.get(i + 1);
      Location loc1 = new Location(this.arenas.get(arenaNumber).getWorld(), x, y + i * arenasGap, z);
      p1.teleport(loc1);
      p2.teleport(loc1);
      Pair<Player, Player> pair = Pair.of(p1, p2);

      this.playerPairs.add(pair);
    }

  }

  @Subcommand("win")
  @CommandPermission("teammingslots.executer")
  @Description("Termina una match individual y tepea a los dos jugadores que estaban en la match a su slot del hoyo")
  public void onStart(CommandSender sender, OnlinePlayer player) {
    Pair<Player, Player> pair = getPlayerPair(player.getPlayer());
    if (pair != null) {
      Player player1 = pair.getLeft();
      Player player2 = pair.getRight();
      // tp the players  to their location
      player1.teleport(this.teamManager.getPlayerTeam(player1).getTeamLocation());
      player2.teleport(this.teamManager.getPlayerTeam(player2).getTeamLocation());

    } else {
      sender.sendMessage(ChatColor.RED + "El jugador " + player.getPlayer().getName() +  " tiene pareja nula. No existe o no tiene pareja");
    }
  }


  public Pair<Player, Player> getPlayerPair(Player player) {
    for (Pair<Player, Player> pair : playerPairs) {
      if (pair.getLeft().getName().equals(player.getName()) || pair.getRight().getName().equals(player.getName())) {
        return pair;
      }
    }
    return null;
  }





}

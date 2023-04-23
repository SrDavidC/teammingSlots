package srdqrk.teammingslots.matches;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.teams.TeamManager;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class MatchManager {
  final String arenasWorld = "world";
  TeamManager teamManager;
  Map<Integer, Location> arenas;
  List<MatchPair> playerPairs;

  TeammingSlots instance;
  public MatchManager(TeammingSlots instance) {
    this.instance = instance;

    this.teamManager = TeammingSlots.instance().getTeamManager();
    this.arenas = new HashMap<>();
    this.playerPairs = new ArrayList<>();

    arenas.put(1,new Location(Bukkit.getWorld(arenasWorld),100,100,0));
    arenas.put(2,new Location(Bukkit.getWorld(arenasWorld),200,100,0));
    arenas.put(3,new Location(Bukkit.getWorld(arenasWorld),300,100,0));
    arenas.put(4,new Location(Bukkit.getWorld(arenasWorld),400,100,0));
    arenas.put(5,new Location(Bukkit.getWorld(arenasWorld),500,100,0));
    arenas.put(6,new Location(Bukkit.getWorld(arenasWorld),600,100,0));

  }

  public MatchPair getPlayerPair(Player player) {
    for (MatchPair matchPair : this.playerPairs) {
      Pair<Team, Team> pair = matchPair.getPair();
      if (pair.getLeft().containsPlayer(player.getName()) || pair.getLeft().containsPlayer(player.getName())) {
        return matchPair;
      }
    }
    return null;
  }

}

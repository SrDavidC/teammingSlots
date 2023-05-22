package srdqrk.teammingslots.matches;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.game.CurrentArena;
import srdqrk.teammingslots.matches.arenas.Arena;
import srdqrk.teammingslots.matches.listeners.XPListener;
import srdqrk.teammingslots.teams.TeamManager;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.*;


@Data
public class MatchManager {
  final String arenasWorld = "minigames";
  TeamManager teamManager;
  Map<Integer, Location> arenas;
  List<MatchPair> playerPairs;

  TeammingSlots instance;

  Arena actualArena;
  final private Map<Integer, Arena> arenasMap;
  public MatchManager(TeammingSlots instance) {
    this.instance = instance;

    this.teamManager = TeammingSlots.instance().getTeamManager();
    this.arenas = new HashMap<>();
    this.playerPairs = new ArrayList<>();
    this.arenasMap = new HashMap<>();
    World arenasWorld = Bukkit.getWorld("minigames");
    this.actualArena = null;

  }

  public MatchPair getPlayerPair(Player player) {
    for (MatchPair matchPair : this.actualArena.getPairs()) {
      Pair<Team, Team> pair = matchPair.getPair();
      if (pair.getLeft() != null && pair.getLeft().containsPlayer(player.getName()) ||
              pair.getRight() != null && pair.getRight().containsPlayer(player.getName())) {
        return matchPair;
      }
    }
    return null;
  }


  public List<MatchPair> createPairsList() {
    List<Team> teams = this.getTeamManager().getTeams();
    Collections.shuffle(teams);
    List<MatchPair> pairList = new ArrayList<>();

    for (int i = 0; i < teams.size() - 1; i += 2) {
      Team t1 = teams.get(i);
      Team t2 = teams.get(i + 1);
      pairList.add(new MatchPair(t1, t2));
    }

    if (teams.size() % 2 != 0) {
      Team lastTeam = teams.get(teams.size() - 1);
      pairList.add(new MatchPair(lastTeam, null));
    }

    return pairList;
  }


  public void createArena(CurrentArena arena) {
    if (arena != CurrentArena.NONE) {
      List<MatchPair> pairs = createPairsList();

      Location coordsArena;
      Vector gap;
      Vector gapTeam;
      CurrentArena arenaID;
      Listener listener;
      switch (arena) {
        case ARENA_1 -> {
          coordsArena = new Location(Bukkit.getWorld("world"),100,73,100);
          gap = new Vector(25,0,0);
          gapTeam = new Vector(10,0,0);
          arenaID = CurrentArena.ARENA_1;
          listener = new XPListener(0.05f,0.07f, arenasWorld);
          this.actualArena = new Arena(pairs,coordsArena,gap, gapTeam,arenaID,listener);
        }
        case ARENA_2 -> {

        }
        case ARENA_3 -> {
          coordsArena = new Location(Bukkit.getWorld(arenasWorld),100,73,100);
          gap = new Vector(25,0,0);
          gapTeam = new Vector(10,0,0);
          arenaID = CurrentArena.ARENA_3;
          listener = new XPListener(0.05f,0.07f, arenasWorld);
          this.actualArena = new Arena(pairs,coordsArena,gap, gapTeam,arenaID,listener);
        }
        case ARENA_4 -> {

        }
        case ARENA_5 -> {

        }
      }
    }

  }



}

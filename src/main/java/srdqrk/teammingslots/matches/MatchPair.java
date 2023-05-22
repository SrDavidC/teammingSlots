package srdqrk.teammingslots.matches;

import lombok.Data;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.HashMap;
import java.util.Map;

@Data
public class MatchPair {

  private Pair<Team, Team> pair;
  private HashMap<Team, Location> spawnLocationsMap;

  public MatchPair(Team team1, Team team2) {
    this.pair = new MutablePair<>(team1, team2);
    this.spawnLocationsMap = new HashMap<>();
  }

  public Team getLeft() {
    return this.pair.getLeft();
  }
  public Team getRight() {
    return this.pair.getRight();
  }

  public void setSpawnLocation(int slot, Location location) {
    if (this.pair != null) {
      if (this.pair.getLeft() != null && this.pair.getLeft().getSlot().getNumber() == slot) {
        this.spawnLocationsMap.put(this.getLeft(),location);
      } else if (this.pair.getRight() != null && this.pair.getRight().getSlot().getNumber() == slot) {
        this.spawnLocationsMap.put(this.getRight(),location);
      }
    }
  }
  public Location getPlayerSpawnPoint(Player p) {
    for (Map.Entry<Team, Location> entry : spawnLocationsMap.entrySet()) {
      Team team = entry.getKey();
      if (team.containsPlayer(p.getName())) {
        return entry.getValue();
      }
    }
    return null;
  }

}

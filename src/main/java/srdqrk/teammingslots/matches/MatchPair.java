package srdqrk.teammingslots.matches;

import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import srdqrk.teammingslots.teams.objects.Team;

@Data
public class MatchPair {

  private Pair<Team, Team> pair;
  private Location spawnLocation;

  public MatchPair(Team team1, Team team2, @NonNull Location location) {
    this.pair = new MutablePair<>(team1, team2);
    this.spawnLocation = location;
  }
  public MatchPair(Team team1, Team team2) {
    this.pair = new MutablePair<>(team1, team2);
  }

  public Team getLeft() {
    return this.pair.getLeft();
  }
  public Team getRight() {
    return this.pair.getRight();
  }
}

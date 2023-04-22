package srdqrk.teammingslots.matches;

import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
public class MatchPair {

  private Pair<Player, Player> pair;
  private Location spawnLocation;

  public MatchPair(Player player1, Player player2, @NonNull Location location) {
    this.pair.of(player1, player2);
    this.spawnLocation = location;
  }
}

package srdqrk.teammingslots.teams;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.teams.objects.Slot;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.*;

@Data
public class TeamManager {
  TeammingSlots instance;
  private List<Team> teams;
  private List<Slot> slots;

  final public int X_MEASURE = 30;
  final public int Y_MEASURE = 10;
  final public int Z_MEASURE = 18;

  final public int MEASURE_TO_HOLE = 6;
  final public int Z_HOLE_MEASURE = 6;
  final public int X_HOLE_MEASURE = 10;

  final public int MAX_SLOTS = 60;
  FileConfiguration config;
  Location startCorner;
  private List<String> hoyos;
  private List<Integer> bannedSlots;

  public TeamManager(TeammingSlots instance) {
    this.teams = new ArrayList<>();
    this.instance = instance;
    this.slots = new ArrayList<>();
    this.config = instance.getConfig();
    this.hoyos = new ArrayList<>();
    hoyos.add("hoyo_1");
    hoyos.add("hoyo_2");
    hoyos.add("hoyo_3");
    this.bannedSlots = new ArrayList<>();
    bannedSlots.add(1);
    bannedSlots.add(2);
    bannedSlots.add(37);
    bannedSlots.add(38);
    bannedSlots.add(68);
    bannedSlots.add(69);
    bannedSlots.add(70);

    createSlots(MAX_SLOTS);
  }

  public void createTeams(int maxPlayersPerTeam) {
    this.deleteTeams();

    List<String> participants = config.getStringList("participantes");
    // Players that are online and are in "participantes"
    List<Player> onlinePaticipants = new ArrayList<>();

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (participants.contains(player.getName())) {
        onlinePaticipants.add(player);
      }
    }
    Collections.shuffle(onlinePaticipants); // Shuffle players to randomize team assignment

    int numTeams = (int) Math.ceil((double) onlinePaticipants.size() / maxPlayersPerTeam); // Calculate number of teams needed
    int playerIndex = 0;

    onlinePaticipants = formatOnlinePlayers(onlinePaticipants); // format participants to force roleplayers team up
    for (int i = 0; i < numTeams; i++) {
      List<Player> teamPlayers = new ArrayList<>();
      for (int j = 0; j < maxPlayersPerTeam && playerIndex < onlinePaticipants.size(); j++) {
        Player player = onlinePaticipants.get(playerIndex++);
        teamPlayers.add(player);
      }
      teams.add(new Team(teamPlayers)); // Create team with player list
    }
    Collections.shuffle(teams); // Shuffle teams to randomize slots assignment
    for (int index = 0; index < this.teams.size(); index++) { // for each team assing a slot from slots list
      Team team = this.teams.get(index);
      team.setSlot(this.slots.get(index));
    }
  }

  public List<Player> formatOnlinePlayers(List<Player> playersList) {
    List<String> roleplayers = config.getStringList("roleplayers");
    Set<String> roleplayersSet = new HashSet<>(roleplayers);

    List<Player> formattedPlayers = new ArrayList<>();

    for (Player player : playersList) {
      if (!roleplayersSet.contains(player.getName())) {
        formattedPlayers.add(player);
      }
    }

    for (String roleName : roleplayers) {
      Player rolePlayer = Bukkit.getPlayer(roleName);
      if (rolePlayer != null) {
        formattedPlayers.add(rolePlayer);
      }
    }

    return formattedPlayers;
  }

  public void createSlots(int maxSlots) {
    int holeIndex = 0;
    Location startCorner = loadStartCorner(this.hoyos.get(holeIndex));

    for (int slotCounter = 0; slotCounter < maxSlots; slotCounter++) {
      int slotNumber = (slotCounter + 1);
      if (bannedSlots.contains(slotNumber)) {
        continue;
      }
      Slot newSlot = new Slot(startCorner, slotNumber, (startCorner.getBlockY() - (Y_MEASURE * slotCounter)));
      this.slots.add(newSlot);

      if (slotCounter % 37 == 0 && holeIndex < 2) {
        holeIndex++;
        // Update startCorner to the next hole's start corner
        startCorner = loadStartCorner(this.hoyos.get(holeIndex));
      }
    }
  }

  public Location loadStartCorner(String hoyo_name) {
    double x = config.getDouble(hoyo_name + ".x");
    double y = config.getDouble(hoyo_name + ".y");
    double z = config.getDouble(hoyo_name + ".z");
    String worldName = config.getString(hoyo_name + ".world");
    World world = Bukkit.getWorld(worldName);
    return new Location(world, x, y, z);
  }

  public void deleteTeams() {
    this.teams.clear();
  }

  public Team getPlayerTeam(Player playerToFind) {
    for (Team team : this.getTeams()) {
      if (team.containsPlayer(playerToFind.getName())) {
        return team;
      }
    }
    return null;
  }
}

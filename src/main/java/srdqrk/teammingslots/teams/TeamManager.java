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
    int slotProgressedInHole = 0;

    // Obtener la esquina de inicio del primer hoyo
    Location startCorner = loadStartCorner(this.hoyos.get(holeIndex));

    for (int slotCounter = 1; slotCounter < maxSlots; slotCounter++) {
      int slotNumber = slotCounter;
      System.out.println("[!!!] Slot counter: " + slotCounter);

      // Verificar si se ha completado un ciclo completo de hoyos (37 slots cada uno)
      if (slotCounter > 2 && slotCounter % 37 == 0 && holeIndex < 2) {
        System.out.println("Slot counter % 37 or hole index more than 2");
        holeIndex++;

        // Actualizar startCorner a la esquina de inicio del próximo hoyo
        startCorner = loadStartCorner(this.hoyos.get(holeIndex));
        System.out.println("Hole index: " + holeIndex);
        System.out.println("Nuevo valor asignado a start corner: " + startCorner);

        slotProgressedInHole = 0;
      }
      // Omitir los slots prohibidos
      if (bannedSlots.contains(slotNumber)) {
        slotProgressedInHole++;
        System.out.println("Slot no valido, se salta: Slot # " + slotCounter);
        continue;
      }

      System.out.println("Progresado: " + slotProgressedInHole);
      int yLayer = startCorner.getBlockY() - (Y_MEASURE * slotProgressedInHole);
      System.out.println("Y Layer: " + yLayer);

      // Crear un nuevo Slot y agregarlo a la lista
      Slot newSlot = new Slot(startCorner, slotNumber, yLayer);
      this.slots.add(newSlot);

      slotProgressedInHole++;

      System.out.println("[!!!] Start corner: X: " + startCorner.getBlockX() + " Y: " + yLayer + " Z: " + startCorner.getBlockZ());
      System.out.println("[!!!] Hole Index: " + holeIndex);

    }
  }


  public Location loadStartCorner(String hoyo_name) {
    System.out.println("Load Start corner: " + hoyo_name);
    System.out.println("Load Start corner: " + config.getDouble(hoyo_name + ".x"));
    System.out.println("Load Start corner: " + config.getDouble(hoyo_name + ".y"));
    System.out.println("Load Start corner: " + config.getDouble(hoyo_name + ".z"));

    double x = config.getDouble(hoyo_name + ".x") - 14d;
    double y = config.getDouble(hoyo_name + ".y");
    double z = config.getDouble(hoyo_name + ".z")- 12.5d;
    String worldName = config.getString(hoyo_name + ".world");
    World world = Bukkit.getWorld(worldName);
    Location l = new Location(world, x, y, z, 90f, 0);
    System.out.println(" New location calculated: " + l);
    return l.clone();
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

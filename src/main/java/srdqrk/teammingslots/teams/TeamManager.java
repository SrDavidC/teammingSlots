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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public TeamManager(TeammingSlots instance) {
        this.teams = new ArrayList<>();
        this.instance = instance;
        this.slots = new ArrayList<>();
        this.config = instance.getConfig();
        this.hoyos = new ArrayList<>();
        hoyos.add("hoyo_1");
        hoyos.add("hoyo_2");
        hoyos.add("hoyo_3");
        createSlots(MAX_SLOTS);
    }
    public void createTeams(int maxPlayersPerTeam) {
        this.deleteTeams();

        List<String> participantes = config.getStringList("participantes");
        List<Player> onlinePlayers = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (participantes.contains(player.getName())) {
                onlinePlayers.add(player);
            }
        }
        Collections.shuffle(onlinePlayers); // Shuffle players to randomize team assignment
        int numTeams = (int) Math.ceil((double) onlinePlayers.size() / maxPlayersPerTeam); // Calculate number of teams needed
        int playerIndex = 0;
        for (int i = 0; i < numTeams; i++) {
            List<Player> teamPlayers = new ArrayList<>();
            for (int j = 0; j < maxPlayersPerTeam && playerIndex < onlinePlayers.size(); j++) {
                Player player = onlinePlayers.get(playerIndex++);
                teamPlayers.add(player);
            }
            teams.add(new Team(teamPlayers, slots.get(i))); // Create team with player list and team location
        }
    }


    public void createSlots(int maxSlots) {
        int hoyo_index = 0;
        Location startCroner = loadStartCorner(this.hoyos.get(hoyo_index));
        for (int slotCounter = 0; slotCounter <= maxSlots ; slotCounter++) {
            this.slots.add(new Slot(startCroner, slotCounter,(startCroner.getBlockY() + (Y_MEASURE * slotCounter))));
            if (slotCounter % 20 == 0) {
                hoyo_index++;
                startCroner = loadStartCorner(this.hoyos.get(hoyo_index));
            }

        }
    }
    public Location loadStartCorner(String hoyo_name) {
        double x = config.getDouble( hoyo_name+".x");
        double y = config.getDouble(hoyo_name+"y");
        double z = config.getDouble(hoyo_name+".z");
        String worldName = config.getString(hoyo_name+".world");
        World world = Bukkit.getWorld(worldName);
        return new Location(world, x, y, z);
    }
    public void deleteTeams() {
        this.teams.clear();
    }
}

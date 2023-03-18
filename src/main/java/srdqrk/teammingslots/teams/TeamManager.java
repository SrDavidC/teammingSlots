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

    FileConfiguration config = instance.getConfig();
    Location startCorner;
    public TeamManager(TeammingSlots instance) {
        this.teams = new ArrayList<>();
        this.instance = instance;
        this.slots = new ArrayList<>();
    }
    public void createTeams(int maxPlayersPerTeam) {
        this.deleteTeams();
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
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


    public void createSlots(int maxSlots, Location startCroner) {
        for (int slotCounter = 0; slotCounter <= maxSlots ; slotCounter++) {
            this.slots.add(new Slot(startCroner, slotCounter,(startCroner.getBlockY() + (10 * slotCounter))));
        }
    }
    public Location loadStartCorner() {
        double x = config.getDouble("myLocation.x");
        double y = config.getDouble("myLocation.y");
        double z = config.getDouble("myLocation.z");
        String worldName = config.getString("myLocation.world");
        World world = Bukkit.getWorld(worldName);
        return new Location(world, x, y, z);
    }
    public void deleteTeams() {
        this.teams.clear();
    }
}

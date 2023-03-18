package srdqrk.teammingslots.teams;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Data
public class TeamManager {
    TeammingSlots instance;
    private List<Team> teams;

    public TeamManager(TeammingSlots instance) {
        this.teams = new ArrayList<>();
        this.instance = instance;
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
            Location l = null; // TODO: Complete where to find the team location and slot
            int slot = 0;
            teams.add(new Team(teamPlayers, l, slot)); // Create team with player list and team location
        }
    }

    public void deleteTeams() {
        this.teams.clear();
    }
}

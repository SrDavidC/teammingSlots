package srdqrk.teammingslots.teams.objects;

import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
@Data
public class Team {

    List<Player> players;
    Location teamLocation;
    Slot slot;

    /*
    public Team(List<Player> players, Slot slot) {
        this.players = players;
        this.slot = slot;
        this.teamLocation = slot.getSpawnLocation();
    }
    */

    public Team(List<Player> players) {
        this.players = players;
    }

    public void teleportTeam(Location location) {
        for (Player player: this.players) {
            player.teleport(location);
        }
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
        this.teamLocation = slot.getSpawnLocation();
    }
    public void teleportTeamToOwnLocation() {
        this.teleportTeam(this.teamLocation);
    }

    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.YELLOW + "Slot #")
                .append(this.slot.getNumber())
                .append("\n" + ChatColor.GREEN);
        for (Player p: this.players) {
            info.append(p.getName()).append(",");
        }
        info = new StringBuilder(info.substring(0, info.length() - 1));
        return info.toString();
    }
    public boolean containsPlayer(String playerName) {
        for (Player p : this.players) {
            if (p.getName().equals(playerName)) {
                return true;
            }
        }
        return false;
    }

}

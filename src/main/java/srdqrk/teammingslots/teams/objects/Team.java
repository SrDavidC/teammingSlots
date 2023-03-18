package srdqrk.teammingslots.teams.objects;

import lombok.Data;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
@Data
public class Team {

    List<Player> players;
    Location teamLocation;
    Slot slot;
    public Team(List<Player> players, Slot slot) {
        this.players = players;
        this.slot = slot;
    }

    public void teleportTeam(Location location) {
        for (Player player: this.players) {
            player.teleport(location);
        }
    }

    public void teleportTeamToOwnLocation() {
        this.teleportTeam(this.teamLocation);
    }

    public String getInfo() {
        String info = "";
        info += ChatColor.YELLOW + "Slot #" + this.slot;
        info += "\n" + ChatColor.GREEN;
        for (Player p: this.players) {
            info += p.getName() + ",";
        }
        info = info.substring(0, info.length()-1)  ;
        return info;
    }

}

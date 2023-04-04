package srdqrk.teammingslots.teams;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.Dependency;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import srdqrk.teammingslots.TeammingSlots;

public class TeamListener  implements Listener {

    @Dependency
    TeammingSlots instance;
    public TeamListener(TeammingSlots instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // TODO: Revisar si es participante, si lo es, eliminarlo de la lista
        System.out.println("El jugador " + e.getEntity().getName() + " ha muerto");
        Bukkit.dispatchCommand(instance.getServer().getConsoleSender(), "removeplayer " + e.getEntity().getName());

    }
}

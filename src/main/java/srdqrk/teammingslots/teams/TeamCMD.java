package srdqrk.teammingslots.teams;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("team|teams")
public class TeamCMD extends BaseCommand {

    private final TeammingSlots instance;
    private final TeamManager teamManager;
    private final FileConfiguration config;
    public TeamCMD(TeammingSlots instance) {
        this.instance = instance;
        this.config = instance.getConfig();
        this.teamManager = this.instance.getTeamManager();

    }
    @CommandAlias("createTeams")
    @Subcommand("createTeams")
    @CommandPermission("teammingslots.executer")
    @Syntax("/team createTeams <maxPlayersPerTeam>")
    public void createTeams(CommandSender sender, Integer maxPlayersPerTeam) {
        try {

            this.teamManager.createTeams(maxPlayersPerTeam);
            sender.sendMessage(ChatColor.GREEN + "Todos los equipos han sido creados."
            + "\nTotalidad de equipos: " + ChatColor.YELLOW + this.teamManager.getTeams().size()
            + ChatColor.GREEN + "\nCantidad máxima de jugadores por equipo: " +  ChatColor.YELLOW + maxPlayersPerTeam
            + ChatColor.DARK_GREEN + "\nRecuerda que puedes ver los equipos usando el comando /teamsview");
        } catch (Exception e) {
            System.out.println(e);
            sender.sendMessage(ChatColor.RED + "Error. Los equipos no pudieron ser creados, consulte la consola para " +
                    "más información acerca de este error.");
        }

    }
    @CommandAlias("deleteTeams")
    @Subcommand("deleteTeams")
    @CommandPermission("teammingslots.executer")
    @Syntax("/team deleteTeams")
    public void deleteTeams(CommandSender sender) {
        try {
            this.teamManager.deleteTeams();
            sender.sendMessage(ChatColor.GREEN + "Los equipos han sido disueltos.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Los equipos no pudieron ser disueltos. Consulte la consola para más " +
                    "información acerca de este error");
            System.out.println(e);
        }


    }
    @CommandAlias("teleportTeamTo")
    @Subcommand("teleportTeamTo")
    @CommandPermission("teammingslots.executer")
    @Syntax("/team teleportTeamTo <location> <teamSlot>")
    public void teleportTeamTo(CommandSender sender, @Conditions("x,y,z") Location location, int teamSlot) {
        Team teamToTeleport = this.teamManager.getTeams().stream().filter(team -> team.getSlot() == teamSlot)
                .findFirst().orElse(null);
        if (teamToTeleport != null) {
            teamToTeleport.teleportTeam(location);
            sender.sendMessage(ChatColor.GREEN +  "El equipo con el slot " + teamSlot + " ha sido teletransportado"
            + " correctamente a la ubicación " + location.toString());
        }
        else
            sender.sendMessage(ChatColor.RED + "[!] El team con el slot " + teamSlot + " no fue encontrado. Intente un "
            + "un slot menor");
    }
    @CommandAlias("teleportTeamToOwn")
    @Subcommand("teleportTeamToOwn")
    @CommandPermission("teammingslots.executer")
    @Syntax("/team teleportTeamsToOwn <teamSlot>")
    public void teleportTeamToOwn(CommandSender sender, int teamSlot) {
        Team teamToTeleport = this.teamManager.getTeams().stream().filter(team -> team.getSlot() == teamSlot)
                .findFirst().orElse(null);
        if (teamToTeleport != null)
            teamToTeleport.teleportTeam(teamToTeleport.getTeamLocation());
        else
            sender.sendMessage(ChatColor.RED + "[!] El team con el slot " + teamSlot + " no fue encontrado. Intente un "
                    + "un slot menor");
    }
    @CommandAlias("teleportAllTeamsToOwn")
    @Subcommand("teleportAllTeamsToOwn")
    @CommandPermission("teammingslots.executer")
    @Syntax("/team teleportAllTeamsToOwn")
    public void teleportAllTeamsToOwn(CommandSender sender) {
        for (Team team: this.teamManager.getTeams()
             ) {
            team.teleportTeamToOwnLocation();
        }
        sender.sendMessage(ChatColor.GREEN +  "Todos los equipos han sido teletransportados a su ubicación de equipo.");
    }
    @CommandAlias("teleportAllTeamsTo")
    @Subcommand("teleportAllTeamsTo")
    @CommandPermission("teammingslots.executer")
    @Syntax("/team teleportAllTeamsTo <x,y,z>")
    public void teleportAllTeamsTo(Player sender, @Conditions("x,y,z") Location location) {
        for (Team team : teamManager.getTeams()) {
            for (Player player : team.getPlayers()) {
                player.teleport(location);
            }
        }
        sender.sendMessage( ChatColor.GREEN + "Todos los equipos han sido teletransportados a la ubicación " + location.toString() + ".");
    }
    @CommandAlias("addAllParticipants")
    @Subcommand("addAllParticipants")
    @CommandPermission("teammingslots.executer")
    public void onAddAllParticipants(Player sender) {
        if (!sender.hasPermission("teammingslots.executer")) {
            sender.sendMessage(ChatColor.RED +  "No tienes permiso para ejecutar este comando.");
            return;
        }
        if (!config.contains("participantes")) {
            config.set("participantes", new ArrayList<String>());
        }
        List<String> participants = config.getStringList("participantes");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR || participants.contains(player.getName())) {
                continue;
            }

            participants.add(player.getName());
            sender.sendMessage(ChatColor.GREEN +  "Se ha agregado a " + player.getName() + " a la lista de participantes.");
        }

        config.set("participantes", participants);
        this.instance.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Se ha agregado a todos los jugadores elegibles a la lista de participantes.");
    }
    @CommandAlias("removeAllParticipants")
    @Subcommand("removeAllParticipants")
    @CommandPermission("teammingslots.executer")
    public void onRemoveAllParticipants(Player sender) {
        if (!sender.hasPermission("teammingslots.executer")) {
            sender.sendMessage(ChatColor.RED +  "No tienes permiso para ejecutar este comando.");
            return;
        }
        if (!config.contains("participantes")) {
            config.set("participantes", new ArrayList<String>());
        }
        config.set("participantes", null);
        this.instance.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Se eliminaron todos los participantes del archivo de configuración.");
    }
    @CommandAlias("removeplayer")
    @Subcommand("removeAllParticipants")
    @CommandPermission("teammingslots.executer")
    @Description("Elimina un jugador de la lista de participantes")
    public void descalificar(Player player, CommandSender sender) {
        if (this.config.getList("participantes") != null && this.config.getList("participantes").contains(player.getName())) {
            sender.sendMessage(ChatColor.RED + "El jugador " + player.getName() + " no se encuentra en la lista de participantes.");
            return;
        }

        List<String> participantes = this.config.getStringList("participantes");
        participantes.remove(player.getName());
        this.config.set("participantes", participantes);
        this.instance.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "Se ha eliminado al jugador " + player.getName() + " de la lista de participantes.");
    }
    @CommandAlias("teamsview")
    @Subcommand("teamsview")
    @CommandPermission("teammingslots.executer")
    @Description("Observar la conformacion de equipos")
    public void onTeamsView(CommandSender sender) {
        String teamsView = "";
        for (Team team : this.teamManager.getTeams()) {
            teamsView += team.getInfo();
            teamsView += "\n";
        }
        sender.sendMessage(teamsView);
    }




}


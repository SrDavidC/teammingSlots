package srdqrk.teammingslots.teams;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import srdqrk.teammingslots.TeammingSlots;
import srdqrk.teammingslots.teams.objects.Team;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("team|teams|slot|slots|ts")
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
    @CommandCompletion("@range:1-10")
    @CommandPermission("teammingslots.executer")
    @Syntax("/team createTeams <maxPlayersPerTeam>")
    public void createTeams(CommandSender sender, @Values("@range:1-10") @Default("2") Integer maxPlayersPerTeam) {
        try {
            this.teamManager.createTeams(maxPlayersPerTeam);
            String log = ChatColor.GREEN + "Todos los equipos han sido creados."
                    + "\nTotalidad de equipos: " + ChatColor.YELLOW + this.teamManager.getTeams().size()
                    + ChatColor.GREEN + "\nCantidad máxima de jugadores por equipo: " + ChatColor.YELLOW + maxPlayersPerTeam
                    + ChatColor.DARK_GREEN + "\nRecuerda que puedes ver los equipos usando el comando /teamsview";
            this.instance.logStaff(log);
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
            String log = ChatColor.GREEN + "Los equipos han sido disueltos";
            this.instance.logStaff(log);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Los equipos no pudieron ser disueltos. Consulte la consola para más " +
                    "información acerca de este error");
            System.out.println(e);
        }


    }
    @CommandAlias("loadParticipants")
    @Subcommand("loadParticipants")
    @CommandPermission("teammingslots.executer")
    public void onLoadParticipants(CommandSender sender) {
        if (!sender.hasPermission("teammingslots.executer")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para ejecutar este comando");
            return;
        }
        List<String> participants = config.getStringList("participantes");
        List<String> noParticipantes = config.getStringList("noParticipantes");

        if (participants == null) {
            config.set("participantes", new ArrayList<String>());
        }
        /*
        if (noParticipantes == null) {
            config.set("noParticipantes", new ArrayList<String>());
        }
*/
        for (Player player : Bukkit.getOnlinePlayers()) {
            // if not participant or already in participants do nothing
            if (noParticipantes.contains(player.getName()) || participants.contains(player.getName())) {
                continue;
            }

            participants.add(player.getName());
            sender.sendMessage(ChatColor.GREEN + "Se ha agregado a " + player.getName() + " a la lista de participantes");
        }

        config.set("participantes", participants);
        this.instance.saveConfig();

        String log = ChatColor.GREEN + "Se han agregado a todos los jugadores elegibles a la lista de participantes";
        this.instance.logStaff(log);
    }
    @CommandAlias("addParticipant")
    @CommandCompletion("@participants")
    @Subcommand("addParticipant")
    @CommandPermission("teammingslots.executer")
    @Description("Agrega un jugador de la lista de participantes")
    public void onAddParticipant(CommandSender sender,OnlinePlayer onlinePlayer) {
        Player player = onlinePlayer.getPlayer();
        List<String> participantes = this.config.getStringList("participantes");
        List<String> noParticipantes = this.config.getStringList("noParticipantes");

        // Add from participantes and delete to noParticipantes
        participantes.add(player.getName());
        noParticipantes.remove(player.getName());
        this.config.set("participantes", participantes);
        this.config.set("noParticipantes", noParticipantes);
        // save config
        this.instance.saveConfig();
        // Broadcast to all staffs
        String log = ChatColor.RED + " El jugador " + player.getName() +
                " ha sido agregado de la lista de participantes y eliminado de la lista de noParticipantes";
        this.instance.logStaff(log);
    }

    @CommandAlias("clearParticipants")
    @Subcommand("clearParticipants")
    @CommandPermission("teammingslots.executer")
    public void onRemoveAllParticipants(CommandSender sender) {
        if (!sender.hasPermission("teammingslots.executer")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para ejecutar este comando");
            return;
        }
        if (!config.contains("participantes")) {
            config.set("participantes", new ArrayList<String>());
        }
        config.set("participantes", null);
        this.instance.saveConfig();
        String log = ChatColor.GREEN + "Se eliminaron todos los participantes del archivo de configuración";
        this.instance.logStaff(log);
    }

    @CommandAlias("removeplayer")
    @CommandCompletion("@participants")
    @Subcommand("removePlayer")
    @CommandPermission("teammingslots.executer")
    @Description("Elimina un jugador de la lista de participantes")
    public void onRemovePlayer(CommandSender sender,OnlinePlayer onlinePlayer) {
        Player player = onlinePlayer.getPlayer();
        List<String> participantes = this.config.getStringList("participantes");
        List<String> noParticipantes = this.config.getStringList("noParticipantes");

        // Delete from participantes and add to noParticipantes
        participantes.remove(player.getName());
        noParticipantes.add(player.getName());
        this.config.set("participantes", participantes);
        this.config.set("noParticipantes", noParticipantes);
        // save config
        this.instance.saveConfig();
        // Broadcast to all staffs
        String log = ChatColor.RED + " El jugador " + player.getName() +
                " ha sido eliminado de la lista de participantes";
        this.instance.logStaff(log);
    }

    @CommandAlias("removeNPplayer")
    @CommandCompletion("@participants")
    @Subcommand("removeNPplayer")
    @CommandPermission("teammingslots.executer")
    @Description("Elimina un jugador de la lista de participantes")
    public void onRemoveNPPlayer(CommandSender sender,OnlinePlayer onlinePlayer) {
        Player player = onlinePlayer.getPlayer();
        List<String> noParticipantes = this.config.getStringList("noParticipantes");

        // Delete from participantes and add to noParticipantes
        noParticipantes.remove(player.getName());
        this.config.set("noParticipantes", noParticipantes);
        // save config
        this.instance.saveConfig();
        // Broadcast to all staffs
        String log = ChatColor.RED + " El jugador " + player.getName() +
                " ha sido eliminado de la lista de NO participantes";
        this.instance.logStaff(log);
    }

    @CommandAlias("clearNoParticipantes")
    @CommandCompletion("@participants")
    @Subcommand("clearNoParticipantes")
    @CommandPermission("teammingslots.executer")
    @Description("Elimina todos los jugadores de la lista de no participantes")
    public void onClearNP(CommandSender sender) {
        if (!sender.hasPermission("teammingslots.executer")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para ejecutar este comando");
            return;
        }
        if (!config.contains("noParticipantes")) {
            config.set("noParticipantes", new ArrayList<String>());
        }
        config.set("noParticipantes", null);
        this.instance.saveConfig();
        String log = ChatColor.GREEN + "Se eliminaron todos los NO participantes del archivo de configuración";
        this.instance.logStaff(log);
    }

    @CommandAlias("teamsview")
    @Subcommand("teamsview")
    @CommandPermission("teammingslots.executer")
    @Description("Observar la conformacion de equipos")
    public void onTeamsView(CommandSender sender) {
        String teamsView = "";
        if (this.teamManager.getTeams().isEmpty()) {
            teamsView = ChatColor.RED + "La lista de equipos está vacía";
        } else {
            for (Team team : this.teamManager.getTeams()) {
                teamsView += team.getInfo();
                teamsView += "\n";
            }
        }
        sender.sendMessage(teamsView);
    }

    /**
     * TP Command, firsts where, then who
     * Where: could be: own (where 'who' team spawn location) or a coordinate like 'x,y,z', example: 100,0,50
     * Who: Could be 'all' or a slot. It's called identifier because it identifies all to refer all or a slot number in a range
     * of [1,60]
     *
     * @param sender
     * @param location
     * @param identifier
     */
    @Subcommand("teleport")
    @CommandPermission("teammingslots.executer")
    @Description("Teletransportar uno por slot o todos los slots a una coordenada.La coordenada es dada o automatica " +
            "'own'.")
    @Syntax("/slot teleport <coordenada, own> <all,slotNumber>")
    public void onTeletransportTeam(CommandSender sender, String location, String identifier) {
        if (!(identifier.equalsIgnoreCase("all"))) {
            int teamSlot = Integer.parseInt(identifier);
            Team teamToTeleport = this.teamManager.getTeams().stream().filter(team -> team.getSlot().getNumber() == teamSlot)
                    .findFirst().orElse(null);
            if (teamToTeleport != null) {
                Location teleportLocation = getLocationFromString(location);
                teamToTeleport.teleportTeam(teleportLocation);
                sender.sendMessage(ChatColor.GREEN + "El equipo con el slot " + teamSlot + " ha sido teletransportado"
                        + " correctamente a la ubicación " + location);
            } else {
                sender.sendMessage(ChatColor.RED + "[!] El team con el slot " + teamSlot + " no fue encontrado. Intente un "
                        + "un slot menor");
            }
        } else {
            if (teamManager.getTeams().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No hay equipos creados");
                return;
            }
            for (Team team : teamManager.getTeams()) {
                Location teleportLocation;
                if (location.equals("own")) {
                    team.teleportTeamToOwnLocation();
                } else {
                    teleportLocation = getLocationFromString(location);
                    for (Player player : team.getPlayers()) {
                        player.teleport(teleportLocation);
                    }
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Todos los equipos han sido teletransportados a la ubicación "
                    + location);

        }
    }
    @CommandAlias("return")
    @CommandCompletion("@participants")
    @CommandPermission("teammingslots.executer")
    @Description("Retorna un jugador a su posicion de team")
    public void onReturnPlayer(CommandSender sender, OnlinePlayer playerSearched) {
        Team playerSrchdTeam = this.teamManager.getPlayerTeam(playerSearched.getPlayer());
        if (playerSrchdTeam != null) {
            playerSearched.getPlayer().teleport(playerSrchdTeam.getTeamLocation());
            String log = ChatColor.GREEN + playerSearched.getPlayer().getName() + " ha sido teletransportado";
            this.instance.logStaff(log);
        } else {
            sender.sendMessage(ChatColor.RED + "El jugador " + playerSearched.getPlayer().getName() + " no existe o no " +
                    "tiene equipo");
        }
    }

    private Location getLocationFromString(String stringLocation) {
        String[] parts = stringLocation.split(",");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        return new Location(Bukkit.getWorld("world"), x, y, z);
    }
    @Subcommand("start")
    @CommandPermission("teammingslots.executer")
    public void onStart(CommandSender sender, @Default("2") Integer teamSize) {
        this.onLoadParticipants(sender);
        this.createTeams(sender, teamSize);
        this.onTeletransportTeam(sender,"own", "all");
    }

}


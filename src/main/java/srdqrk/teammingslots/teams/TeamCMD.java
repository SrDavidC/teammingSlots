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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@CommandAlias("team|teams|slot|slots|ts")
public class TeamCMD extends BaseCommand {

    private final TeammingSlots instance;
    private final TeamManager teamManager;
    private final FileConfiguration config;

    public TeamCMD(TeammingSlots instance) {
        this.instance = instance;
        this.config = instance.getConfig();
        this.teamManager = this.instance.getTeamManager();

        instance.getCommandManager().getCommandCompletions().registerStaticCompletion("lists",
                List.of("NP", "participantes"));
        instance.getCommandManager().getCommandCompletions().registerStaticCompletion("locations",
                List.of("x,y,z", "own"));
        List<String> opciones = new ArrayList<>(List.of("all"));
        List<String> numeros = IntStream.rangeClosed(1, 60)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        opciones.addAll(numeros);
        instance.getCommandManager().getCommandCompletions().registerStaticCompletion("identifiers", opciones);

    }

    @Subcommand("start")
    @CommandPermission("teammingslots.executer")
    public void onStart(CommandSender sender, @Default("2") Integer teamSize) {
        this.onLoadParticipants(sender);
        this.createTeams(sender, teamSize);
        this.onTeletransportTeam(sender,"own", "all");

        this.instance.getMatchManager().getArenas().put(1,new Location(Bukkit.getWorld("minigames"),-45,17,0));
        this.instance.getMatchManager().getArenas().put(2,new Location(Bukkit.getWorld("minigames"),200,100,0));
        this.instance.getMatchManager().getArenas().put(3,new Location(Bukkit.getWorld("minigames"),300,100,0));
        this.instance.getMatchManager().getArenas().put(4,new Location(Bukkit.getWorld("minigames"),400,100,0));
        this.instance.getMatchManager().getArenas().put(5,new Location(Bukkit.getWorld("minigames"),500,100,0));
        this.instance.getMatchManager().getArenas().put(6,new Location(Bukkit.getWorld("minigames"),600,100,0));
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
    @CommandAlias("add")
    @Subcommand("add")
    @CommandPermission("teammingslots.executer")
    @CommandCompletion("@players @lists")
    @Description("Elimina un jugador de alguna de la listas de Teamming Slots")
    public void onAddPlayer(CommandSender sender, OnlinePlayer onlinePlayer, String list) {
        Player player = onlinePlayer.getPlayer();
        if (list.equals("NP")) {
            List<String> noParticipantes = this.config.getStringList("noParticipantes");
            noParticipantes.add(player.getName());
            this.config.set("noParticipantes", noParticipantes);
        } else if (list.equals("participantes")) {
            List<String> participantes = this.config.getStringList("participantes");
            participantes.add(player.getName());
            this.config.set("participantes", participantes);
        } else {
            sender.sendMessage(ChatColor.RED + "La lista llamada " + list + " no existe.");
            return;
        }
        this.instance.saveConfig();
        // Broadcast to all staffs
        String log = ChatColor.RED + " El jugador " + player.getName() +
                " ha sido agregado de la lista " + list;
        this.instance.logStaff(log);
    }
    @CommandAlias("remove")
    @Subcommand("remove")
    @CommandPermission("teammingslots.executer")
    @CommandCompletion("@players @lists")
    @Description("Elimina un jugador de alguna de la listas de Teamming Slots")
    public void onRemovePlayer(CommandSender sender, OnlinePlayer onlinePlayer, @Values("@lists") String list) {
        Player player = onlinePlayer.getPlayer();
        if (list.equals("NP")) {
            List<String> noParticipantes = this.config.getStringList("noParticipantes");
            noParticipantes.remove(player.getName());
            this.config.set("noParticipantes", noParticipantes);
        } else if (list.equals("participantes")) {
            List<String> participantes = this.config.getStringList("participantes");
            participantes.remove(player.getName());
            this.config.set("participantes", participantes);
        } else {
            sender.sendMessage(ChatColor.RED + "La lista llamada " + list + " no existe.");
            return;
        }
        this.instance.saveConfig();
        // Broadcast to all staffs
        String log = ChatColor.RED + " El jugador " + player.getName() +
                " ha sido eliminado de la lista " + list;
        this.instance.logStaff(log);
    }
    @Subcommand("clear")
    @CommandPermission("teammingslots.executer")
    @CommandCompletion("@lists")
    public void onClear(CommandSender sender, String list) {
        if (!sender.hasPermission("teammingslots.executer")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para ejecutar este comando");
            return;
        }
        if (list.equals("NP")) {
            config.set("noParticipantes", null);
        } else if (list.equals("participantes")) {
            config.set("participantes", null);
        } else {
            sender.sendMessage(ChatColor.RED + "La lista llamada " + list + " no existe.");
            return;
        }

        this.instance.saveConfig();
        String log = ChatColor.RED + "Se eliminaron todos los elementos de la lista " + list;
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
    @CommandCompletion("@locations @identifiers")
    public void onTeletransportTeam(CommandSender sender, String location, String identifier) {
        System.out.println(location);
        System.out.println(location.length());

        if (!(identifier.equalsIgnoreCase("all"))) {
            int teamSlot = Integer.parseInt(identifier);
            Team teamToTeleport = this.teamManager.getTeams().stream().filter(team -> team.getSlot().getNumber() == teamSlot)
                    .findFirst().orElse(null);
            if (teamToTeleport != null) {
                Location teleportLocation;
                if (location.equals("own")) {
                    teleportLocation = teamToTeleport.getTeamLocation();
                } else {
                    teleportLocation = getLocationFromString(location);
                }
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

}


package srdqrk.teammingslots.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {
  public static final String TELEPORT_ASCII = "ASCII CORRECTO";

  public static void sendTeleportTitle(Player player) {
    player.sendTitle(ChatColor.WHITE + TELEPORT_ASCII, "", 10, 30, 10);
  }
  public static void sendTitleToAll() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      sendTeleportTitle(player);
    }
  }
}

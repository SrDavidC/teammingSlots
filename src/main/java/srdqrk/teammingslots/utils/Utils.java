package srdqrk.teammingslots.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Utils {
  public static final String TELEPORT_ASCII = "\uE001";

  public static void sendTeleportTitle(Player player) {
    player.sendTitle(ChatColor.WHITE + TELEPORT_ASCII, "", 20, 40, 20);
    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20,1));
  }
  public static void sendTitleToAll() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      sendTeleportTitle(player);
    }
  }
}

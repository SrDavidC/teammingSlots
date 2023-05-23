package srdqrk.teammingslots.matches.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

import javax.management.remote.JMXServerErrorException;

public class ExampleListener implements Listener {

  public String message;
  public ExampleListener(String message) {
    this.message = message;
  }
  @EventHandler
  public void onEvent(PlayerEggThrowEvent e) {
    e.getPlayer().sendMessage(ChatColor.GOLD + message);
  }

}

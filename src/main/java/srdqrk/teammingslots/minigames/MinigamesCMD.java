package srdqrk.teammingslots.minigames;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import srdqrk.teammingslots.TeammingSlots;

@CommandAlias("mg|minigames")
public class MinigamesCMD extends BaseCommand {
  TeammingSlots instance;
  private @Getter boolean xpBar;
  private XPBar xpBarListener;

  private @Getter @Setter float additionalXP = 0.05f;
  private @Getter @Setter float decreaseXP = 0.07f;

  public MinigamesCMD() {
    this.instance = TeammingSlots.instance();
    this.xpBar = false;
  }

  @Subcommand("xpbar")
  public void onXPBar(CommandSender sender) {
    if (xpBar) { // if on, turn off
      if (this.xpBarListener != null) {
        int xpTaskID = this.xpBarListener.getTask();
        instance.getServer().getScheduler().cancelTask(xpTaskID);
        HandlerList.unregisterAll(this.xpBarListener);
        this.instance.logStaff(ChatColor.YELLOW + "XP Bar ha sido apagado");
        this.xpBar = false;
      } else {
        sender.sendMessage(ChatColor.RED + "Error: XP bar listener doesnt exists");
      }
    } else { // if off, turn on
      for (Player player : instance.getServer().getOnlinePlayers()) {
        player.setExp(0.5f);
      }
      this.xpBarListener = new XPBar(this.additionalXP, this.decreaseXP);
      instance.getServer().getPluginManager().registerEvents(this.xpBarListener, instance);
      xpBar = true;
      this.instance.logStaff(ChatColor.GREEN + "XP Bar ha sido encendido\nCon valores de :" + this.additionalXP + " y "+ this.decreaseXP);
    }
  }

  @Subcommand("timingsXP")
  public void onXPBarTimings(CommandSender sender, float additionalXP, float decreaseXP) {
    try {
      additionalXP = (float) additionalXP;
      decreaseXP = (float) decreaseXP;
      System.out.println(additionalXP);
      System.out.println(decreaseXP);
      this.additionalXP = additionalXP;
      this.decreaseXP = decreaseXP;
      this.instance.logStaff(ChatColor.GREEN + "Se ajustaron los nuevos timings a " + this.additionalXP + "f en ADDIONAL XP " +
                      " y " + this.decreaseXP + "f en DECREASE XP");
      sender.sendMessage(ChatColor.GREEN + "Vuelve a iniciar la XPbar para ver los cambios.");
    } catch (Exception exception) {
      exception.printStackTrace();
      sender.sendMessage(ChatColor.RED + "No se pudieron asignar los nuevos timings de XP. Consulte la consola");
    }

  }



}

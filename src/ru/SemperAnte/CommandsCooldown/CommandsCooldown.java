package ru.SemperAnte.CommandsCooldown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.SemperAnte.CommandsCooldown.Utils.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandsCooldown extends JavaPlugin implements Listener
{
	 private Config configClass = new Config(this, "config.yml");
	 private FileConfiguration config = configClass.getConfig();
	 private Map<String, Integer> commands = new HashMap<>();
	 private Config usersCoolDown = new Config(this, "users.yml");
	 private FileConfiguration users = usersCoolDown.getConfig();

	 @Override
	 public void onEnable()
	 {
		  Map<String, Object> cmds = config.getConfigurationSection("commands").getValues(true);
		  for (String s : cmds.keySet())
				commands.put(s.toLowerCase(), (int) cmds.get(s));

		  Bukkit.getPluginManager().registerEvents(this, this);
	 }

	 @EventHandler
	 public void onCommandEvent(PlayerCommandPreprocessEvent event)
	 {
		  String cmd = event.getMessage().replaceAll("/", "").split(" ")[0];
		  PluginCommand Pcom = Bukkit.getPluginCommand(cmd);
		  if (Pcom == null)
				return;
		  List<String> aliases = Pcom.getAliases();
		  Player player = event.getPlayer();
		  aliases.add(Pcom.getName());
		  for (String command : Pcom.getAliases())
		  {
				command = command.toLowerCase();
				if (commands.containsKey(command) && !player.hasPermission("CommandCooldown.bypass"))
				{
					 int coolDown = commands.get(command);
					 long raz = System.currentTimeMillis() - users.getLong(player.getName() + "." + command);
					 if (raz < coolDown)
					 {
						  player.sendMessage(ChatColor.GOLD + "[Задержка] " + ChatColor.RED + " Вы недавно отправляли эту команду. Подождите ещё " + raz + "с.");
						  event.setCancelled(true);
					 }
					 else
					 {
						  users.set(player.getName() + "." + command, System.currentTimeMillis());
						  usersCoolDown.saveConfig();
					 }
					 break;
				}
		  }

	 }

	 @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	 {
		  try
		  {
				args[0] = args[0].toLowerCase();
				switch (args[0])
				{
					 case "reload":
						  reloadConfig();
						  sender.sendMessage("[CommandCooldown] Config reloaded");
						  break;
					 case "add":
						  if (commands.containsKey(args[1]))
								sender.sendMessage("[CommandCooldown] Command already exists");
						  else
						  {
								commands.put(args[1], Integer.valueOf(args[2]));
								config.set("commands", commands);
								configClass.saveConfig();
								sender.sendMessage("[CommandCooldown] Command cooldown add");
						  }
						  break;
					 case "remove":
						  if (!commands.containsKey(args[1]))
								sender.sendMessage("[CommandCooldown] Command not exists");
						  else
						  {
								commands.remove(args[1]);
								config.set("commands", commands);
								configClass.saveConfig();
								sender.sendMessage("[CommandCooldwn] Command Removed");
						  }
						  break;
				}
		  }
		  catch (ArrayIndexOutOfBoundsException | NumberFormatException e)
		  {
				sender.sendMessage("[CommandCooldown] /ComCol reload - reload config");
				sender.sendMessage("[CommandCooldown] /ComCol add <command> <second> - add command cooldown");
				sender.sendMessage("[CommandCooldown] /ComCol rmeove <command> - remove command cooldown");
		  }
		  return true;
	 }

	 public void reloadConfig()
	 {
		  configClass.reloadCFG();
		  config = configClass.getConfig();
		  usersCoolDown.reloadCFG();
		  users = usersCoolDown.getConfig();
	 }

}

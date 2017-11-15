package ru.SemperAnte.CommandsCooldown.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

public class Config
{
	 private final JavaPlugin plugin;
	 private File ConfigFile;
	 private FileConfiguration Config;
	 private final String file;

	 public Config(JavaPlugin main, String name)
	 {
		  plugin = main;
		  file = name;
	 }

	 public void reloadConfig()
	 {
		  if (ConfigFile == null)
		  {
				ConfigFile = new File(plugin.getDataFolder(), file);
		  }
		  Config = YamlConfiguration.loadConfiguration(ConfigFile);

		  Reader defConfigStream;
		  try
		  {
				defConfigStream = new InputStreamReader(plugin.getResource(file), "UTF8");
				if (defConfigStream != null)
				{
					 YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
					 Config.setDefaults(defConfig);
					 plugin.saveResource(file, false);
				}
		  }
		  catch (UnsupportedEncodingException e)
		  {
				e.printStackTrace();
		  }
	 }

	 public void reloadCFG()
	 {
		  if (ConfigFile == null)
		  {
				ConfigFile = new File(plugin.getDataFolder(), file);
		  }
		  Config = YamlConfiguration.loadConfiguration(ConfigFile);

	 }

	 public FileConfiguration getConfig()
	 {
		  if (Config == null)
		  {
				reloadConfig();
		  }
		  return Config;
	 }

	 public void saveConfig()
	 {
		  if (Config == null || ConfigFile == null)
		  {
				return;
		  }
		  try
		  {
				getConfig().save(ConfigFile);
		  }
		  catch (IOException ex)
		  {
				if (!ConfigFile.exists())
					 plugin.getLogger().log(Level.SEVERE, "Could not save config to " + ConfigFile, ex);
		  }
	 }

	 public void saveDefaultConfig()
	 {
		  if (ConfigFile == null)
		  {
				ConfigFile = new File(plugin.getDataFolder(), file);
		  }
		  if (!ConfigFile.exists())
		  {
				plugin.saveResource(file, false);
		  }
	 }
}

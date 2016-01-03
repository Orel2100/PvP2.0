package at.lukasberger.bukkit.pvp.core.objects;

import at.lukasberger.bukkit.pvp.PvP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.Charset;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class Config
{

    File configFile = null;
    public FileConfiguration config = null;
    String configFileName = "";

    /**
     * Create a new instance of a configuration
     * @param fileName The name of the file without ".yml"
     */
    public Config(String fileName)
    {
        this.configFileName = fileName.replace("/", File.separator) + ".yml";
        this.configFile = new File(PvP.getInstance().getDataFolder(), this.configFileName);

        this.configFile.getParentFile().mkdirs();

        if(this.exists())
            this.reloadConfig();
    }

    /**
     * Indicates if the file exists
     * @return If the file exists or not
     */
    public boolean exists()
    {
        return configFile.exists();
    }

    /**
     * Deletes the config-file
     */
    public void delete()
    {
        configFile.delete();
        this.reloadConfig();
    }

    /**
     * Reloads the config from file
     */
    public void reloadConfig()
    {
        if (this.configFile == null)
            this.configFile = new File(PvP.getInstance().getDataFolder(), this.configFileName);

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Saves the config to file
     */
    public void saveConfig()
    {
        if (this.config == null || this.configFile == null)
            return;

        try
        {
            this.config.save(this.configFile);
        }
        catch (IOException ex)
        {
            PvP.getInstance().getLogger().severe(ex.getMessage());
        }
    }

    /**
     * Saves the given resource to the file
     * @param resourceName Name of resource without ".yml"
     */
    public void saveDefaultConfig(String resourceName)
    {
        this.saveDefaultConfig(resourceName, false);
    }

    /**
     * Saves the given resource to the file
     * @param resourceName Name of resource without ".yml"
     * @param force If the saving should be forced
     */
    public void saveDefaultConfig(String resourceName, boolean force)
    {
        if (this.configFile == null)
            this.configFile = new File(PvP.getInstance().getDataFolder(), this.configFileName);

        if (!this.configFile.exists() || force)
        {
            PvP.getInstance().getLogger().info("Writing default \"" + resourceName + "\" to " + this.configFile.getAbsolutePath());
            InputStreamReader defConfigStream = new InputStreamReader(PvP.getInstance().getResource(resourceName + ".yml"), Charset.forName("UTF-8"));
            FileOutputStream defConfigOutStream;

            try
            {
                defConfigOutStream = new FileOutputStream(this.configFile);

                int data = defConfigStream.read();
                while(data != -1)
                {
                    defConfigOutStream.write(data);
                    data = defConfigStream.read();
                }

                defConfigStream.close();
                defConfigOutStream.flush();
                defConfigOutStream.close();
            }
            catch (IOException ex)
            {
                PvP.getInstance().getLogger().severe(ex.getMessage());
            }
        }

        this.saveConfig();
        this.reloadConfig();
    }

}

package at.lukasberger.bukkit.pvp.core.objects;

import at.lukasberger.bukkit.pvp.PvP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class Config
{

    File configFile = null;
    public FileConfiguration config = null;
    String configFileName = "";

    public Config(String fileName)
    {
        this.configFileName = fileName.replace("/", File.separator) + ".yml";
        this.configFile = new File(PvP.getInstance().getDataFolder(), this.configFileName);

        this.configFile.getParentFile().mkdirs();

        if(this.exists())
            this.reloadConfig();
    }

    public boolean exists()
    {
        return configFile.exists();
    }

    public void delete()
    {
        configFile.delete();
    }

    public void reloadConfig()
    {
        if (this.configFile == null)
            this.configFile = new File(PvP.getInstance().getDataFolder(), this.configFileName);

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

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

    public void saveDefaultConfig(String resourceName)
    {
        this.saveDefaultConfig(resourceName, false);
    }

    public void saveDefaultConfig(String resourceName, boolean force)
    {
        if (this.configFile == null)
            this.configFile = new File(PvP.getInstance().getDataFolder(), this.configFileName);

        if (!this.configFile.exists() || force)
        {
            try
            {
                Reader defConfigStream = new InputStreamReader(PvP.getInstance().getResource(resourceName + ".yml"), "UTF8");
                OutputStream defConfigOutStream;

                try
                {
                    defConfigOutStream = new FileOutputStream(this.configFile);

                    int data = defConfigStream.read();
                    while(data != -1)
                    {
                        defConfigOutStream.write(data);
                        data = defConfigStream.read();
                    }
                }
                catch (IOException ex)
                {
                    PvP.getInstance().getLogger().severe(ex.getMessage());
                }
                finally
                {
                    defConfigStream = null;
                    defConfigOutStream = null;
                }
            }
            catch (UnsupportedEncodingException ex)
            {
                PvP.getInstance().getLogger().severe(ex.getMessage());
            }
        }

        this.saveConfig();
        this.reloadConfig();
    }

}

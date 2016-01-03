package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.objects.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class MessageManager
{

    private HashMap<String, Config> messagesFiles = new HashMap<>();

    public static MessageManager instance = new MessageManager();

    private MessageManager() { }

    /**
     * Loads the given language into memory and adds missing variables
     * @param langName The name of the language
     */
    public void loadLanguage(String langName)
    {
        Config tmp = new Config("langs/" + langName);

        Config defaultMessages = new Config("langs/default");
        defaultMessages.saveDefaultConfig("lang", true);

        // updating variables from language which should be loaded
        Set<String> keys = defaultMessages.config.getKeys(true);
        for(String key : keys)
        {
            if (!tmp.config.getKeys(true).contains(key))
            {
                PvP.getInstance().getLogger().info("[Language][" + langName.toUpperCase() + "] Updating \"" + key + "\" to value \"" + defaultMessages.config.get(key).toString() + "\"");
                tmp.config.set(key, defaultMessages.config.get(key));
            }
        }

        defaultMessages.delete();
        tmp.saveConfig();

        messagesFiles.put(langName, tmp);
    }

    /**
     * Returns the given text in players language and replaces placeholders
     * @param p The player
     * @param name Name of the language-variable
     * @param params Values of the placeholders
     * @return The translated and replaced text
     */
    public String get(CommandSender p, String name, Object... params)
    {
        String value = "";
        String lang = "";

        if(p instanceof Player)
            lang = InGameManager.instance.getPlayer((Player)p).getLanguage();
        else
            lang = PvP.getInstance().getConfig().getString("language");

        if(!messagesFiles.containsKey(lang))
            this.loadLanguage(lang);

        Config messagesFile = messagesFiles.get(lang);
        value = messagesFile.config.getString(messagesFile.config.getKeys(false).toArray()[0] + "." + name);

        if(params == null || params.length == 0)
            return ChatColor.translateAlternateColorCodes('&', value);
        else
        {
            String formatted = ChatColor.translateAlternateColorCodes('&', value);

            for(int i = 0; i < params.length; i++)
                formatted = formatted.replace("%" + (i + 1), params[i].toString());

            return formatted;
        }
    }

}

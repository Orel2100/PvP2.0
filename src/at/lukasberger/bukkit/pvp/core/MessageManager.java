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
     * @param lang The name of the language
     */
    public void loadLanguage(String lang)
    {
        Config tmp = new Config("langs/" + lang);

        Config defaultMessages = new Config("langs/default");
        defaultMessages.saveDefaultConfig("lang", true);

        // updating variables from language which should be loaded
        Set<String> keys = defaultMessages.config.getConfigurationSection("en").getKeys(true);
        for(String key : keys)
        {
            if (!tmp.config.getKeys(true).contains(lang + "." + key))
            {
                PvP.getInstance().getLogger().info("[Language] Updating \"" + lang + "." + key +
                        "\" to value \"" + defaultMessages.config.get("en." + key).toString() + "\"");
                tmp.config.set(lang + key, defaultMessages.config.get("en." + key));
            }
        }

        defaultMessages.delete();
        tmp.saveConfig();

        messagesFiles.put(lang, tmp);
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
        value = messagesFile.config.getString(lang + "." + name);

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

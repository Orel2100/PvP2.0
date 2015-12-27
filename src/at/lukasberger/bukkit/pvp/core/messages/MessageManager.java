package at.lukasberger.bukkit.pvp.core.messages;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.objects.Config;
import org.bukkit.ChatColor;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class MessageManager
{

    private Config messagesFile;
    private Config defaultMessages;

    public static MessageManager instance = new MessageManager();

    private MessageManager() { }

    // set the given language as standard
    public void loadLanguage(String langName)
    {
        messagesFile = new Config("langs/" + langName);
        messagesFile.saveDefaultConfig("lang");

        defaultMessages = new Config("langs/default");
        defaultMessages.delete();

        defaultMessages = new Config("langs/default");
        defaultMessages.delete();
        defaultMessages.saveDefaultConfig("lang");
    }

    public String get(String name, Object... params)
    {
        String value = "";

        if(!messagesFile.config.contains(name))
        {
            if(!defaultMessages.config.contains(name))
            {

            }
            else
            {
                value = defaultMessages.config.getString(name);
                PvP.getInstance().getLogger().info("Adding missing language variable from default to current language file: " + name);

                messagesFile.config.set(name, value);
                messagesFile.saveConfig();
            }
        }
        else
            value = messagesFile.config.getString(name);

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

package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.objects.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class MessageManager
{

    private HashMap<String, Config> messagesFiles = new HashMap<>();
    private Config defaultMessages;

    public static MessageManager instance = new MessageManager();

    private MessageManager() { }

    // set the given language as standard
    public void loadLanguage(String langName)
    {
        Config tmp = new Config("langs/" + langName);
        tmp.saveDefaultConfig("lang");

        messagesFiles.put(langName, tmp);

        defaultMessages = new Config("langs/default");
        defaultMessages.delete();
        defaultMessages.saveDefaultConfig("lang", true);
    }

    public String get(CommandSender p, String name, Object... params)
    {
        String value = "";
        String lang = "";

        if(p instanceof Player)
            lang = InGameManager.instance.getPlayer((Player)p).getLanguage();
        else
            lang = PvP.getInstance().getConfig().getString("language");

        Config messagesFile = messagesFiles.get(lang);

        if(!messagesFile.config.contains(name))
        {
            if(!defaultMessages.config.contains(name))
            {
                PvP.getInstance().getLogger().severe("The language-variable \"" + name + "\" does not exists");
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
